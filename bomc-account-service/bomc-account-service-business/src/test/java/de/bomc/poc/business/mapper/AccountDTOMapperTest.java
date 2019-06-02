package de.bomc.poc.business.mapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.bomc.poc.api.dto.AccountDTO;
import de.bomc.poc.model.account.Account;
import de.bomc.poc.model.account.AccountUser;
import de.bomc.poc.model.account.Address;
import de.bomc.poc.model.account.AuthorizationTypeEnum;
import de.bomc.poc.model.account.LegalUser;
import de.bomc.poc.model.account.NaturalUser;
import de.bomc.poc.model.account.User;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This class tests Account mapping functionality.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountDTOMapperTest {

	private static final Logger LOGGER = Logger.getLogger(AccountDTOMapperTest.class);
	private static final String LOG_PREFIX = "AccountDTOMapperTest#";

	// ________________________________________________
	// Account
	protected static final Long ACCOUNT_ID = 333L;
	protected static final String ACCOUNT_DISPLAY_NAME = "ACCOUNT_DISPLAY_NAME";
	// ________________________________________________
	// AccountUser
	public static final Boolean ACCOUNT_USER_OWNER_FLAG_TRUE = true;
	public static final Boolean ACCOUNT_USER_OWNER_FLAG_FALSE = false;
	public static final String ACCOUNT_USER_AUTH_TYPE_STRING = AuthorizationTypeEnum.WEAK.name();
	// ________________________________________________
	// Person
	protected static final Long USER_ID = 111L;
	protected static final String USER_USERNAME = "USER_USERNAME";
	protected static final String USER_CHANGED_USERNAME = "USER_CHANGED_USERNAME";
	protected static final String USER_USERNAME_UNKNOWN = "USER_NAME_UNKNOWN";
	protected static final String LEGAL_USER_COMPANY_ID = "LEGAL_USER_COMPANY_ID";
	protected static final LocalDate NATURAL_USER_BIRTHDATE = LocalDate.of(2011, 11, 13);
	// ________________________________________________
	// Address
	protected static final Long ADDRESS_ID = 222L;
	protected static final String ADDRESS_CITY = "ADDRESS_CITY";
	protected static final String ADDRESS_COUNTRY = "ADDRESS_COUNTRY";
	protected static final String ADDRESS_STREET = "ADDRESS_STREET";
	protected static final String ADDRESS_ZIP = "4000";

	@Test
	public void test010_map_Account_To_AccountDTO_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_map_Account_To_AccountDTO_pass");

		// ___________________________________________
		// Do test preparations.
		// -------------------------------------------

		final Account account = this.getNotPersistedAccount(ACCOUNT_DISPLAY_NAME);
		account.setId(ACCOUNT_ID);
		final User user = this.getNotPersistedLegalUser(USER_USERNAME);
		user.setId(USER_ID);
		final Address address = this.getNotPersistedAddress(ADDRESS_ZIP);
		address.setId(ADDRESS_ID);

		final Set<AuthorizationTypeEnum> authorizationTypeSet = new HashSet<>(1);
		authorizationTypeSet.add(AuthorizationTypeEnum.STRONG);
		final AccountUser accountUser = new AccountUser(user, account, ACCOUNT_USER_OWNER_FLAG_TRUE,
				authorizationTypeSet);

		// ___________________________________________
		// Run actual test.
		// -------------------------------------------

		final AccountDTO accountDTO = Account_To_AccountDTO_Mapper.INSTANCE.map_Account_To_AccountDTO(account,
				accountUser, user, address);

		LOGGER.debug(LOG_PREFIX + "test010_map_Account_To_AccountDTO_pass " + accountDTO);

		// ___________________________________________
		// Do asserts.
		// -------------------------------------------

		assertThat(account.getId(), equalTo(ACCOUNT_ID));
		assertThat(account.getName(), equalTo(ACCOUNT_DISPLAY_NAME));
		assertThat(user.getId(), equalTo(USER_ID));
		assertThat(user.getUsername(), equalTo(USER_USERNAME));
		// assertThat(legalUser.getCompanyId, equalTo(LEGAL_USER_COMPANY_ID));
		// assertThat(naturalUser.getBirthDate(),
		// equalTo(NATURAL_USER_BIRTHDATE));
		assertThat(accountUser.getOwnerFlag(), equalTo(ACCOUNT_USER_OWNER_FLAG_TRUE));
		assertThat(accountUser.getAuthTypes().iterator().next(), equalTo(AuthorizationTypeEnum.STRONG));
		assertThat(address.getId(), equalTo(ADDRESS_ID));
		assertThat(address.getCity(), equalTo(ADDRESS_CITY));
		assertThat(address.getCountry(), equalTo(ADDRESS_COUNTRY));
		assertThat(address.getStreet(), equalTo(ADDRESS_STREET));
		assertThat(address.getZipCode(), equalTo(ADDRESS_ZIP));
	}

	/**
	 * Tests updating an <code>AccountDTO</code> by an given
	 * <code>Account</code> instance.
	 */
	@Test
	public void test020_map_Account_Into_AccountDTO_pass() {
		LOGGER.debug(LOG_PREFIX + "test020_map_Account_Into_AccountDTO_pass");

		// ___________________________________________
		// Do test preparation.
		// -------------------------------------------
		
		final Account account = this.getNotPersistedAccount(ACCOUNT_DISPLAY_NAME);
		account.setId(ACCOUNT_ID);

		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		
		final AccountDTO accountDTO = new AccountDTO();
		Account_To_AccountDTO_Mapper.INSTANCE.map_Account_Into_AccountDTO(account, null, null, null, accountDTO);

		// __________________________________________
		// Do asserts.
		
		assertThat(accountDTO.getAccount_id(), equalTo(ACCOUNT_ID));
		assertThat(accountDTO.getAccount_name(), equalTo(ACCOUNT_DISPLAY_NAME));
	}

	/**
	 * Test updating an <code>AccountDTO</code> by an given
	 * <code>AccountUser</code> instance.
	 */
	@Test
	public void test030_map_AccountUser_Into_AccountDTO_pass() {
		LOGGER.debug(LOG_PREFIX + "test030_map_AccountUser_Into_AccountDTO_pass");

		// ___________________________________________
		// Do test preparation.
		// -------------------------------------------
		
		final AccountUser accountUser = this.getNotPersistedAccountUser();

		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		
		final AccountDTO accountDTO = new AccountDTO();
		Account_To_AccountDTO_Mapper.INSTANCE.map_Account_Into_AccountDTO(null, accountUser, null, null, accountDTO);

		// __________________________________________
		// Do asserts.
		
		assertThat(accountDTO.getAccountUser_authType(), equalTo(ACCOUNT_USER_AUTH_TYPE_STRING));
		assertThat(accountDTO.getAccountUser_ownerFlag(), equalTo(ACCOUNT_USER_OWNER_FLAG_TRUE));
	}
	
	/**
	 * Test updating an <code>AccountDTO</code> by an given
	 * <code>Person</code> instance.
	 */
	@Test
	public void test040_map_User_Into_AccountDTO_pass() {
		LOGGER.debug(LOG_PREFIX + "test040_map_User_Into_AccountDTO_pass");

		// ___________________________________________
		// Do test preparation.
		// -------------------------------------------
		
		final User user = this.getNotPersistedNaturalUser(USER_USERNAME);
		user.setId(USER_ID);

		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		
		final AccountDTO accountDTO = new AccountDTO();
		Account_To_AccountDTO_Mapper.INSTANCE.map_Account_Into_AccountDTO(null, null, user, null, accountDTO);

		// __________________________________________
		// Do asserts.
		// ------------------------------------------
		
		assertThat(accountDTO.getUser_id(), equalTo(USER_ID));
	}
	
	@Test
	public void test040_map_AccountDTO_To_Account_pass() {
		LOGGER.debug(LOG_PREFIX + "test040_map_AccountDTO_To_Account_pass");
		
		// ___________________________________________
		// Do test preparation.
		// -------------------------------------------
		
		final AccountDTO accountDTO = this.getAccountDTO(); 
		
		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		
		final Account account = AccountDTO_To_Account_Mapper.INSTANCE.map_AccountDTO_To_Account(accountDTO);
		
		// __________________________________________
		// Do asserts.
		// ------------------------------------------
		
		assertThat(account.getId(), equalTo(ACCOUNT_ID));
		assertThat(account.getName(), equalTo(ACCOUNT_DISPLAY_NAME));
	}

	/**
	 * Tests updating an <code>Account</code> by an given
	 * <code>AccountDTO</code> instance.
	 */
	@Test
	public void test050_map_AccountDTO_Into_Account_pass() {
		LOGGER.debug(LOG_PREFIX + "test050_map_AccountDTO_Into_Account_pass");
		
		// ___________________________________________
		// Do test preparation.
		// -------------------------------------------
		
		final AccountDTO accountDTO = this.getAccountDTO(); 
		final Account account = new Account(ACCOUNT_DISPLAY_NAME + "_COMPARE");
		
		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		
		AccountDTO_To_Account_Mapper.INSTANCE.map_AccountDTO_Into_Account(accountDTO, account);
		
		// __________________________________________
		// Do asserts.
		// ------------------------------------------
		
		assertThat(account, notNullValue());
		assertThat(account.getId(), equalTo(ACCOUNT_ID));
		assertThat(account.getName(), equalTo(ACCOUNT_DISPLAY_NAME));
	}
	
