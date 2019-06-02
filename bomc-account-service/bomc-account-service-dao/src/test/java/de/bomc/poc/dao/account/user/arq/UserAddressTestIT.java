package de.bomc.poc.dao.account.user.arq;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
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
import de.bomc.poc.dao.generic.JpaGenericDao;
import de.bomc.poc.dao.generic.impl.AbstractJpaDao;
import de.bomc.poc.dao.generic.qualifier.JpaDao;
import de.bomc.poc.dao.jpa.JpaUserDao;
import de.bomc.poc.dao.jpa.impl.JpaUserDaoImpl;
import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.model.account.Address;
import de.bomc.poc.model.account.User;

/**
 * <pre>
 *	mvn clean install -Parq-wildfly-remote -Dtest=UserAddressTestIT
 * </pre>
 * 
 * Tests the {link JpaAccountDao} layer for user address functionality.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
@RunWith(Arquillian.class)
@PerformanceTest(resultsThreshold = 10)
public class UserAddressTestIT  extends ArquillianAccountBase {
	private static final String WEB_ARCHIVE_NAME = "account-user";
	private static final String LOG_PREFIX = "UserAddressTestIT#";
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	@JpaDao
	private JpaUserDao jpaUserDao;
	@Inject
	private UserTransaction utx;
	@Inject
	private EntityManager em;

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Deployment
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = ArquillianAccountBase.createTestArchiveWithH2Db(WEB_ARCHIVE_NAME);
		webArchive.addClass(UserAddressTestIT.class);
		webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
		webArchive.addClasses(JpaUserDao.class, JpaUserDaoImpl.class);
		webArchive.addClasses(DatabaseProducer.class, LocalDatabase.class, JpaGenericDao.class, AbstractJpaDao.class, JpaDao.class);

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
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=UserAddressTestIT#test010_addUser_Pass
	 * 
	 * 
	 * <b><code>test010_addUser_Pass</code>:</b><br>
	 *	Test add a {@link Person} to db.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 *	No preconditions
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 *	The following steps are executed:
	 *	<ol type="1">
	 *	<li>Create a {@link Person} instance.</li>
	 *	<li>Add user to db.</li>
	 *	</ol>
	 *
	 * <b>Postconditions:</b><br>
	 *	{@link Person} is added to db and id is not null.
	 * </pre>
	 * 
	 * @throws Exception if transaction operation failed, is not expected.
	 */
	@Test
	@InSequence(10)
