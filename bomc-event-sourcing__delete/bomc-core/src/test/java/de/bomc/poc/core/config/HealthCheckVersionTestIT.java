package de.bomc.poc.core.config;

import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import de.bomc.poc.core.arq.ArquillianBase;
import de.bomc.poc.core.category.IntegrationTestIT;
import de.bomc.poc.core.config.ConfigurationPropertiesConstants;
import de.bomc.poc.core.config.producer.PropertiesConfiguratorProducer;
import de.bomc.poc.core.config.qualifier.PropertiesConfigTypeQualifier;
import de.bomc.poc.core.config.type.PropertiesConfigTypeEnum;
import de.bomc.poc.core.rest.JaxRsActivator;
import de.bomc.poc.core.rest.endpoints.VersionEndpoint;
import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link VersionEndpoint} to ensure the kubernetes health check.
 * 
 * <pre>
 *	mvn clean install -Parq-wildfly-remote -Dtest=HealthCheckVersionTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
@RunWith(Arquillian.class)
@Category(IntegrationTestIT.class)
public class HealthCheckVersionTestIT extends ArquillianBase {

	// _______________________________________________
	// Private constants
	// -----------------------------------------------
	private static final String LOG_PREFIX = "HealthCheckVersionTestIT#";
	// NOTE: Check jboss-web.xml for 'context-root'.
	private static final String WEB_ARCHIVE_NAME = "registrator-app";
	// _______________________________________________
	// Member variables.
	// -----------------------------------------------
	@Inject
	@LoggerQualifier
	private Logger logger;
	private ResteasyClient client;

	// 'testable = true', means all the tests are running inside of the
	// container.
	@Deployment(testable = true)
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME, "none");
		webArchive.addClasses(HealthCheckVersionTestIT.class, IntegrationTestIT.class);

		webArchive.addClasses(VersionEndpoint.class, PropertiesConfiguratorProducer.class,
				PropertiesConfigTypeQualifier.class, PropertiesConfigTypeEnum.class,
				ConfigurationPropertiesConstants.class);
		webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
		webArchive.addClasses(JaxRsActivator.class);
		webArchive.addAsWebInfResource(ArquillianBase.getEmptyBeansXml(), "beans.xml");
		webArchive.addAsResource(ConfigurationPropertiesConstants.VERSION_PROPERTIES_FILE,
				ConfigurationPropertiesConstants.VERSION_PROPERTIES_FILE);

		// Add dependencies
		final ConfigurableMavenResolverSystem resolver = Maven.configureResolver().withMavenCentralRepo(false);

		webArchive.addAsLibraries(
				resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:rest-lib:jar:?").withTransitivity().asFile());

		System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

		return webArchive;
	}

	/**
	 * Setup.
	 */
	@Before
	public void setupClass() {
		// Create rest client with Resteasy Client Framework.
		this.client = new ResteasyClientBuilder().build();
		this.client.register(new ResteasyClientLogger(this.logger, true));
	}

	/**
	 * Test reading (artifact) version from app server.
	 * 
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=VersionTestIT#test010_v1_readVersion_Pass
	 * </pre>
	 * 
	 * @throws URISyntaxException
	 *             is thrown during URI creation, is not expected.
	 */
	@Test
	@InSequence(10)
	public void test010_v1_readVersion_Pass() throws URISyntaxException {
		this.logger.info(LOG_PREFIX + "test010_v1_readVersion_Pass [uri=" + this.buildUri(WEB_ARCHIVE_NAME) + "]");

		Response response = null;

		try {
			final ResteasyWebTarget target = client
					.target(this.buildUri(WEB_ARCHIVE_NAME) + VersionEndpoint.VERSION_ENDPOINT_PATH + "/version");
			response = target.request("application/json").get();

			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				final JsonObject jsonObject = response.readEntity(JsonObject.class);
				this.logger.info(LOG_PREFIX + "test01_v1_readVersion_Pass [version=" + jsonObject + "]");
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}

		final Integer expected = Integer.valueOf(Response.Status.OK.getStatusCode());
		final Integer actual = Integer.valueOf(response.getStatus());

		assertThat(expected, equalTo(actual));
	}

}
