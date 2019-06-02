package de.bomc.poc.business.arq.account;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Optional;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.performance.annotation.Performance;
import org.jboss.arquillian.performance.annotation.PerformanceTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import de.bomc.poc.api.dto.AccountDTO;
import de.bomc.poc.business.account.AccountController;
import de.bomc.poc.business.account.impl.AccountControllerEJB;
import de.bomc.poc.business.arq.ArquillianBase;
import de.bomc.poc.dao.db.producer.DatabaseProducer;
import de.bomc.poc.dao.db.qualifier.LocalDatabase;
import de.bomc.poc.dao.generic.JpaGenericDao;
import de.bomc.poc.dao.generic.impl.AbstractJpaDao;
import de.bomc.poc.dao.generic.qualifier.JpaDao;
import de.bomc.poc.dao.jpa.JpaAccountDao;
import de.bomc.poc.dao.jpa.JpaAccountUserDao;
import de.bomc.poc.dao.jpa.JpaUserDao;
import de.bomc.poc.dao.jpa.impl.JpaAccountDaoImpl;
import de.bomc.poc.dao.jpa.impl.JpaAccountUserDaoImpl;
import de.bomc.poc.dao.jpa.impl.JpaUserDaoImpl;
import de.bomc.poc.exception.IllegalParameterException;
import de.bomc.poc.exception.errorcode.AccountServiceErrorCode;
import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.model.account.Account;

/**
 * This class tests {@link AccountController} functionality.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
@RunWith(Arquillian.class)
@PerformanceTest(resultsThreshold = 10)
public class AccountControllerTestIT extends ArquillianBase {
	private static final String WEB_ARCHIVE_NAME = "account-controller";
	private static final String LOG_PREFIX = "AccountControllerTestIT#";
	private static final Long ACCOUNT_ID = 700000001L;
	@Inject
	@LoggerQualifier
	private Logger logger;

	@EJB
	private AccountController accountController;
	
	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Deployment
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = ArquillianBase.createTestArchiveWithH2Db(WEB_ARCHIVE_NAME);
		webArchive.addClass(AccountControllerTestIT.class);
		webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
		webArchive.addClasses(AccountController.class, AccountControllerEJB.class);
		webArchive.addClasses(JpaUserDao.class, JpaUserDaoImpl.class);
		webArchive.addClasses(JpaAccountDao.class, JpaAccountDaoImpl.class);
		webArchive.addClasses(JpaAccountUserDao.class, JpaAccountUserDaoImpl.class);
		webArchive.addClasses(DatabaseProducer.class, LocalDatabase.class, JpaGenericDao.class, AbstractJpaDao.class,
				JpaDao.class);
		webArchive.addClasses(AccountDTO.class);
		webArchive.addClasses(IllegalParameterException.class, AccountServiceErrorCode.class);
		webArchive.addPackages(true, "de.bomc.poc.business.mapper");

        // Add dependencies
        final MavenResolverSystem resolver = Maven.resolver();

        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("org.mapstruct:mapstruct-jdk8:jar:?")
                                          .withMavenCentralRepo(false)
                                          .withTransitivity()
                                          .asFile());
        
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                .resolve("de.bomc.poc:exception-lib:jar:?")
                .withMavenCentralRepo(false)
                .withTransitivity()
                .asFile());

        
		System.out.println(LOG_PREFIX + "archiveContent: " + webArchive.toString(true));

		return webArchive;
	}

	@Before
	public void setup() throws Exception {
		//
	}
	
	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=AccountControllerTestIT#test010_createAccount_Pass
	 * </pre>
	 * 
	 * <b><code>test010_createAccount_Pass</code>:</b><br>
	 *	Test add a {@link Account} to db.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 *	- The accountController ejb is successful injected.
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * 	The following steps are executed:
	 * <ol type="1">
	 * <li>Create a {@link AccountDTO} instance. </li>
	 * <li>Invoke the 'createAccount' method of the accountController ejb to create an account in db.</li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 *	The {@link Account} is created in db and id is not null. An the created account has created relationships to a user and his address.
	 * <p>
	 * 
	 * @throws Exception
	 */
	@Test
	@InSequence(10)
	@Performance(time = 1000)
	public void test010_createAccount_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test010_createAccount_Pass");

		// ___________________________________________
		// Do test preparation.
		// -------------------------------------------
		
		final AccountDTO accountDTO = this.getAccountDTO();
		
		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		
		final Optional<Long> accountId = this.accountController.createAccount(accountDTO);

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------
		
		final Long retAccountId = accountId.get();
		assertThat(retAccountId, notNullValue());
		assertThat(accountId.get(), equalTo(ACCOUNT_ID));
	}
}
