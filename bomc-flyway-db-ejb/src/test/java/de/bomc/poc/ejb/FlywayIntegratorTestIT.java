/**
 * Project: bomc-flyway-db-ejb
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2017
 */
package de.bomc.poc.ejb;

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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.model.Product;

/**
 * Tests the validating and migrating of db-schema with flyway.
 * 
 * <pre>
 *     mvn clean install -Parq-wildfly-remote -Dtest=FlywayIntegratorTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@RunWith(Arquillian.class)
public class FlywayIntegratorTestIT extends ArquillianBase {
	private static final String LOG_PREFIX = "FlywayIntegratorTestIT#";
	private static final String WEB_ARCHIVE_NAME = "bomc-flyway-ejb-invoker";
	// private static final String BASE_URI = BASE_URL + WEB_ARCHIVE_NAME;
	@Inject
	@LoggerQualifier
	private Logger logger;
	@EJB
	private DelegateDbAccessEJB delegateDbAccessEJB;
	
	// 'testable = true', means all the tests are running inside of the
	// container.
	@Deployment(testable = true)
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
		webArchive.addClass(FlywayIntegratorTestIT.class);
		webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
		webArchive.addClasses(FlywayIntegratorSingletonEJB.class);
		webArchive.addClasses(Product.class);
		webArchive.addClasses(DelegateDbAccessEJB.class);
		webArchive.addAsWebInfResource("META-INF/beans.xml", "beans.xml");
		webArchive.addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml");
		webArchive.addAsResource("db/migration/V1_0__initial.sql", "db/migration/V1_0__initial.sql");
		webArchive.addAsResource("db/migration/V1_1__add_product.sql", "db/migration/V1_1__add_product.sql");
		webArchive.addAsResource("db/initial_db_setup.sql", "db/initial_db_setup.sql");

		// Add dependencies
		final ConfigurableMavenResolverSystem resolver = Maven.configureResolver().withMavenCentralRepo(false);

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.flywaydb:flyway-core:jar:?")
				.withTransitivity().asFile());

		System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

		return webArchive;
	}

	/**
	 * Setup.
	 */
	@Before
	public void setupClass() {
		//
	}

	/**
	 * Test reading available heap from app server.
	 * 
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=FlywayIntegratorTestIT#test010_v1_migrate_Pass
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	@InSequence(1)
	public void test010_v1_migrate_Pass() throws Exception {
		this.logger.debug(LOG_PREFIX + "test010_v1_migrate_Pass");

		this.delegateDbAccessEJB.readAllProductsFromDb();
	}
}
