package de.bomc.poc.test.arq.togglz;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.performance.annotation.Performance;
import org.jboss.arquillian.performance.annotation.PerformanceTest;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.togglz.core.Feature;
import org.togglz.core.context.FeatureContext;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.repository.FeatureState;

import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.JaxRsActivator;
import de.bomc.poc.rest.endpoints.impl.TogglzRESTResourceImpl;
import de.bomc.poc.rest.endpoints.v1.TogglzRESTResource;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;
import de.bomc.poc.service.impl.VersionSingletonEJB;
import de.bomc.poc.togglz.MultipleEnumFeaturesProvider;
import de.bomc.poc.togglz.TogglzFeatureProducer;
import de.bomc.poc.togglz.TogglzFeatures;
import de.bomc.poc.togglz.TogglzUserProvider;

/**
 * Tests the programmatically toggle of features.
 * 
 * <pre>
 *     mvn clean install -Parq-wildfly-remote -Dtest=TogglzTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@PerformanceTest(resultsThreshold = 10)
@RunWith(Arquillian.class)
public class TogglzTestIT extends ArquillianBase {

	private static final String LOG_PREFIX = "TogglzTestIT#";
	private static final String WEB_ARCHIVE_NAME = "bomc-togglz";
	private static ResteasyWebTarget webTarget;
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	private FeatureManager featureManager;

	// 'testable = true', means all the tests are running inside of the
	// container.
	@Deployment(testable = true)
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
		webArchive.addClass(VersionRestResourceTestIT.class);
		webArchive.addClasses(TogglzFeatureProducer.class, TogglzFeatures.class, TogglzUserProvider.class,
				MultipleEnumFeaturesProvider.class);
		webArchive.addClasses(TogglzRESTResource.class, TogglzRESTResourceImpl.class);
		webArchive.addClasses(VersionSingletonEJB.class, JaxRsActivator.class, LoggerQualifier.class,
				LoggerProducer.class);
		webArchive.addAsWebInfResource("META-INF/beans.xml", "beans.xml");
		webArchive.addAsResource(VersionSingletonEJB.VERSION_PROPERTIES_FILE,
				VersionSingletonEJB.VERSION_PROPERTIES_FILE);
		webArchive.addAsResource(TogglzFeatureProducer.TOGGLZ_FEATURES_PROPERTIES_FILE,
				TogglzFeatureProducer.TOGGLZ_FEATURES_PROPERTIES_FILE);

		// Add dependencies
		final MavenResolverSystem resolver = Maven.resolver();

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:exception-lib:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:rest-lib:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.togglz:togglz-cdi:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.togglz:togglz-core:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

		return webArchive;
	}

	/**
	 * Setup.
	 */
	@Before
	public void setupClass() {
		// Create rest client with Resteasy Client Framework.
		final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
				.socketTimeout(10, TimeUnit.MINUTES).connectionPoolSize(3).build();
		client.register(new ResteasyClientLogger(this.logger, true));

		webTarget = client.target(
				UriBuilder.fromPath(this.buildBaseUrl(WEB_ARCHIVE_NAME) + "/" + JaxRsActivator.APPLICATION_PATH));

		assertThat(featureManager, notNullValue());

		this.logger.debug(LOG_PREFIX + "init [featureManager=" + featureManager + "]");
	}

	/**
	 * Test reading available heap from app server.
	 * 
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=TogglzTestIT#test010_v1_readVersion_Pass
	 * </pre>
	 */
	@Test
	@InSequence(10)
	@Performance(time = 1500)
	public void test010_v1_readVersion_Pass() {
		this.logger.info(LOG_PREFIX + "test010_v1_readVersion_Pass");

		Response response = null;

		try {
			// Testing FEATURE_2, that is initialized false, so we get
			// "TOOGLZ-SNAPSHOT" as answer.
			response = webTarget.proxy(TogglzRESTResource.class).getVersion();

			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				final JsonObject jsonObject = response.readEntity(JsonObject.class);

				assertThat(jsonObject.getJsonString("Version").getString(), containsString("TOOGLZ-SNAPSHOT"));

				this.logger.info(LOG_PREFIX + "test010_v1_readVersion_Pass [version=" + jsonObject + "]");
			}

			// _______________________________________
			// Enable the FEATURE_2 toggle programmatically to true.

			final List<Feature> featureList = this.featureManager.getFeatures().stream()
					.filter(feature -> feature.name().equals(TogglzFeatures.FEATURE_2.name()))
					.collect(Collectors.toList());
			final FeatureState featureState = this.featureManager.getFeatureState(featureList.get(0));

			this.logger.info(LOG_PREFIX + "test010_v1_readVersion_Pass [feature=" + featureList.get(0).name()
					+ ", state=" + featureState.isEnabled() + "]");

			// ________________________________________
			// Set the feature state.
			FeatureManager featureManager = FeatureContext.getFeatureManagerOrNull();
			featureManager.setFeatureState(featureState.enable());

			// Testing FEATURE_2, that is initialized false, so we get
			// "1.00.00-SNAPSHOT" as answer.
			response = webTarget.proxy(TogglzRESTResource.class).getVersion();

			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				final JsonObject jsonObject = response.readEntity(JsonObject.class);

				final String retVersion = jsonObject.getJsonString("Version").getString();

				assertThat(retVersion, equalTo("1.00.00-SNAPSHOT"));

				this.logger.info(LOG_PREFIX + "test010_v1_readVersion_Pass [version=" + jsonObject + "]");
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}

		assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
	}
}
