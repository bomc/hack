package de.bomc.poc.prometheus.arquillian;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.prometheus.collector.boundary.MetricsResource;
import de.bomc.poc.prometheus.collector.boundary.MetricsResourceEndpoint;
import de.bomc.poc.prometheus.collector.control.DataCollectorEJB;
import de.bomc.poc.prometheus.collector.control.MetricNotConfiguredException;
import de.bomc.poc.prometheus.collector.entity.Metric;
import de.bomc.poc.prometheus.configuration.boundary.ConfigurationResource;
import de.bomc.poc.prometheus.configuration.boundary.ConfigurationStore;
import de.bomc.poc.prometheus.configuration.control.EnvironmentVariables;
import de.bomc.poc.prometheus.mock.JaxRsMockActivator;
import de.bomc.poc.prometheus.mock.MetricsMockResource;
import de.bomc.poc.prometheus.mock.MetricsMockResourceEndpoint;
import de.bomc.poc.prometheus.rest.JaxRsActivator;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests handles configuration services to prometheus.
 * 
 * <pre>
 *     mvn clean install -Parq-wildfly-remote -Dtest=PrometheusConfigTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@RunWith(Arquillian.class)
public class PrometheusConfigTestIT extends ArquillianBase {
	private static final String LOG_PREFIX = "PrometheusConfigTestIT#";
	private static final String WEB_ARCHIVE_SAMPLE_SERVICE_NAME = "sample-service";
	private static final String WEB_ARCHIVE_PROMETHEUS_SERVICE_NAME = "prometheus-service";
	private static final String BASE_SAMPLE_SERVICE_URI = BASE_URL + WEB_ARCHIVE_SAMPLE_SERVICE_NAME;
	private static final String BASE_PROMETHEUS_SERVICE_URI = BASE_URL + WEB_ARCHIVE_PROMETHEUS_SERVICE_NAME;
	private static ResteasyWebTarget webTargetSampleService;
	private static ResteasyWebTarget webTargetPrometheusConfigService;
	@Inject
	@LoggerQualifier
	private Logger logger;

