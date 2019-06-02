package de.bomc.poc.prometheus.arquillian;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

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

/**
 * Tests handles metrics endpoint to prometheus.
 * 
 * <pre>
 *     mvn clean install -Parq-wildfly-remote -Dtest=PrometheusMetricsTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@RunWith(Arquillian.class)
public class PrometheusMetricsTestIT extends ArquillianBase {
	private static final String LOG_PREFIX = "PrometheusMetricsTestIT#";
	private static final String WEB_ARCHIVE_SAMPLE_SERVICE_NAME = "sample-service";
	private static final String WEB_ARCHIVE_PROMETHEUS_SERVICE_NAME = "prometheus-service";
	private static final String BASE_PROMETHEUS_SERVICE_URI = BASE_URL + WEB_ARCHIVE_PROMETHEUS_SERVICE_NAME;
	private static ResteasyWebTarget webTargetPrometheusConfigService;
	@Inject
	@LoggerQualifier
	private Logger logger;
	
	// 'testable = true', means all the tests are running inside of the
	// container.
	@Deployment(testable = true, name = WEB_ARCHIVE_SAMPLE_SERVICE_NAME, order = 1)
	public static Archive<?> createSampleServiceDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_SAMPLE_SERVICE_NAME);
		webArchive.addClass(PrometheusMetricsTestIT.class);
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
		webArchive.addClass(PrometheusMetricsTestIT.class);
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
		// Create rest client with Resteasy Client Framework for sample service
		final ResteasyClient clientPrometheusConfigService = new ResteasyClientBuilder()
				.establishConnectionTimeout(10, TimeUnit.SECONDS).socketTimeout(10, TimeUnit.MINUTES)
				.register(new ResteasyClientLogger(this.logger, true)).connectionPoolSize(10).build();
		webTargetPrometheusConfigService = clientPrometheusConfigService
				.target(PrometheusMetricsTestIT.BASE_PROMETHEUS_SERVICE_URI + "/" + JaxRsActivator.APPLICATION_PATH
						+ "/configurations");
	}
	
	/**
	 * 
	 * 
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=PrometheusMetricsTestIT#test010_v1_prometheusMetrics_Pass
	 * </pre>
	 */
	@Test
	@InSequence(10)
	@OperateOnDeployment(WEB_ARCHIVE_PROMETHEUS_SERVICE_NAME)
	public void test010_v1_prometheusMetrics_Pass() {
		this.logger.info(LOG_PREFIX + "test010_v1_prometheusMetrics_Pass");

		Response createdOrUpdated = null;

		try {
			// Put configuration data from sample-service in the configuration store.
			final String configurationName = "sampleservice";

			JsonObject origin = Json.createObjectBuilder()
					.add("uri", "http://192.168.4.1:8180/sample-service/resources/metrics")
					.add("application", configurationName).build();
			this.logger.debug(LOG_PREFIX + "test030_v1_prometheusConfiguration_Pass  [origin = " + origin + "]");

			createdOrUpdated = webTargetPrometheusConfigService.path(configurationName)
					.request(MediaType.APPLICATION_JSON).put(Entity.json(origin));

			this.logger.debug(LOG_PREFIX + "test030_v1_prometheusConfiguration_Pass [status="
					+ createdOrUpdated.getStatus() + "]");

			assertThat(createdOrUpdated.getStatusInfo().getFamily(), equalTo(Response.Status.Family.SUCCESSFUL));
			
			// Read metrics from MetricsResourceEndpoint.
			final ResteasyClient clientPrometheusMetricsService = new ResteasyClientBuilder()
					.establishConnectionTimeout(10, TimeUnit.SECONDS).socketTimeout(10, TimeUnit.MINUTES)
					.register(new ResteasyClientLogger(this.logger, true)).connectionPoolSize(1).build();
			
			final Response pingResponse = clientPrometheusMetricsService
					.target(PrometheusMetricsTestIT.BASE_PROMETHEUS_SERVICE_URI + "/" + JaxRsActivator.APPLICATION_PATH
							+ "/metrics").path(configurationName).request().accept(MediaType.TEXT_PLAIN).get();
			
			assertThat(pingResponse.getStatusInfo().getFamily(), equalTo(Response.Status.Family.SUCCESSFUL));
			
			final String result = pingResponse.readEntity(String.class);
			this.logger.debug(LOG_PREFIX + "test010_v1_prometheusMetrics_Pass [result=" + result + "]");
		} finally {
			if (createdOrUpdated != null) {
				createdOrUpdated.close();
			}
		}
	}
	
	
}
