package de.bomc.poc.dao.account.user.arq;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.performance.annotation.Performance;
import org.jboss.arquillian.performance.annotation.PerformanceTest;
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
import de.bomc.poc.model.account.Account;
import de.bomc.poc.model.account.AccountUser;
import de.bomc.poc.model.account.Address;
import de.bomc.poc.model.account.AuthorizationTypeEnum;
import de.bomc.poc.model.account.NaturalUser;
import de.bomc.poc.model.account.User;

/**
 * <pre>
 *	mvn clean install -Parq-wildfly-remote -Dtest=AccountUserTestIT
 * </pre>
 * 
 * Tests the dao layer for {@link AccountUser} functionality.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
@RunWith(Arquillian.class)
@PerformanceTest(resultsThreshold = 10)
public class AccountUserTestIT extends ArquillianAccountBase {
	private static final String WEB_ARCHIVE_NAME = "account-user";
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
	@Inject
	private UserTransaction utx;

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
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=AccountUserTestIT#test010_addUser_Pass
	 * </pre>
	 * 
	 * <b><code>test010_addUser_Pass</code>:</b><br>
	 * Test add a {@link Person} to db.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 * No preconditions
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * The following steps are executed:
	 * <ol type="1">
	 * <li>Create a {@link Person}.</li>
	 * <li>Add user to db.</li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 * {@link Person} is added to db and id is not null.
	 * <p>
	 * 
	 * @throws Exception
	 */
	@Test
	@InSequence(10)
	@Performance(time = 1000)
	public void test010_addUser_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test010_addUser_Pass");

		// ___________________________________________
		// Do actual test.
		// -------------------------------------------

		utx.begin();
		
		final User user = new User(USER_USERNAME);
		this.jpaUserDao.persist(user);
		
		utx.commit();

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------

		assertThat(user.getId(), notNullValue());
	}
	
	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=AccountUserTestIT#test010_addUser_Pass+test020_addAlreadyAddedUser_Fail
	 * </pre>
	 * 
	 * <b><code>test010_addUser_Pass</code>:</b><br>
	 * Test tries to add a already stored {@link Person} to db.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 * Previous test must be successful finished.
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * The following steps are executed:
	 * <ol type="1">
	 * <li>Create a {@link Person}.</li>
	 * <li>Try to add user to db.</li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 * A exception is thrown of type {@link PersistenceException}
	 * <p>
	 * 
	 * @throws PersistenceException
	 */
	@Test
	@InSequence(20)
	@Performance(time = 1000)
	public void test020_addAlreadyAddedUser_Fail() throws Exception {
		this.logger.info(LOG_PREFIX + "test020_addAlreadyAddedUser_Fail");

		this.thrown.expect(PersistenceException.class);

		// ___________________________________________
		// Do actual test.
		// -------------------------------------------

		try {
			utx.begin();

			final User user = new User(USER_USERNAME);
			this.jpaUserDao.persist(user);

			utx.commit();
		} finally {
			utx.rollback();
		}
	}

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=AccountUserTestIT#test010_addUser_Pass+test030_findUserById_Pass
	 * </pre>
	 * 
	 * <b><code>test030_findUserById_Pass</code>:</b><br>
	 * Test find user by method 'findById' from {@link JpaGenericDao}.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 * Previous test must be successful finished.
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * The following steps are executed:
	 * <ol type="1">
	 * <li>Perform method 'findById' from {@link JpaGenericDao}.</li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 * Found {@link Person} and id is not null.
	 * <p>
	 * 
	 * @throws Exception
	 */
	@Test
	@InSequence(30)
	@Performance(time = 1000)
	public void test030_findUserById_Pass() {
		this.logger.info(LOG_PREFIX + "test030_findUserById_Pass");

		// ___________________________________________
		// Do actual test.
		// -------------------------------------------

		final List<User> findAllUserList = this.jpaUserDao.findAll();
		assertThat(findAllUserList, not(Collections.EMPTY_LIST));

		final User retUser = this.jpaUserDao.findById(1L);

		this.logger.debug(LOG_PREFIX + "test030_findUserById_Pass [user=" + retUser.toString() + "]");

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------

		assertThat(retUser, notNullValue());
		assertThat(retUser.getId(), notNullValue());
	}

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=AccountUserTestIT#test010_addUser_Pass+test040_findByPositionalParameters_Pass
	 * </pre>
	 * 
	 * <b><code>test040_findByPositionalParameters_Pass</code>:</b><br>
	 * Test find user by method 'findByPositionalParameters' from
	 * {@link JpaGenericDao}.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 * Previous tests must be successful finished.
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * The following steps are executed:
	 * <ol type="1">
	 * <li>Find user by method 'findByPositionalParameters' from
	 * {@link JpaGenericDao}.</li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 * Found {@link Person} and id is not null.
	 * <p>
	 */
	@Test
	@InSequence(40)
	@Performance(time = 1000)
	public void test040_findByPositionalParameters_Pass() {
		this.logger.info(LOG_PREFIX + "test040_findByPositionalParameters_Pass");

		// ___________________________________________
		// Do actual test.
		// -------------------------------------------

		final List<User> retUserList = this.jpaUserDao.findByPositionalParameters(User.NQ_FIND_BY_USERNAME,
				new Object[] { USER_USERNAME });

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------

		assertThat(retUserList.size(), equalTo(1));
		assertThat(retUserList.iterator().next().getId(), notNullValue());
	}

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=AccountUserTestIT#test010_addUser_Pass+test050_findByPositionalParametersByUnknownUsername_Pass
	 * </pre>
	 * 
	 * <b><code>test050_findByPositionalParametersByUnknownUsername_Pass</code>:</b><br>
	 * Test method 'findByPositionalParameters' from {@link JpaGenericDao} with
	 * a not stored Person.username.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 * Previous tests must be successful finished.
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * The following steps are executed:
	 * <ol type="1">
	 * <li>Find user by method 'findByPositionalParameters' from
	 * {@link JpaGenericDao}.</li>
	 * <li>A user with the given username is not known in db.</li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 * Returned user list is empty.
	 * <p>
	 */
	@Test
	@InSequence(50)
	@Performance(time = 1000)
	public void test050_findByPositionalParametersByUnknownUsername_Pass() {
		this.logger.info(LOG_PREFIX + "test050_findByPositionalParametersByUnknownUsername_Pass");

		// ___________________________________________
		// Do actual test.
		// -------------------------------------------

		final List<User> retUserList = this.jpaUserDao.findByPositionalParameters(User.NQ_FIND_BY_USERNAME,
				new Object[] { USER_USERNAME_UNKNOWN });

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------

		assertThat(retUserList, equalTo(Collections.EMPTY_LIST));
	}

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=AccountUserTestIT#test060_createAccountWithPrivateUserAndAddress_Pass
	 * </pre>
	 * 
	 * <b><code>test060_createAccountWithPrivateUserAndAddress_Pass</code>:</b><br>
	 * Test add creates a {@link Account} the relationship to a
	 * {@link NaturalUser} with its address.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 * No preconditions
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * The following steps are executed:
	 * <ol type="1">
	 * <li>Create a {@link Account}.</li>
	 * <li>Add account to db.</li>
	 * <li>Add naturalUser with its address to db.</li>
	 * <li>Add the relationship account user and persist it.</li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 * {@link Account} and the relationship {@link AccountUser} is made to the
	 * {@link NaturalUser}.
	 * <p>
	 * 
	 * @throws Exception
	 */
	@Test
	@InSequence(60)
	@Performance(time = 1000)
	public void test060_createAccountWithPrivateUserAndAddress_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test060_createAccountWithPrivateUserAndAddress_Pass");

		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		this.utx.begin();
		
		// __________________________________________
		// Create a account.
		final Account account = this.getNotPersistedAccount(ACCOUNT_DISPLAY_NANE + "_new");
		this.jpaAccountDao.persist(account);
		// __________________________________________
		// Create a naturalUser and a address.
		final NaturalUser naturalUser = this.getNotPersistedNaturalUser(USER_USERNAME + "_new");
		final Address address = this.getNotPersistedAddress("4001");
		naturalUser.addAddress(address);
		this.jpaUserDao.persist(naturalUser);
		// __________________________________________
		// Create the relationship legalUser and account.
		Set<AuthorizationTypeEnum> authTypes = new LinkedHashSet<>(1);
		authTypes.add(AuthorizationTypeEnum.WEAK);
		final AccountUser accountUser = new AccountUser(naturalUser, account, ACCOUNT_USER_OWNER_FLAG_TRUE, authTypes);

		this.jpaAccountUserDao.persist(accountUser);

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------

		this.utx.commit();
	}

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=AccountUserTestIT#test060_createAccountWithPrivateUserAndAddress_Pass+test070_findUserByAuthType_Pass
	 * </pre>
	 * 
	 * <b><code>test070_findUserByAuthType_Pass</code>:</b><br>
	 * Test find all {@link AccountUser} relationship by authTypes.
	 * {@link NaturalUser} with its address.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 * The previuos test must be successfully finished.
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * The following steps are executed:
	 * <ol type="1">
	 * <li>Create a {@link Account}.</li>
	 * <li>Add account to db.</li>
	 * <li>Add naturalUser with its address to db.</li>
	 * <li>Add the relationship account user and persist it.</li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 * {@link Account} and the relationship {@link AccountUser} is made to the
	 * {@link NaturalUser}.
	 * <p>
	 * 
	 * @throws Exception
	 */
	@Test
	@InSequence(70)
	@Performance(time = 1000)
	public void test070_findUserByAuthType_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test070_findUserByAuthType_Pass");

		// ___________________________________________
		// Do test preparations.
		// -------------------------------------------

		this.utx.begin();

		// __________________________________________
		// Add more user to available account. displayName comes from previous
		// test 'ACCOUNT_DISPLAY_NANE + "_new"'.
		final List<Account> accountList = this.jpaAccountDao.findByPositionalParameters(Account.NQ_FIND_BY_NAME,
				new Object[] { ACCOUNT_DISPLAY_NANE + "_new" });
		assertThat(accountList, not(Collections.EMPTY_LIST));
		final Account accountToPrepare = accountList.get(0);

		for (int i = 0; i < 5; i++) {
			final User user = this.getNotPersistedUser(USER_USERNAME + "080_" + i);
			this.jpaUserDao.persist(user);

			AccountUser accountUser;

			if ((i % 2) == 0) {
				accountUser = this.createAccountUserRelationship(user, accountToPrepare, AuthorizationTypeEnum.STRONG,
						ACCOUNT_USER_OWNER_FLAG_FALSE);
			} else {
				accountUser = this.createAccountUserRelationship(user, accountToPrepare,
						AuthorizationTypeEnum.VERY_STONG, ACCOUNT_USER_OWNER_FLAG_FALSE);
			}

			this.jpaAccountUserDao.persist(accountUser);
		}

		this.utx.commit();

		// ___________________________________________
		// Do actual test.
		// -------------------------------------------

		final int weakCount = this.jpaUserDao
				.findByPositionalParameters(User.NQ_FIND_BY_AUTH_TYPE, new Object[] { AuthorizationTypeEnum.WEAK })
				.size();
		final int strongCount = this.jpaUserDao
				.findByPositionalParameters(User.NQ_FIND_BY_AUTH_TYPE, new Object[] { AuthorizationTypeEnum.STRONG })
				.size();
		final int veryStrongCount = this.jpaUserDao.findByPositionalParameters(User.NQ_FIND_BY_AUTH_TYPE,
				new Object[] { AuthorizationTypeEnum.VERY_STONG }).size();

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------

		assertThat(weakCount, equalTo(1));
		assertThat(strongCount, equalTo(3));
		assertThat(veryStrongCount, equalTo(2));
	}

	private AccountUser createAccountUserRelationship(final User user, final Account account,
			final AuthorizationTypeEnum authType, boolean isOwner) {
		final Set<AuthorizationTypeEnum> authTypeSet = new HashSet<>();
		authTypeSet.add(authType);
		final AccountUser accountUser = new AccountUser(user, account, isOwner, authTypeSet);

		return accountUser;
	}

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=AccountUserTestIT#test060_createAccountWithPrivateUserAndAddress_Pass+test070_findUserByAuthType_Pass+test080_findAccountUserWithAccountAndUserDataByAccountId_Pass
	 * </pre>
	 * 
	 * <b><code>test080_findAccountUserWithAccountAndUserDataByAccountId_Pass</code>:</b><br>
	 * Test find account with loaded data from user with address and account data.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 * Previous test must be successfully finished.
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * The following steps are executed:
	 * <ol type="1">
	 * <li>Create a {@link Account}.</li>
	 * <li>Read an account back from db to get id.</li>
	 * <li>Uses the NQ-'findAccountUserWithAccountAndUserDataByAccountId' to the AccountUser. </li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 * {@link AccountUser} and their relationships are loaded from db.
	 * <p>
	 * 
	 * @throws Exception
	 */
	@Test
	@InSequence(80)
	@Performance(time = 1000)
	public void test080_findAccountUserWithAccountAndUserDataByAccountId_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test080_findAccountUserWithAccountAndUserDataByAccountId_Pass");

		// ___________________________________________
		// Do test preparations.
		// -------------------------------------------
		final List<Account> accountList = this.jpaAccountDao.findAll();
		assertThat(accountList, not(Collections.EMPTY_LIST));
		final Long accountId = accountList.get(0).getId();
		
		// ___________________________________________
		// Run actual test.
		// -------------------------------------------
		
		final Optional<AccountUser> retAccountUserOptional = this.jpaAccountUserDao.findAccountUserWithAccountAndUserDataByAccountId(accountId);
		final AccountUser accountUser = retAccountUserOptional.get();

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------
		
		assertThat(accountUser, notNullValue());
		assertThat(accountUser.getUser(), notNullValue());
		final User user = accountUser.getUser();
		assertThat(user.getAddresses().size(), equalTo(1));
	}
	
	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=AccountUserTestIT#test060_createAccountWithPrivateUserAndAddress_Pass+test090_removeAccountWithPrivateUser_Pass
	 * </pre>
	 * 
	 * <b><code>test090_removeAccountWithPrivateUser_Pass</code>:</b><br>
	 * Test add creates a {@link Account} the relationship to a
	 * {@link NaturalUser} with its address.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 * No preconditions
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * The following steps are executed:
	 * <ol type="1">
	 * <li>Create a {@link Account}.</li>
	 * <li>Add account to db.</li>
	 * <li>Add naturalUser with its address to db.</li>
	 * <li>Add the relationship account user and persist it.</li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 * {@link Account} and the relationship {@link AccountUser} is made to the
	 * {@link NaturalUser}.
	 * <p>
	 * 
	 * @throws Exception
	 */
	@Test
	@InSequence(90)
	@Performance(time = 1000)
	public void test090_removeAccountWithPrivateUser_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test90_removeAccountWithPrivateUser_Pass");

		// ___________________________________________
		// Do test preparation.
		// -------------------------------------------

		this.utx.begin();

		final int allUserCount = this.jpaUserDao.findAll().size();
		
		final List<Account> accountList = this.jpaAccountDao.findAll();

		final List<Account> collectAccountList = accountList.stream().filter(account -> account.getUsers().size() > 0)
				.collect(Collectors.toList());

		assertThat(collectAccountList, not(Collections.EMPTY_LIST));

		final Account accountToRemove = collectAccountList.get(0);

		// ___________________________________________
		// Do actual test.
		// -------------------------------------------

		final Set<AccountUser> accountUserToRemoveSet = accountToRemove.getUsers();
		
		final int givenUserCount = accountUserToRemoveSet.size();
		
		final AccountUser[] accountUserArray = accountUserToRemoveSet
				.toArray(new AccountUser[accountUserToRemoveSet.size()]);

		for (final AccountUser accountUserToRemove : accountUserArray) {
			// Remove relationship.
			final User userToRemove = accountUserToRemove.getUser();
			// Remove user with its address from db.
			this.jpaUserDao.remove(userToRemove);
			// Remove account from db.
			this.jpaAccountUserDao.remove(accountUserToRemove);
		}
		this.jpaAccountDao.remove(accountToRemove);

		utx.commit();

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------

		assertThat(this.jpaAccountDao.findAll().size(), equalTo(0));
		assertThat(this.jpaAccountUserDao.findAll().size(), equalTo(0));
		assertThat(this.jpaUserDao.findAll().size(), equalTo(allUserCount - givenUserCount));
	}
}
