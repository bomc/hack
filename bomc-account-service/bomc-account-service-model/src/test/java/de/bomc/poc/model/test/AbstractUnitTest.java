package de.bomc.poc.model.test;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import de.bomc.poc.model.account.Account;
import de.bomc.poc.model.account.Address;
import de.bomc.poc.model.account.NaturalUser;
import de.bomc.poc.model.account.User;

import java.time.LocalDate;

import javax.persistence.EntityManager;

/**
 * This class is a helper class for SE unit tests.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
public abstract class AbstractUnitTest {

	// ________________________________________________
	// Address
	public static final String ADDRESS_CITY = "myCity";
	public static final String ADDRESS_COUNTRY = "myCountry";
	public static final String ADDRESS_STREET = "myStreet";
	public static final String ADDRESS_ZIP_CODE = "myZipCode";
	// _______________________________________________
	// Person
	public static final String USER_DEFAULT_USERNAME = "default_username";
	// _______________________________________________
	// Account
	public static final String ACCOUNT_DEFAULT_NAME = "default_accountname";
	public static final Boolean ACCOUNT_OWNER_FLAG_TRUE = true;
	public static final Boolean ACCOUNT_OWNER_FLAG_FALSE = false;
	//
	// Read this from 'test/resources/META-INF/persistence.xml',
	// 'persistence-unit name'.
	private static final String PERSISTENCE_UNIT_NAME = "bomc-PU";
	@Rule
	public EntityManagerProvider emProvider = EntityManagerProvider.persistenceUnit(PERSISTENCE_UNIT_NAME);
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	protected EntityManager entityManager;

	// ________________________________________________
	// Helper methods
	// ------------------------------------------------

	public Address getAddress() {
		final Address address = new Address();
		address.setCity(ADDRESS_CITY);
		address.setCountry(ADDRESS_COUNTRY);
		address.setStreet(ADDRESS_STREET);
		address.setZipCode(ADDRESS_ZIP_CODE);

		return address;
	}

	public User getUser(final String username) {
		User user = null;

		if (username != null) {
			user = new User(username);
		} else {
			user = new User(USER_DEFAULT_USERNAME);
		}

		return user;
	}

	public NaturalUser getPrivateUser(final String username) {
		NaturalUser user = null;

		if (username != null) {
			user = new NaturalUser(username);
		} else {
			user = new NaturalUser(USER_DEFAULT_USERNAME);
		}

		user.setBirthDate(LocalDate.of(2011, 11, 13));
		
		return user;
	}
	
	public Account getAccount(final String name) {
		Account account = null;

		if (name != null) {
			account = new Account(name);
		} else {
			account = new Account(ACCOUNT_DEFAULT_NAME);
		}

		return account;
	}
}