//	@Test
//	public void test060_map_AccountDTO_To_AccountUser_pass() {
//		LOGGER.debug(LOG_PREFIX + "test060_map_AccountDTO_To_AccountUser_pass");
//		
//		// ___________________________________________
//		// Do test preparation.
//		// -------------------------------------------
//		
//		final AccountDTO accountDTO = this.getAccountDTO();
//		
//		// ___________________________________________
//		// Do actual test.
//		// -------------------------------------------
//		
//		final AccountUser accountUser = AccountDTO_To_AccountUser_Mapper.INSTANCE.map_AccountDTO_To_AccountUser(accountDTO);
//		
//		// __________________________________________
//		// Do asserts.
//		// ------------------------------------------
//		
//		assertThat(accountUser, notNullValue());
//		assertThat(accountUser.getOwnerFlag(), equalTo(ACCOUNT_USER_OWNER_FLAG_TRUE));
//		assertThat(accountUser.getAuthTypes(), notNullValue());
//		assertThat(accountUser.getAuthTypes().iterator().next(), equalTo(AuthorizationTypeEnum.WEAK));
//	}
	
//	/**
//	 * Tests updating an <code>AccountUser</code> by an given
//	 * <code>AccountDTO</code> instance.
//	 */
//	@Test
//	public void test070_map_AccountDTO_Into_AccountUser_pass() {
//		LOGGER.debug(LOG_PREFIX + "test070_map_AccountDTO_Into_AccountUser_pass");
//		
//		// ___________________________________________
//		// Do test preparation.
//		// -------------------------------------------
//		
//		final AccountDTO accountDTO = this.getAccountDTO(); 
//		final AccountUser accountUser = this.getNotPersistedAccountUser();
//		
//		// ___________________________________________
//		// Do actual test.
//		// -------------------------------------------
//		
//		AccountDTO_To_AccountUser_Mapper.INSTANCE.map_AccountDTO_Into_AccountUser(accountDTO, accountUser);
//		
//		// __________________________________________
//		// Do asserts.
//		// ------------------------------------------
//		
//		assertThat(accountUser, notNullValue());
//		assertThat(accountUser.getOwnerFlag(), equalTo(ACCOUNT_USER_OWNER_FLAG_TRUE));
//		assertThat(accountUser.getAuthTypes().iterator().next(), equalTo(AuthorizationTypeEnum.WEAK));
//	}
	
	@Test
	public void test080_map_AccountDTO_To_Address_pass() {
		LOGGER.debug(LOG_PREFIX + "test080_map_AccountDTO_To_Address_pass");
		
		// ___________________________________________
		// Do test preparation.
		// -------------------------------------------
		
		final AccountDTO accountDTO = this.getAccountDTO();
		
		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		
		final Address address = AccountDTO_To_Address_Mapper.INSTANCE.map_AccountDTO_To_Address(accountDTO);
		
		// __________________________________________
		// Do asserts.
		// ------------------------------------------
		
		assertThat(address, notNullValue());
		assertThat(address.getCountry(), equalTo(ADDRESS_COUNTRY));
		assertThat(address.getStreet(), equalTo(ADDRESS_STREET));
		assertThat(address.getUser(), nullValue());
		assertThat(address.getZipCode(), equalTo(ADDRESS_ZIP));
	}
	
	/**
	 * Tests updating an <code>AccountUser</code> by an given
	 * <code>AccountDTO</code> instance.
	 */
	@Test
	public void test090_map_AccountDTO_Into_Address_pass() {
		LOGGER.debug(LOG_PREFIX + "test090_map_AccountDTO_Into_Address_pass");
		
		// ___________________________________________
		// Do test preparation.
		// -------------------------------------------
		
		final AccountDTO accountDTO = this.getAccountDTO(); 
		accountDTO.setAddress_zipCode("4001");
		final Address address = this.getNotPersistedAddress(ADDRESS_ZIP);
		
		
		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		
		AccountDTO_To_Address_Mapper.INSTANCE.map_AccountDTO_Into_Address(accountDTO, address);
		
		// __________________________________________
		// Do asserts.
		// ------------------------------------------
		
		assertThat(address.getZipCode(), equalTo("4001"));
	}
	
	@Test
	public void test100_map_AccountDTO_To_User_pass() {
		LOGGER.debug(LOG_PREFIX + "test100_map_AccountDTO_To_User_pass");
		
		// ___________________________________________
		// Do test preparation.
		// -------------------------------------------
		
		final AccountDTO accountDTO = this.getAccountDTO();
		
		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		
		final User user = AccountDTO_To_User_Mapper.INSTANCE.map_AccountDTO_To_User(accountDTO);
		
		// __________________________________________
		// Do asserts.
		// ------------------------------------------
		
		assertThat(user, notNullValue());
		assertThat(user.getUsername(), equalTo(USER_USERNAME));
	}
	
	/**
	 * Tests updating an <code>Person</code> by an given
	 * <code>AccountDTO</code> instance.
	 */
	@Test
	public void test110_map_AccountDTO_Into_User_pass() {
		LOGGER.debug(LOG_PREFIX + "test110_map_AccountDTO_Into_User_pass");
		
		// ___________________________________________
		// Do test preparation.
		// -------------------------------------------
		
		final AccountDTO accountDTO = this.getAccountDTO(); 
		final User user = this.getNotPersistedUser(USER_USERNAME);
		
		
		// ___________________________________________
		// Do actual test.
		// -------------------------------------------
		
		AccountDTO_To_User_Mapper.INSTANCE.map_AccountDTO_Into_User(accountDTO, user);
		
		// __________________________________________
		// Do asserts.
		// ------------------------------------------
		
		assertThat(user.getUsername(), equalTo(USER_USERNAME));
	}
	
	// _______________________________________________
	// Helper methods.
	// -----------------------------------------------

	protected Address getNotPersistedAddress(final String zip) {
		final Address address = new Address();

		address.setCity(ADDRESS_CITY);
		address.setCountry(ADDRESS_COUNTRY);
		address.setStreet(ADDRESS_STREET);
		address.setZipCode(zip);

		return address;
	}

	protected User getNotPersistedUser(final String username) {
		final User user = new User(username);

		return user;
	}

	protected LegalUser getNotPersistedLegalUser(final String username) {
		final LegalUser legalUser = new LegalUser(username, LEGAL_USER_COMPANY_ID);

		return legalUser;
	}

	protected NaturalUser getNotPersistedNaturalUser(final String username) {
		final NaturalUser naturalUser = new NaturalUser(username);
		naturalUser.setBirthDate(NATURAL_USER_BIRTHDATE);

		return naturalUser;
	}

	protected Account getNotPersistedAccount(final String displayName) {
		final Account account = new Account(displayName);

		return account;
	}

	protected AccountUser getNotPersistedAccountUser() {
		final User user = this.getNotPersistedUser(USER_USERNAME);
		user.setId(USER_ID);
		
		final Account account = this.getNotPersistedAccount(ACCOUNT_DISPLAY_NAME);
		account.setId(ACCOUNT_ID);
		
		final Set<AuthorizationTypeEnum> authTypes = new LinkedHashSet<>(Arrays.asList(AuthorizationTypeEnum.WEAK));
		
		final AccountUser accountUser = new AccountUser(user, account, ACCOUNT_USER_OWNER_FLAG_TRUE, authTypes);

		return accountUser;
	}
	
	protected AccountDTO getAccountDTO() {
		AccountDTO accountDTO = new AccountDTO();

		accountDTO.setAccount_id(ACCOUNT_ID);
		accountDTO.setAccount_name(ACCOUNT_DISPLAY_NAME);
		accountDTO.setAccountUser_authType(ACCOUNT_USER_AUTH_TYPE_STRING);
		accountDTO.setAccountUser_ownerFlag(ACCOUNT_USER_OWNER_FLAG_TRUE);
		accountDTO.setAddress_city(ADDRESS_CITY);
		accountDTO.setAddress_country(ADDRESS_COUNTRY);
		accountDTO.setAddress_id(ADDRESS_ID);
		accountDTO.setAddress_street(ADDRESS_STREET);
		accountDTO.setAddress_zipCode(ADDRESS_ZIP);
		accountDTO.setLegalUser_companyId(LEGAL_USER_COMPANY_ID);
		accountDTO.setNaturalUser_birthDate(NATURAL_USER_BIRTHDATE);
		accountDTO.setUser_id(USER_ID);
		accountDTO.setUser_username(USER_USERNAME);

		return accountDTO;
	}
}
