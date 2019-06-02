package de.bomc.poc.core.infrastructure.repositories.jpa;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import de.bomc.poc.core.arq.ArquillianBase;
import de.bomc.poc.core.category.IntegrationTestIT;
import de.bomc.poc.core.domain.AbstractEntity;
import de.bomc.poc.core.domain.DomainObject;
import de.bomc.poc.core.domain.customer.Customer;
import de.bomc.poc.core.domain.customer.CustomerRepository;
import de.bomc.poc.core.domain.customer.CustomerStatusEnum;
import de.bomc.poc.core.infrastructure.repositories.jpa.impl.AbstractJpaDao;
import de.bomc.poc.core.infrastructure.repositories.jpa.impl.CustomerRepositoryImpl;
import de.bomc.poc.core.infrastructure.repositories.jpa.producer.DatabaseH2Producer;
import de.bomc.poc.core.infrastructure.repositories.jpa.qualifier.DatabaseH2Qualifier;
import de.bomc.poc.core.infrastructure.repositories.jpa.qualifier.RepositoryQualifier;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link CustomerRepository}.
 * 
 * <pre>
 * 	mvn clean install -Parq-wildfly-remote -Dtest=CustomerRepositoryTestIT#test010_createCustomer_Pass
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @since 15.08.2018
 */
@RunWith(Arquillian.class)
@Category(IntegrationTestIT.class)
public class CustomerRepositoryTestIT extends ArquillianBase {

	// _______________________________________________
	// Private constants
	// -----------------------------------------------
	private static final String LOG_PREFIX = "CustomerRepositoryTestIT#";
	// NOTE: Check jboss-web.xml for 'context-root'.
	private static final String WEB_ARCHIVE_NAME = "customer-repository";
	// _______________________________________________
	// Member variables.
	// -----------------------------------------------
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	@RepositoryQualifier
	private CustomerRepository customerRepository;
	@Inject
	private UserTransaction utx;

	// 'testable = true', means all the tests are running inside of the
	// container.
	@Deployment(testable = true)
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchiveWithH2Db(WEB_ARCHIVE_NAME, "create-drop");
		webArchive.addClasses(CustomerRepositoryTestIT.class, IntegrationTestIT.class);
		webArchive.addClasses(DatabaseH2Producer.class, DatabaseH2Qualifier.class, JpaGenericDao.class, AbstractJpaDao.class,
				RepositoryQualifier.class, CustomerRepositoryImpl.class, CustomerRepository.class, CustomerStatusEnum.class,
				Customer.class, DomainObject.class, AbstractEntity.class);
		webArchive.addClasses();

		webArchive.addAsWebInfResource(ArquillianBase.getEmptyBeansXml(), "beans.xml");

		// Add dependencies
		final ConfigurableMavenResolverSystem resolver = Maven.configureResolver().withMavenCentralRepo(false);

		webArchive.addAsLibraries(
				resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:rest-lib:jar:?").withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:logging-lib:jar:?")
				.withTransitivity().asFile());

		System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

		return webArchive;
	}

	@Before
	public void setup() throws Exception {
		assertThat(this.customerRepository, notNullValue());
	}

	@After
	public void cleanup() {
		//
	}

	@Test
	@InSequence(10)
	public void test010_createCustomer_Pass() throws Exception {
		this.logger.debug(LOG_PREFIX + "test010_createCustomer_Pass");

		this.utx.begin();

		final Customer customer = new Customer("myName");

		this.customerRepository.persist(customer);

		this.utx.commit();

		assertThat(customer.getId(), notNullValue());
		assertThat(customer.getCustomerStatus(), equalTo(CustomerStatusEnum.BRONZE));
	}
}