	// 'testable = true', means all the tests are running inside of the
	// container.
	@Deployment(testable = true, name = WEB_ARCHIVE_SAMPLE_SERVICE_NAME, order = 1)
	public static Archive<?> createSampleServiceDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_SAMPLE_SERVICE_NAME);
		webArchive.addClass(PrometheusConfigTestIT.class);
		webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
		webArchive.addClass(ResteasyClientLogger.class);
		webArchive.addClasses(JaxRsMockActivator.class, MetricsMockResource.class, MetricsMockResourceEndpoint.class);
		webArchive.addAsWebInfResource("META-INF/beans.xml", "beans.xml");
		webArchive.addAsWebInfResource("META-INF/jboss-web-sample-service.xml", "jboss-web.xml");

		System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

		return webArchive;
	}

	// 'testable = true', means all the tests are running inside of the
	// container.
	@Deployment(testable = true, name = WEB_ARCHIVE_PROMETHEUS_SERVICE_NAME, order = 2)
	public static Archive<?> createPrometheusServiceDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_PROMETHEUS_SERVICE_NAME);
		webArchive.addClass(PrometheusConfigTestIT.class);
		webArchive.addClasses(MetricsResource.class, MetricsResourceEndpoint.class);
		webArchive.addClasses(DataCollectorEJB.class, MetricNotConfiguredException.class);
		webArchive.addClasses(Metric.class);
		webArchive.addClasses(ConfigurationResource.class, ConfigurationStore.class, JaxRsActivator.class);
		webArchive.addClasses(EnvironmentVariables.class);
		webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
		webArchive.addClass(ResteasyClientLogger.class);
		webArchive.addAsWebInfResource("META-INF/beans.xml", "beans.xml");
		webArchive.addAsWebInfResource("META-INF/jboss-web-prometheus-service.xml", "jboss-web.xml");

		System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

		return webArchive;
	}

	/**
	 * Setup.
	 */
	@Before
	public void setupClass() {
		//
		// Create rest client with Resteasy Client Framework for sample service.
		final ResteasyClient clientSampleService = new ResteasyClientBuilder()
				.establishConnectionTimeout(10, TimeUnit.SECONDS).socketTimeout(10, TimeUnit.MINUTES)
				.register(new ResteasyClientLogger(this.logger, true)).build();
		webTargetSampleService = clientSampleService
				.target(PrometheusConfigTestIT.BASE_SAMPLE_SERVICE_URI + "/" + JaxRsMockActivator.APPLICATION_PATH);
		//
		// Create rest client with Resteasy Client Framework for sample service
		final ResteasyClient clientPrometheusConfigService = new ResteasyClientBuilder()
				.establishConnectionTimeout(10, TimeUnit.SECONDS).socketTimeout(10, TimeUnit.MINUTES)
				.register(new ResteasyClientLogger(this.logger, true)).connectionPoolSize(10).build();
		webTargetPrometheusConfigService = clientPrometheusConfigService
				.target(PrometheusConfigTestIT.BASE_PROMETHEUS_SERVICE_URI + "/" + JaxRsActivator.APPLICATION_PATH
						+ "/configurations");
	}

	/**
	 * Test reads metrics endpoint direct from endpoint.
	 * 
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=PrometheusConfigTestIT#test010_v1_prometheusSampleService_Pass
	 * </pre>
	 */
	@Test
	@InSequence(10)
	@OperateOnDeployment(WEB_ARCHIVE_SAMPLE_SERVICE_NAME)
	public void test010_v1_prometheusSampleService_Pass() {
		this.logger.info(LOG_PREFIX + "test010_v1_prometheusSampleService_Pass");

		Response response = null;

		try {
			response = webTargetSampleService.proxy(MetricsMockResource.class).metric();

			assertThat(response.getStatusInfo().getFamily(), equalTo(Response.Status.Family.SUCCESSFUL));

			if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
				final String jsonString = response.readEntity(String.class);

				assertThat(jsonString, containsString("MetricsMockResourceEndpoint"));

				this.logger
						.info(LOG_PREFIX + "test010_v1_prometheusSampleService_Pass [jsonString=" + jsonString + "]");
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * CRUD against the cache.
	 * 
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=PrometheusConfigTestIT#test020_v1_prometheusConfigurationCrud_Pass
	 * </pre>
	 */
	@Test
	@InSequence(20)
	@OperateOnDeployment(WEB_ARCHIVE_PROMETHEUS_SERVICE_NAME)
	public void test020_v1_prometheusConfigurationCrud_Pass() {
		this.logger.info(LOG_PREFIX + "test020_v1_prometheusConfigurationCrud_Pass");

		Response response = null;

		try {
			final String configurationName = "bomc-prometheus-config-name-" + System.currentTimeMillis();

			final JsonObject origin = Json.createObjectBuilder().add("host", "bomc_host:4242_port").build();

			final Response initiallyCreated = webTargetPrometheusConfigService.path(configurationName)
					.request(MediaType.APPLICATION_JSON).put(Entity.json(origin));

			assertThat(initiallyCreated.getStatusInfo().getFamily(), equalTo(Response.Status.Family.SUCCESSFUL));

			final String location = initiallyCreated.getHeaderString("Location");
			this.logger.debug(LOG_PREFIX + "test020_v1_prometheusConfigurationCrud_Pass [location=" + location + "]");
			assertThat(location, notNullValue());

			final ResteasyClient clientPrometheusConfigService = new ResteasyClientBuilder()
					.establishConnectionTimeout(10, TimeUnit.SECONDS).socketTimeout(10, TimeUnit.MINUTES)
					.register(new ResteasyClientLogger(this.logger, true)).build();
			final Response evenCreated = clientPrometheusConfigService.target(location)
					.request(MediaType.APPLICATION_JSON).get();
			assertThat(evenCreated.getStatusInfo().getFamily(), equalTo(Response.Status.Family.SUCCESSFUL));

			final JsonObject existingConfiguration = evenCreated.readEntity(JsonObject.class);
			assertThat(origin.getString("host"), equalTo(existingConfiguration.getString("host")));

			final JsonObject updated = Json.createObjectBuilder().add("host", "bomc_not_host:4242_port").build();
			final Response updatedResponse = webTargetPrometheusConfigService.path(configurationName)
					.request(MediaType.APPLICATION_JSON).put(Entity.json(updated));
			assertThat(updatedResponse.getStatus(), equalTo(204));

			final Response evenUpdated = webTargetPrometheusConfigService.path(configurationName)
					.request(MediaType.APPLICATION_JSON).get();
			assertThat(evenUpdated.getStatus(), equalTo(200));

			final Response deleteResponse = webTargetPrometheusConfigService.path(configurationName).request().delete();
			assertThat(deleteResponse.getStatus(), equalTo(204));

			final Response evenDeleted = webTargetPrometheusConfigService.path(configurationName)
					.request(MediaType.APPLICATION_JSON).get();
			assertThat(evenDeleted.getStatus(), equalTo(204));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * 
	 * 
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=PrometheusConfigTestIT#test030_v1_prometheusConfiguration_Pass
	 * </pre>
	 */
	@Test
	@InSequence(30)
	@OperateOnDeployment(WEB_ARCHIVE_PROMETHEUS_SERVICE_NAME)
	public void test030_v1_prometheusConfiguration_Pass() {
		this.logger.info(LOG_PREFIX + "test030_v1_prometheusConfiguration_Pass");

		Response createdOrUpdated = null;

		try {
			final String configurationName = "prometheus-service";

			JsonObject origin = Json.createObjectBuilder()
					.add("uri", "http://prometheus-service:8080/sample-service/rest/metrics")
					.add("application", "sampleservice").build();
			this.logger.debug(LOG_PREFIX + "test030_v1_prometheusConfiguration_Pass  [origin = " + origin + "]");

			createdOrUpdated = webTargetPrometheusConfigService.path(configurationName)
					.request(MediaType.APPLICATION_JSON).put(Entity.json(origin));

			this.logger.debug(LOG_PREFIX + "test030_v1_prometheusConfiguration_Pass [status="
					+ createdOrUpdated.getStatus() + "]");

			assertThat(createdOrUpdated.getStatusInfo().getFamily(), equalTo(Response.Status.Family.SUCCESSFUL));
		} finally {
			if (createdOrUpdated != null) {
				createdOrUpdated.close();
			}
		}
	}
}