//	@Performance(time = 1000)
	public void test010_addUser_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test010_addUser_Pass");

		utx.begin();

		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
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
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=UserAddressTestIT#test010_addUser_Pass+test020_addAddressesToUser_Pass
	 * </pre>
	 * 
	 * <b><code>test020_addAddressesToUser_Pass</code>:</b><br>
	 *	Test add three {@link Address}es to a {@link Person} write it down to db.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 * 	- Previous test must be successfully finished.
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * The following steps are executed:
	 * <ol type="1">
	 * <li>Add three addresses to user.</li>
	 * <li>Merge user.</li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 * 	Addresses are in db added to user.
	 * <p>
	 * 
	 * @throws Exception if transaction operation failed, is not expected.
	 */
	@Test
	@InSequence(20)
	@Performance(time = 1000)
	public void test020_addAddressesToUser_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test020_addAddressesToUser_Pass");
		
		// ___________________________________________
		// Do test preparations.
		// -------------------------------------------
		utx.begin();

		final List<User> userList = this.jpaUserDao.findAll();
		assertThat(userList.size(), equalTo(1));
		final User retUser = userList.get(0); 
		
		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		final Address address1 = this.getNotPersistedAddress(ADDRESS_ZIP);
		final Address address2 = this.getNotPersistedAddress(ADDRESS_ZIP + "1");
		final Address address3 = this.getNotPersistedAddress(ADDRESS_ZIP + "2");
		retUser.addAddress(address1);
		retUser.addAddress(address2);
		retUser.addAddress(address3);
		
		final User mergedUser = this.jpaUserDao.merge(retUser);
		
		utx.commit();

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------
		assertThat(mergedUser.getAddresses().size(), equalTo(3));
	}

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=UserAddressTestIT#test030_persistAddress_fail
	 * </pre>
	 * 
	 * <b><code>test030_persistAddress_fail</code>:</b><br>
	 *	Test tries to persist a not persisted {@link Address}.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 * 	- No preconditions.
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * The following steps are executed:
	 * <ol type="1">
	 * <li>Persist a the address.</li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 * 	Action failed, address could not be persist. A RollbackException is thrown.
	 * <p>
	 */
	@Test
	@InSequence(30)
	@Performance(time = 1000)
	public void test030_persistAddress_fail() throws Exception {
		this.logger.info(LOG_PREFIX + "test030_persistAddress_fail");
		
		thrown.expect(javax.transaction.RollbackException.class);
		
		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		utx.begin();
		
		final Address address1 = this.getNotPersistedAddress(ADDRESS_ZIP);

		this.em.persist(address1);
		
		utx.commit();
	}
	
	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=UserAddressTestIT#test010_addUser_Pass+test020_addAddressesToUser_Pass+test040_removeAddressFromUser_Pass
	 * </pre>
	 * 
	 * <b><code>test040_removeAddressFromUser_Pass</code>:</b><br>
	 *	Test remove a {@link Address} to a {@link Person} write it down to db.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 * 	- Previous test must be successfully finished.
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * The following steps are executed:
	 * <ol type="1">
	 * <li>Remove a address from user.</li>
	 * <li>Merge user.</li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 * 	Address is in db removed from user.
	 * <p>
	 * 
	 * @throws Exception if transaction operation failed, is not expected.
	 */
	@Test
	@InSequence(40)
	@Performance(time = 1000)
	public void test040_removeAddressFromUser_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test040_removeAddressFromUser_Pass");
		
		// ___________________________________________
		// Do test preparations.
		// -------------------------------------------
		utx.begin();

		final List<User> userList = this.jpaUserDao.findAll();
		assertThat(userList.size(), equalTo(1));
		final User retUser = userList.get(0); 
		final Set<Address> retAddressesSet = retUser.getAddresses();
		assertThat(retAddressesSet, not(Collections.EMPTY_SET));
		final Address addressToRemove = retAddressesSet.iterator().next();
		
		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		retUser.removeAddress(addressToRemove);
		final User mergedUser = this.jpaUserDao.merge(retUser);
		
		utx.commit();

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------
		assertThat(mergedUser.getAddresses().size(), equalTo(2));
		final Address assertAddress = this.em.find(Address.class, addressToRemove.getId());
		assertThat(assertAddress, nullValue());
	}
	
	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=UserAddressTestIT#test010_addUser_Pass+test020_addAddressesToUser_Pass+test040_removeAddressFromUser_Pass+test050_mergeUserAdress_Pass
	 * </pre>
	 * 
	 * <b><code>test050_mergeUserAdress_Pass</code>:</b><br>
	 *	Merge {@link Person} and changed {@link Address}.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 * 	- Previous tests must be successfully finished.
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * The following steps are executed:
	 * <ol type="1">
	 * <li></li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 * 	
	 * <p>
	 * 
	 * @throws Exception if transaction operation failed, is not expected.
	 */
	@Test
	@InSequence(50)
	@Performance(time = 1000)
	public void test050_mergeUserAdress_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test050_mergeUserAdress_Pass");
		
		// ___________________________________________
		// Do test preparations.
		// -------------------------------------------
		utx.begin();

		final List<User> userList = this.jpaUserDao.findAll();
		assertThat(userList.size(), equalTo(1));
		final User userToMerge = userList.get(0); 
		final Set<Address> addressesForAssertsSet = userToMerge.getAddresses();
		
		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		userToMerge.setUsername(USER_CHANGED_USERNAME);
		addressesForAssertsSet.forEach(address -> address.setZipCode(ADDRESS_ZIP + "1"));

		final User mergedUser = this.jpaUserDao.merge(userToMerge);
		
		utx.commit();

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------
		assertThat(mergedUser.getUsername(), equalTo(USER_CHANGED_USERNAME));
		addressesForAssertsSet.forEach(address -> assertThat(address.getZipCode(), equalTo(ADDRESS_ZIP + "1")));
	}
	
	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=UserAddressTestIT#test010_addUser_Pass+test020_addAddressesToUser_Pass+test040_removeAddressFromUser_Pass+test060_removeUserWithAddedAddresses_Pass
	 * </pre>
	 * 
	 * <b><code>test060_removeUserWithAddedAddresses_Pass</code>:</b><br>
	 *	Remove {@link Person} with all {@link Person}s.
	 * <p>
	 *
	 * <b>Preconditions:</b><br>
	 * 	- Previous tests must be successfully finished.
	 * <p>
	 *
	 * <b>Scenario:</b><br>
	 * The following steps are executed:
	 * <ol type="1">
	 * <li>Remove user with all adresses.</li>
	 * </ol>
	 *
	 * <b>Postconditions:</b><br>
	 * 	Person and address are removed from db.
	 * <p>
	 * 
	 * @throws Exception if transaction operation failed, is not expected.
	 */
	@Test
	@InSequence(60)
	@Performance(time = 1000)
	public void test060_removeUserWithAddedAddresses_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test060_removeUserWithAddedAddresses_Pass");
		
		// ___________________________________________
		// Do test preparations.
		// -------------------------------------------
		utx.begin();

		final List<User> userList = this.jpaUserDao.findAll();
		assertThat(userList.size(), equalTo(1));
		final User userToRemove = userList.get(0); 
		final Set<Address> addressesForAssertsSet = userToRemove.getAddresses();
		
		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		this.jpaUserDao.remove(userToRemove);
		
		utx.commit();

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------
		assertThat(this.jpaUserDao.findById(userToRemove.getId()), nullValue());
		
		addressesForAssertsSet.forEach(address -> assertThat(this.em.find(Address.class, address.getId()), nullValue()));
	}
}
