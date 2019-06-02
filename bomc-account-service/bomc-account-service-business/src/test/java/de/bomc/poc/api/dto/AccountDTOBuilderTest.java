package de.bomc.poc.api.dto;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * This class tests {@link AccountDTO} functionality.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountDTOBuilderTest {

	private static final Logger LOGGER = Logger.getLogger(AccountDTOBuilderTest.class);
	private static final String LOG_PREFIX = "AccountDTOBuilderTest#";

	// _______________________________________________
	// Account data.
	private static final String ACCOUNT_NAME = "account_name";
	// _______________________________________________
	// Person data.
	private static final String USER_USERNAME = "user_username";
	private static final String LEGALUSER_COMPANY_ID = "legalUser_companyId";
	private static final LocalDate NATURALUSER_BIRTH_DATE = LocalDate.of(2011, 11, 4);
	// _______________________________________________
	// Account Person data.
	private static final Boolean ACCOUNT_USER_OWNER_FLAG = Boolean.TRUE;
	private static final String ACCOUNT_USER_AUTH_TYPE = "accountUser_authType";
	// _______________________________________________
	// Address data.
	private static final String ADDRESS_CITY = "address_city";
	private static final String ADDRESS_COUNTRY = "address_country";
	private static final String ADDRESS_STREET = "address_street";
	private static final String ADDRESS_ZIP_CODE = "address_zipCode";

	@Test
	public void test010_createAccountDTOByBuilder() {
		LOGGER.debug(LOG_PREFIX + "test010_createAccountDTOByBuilder");

		final AccountDTO accountDTO = AccountDTO.account_Name(ACCOUNT_NAME).user_Username(USER_USERNAME).legalUser_CompanyId(LEGALUSER_COMPANY_ID)
				.naturalUser_BirthDate(NATURALUSER_BIRTH_DATE).accountUser_OwnerFlag(ACCOUNT_USER_OWNER_FLAG)
				.accountUser_AuthType(ACCOUNT_USER_AUTH_TYPE).address_City(ADDRESS_CITY)
				.address_Country(ADDRESS_COUNTRY).address_Street(ADDRESS_STREET).address_ZipCode(ADDRESS_ZIP_CODE).build();
		
		assertThat(accountDTO.getAccount_name(), equalTo(ACCOUNT_NAME));
		assertThat(accountDTO.getUser_username(), equalTo(USER_USERNAME));
		assertThat(accountDTO.getLegalUser_companyId(), equalTo(LEGALUSER_COMPANY_ID));
		assertThat(accountDTO.getNaturalUser_birthDate(), equalTo(NATURALUSER_BIRTH_DATE));
		assertThat(accountDTO.getAccountUser_ownerFlag(), equalTo(ACCOUNT_USER_OWNER_FLAG));
		assertThat(accountDTO.getAccountUser_authType(), equalTo(ACCOUNT_USER_AUTH_TYPE));
		assertThat(accountDTO.getAddress_city(), equalTo(ADDRESS_CITY));
		assertThat(accountDTO.getAddress_country(), equalTo(ADDRESS_COUNTRY));
		assertThat(accountDTO.getAddress_street(), equalTo(ADDRESS_STREET));
		assertThat(accountDTO.getAddress_zipCode(), equalTo(ADDRESS_ZIP_CODE));
	}
}
