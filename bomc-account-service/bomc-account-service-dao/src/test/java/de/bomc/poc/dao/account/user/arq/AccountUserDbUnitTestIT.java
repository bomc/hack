package de.bomc.poc.dao.account.user.arq;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.performance.annotation.Performance;
import org.jboss.arquillian.performance.annotation.PerformanceTest;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import de.bomc.poc.dao.account.arq.ArquillianAccountBase;
import de.bomc.poc.dao.db.producer.DatabaseProducer;
import de.bomc.poc.dao.db.qualifier.LocalDatabase;
import de.bomc.poc.dao.db.qualifier.ProdDatabase;
import de.bomc.poc.dao.generic.JpaGenericDao;
import de.bomc.poc.dao.generic.impl.AbstractJpaDao;
import de.bomc.poc.dao.generic.qualifier.JpaDao;
import de.bomc.poc.dao.jpa.JpaAccountDao;
import de.bomc.poc.dao.jpa.JpaAccountUserDao;
import de.bomc.poc.dao.jpa.JpaUserDao;
import de.bomc.poc.dao.jpa.impl.JpaAccountDaoImpl;
import de.bomc.poc.dao.jpa.impl.JpaAccountUserDaoImpl;
import de.bomc.poc.dao.jpa.impl.JpaUserDaoImpl;
import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.model.account.AccountUser;
import de.bomc.poc.model.account.Address;
import de.bomc.poc.model.account.User;

/**
 * <pre>
 *	mvn clean install -Parq-wildfly-remote -Dtest=AccountUserDbUnitTestIT
 * </pre>
 * 
 * Tests the dao layer for {@link AccountUser} functionality with dbunit.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
@RunWith(Arquillian.class)
@PerformanceTest(resultsThreshold = 10)
public class AccountUserDbUnitTestIT extends ArquillianAccountBase {
	private static final String WEB_ARCHIVE_NAME = "account-use-dbunit";
	private static final String LOG_PREFIX = "AccountUserTestIT#";

	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	@JpaDao
	private JpaUserDao jpaUserDao;
	@Inject
	@JpaDao
	private JpaAccountDao jpaAccountDao;
	@Inject
	@JpaDao
	private JpaAccountUserDao jpaAccountUserDao;

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Deployment
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = ArquillianAccountBase.createTestArchiveWithH2Db(WEB_ARCHIVE_NAME);
		webArchive.addClass(AccountUserTestIT.class);
		webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
		webArchive.addClasses(JpaUserDao.class, JpaUserDaoImpl.class);
		webArchive.addClasses(JpaAccountDao.class, JpaAccountDaoImpl.class);
		webArchive.addClasses(JpaAccountUserDao.class, JpaAccountUserDaoImpl.class);
		webArchive.addClasses(DatabaseProducer.class, LocalDatabase.class, ProdDatabase.class, JpaGenericDao.class,
				AbstractJpaDao.class, JpaDao.class);

		System.out.println(LOG_PREFIX + "archiveContent: " + webArchive.toString(true));

		return webArchive;
	}

	@Before
	public void setup() throws Exception {
		assertThat(this.jpaUserDao, notNullValue());
	}

	@After
	public void cleanup() {
		//
	}

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=AccountUserDbUnitTestIT#test010_addAddressToUser_Pass
	 * 
	 *	https://bintray.com/version/readme/rmpestano/dbunit-rules/dbunit-rules/0.3
	 *	https://docs.jboss.org/author/display/ARQ/Persistence#Persistence-configuration
	 * </pre>
	 * 
	 * <b><code>test010_addAddressToUser_Pass</code>:</b><br>
	 *	Test adds a {@link Address} to a {@link Person}.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 *	The datasets are loaded by dbUnit.
	 *	The test archive is successful deployed.
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 *	The following steps are executed:
	 * <ol type="1">
	 *	<li>Creates a not persisted address instance, with zipCode = '4002'.</li>
	 *	<li>Read a user instance back from db by given id. See t_user.yml for id.</li>
	 *	<li>Add address to user.</li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 *	Assertions is done by dbUnit 'ShouldMatchDataSet'.
	 *	Address is added to the user.
	 *	After test the db is cleanup, the added row is removed after this.
	 * <p>
	 * 
	 * @throws Exception
	 */
	@Test
	@InSequence(10)
	@Performance(time = 1500)
	@UsingDataSet({ 
		"datasets/init/t_address.yml", 
		"datasets/init/t_user.yml", 
		"datasets/init/t_legal_user.yml",
		"datasets/init/t_private_user.yml", 
		"datasets/init/t_generator_account.yml", 
		"datasets/init/t_account.yml",
		"datasets/init/t_account_user.yml", 
		"datasets/init/t_collect_account_user.yml" })
	@Cleanup(phase = TestExecutionPhase.NONE /*, strategy = CleanupStrategy.DEFAULT*/)
	@ShouldMatchDataSet(
		value = { "datasets/expected/t_address.yml" }, 
		excludeColumns = { "c_id", "c_version", "c_createdate", "c_modifydate" })
	public void test010_addAddressToUser_Pass() {
		this.logger.debug(LOG_PREFIX + "test010_addAddressToUser_Pass");

		// ___________________________________________
		// Do test preparation.
		// -------------------------------------------
		
		final Address address = this.getNotPersistedAddress("4002");
		
		final User user = this.jpaUserDao.findById(1L);
		
		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		
		user.addAddress(address);
		this.jpaUserDao.persist(user);
		
		// ___________________________________________
		// Do asserts by 'ShouldMatchDataSet'.
		// -------------------------------------------
	}
	
}
