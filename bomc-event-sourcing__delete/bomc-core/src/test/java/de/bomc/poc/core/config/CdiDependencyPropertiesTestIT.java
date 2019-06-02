package de.bomc.poc.core.config;

import java.net.URISyntaxException;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import de.bomc.poc.core.arq.ArquillianBase;
import de.bomc.poc.core.category.IntegrationTestIT;
import de.bomc.poc.core.config.ConfigurationPropertiesConstants;
import de.bomc.poc.core.config.producer.PropertiesConfiguratorProducer;
import de.bomc.poc.core.config.qualifier.PropertiesConfigTypeQualifier;
import de.bomc.poc.core.config.type.PropertiesConfigTypeEnum;
import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the CDI dependencies for the injectable properties.
 * 
 * <pre>
 *     mvn clean install -Parq-wildfly-remote -Dtest=CdiDependencyPropertiesTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
@RunWith(Arquillian.class)
@Category(IntegrationTestIT.class)
public class CdiDependencyPropertiesTestIT extends ArquillianBase {

	// _______________________________________________
	// Private constants
	// -----------------------------------------------
	private static final String LOG_PREFIX = "CdiDependencyPropertiesTestIT#";
	// Check jboss-web.xml for 'context-root'.
	private static final String WEB_ARCHIVE_NAME = "cdi-dependency-properties";
	// _______________________________________________
	// Member variables.
	// -----------------------------------------------
	@Inject
	@LoggerQualifier
	private Logger logger;

	// 'testable = true', means all the tests are running inside of the
	// container.
	@Deployment(testable = true)
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME, "none");
		webArchive.addClasses(HealthCheckVersionTestIT.class, IntegrationTestIT.class);

		webArchive.addClasses(PropertiesConfigTypeQualifier.class, PropertiesConfigTypeEnum.class);
		webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
		webArchive.addClasses(PropertiesConfiguratorProducer.class, ConfigurationPropertiesConstants.class);
		webArchive.addClass(CdiDependencyMockEJB.class);
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

	@EJB
	private CdiDependencyMockEJB cdiAmbigMockEJB;

	/**
	 * Test reading (artifact) version from app server.
	 * 
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=VersionTestIT#test010_v1_readProperties_Pass
	 * </pre>
	 * 
	 * @throws URISyntaxException
	 *             is thrown during URI creation, is not expected.
	 */
	@Test
	@InSequence(10)
	public void test010_v1_readProperties_Pass() throws URISyntaxException {
		this.logger.info(LOG_PREFIX + "test010_v1_readProperties_Pass");

		final String version = this.cdiAmbigMockEJB.getVersionProperties()
				.getProperty(ConfigurationPropertiesConstants.VERSION_VERSION_PROPERTY_KEY);

		this.logger.debug(LOG_PREFIX + "test010_v1_readProperties_Pass [version=" + version + "]");

		assertThat(version, equalTo("1.00.00-SNAPSHOT"));
	}
}
