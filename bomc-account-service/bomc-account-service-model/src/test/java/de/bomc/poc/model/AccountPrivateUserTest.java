package de.bomc.poc.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.bomc.poc.model.account.Account;
import de.bomc.poc.model.account.AccountUser;
import de.bomc.poc.model.account.NaturalUser;
import de.bomc.poc.model.account.User;
import de.bomc.poc.model.account.AuthorizationTypeEnum;
import de.bomc.poc.model.test.AbstractUnitTest;

/**
 * This class tests {@link AccountUser} functionality.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountPrivateUserTest extends AbstractUnitTest {

	private static final Logger LOGGER = Logger.getLogger(UserTest.class);
	private static final String LOG_PREFIX = "AccountPrivateUserTest#";

	@Before
	public void setup() {
		this.entityManager = this.emProvider.getEntityManager();
		assertThat(this.entityManager, is(notNullValue()));
	}

	/**
	 * Create a persisted <code>AccountUser</code> relationship.
	 */
	@Test
	public void test010_createAccountUserRelationship_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_createAccountUserRelationship_pass");

		this.emProvider.tx().begin();

		final NaturalUser naturalUser = this.getPrivateUser(null);
		final Account account = this.getAccount(null);
		this.entityManager.persist(naturalUser);
		this.entityManager.persist(account);

		final Set<AuthorizationTypeEnum> accountTypeEnumSet = new HashSet<>();
		accountTypeEnumSet.add(AuthorizationTypeEnum.STRONG);
		final AccountUser accountUser = new AccountUser(naturalUser, account, ACCOUNT_OWNER_FLAG_TRUE, accountTypeEnumSet);
		this.entityManager.persist(accountUser);

		this.emProvider.tx().commit();

		final Account retAccount = this.entityManager.find(Account.class, account.getId());
		assertThat(retAccount, notNullValue());
		assertThat(retAccount.getUsers().size(), equalTo(1));
		final User retUser = this.entityManager.find(User.class, naturalUser.getId());
		assertThat(retUser, notNullValue());
		assertThat(retUser.getAccounts().size(), equalTo(1));
	}

	/**
	 * Remove all relationships between user and account.
	 */
	@Test
	public void test020_removeAllAccountUserRelationship_pass() {
		LOGGER.debug(LOG_PREFIX + "test020_removeAllAccountUserRelationship_pass");

		// ___________________________________________
		// Create the relationship.
		// -------------------------------------------
		this.emProvider.tx().begin();

		final NaturalUser naturalUser = this.getPrivateUser(null);
		final Account account = this.getAccount(null);
		this.entityManager.persist(naturalUser);
		this.entityManager.persist(account);

		final Set<AuthorizationTypeEnum> accountTypeEnumSet = new HashSet<>();
		accountTypeEnumSet.add(AuthorizationTypeEnum.STRONG);
		final AccountUser accountUser = new AccountUser(naturalUser, account, ACCOUNT_OWNER_FLAG_TRUE, accountTypeEnumSet);
		this.entityManager.persist(accountUser);

		this.emProvider.tx().commit();

		final Account retAccount = this.entityManager.find(Account.class, account.getId());
		assertThat(retAccount, notNullValue());
		assertThat(retAccount.getUsers().size(), equalTo(1));
		final User retUser = this.entityManager.find(User.class, naturalUser.getId());
		assertThat(retUser, notNullValue());
		assertThat(retUser.getAccounts().size(), equalTo(1));

		// ___________________________________________
		// Remove the relationship.
		// -------------------------------------------

		this.emProvider.tx().begin();

		final Set<AccountUser> accountUserSet = account.getUsers();

		accountUserSet.forEach(accountUser1 -> {
			// Remove relationships.
			accountUser1.setUser(null);
			accountUser1.setAccount(null);
			// Remove relationship mapping from db.
			this.entityManager.remove(accountUser1);
		});

		this.emProvider.tx().commit();

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------

		final Account assertAccount = this.entityManager.find(Account.class, account.getId());
		assertThat(assertAccount, notNullValue());
		assertThat(assertAccount.getUsers().size(), equalTo(0));
		final User assertUser = this.entityManager.find(NaturalUser.class, naturalUser.getId());
		assertThat(assertUser, notNullValue());
		assertThat(assertUser.getAccounts().size(), equalTo(0));
		final AccountUser assertAccountUser = this.entityManager.find(AccountUser.class, accountUser.getId());
		assertThat(assertAccountUser, nullValue());
	}

	/**
	 * Remove a specific user from account.
	 */
	@Test
	public void test030_removeSpecificAccountUserRelationship_pass() {
		LOGGER.debug(LOG_PREFIX + "test030_removeSpecificAccountUserRelationship_pass");

		// ___________________________________________
		// Create the relationship, one account with two users.
		// -------------------------------------------
		this.emProvider.tx().begin();

		final NaturalUser naturalUser = this.getPrivateUser(null);
		final String USERNAME = USER_DEFAULT_USERNAME + "_1";
		final NaturalUser privateUser1 = this.getPrivateUser(USERNAME);
		final Account account = this.getAccount(null);
		this.entityManager.persist(naturalUser);
		this.entityManager.persist(privateUser1);
		this.entityManager.persist(account);

		// Add first relationship to account instance.
		final Set<AuthorizationTypeEnum> accountTypeEnumSet = new HashSet<>();
		accountTypeEnumSet.add(AuthorizationTypeEnum.STRONG);
		final AccountUser accountUser = new AccountUser(naturalUser, account, ACCOUNT_OWNER_FLAG_TRUE, accountTypeEnumSet);
		this.entityManager.persist(accountUser);

		// Add second relationship to the same account instance.
		final Set<AuthorizationTypeEnum> accountTypeEnumSet1 = new HashSet<>();
		accountTypeEnumSet1.add(AuthorizationTypeEnum.WEAK);
		final AccountUser accountUser1 = new AccountUser(privateUser1, account, ACCOUNT_OWNER_FLAG_TRUE, accountTypeEnumSet1);
		this.entityManager.persist(accountUser1);

		this.emProvider.tx().commit();

		final Account retAccount = this.entityManager.find(Account.class, account.getId());
		assertThat(retAccount, notNullValue());
		assertThat(retAccount.getUsers().size(), equalTo(2));
		final NaturalUser retPrivateUser = this.entityManager.find(NaturalUser.class, naturalUser.getId());
		assertThat(retPrivateUser, notNullValue());
		assertThat(retPrivateUser.getAccounts().size(), equalTo(1));
		final User retPrivateUser1 = this.entityManager.find(User.class, privateUser1.getId());
		assertThat(retPrivateUser1, notNullValue());
		assertThat(retPrivateUser1.getAccounts().size(), equalTo(1));

		// ___________________________________________
		// Remove user1 from relationship.
		// -------------------------------------------

		this.emProvider.tx().begin();

		final Account account1 = this.entityManager.find(Account.class, account.getId());
		final List<AccountUser> accountUserList = account1.findUserById(privateUser1.getId());
		final AccountUser[] accountUserArray = accountUserList.toArray(new AccountUser[accountUserList.size()]);

		for (int i = 0; i < accountUserArray.length; i++) {
			// Remove relationship.
			final AccountUser accountUserToRemove = accountUserArray[i];
			this.entityManager.remove(accountUserToRemove);
		}

		this.emProvider.tx().commit();

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------
		final Account assertAccount = this.entityManager.find(Account.class, account.getId());
		assertThat(assertAccount, notNullValue());
		assertThat("The account must have only one user.", assertAccount.getUsers().size(), equalTo(1));
		final NaturalUser assertPrivateUser_removed = this.entityManager.find(NaturalUser.class, privateUser1.getId());
		assertThat("The user is removed from account, but not from db.", assertPrivateUser_removed, notNullValue());
		assertThat("The account is removed from this user.", assertPrivateUser_removed.getAccounts().size(), equalTo(0));
		final NaturalUser assertPrivateUser = this.entityManager.find(NaturalUser.class, naturalUser.getId());
		assertThat(assertPrivateUser, notNullValue());
		assertThat("The user is also added to the account.", assertPrivateUser.getAccounts().size(), equalTo(1));
		final AccountUser assertAccountUser = this.entityManager.find(AccountUser.class, accountUser.getId());
		assertThat(assertAccountUser, notNullValue());
		final AccountUser assertAccountUser1 = this.entityManager.find(AccountUser.class, accountUser1.getId());
		assertThat("The accountUser1 is removed from db.", assertAccountUser1, nullValue());
	}

	/**
	 * Remove a account.
	 */
	@Test
	public void test040_removeAccountFromDb() {
		LOGGER.debug(LOG_PREFIX + "test040_removeAccountFromDb");

		// ___________________________________________
		// Create the relationship wit two users.
		// -------------------------------------------

		this.emProvider.tx().begin();

		final NaturalUser naturalUser = this.getPrivateUser(null);
		final String USERNAME = USER_DEFAULT_USERNAME + "_1";
		final NaturalUser privateUser1 = this.getPrivateUser(USERNAME);
		final Account account = this.getAccount(null);
		this.entityManager.persist(naturalUser);
		this.entityManager.persist(privateUser1);
		this.entityManager.persist(account);

		// Add first relationship to account instance.
		final Set<AuthorizationTypeEnum> accountTypeEnumSet = new HashSet<>();
		accountTypeEnumSet.add(AuthorizationTypeEnum.STRONG);
		final AccountUser accountUser = new AccountUser(naturalUser, account, ACCOUNT_OWNER_FLAG_TRUE, accountTypeEnumSet);
		this.entityManager.persist(accountUser);

		// Add second relationship to the same account instance.
		final Set<AuthorizationTypeEnum> accountTypeEnumSet1 = new HashSet<>();
		accountTypeEnumSet1.add(AuthorizationTypeEnum.WEAK);
		final AccountUser accountUser1 = new AccountUser(privateUser1, account, ACCOUNT_OWNER_FLAG_TRUE, accountTypeEnumSet1);
		this.entityManager.persist(accountUser1);

		this.emProvider.tx().commit();

		final Account retAccount = this.entityManager.find(Account.class, account.getId());
		assertThat(retAccount, notNullValue());
		assertThat(retAccount.getUsers().size(), equalTo(2));
		final NaturalUser retPrivateUser = this.entityManager.find(NaturalUser.class, naturalUser.getId());
		assertThat(retPrivateUser, notNullValue());
		assertThat(retPrivateUser.getAccounts().size(), equalTo(1));
		final User retPrivateUser1 = this.entityManager.find(User.class, privateUser1.getId());
		assertThat(retPrivateUser1, notNullValue());
		assertThat(retPrivateUser1.getAccounts().size(), equalTo(1));

		// ___________________________________________
		// Remove the relationship first, and than the account.
		// -------------------------------------------

		this.emProvider.tx().begin();

		final Set<AccountUser> accountUserSet = account.getUsers();
		final AccountUser[] accountUserArray = accountUserSet.toArray(new AccountUser[accountUserSet.size()]);

		for (int i = 0; i < accountUserArray.length; i++) {
			// Remove relationship first.
			final AccountUser accountUserToRemove = accountUserArray[i];
			this.entityManager.remove(accountUserToRemove);
		}

		// ... and than remove the account.
		this.entityManager.remove(account);

		this.emProvider.tx().commit();

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------
		final Account assertAccount = this.entityManager.find(Account.class, account.getId());
		assertThat("The account must be null, is removed.", assertAccount, nullValue());
		final AccountUser assertAccountUser = this.entityManager.find(AccountUser.class, accountUser.getId());
		assertThat("The assertAccountUser relationship must be null, is removed.", assertAccountUser, nullValue());
		final AccountUser assertAccountUser1 = this.entityManager.find(AccountUser.class, accountUser1.getId());
		assertThat("The assertAccountUser1 relationship must be null, is removed.", assertAccountUser1, nullValue());
		final NaturalUser assertPrivateUser = this.entityManager.find(NaturalUser.class, naturalUser.getId());
		assertThat("The assertUser relationship must not be null, is not removed.", assertPrivateUser, notNullValue());
		final NaturalUser assertPrivateUser1 = this.entityManager.find(NaturalUser.class, privateUser1.getId());
		assertThat("The assertUser1 relationship must not be null, is not removed.", assertPrivateUser1, notNullValue());
	}

	/**
	 * Create a <code>AccountUser</code> with a null user.
	 */
	@Test
	public void test050_createAccountUserWithNullUser_fail() {
		LOGGER.debug(LOG_PREFIX + "test050_createAccountUserWithNullUser_fail");

		thrown.expect(NullPointerException.class);

		try {
			this.emProvider.tx().begin();
			final Account account = this.getAccount(null);
			this.entityManager.persist(account);

			final Set<AuthorizationTypeEnum> accountTypeEnumSet = new HashSet<>();
			accountTypeEnumSet.add(AuthorizationTypeEnum.STRONG);
			@SuppressWarnings("unused")
			final AccountUser accountUser = new AccountUser(null, account, ACCOUNT_OWNER_FLAG_TRUE, accountTypeEnumSet);
		} finally {
			this.emProvider.tx().commit();
		}
	}

	/**
	 * Create a <code>AccountUser</code> with a null account.
	 */
	@Test
	public void test060_createAccountUserWithNullAccount_fail() {
		LOGGER.debug(LOG_PREFIX + "test060_createAccountUserWithNullAccount_fail");

		thrown.expect(NullPointerException.class);

		try {
			this.emProvider.tx().begin();
			
			final NaturalUser naturalUser = this.getPrivateUser(null);
			this.entityManager.persist(naturalUser);

			final Set<AuthorizationTypeEnum> accountTypeEnumSet = new HashSet<>();
			accountTypeEnumSet.add(AuthorizationTypeEnum.STRONG);
			@SuppressWarnings("unused")
			final AccountUser accountUser = new AccountUser(naturalUser, null, ACCOUNT_OWNER_FLAG_TRUE, accountTypeEnumSet);
		} finally {
			this.emProvider.tx().commit();
		}
	}
	
	/**
	 * Create a <code>AccountUser</code> with a null enumType.
	 */
	@Test
	public void test070_createAccountUserWithNullEnumType_fail() {
		LOGGER.debug(LOG_PREFIX + "test070_createAccountUserWithNullEnumType_fail");

		thrown.expect(NullPointerException.class);

		try {
			this.emProvider.tx().begin();

			final NaturalUser naturalUser = this.getPrivateUser(null);
			final Account account = this.getAccount(null);
			this.entityManager.persist(naturalUser);
			this.entityManager.persist(account);

			final AccountUser accountUser = new AccountUser(naturalUser, account, ACCOUNT_OWNER_FLAG_TRUE, null);
			this.entityManager.persist(accountUser);

		} finally {
			this.emProvider.tx().commit();
		}
	}
	
	/**
	 * Add a user to different accounts.
	 */
	@Test
	public void test080_addUserToDifferentAccounts_pass() {
		LOGGER.debug(LOG_PREFIX + "test080_addUserToDifferentAccounts_pass");
		
		this.emProvider.tx().begin();

		final NaturalUser naturalUser = this.getPrivateUser(null);
		
		final String USERNAME = USER_DEFAULT_USERNAME + "_1";
		final NaturalUser privateUser1 = this.getPrivateUser(USERNAME);
		
		final Account account = this.getAccount(null);
		
		final String ACCOUNT_NAME = ACCOUNT_DEFAULT_NAME + "_1";
		final Account account1 = this.getAccount(ACCOUNT_NAME);
		
		this.entityManager.persist(naturalUser);
		this.entityManager.persist(privateUser1);
		this.entityManager.persist(account);
		this.entityManager.persist(account1);

		final Set<AuthorizationTypeEnum> accountTypeEnumSet = new HashSet<>();
		accountTypeEnumSet.add(AuthorizationTypeEnum.STRONG);
		final AccountUser accountUser = new AccountUser(naturalUser, account, ACCOUNT_OWNER_FLAG_TRUE, accountTypeEnumSet);
		this.entityManager.persist(accountUser);

		final Set<AuthorizationTypeEnum> accountTypeEnumSet1 = new HashSet<>();
		accountTypeEnumSet1.add(AuthorizationTypeEnum.WEAK);
		final AccountUser accountUser1 = new AccountUser(naturalUser, account1, ACCOUNT_OWNER_FLAG_TRUE, accountTypeEnumSet1);
		this.entityManager.persist(accountUser1);
		
		final Set<AuthorizationTypeEnum> accountTypeEnumSet2 = new HashSet<>();
		accountTypeEnumSet2.add(AuthorizationTypeEnum.STRONG);
		final AccountUser accountUser2 = new AccountUser(privateUser1, account, ACCOUNT_OWNER_FLAG_TRUE, accountTypeEnumSet2);
		this.entityManager.persist(accountUser2);

		final Set<AuthorizationTypeEnum> accountTypeEnumSet3 = new HashSet<>();
		accountTypeEnumSet3.add(AuthorizationTypeEnum.WEAK);
		final AccountUser accountUser3 = new AccountUser(privateUser1, account1, ACCOUNT_OWNER_FLAG_TRUE, accountTypeEnumSet3);
		this.entityManager.persist(accountUser3);
		
		this.emProvider.tx().commit();
		
		this.emProvider.tx().begin();

		// _________________________________
		// Get all private users.
		final TypedQuery<NaturalUser> userPrivateQuery = this.entityManager.createNamedQuery(NaturalUser.NQ_FIND_ALL, NaturalUser.class);
		final List<NaturalUser> userPrivateList = userPrivateQuery.getResultList();
		assertThat(userPrivateList.size(), equalTo(2));
	}
}
