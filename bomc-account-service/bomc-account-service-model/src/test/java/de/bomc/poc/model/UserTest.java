package de.bomc.poc.model;

import de.bomc.poc.model.account.Address;
import de.bomc.poc.model.account.User;
import de.bomc.poc.model.test.AbstractUnitTest;
import de.bomc.poc.model.test.UserMother;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * This class tests {@link Person} functionality.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTest extends AbstractUnitTest {

    private static final Logger LOGGER = Logger.getLogger(UserTest.class);
    private static final String LOG_PREFIX = "UserTest#";
    // A username
    private static final String USERNAME = "test-user-1";
    // A Person that will be created on every test.
    private User mUser;

    @Before
    public void setup() {
        this.entityManager = this.emProvider.getEntityManager();
        assertThat(this.entityManager, notNullValue());

        final UserMother userMother = new UserMother(this.emProvider, USERNAME);
        this.mUser = userMother.instance();
        assertThat(this.mUser.getId(), notNullValue());
    }

    /**
     * Read the persisted <code>Person</code>.
     */
    @Test
    public void test010_readPersistedUser_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_readPersistedUser_pass");

        // ___________________________________________
        // Run actual test.
        // -------------------------------------------
        this.entityManager = this.emProvider.getEntityManager();
        assertThat(this.entityManager, notNullValue());

        final User retUser = this.entityManager.find(User.class, this.mUser.getId());

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(retUser, notNullValue());
        assertThat(retUser.isNew(), equalTo(false));
    }

    /**
     * Test NamedQuery 'NQ_FIND_BY_USERNAME'.
     */
    @Test
    public void test020_readByNamedQuery_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_readByNamedQuery_pass");

        // ___________________________________________
        // Run actual test.
        // -------------------------------------------
        this.entityManager = this.emProvider.getEntityManager();
        assertThat(this.entityManager, notNullValue());

        final TypedQuery<User> query = this.entityManager.createNamedQuery(User.NQ_FIND_BY_USERNAME, User.class)
                                                         .setParameter(1, USERNAME);
        final User retUser = query.getSingleResult();

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(retUser, notNullValue());
    }

    /**
     * <pre>
     * This test persist a already available user in db.
     * The test fails and a <code>javax.persistence.PersistenceException</code> is thrown.
     * </pre>
     */
    @Test
    public void test030_persistAlreadyUserInDb_fail() {
        LOGGER.debug(LOG_PREFIX + "test030_persistAlreadyUserInDb_fail");

        this.thrown.expect(PersistenceException.class);

        this.entityManager = this.emProvider.getEntityManager();
        assertThat(this.entityManager, notNullValue());

        this.emProvider.tx()
                       .begin();

        // ___________________________________________
        // Run actual test.
        // -------------------------------------------
        final User user = new User(USERNAME);
        assertThat(user.isNew(), equalTo(true));

        this.entityManager.persist(user);
        
        this.emProvider.tx().commit();
    }

    /**
     * Add a <code>Address</code> to a <code>Person</code> and persist both. 
     */
    @Test
    public void test040_addAddressToUser_pass() {
    	LOGGER.debug(LOG_PREFIX + "test040_addAddressToUser_pass");

        // ___________________________________________
        // Do actual test. Create a user and a address instance.
        // ------------------------------------------
    	final User user = new User(USERNAME + "040");
    	final Address address = this.getAddress();
    	
    	user.addAddress(address);
    	
    	this.emProvider.tx().begin();
    	
    	this.entityManager.persist(user);
    	
    	this.emProvider.tx().commit();
    	
        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
    	assertThat(user.getId(), notNullValue());
    	assertThat(address.getId(), notNullValue());
    	assertThat(user.getAddresses().size(), equalTo(1));
    }
    
    /**
     * Add a not persisted <code>Address</code> to a persisted <code>Person</code>. 
     */
    @Test
    public void test041_addNotPersistedAddressToPersistedUser_pass() {
    	LOGGER.debug(LOG_PREFIX + "test041_addNotPersistedAddressToPersistedUser_pass");

        // ___________________________________________
        // Run actual test. Create a user and a address instance.
        // -------------------------------------------
    	final User user = new User(USERNAME + "040");
    	final Address address = this.getAddress();
    	
    	this.emProvider.tx().begin();
    	
    	this.entityManager.persist(user);
    	
    	this.emProvider.tx().commit();
    	
    	// ___________________________________________
    	// Add not persisted address to persisted user.
    	
    	this.emProvider.tx().begin();
    	
    	user.addAddress(address);
    	this.entityManager.persist(user);
    	
    	this.emProvider.tx().commit();
    	
        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
    	assertThat(user.getId(), notNullValue());
    	assertThat(address.getId(), notNullValue());
    	assertThat(user.getAddresses().size(), equalTo(1));
    }
    
    @Test
    public void test050_addUserToAddress_pass() {
    	LOGGER.debug(LOG_PREFIX + "test050_addUserToAddress_pass");

        // ___________________________________________
        // Run actual test. Create a user and a address instance.
        // -------------------------------------------
    	final User user = new User(USERNAME + "050");
    	final Address address = this.getAddress();
    	
    	address.addUser(user);
    	
    	this.emProvider.tx().begin();
    	
    	this.entityManager.persist(user);
    	
    	this.emProvider.tx().commit();
    	
        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
    	assertThat(user.getId(), notNullValue());
    	assertThat(address.getId(), notNullValue());
    	assertThat(user.getAddresses().size(), equalTo(1));
    }

    @Test
    public void test060_removeAddressFromUser_pass() {
    	LOGGER.debug(LOG_PREFIX + "test060_removeAddressFromUser_pass");

        // ___________________________________________
        // Do test preparation. Use a user and create three address instances.
        // -------------------------------------------
    	final User user = new User(USERNAME + "060");
    	
    	final Address address1 = this.getAddress();
    	address1.setCity("city1");
    	user.addAddress(address1);
    	
    	final Address address2 = this.getAddress();
    	address2.setCity("city2");
    	user.addAddress(address2);

    	final Address address3 = this.getAddress();
    	address2.setCity("city3");
    	user.addAddress(address3);
    	
    	assertThat(user.getAddresses().size(), equalTo(3));
    	
    	this.emProvider.tx().begin();
    	
    	this.entityManager.persist(user);
    	this.emProvider.tx().commit();
    	assertThat(user.getId(), notNullValue());
    	assertThat(address1.getId(), notNullValue());
    	assertThat(address2.getId(), notNullValue());
    	assertThat(user.getAddresses().size(), equalTo(3));
    	
        // ___________________________________________
        // Run actual test. Remove a address from user.
        // -------------------------------------------
    	this.emProvider.tx().begin();
    	
    	user.removeAddress(address1);
    	final User mergedUser = this.entityManager.merge(user);
    	
    	this.emProvider.tx().commit();
    	
        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
    	assertThat(mergedUser.getAddresses().size(), equalTo(2));
    	
    	assertThat(this.entityManager.find(Address.class, address1.getId()), nullValue());
    	assertThat(this.entityManager.find(Address.class, address2.getId()), notNullValue());
    	assertThat(this.entityManager.find(User.class, user.getId()), notNullValue());
    }    
    
    @Test
    public void test070_removeUserWithAddresses_pass() {
    	LOGGER.debug(LOG_PREFIX + "test070_removeUserWithAddresses_pass");

        // ___________________________________________
        // Do test preparation. Use a user and create two address instances.
        // -------------------------------------------
    	final User user = new User(USERNAME + "070");
    	final Address address1 = this.getAddress();
    	address1.setCity("city1");
    	user.addAddress(address1);
    	
    	final Address address2 = this.getAddress();
    	address2.setCity("city2");
    	user.addAddress(address2);
    	
    	this.emProvider.tx().begin();
    	
    	this.entityManager.persist(user);
    	
    	this.emProvider.tx().commit();
    	
    	assertThat(user.getId(), notNullValue());
    	assertThat(address1.getId(), notNullValue());
    	assertThat(address2.getId(), notNullValue());
    	assertThat(user.getAddresses().size(), equalTo(2));
    	
        // ___________________________________________
        // Run actual test. Remove a the user.
        // -------------------------------------------
    	this.emProvider.tx().begin();
    	
    	this.entityManager.remove(user);
    	
    	this.emProvider.tx().commit();
    	
        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
    	final User retUser = this.entityManager.find(User.class, user.getId());
    	assertThat(retUser, nullValue());
    	
    	final Address retAddress1 = this.entityManager.find(Address.class, address1.getId());
    	assertThat(retAddress1, nullValue());
    	
    	final Address retAddress2 = this.entityManager.find(Address.class, address2.getId());
    	assertThat(retAddress2, nullValue());
    }    
    
    @Test
    public void test080_removeAllAddressesFromUser_pass() {
    	LOGGER.debug(LOG_PREFIX + "test080_removeAllAddressesFromUser_pass");

        // ___________________________________________
        // Do test preparation. Use a user and create two address instances.
        // -------------------------------------------
    	final User user = new User(USERNAME + "080");
    	final Address address1 = this.getAddress();
    	address1.setCity("city1");
    	user.addAddress(address1);
    	
    	final Address address2 = this.getAddress();
    	address2.setCity("city2");
    	user.addAddress(address2);
    	
    	this.emProvider.tx().begin();
    	
    	this.entityManager.persist(user);
    	
    	this.emProvider.tx().commit();
    	
    	assertThat(user.getId(), notNullValue());
    	assertThat(address1.getId(), notNullValue());
    	assertThat(address2.getId(), notNullValue());
    	assertThat(user.getAddresses().size(), equalTo(2));
    	
        // ___________________________________________
        // Run actual test. Remove all addresses.
        // -------------------------------------------
    	this.emProvider.tx().begin();
    	
    	user.removeAddress(address1);
    	user.removeAddress(address2);

    	this.entityManager.merge(user);
    	
    	this.emProvider.tx().commit();
    	
        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
    	final User retUser = this.entityManager.find(User.class, user.getId());
    	assertThat(retUser, notNullValue());
    	assertThat(retUser.getAddresses().size(), equalTo(0));
    	
    	final Address retAddress1 = this.entityManager.find(Address.class, address1.getId());
    	assertThat(retAddress1, nullValue());
    	
    	final Address retAddress2 = this.entityManager.find(Address.class, address2.getId());
    	assertThat(retAddress2, nullValue());
    }      
    
    @Test
    public void test100_addAddressesToUserAsSet_pass() {
    	LOGGER.debug(LOG_PREFIX + "test100_addAddressesToUserAsSet_pass");

        // ___________________________________________
        // Do test preparation. Use a user and create two address instances.
        // -------------------------------------------
    	final User user = new User(USERNAME + "070");
    	final Address address1 = this.getAddress();
    	address1.setCity("city1");
    	user.addAddress(address1);
    	
    	final Address address2 = this.getAddress();
    	address2.setCity("city2");
    	
    	final Set<Address> addressSet = new HashSet<>();
    	addressSet.add(address1);
    	addressSet.add(address2);
    	
    	// ___________________________________________
    	// Run actual test.
    	// -------------------------------------------
    	user.setAddresses(addressSet);
    	
    	this.emProvider.tx().begin();
    	
    	this.entityManager.persist(user);
    	
    	this.emProvider.tx().commit();
    	
        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
    	assertThat(user.getId(), notNullValue());
    	assertThat(address1.getId(), notNullValue());
    	assertThat(address2.getId(), notNullValue());
    	assertThat(user.getAddresses().size(), equalTo(2));
    }   
    
    @Test
    public void test110_merge_pass() {
    	LOGGER.debug(LOG_PREFIX + "test110_merge_pass");

        // ___________________________________________
        // Do test preparation. Use a user and create a address instance.
        // -------------------------------------------
    	final User user = new User(USERNAME + "080");
    	final Address address = this.getAddress();
    	
    	user.addAddress(address);
    	
    	this.emProvider.tx().begin();
    	
    	this.entityManager.persist(user);
    	
    	this.emProvider.tx().commit();
    	
    	assertThat(user.getId(), notNullValue());
    	assertThat(address.getId(), notNullValue());
    	assertThat(user.getAddresses().size(), equalTo(1));
    	
    	// ___________________________________________
    	// Run actual test. Merge user and address.
    	// -------------------------------------------
    	this.emProvider.tx().begin();
    	
    	user.setUsername("merged_username");
    	final Set<Address> addressSet = user.getAddresses();
    	final Iterator<Address> iterator = addressSet.iterator();
    	while(iterator.hasNext()) {
    		final Address changeAddress = iterator.next();
    		
    		changeAddress.setCity("merged_city");
    	}

    	this.entityManager.merge(user);
    	
    	this.emProvider.tx().commit();
    	
        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
    	final User retUser = this.entityManager.find(User.class, user.getId());
    	assertThat(retUser.getUsername(), equalTo("merged_username"));
    	
    	final Iterator<Address> mergedIterator = retUser.getAddresses().iterator();
    	while(mergedIterator.hasNext()) {
    		final Address mergedAddress = mergedIterator.next();
    		
    		assertThat(mergedAddress.getCity(), equalTo("merged_city"));
    	}
    }    
    
    @Test
    public void test120_AddressOrderBy() {
    	LOGGER.debug(LOG_PREFIX + "test120_AddressOrderBy");
    	
        // ___________________________________________
        // Do test preparation. Use a user and create three address instances.
        // -------------------------------------------
    	final User user = new User(USERNAME + "070");
    	
    	final Address address = this.getAddress();
    	address.setCountry("fcountryF");
    	
    	final Address address1 = this.getAddress();
    	address1.setCountry("bcountryB");
    	user.addAddress(address1);
    	
    	final Address address2 = this.getAddress();
    	address2.setCountry("scountryS");
    	
    	final Set<Address> addressSet = new HashSet<>();
    	addressSet.add(address);
    	addressSet.add(address1);
    	addressSet.add(address2);
    	user.setAddresses(addressSet);
    	
    	this.emProvider.tx().begin();
    	
    	this.entityManager.persist(user);
    	
    	this.entityManager.flush();
    	this.entityManager.clear();
    	
    	this.emProvider.tx().commit();
    	
    	// ___________________________________________
    	// Run actual test.
    	// -------------------------------------------
    	final User retUser = this.entityManager.find(User.class, user.getId());
    	final Set<Address> retAddressSet = retUser.getAddresses();
    	
        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
    	assertThat(retAddressSet, contains(address2, address, address1));
    }
    
    @Test
    public void test130_equalsHashcode_pass() {
        LOGGER.debug(LOG_PREFIX + "test130_equalsHashcode_pass");

        // ___________________________________________
        // Do test preparation. Check if user is created in db.
        // -------------------------------------------
        assertThat(this.mUser, notNullValue());
        assertThat(this.mUser.getId(), notNullValue());

        // Create an other user for comparison.
        final UserMother userMother = new UserMother(this.emProvider, USERNAME + "_new");
        final User newUser = userMother.instance();
        assertThat(newUser, notNullValue());

        assertThat("Ids must be different.", this.mUser.getId(), not(equalTo(newUser.getId())));

        // ___________________________________________
        // Do actual test with asserts.
        // -------------------------------------------
        final Set<User> userSet = new HashSet<>(2);

        userSet.add(this.mUser);
        assertThat(userSet.size(), equalTo(1));

        // Add user with
        userSet.add(newUser);
        assertThat(userSet.size(), equalTo(2));

        // Add same user again -> means id is already added to the set.
        userSet.add(newUser);
        assertThat(userSet.size(), equalTo(2));

        // Check for different uer objects.
        assertThat("Should not occur, users are not equal.", this.mUser.equals(newUser), not(true));

        // Check for different object instances.
        final UserMother userMother2 = new UserMother(this.emProvider, USERNAME + "_new2");
        assertThat("Should not occur, object types are not same.", this.mUser.equals(userMother2.instance()), not(true));

        final User comparisonUser = new User(USERNAME);
        // Check against id.
        assertThat("Instances are not equal, comparisonUser id is null.", comparisonUser.equals(this.mUser), not(true));
        comparisonUser.setId(this.mUser.getId());
        assertThat("Instances are not equal, comparisonUser id (is null) is not equals to mUsers id.", this.mUser.equals(comparisonUser), equalTo(true));

        comparisonUser.setId(200L);
        assertThat("Instances are not equal, comparisonUser id is not equals to mUsers id.", this.mUser.equals(comparisonUser), not(true));

        // Check if username == null.
        comparisonUser.setId(this.mUser.getId());
        this.mUser.setUsername(null);
        // Check for different object instances.
        assertThat("Should not occur, username are compared in equals method of Person. The usernames are different, beause one of them is null.", this.mUser.equals(comparisonUser), equalTo(false));

        // Check if username == null.
        // Change order of comparables.
        assertThat("Should not occur, username are compared in equals method of Person. The usernames are different, beause one of them is null.", comparisonUser.equals(this.mUser), not(true));

        // Check if username is different.
        this.mUser.setUsername("different_username");
        // Check for different object instances.
        assertThat("Should not occur, username is compared in equals method of Person. The usernames are different.", this.mUser.equals(comparisonUser), not(true));

        assertThat(this.mUser.equals(null), not(true));
    }
}
