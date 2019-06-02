/**
 * Project: POC PaaS
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm;

import java.time.LocalDate;
import java.time.Month;

import de.bomc.poc.hrm.domain.CustomerEntity;
import de.bomc.poc.hrm.interfaces.mapper.CustomerDto;

/**
 * A abstract class for unit tests.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 07.05.2019
 */
public abstract class AbstractBaseUnit {

	/* --------------------- constants ------------------------------ */
	public static final Long CUSTOMER_ID = 42L;
	public static final String CUSTOMER_CITY = "myCity";
	public static final String CUSTOMER_COUNTRY = "CH";
	public static final LocalDate CUSTOMER_DATE_OF_BIRTH = LocalDate.of(2019, Month.JUNE, 3);
	public static final String CUSTOMER_FIRST_NAME = "myFirstName";
	public static final String CUSTOMER_LAST_NAME = "myLastName";
	public static final String CUSTOMER_E_MAIL = "bomc1@bomc.org";
	public static final String CUSTOMER_HOUSE_NUMBER = "42";
	public static final String CUSTOMER_PHONE_NUMBER = "042-424242";
	public static final String CUSTOMER_POSTAL_CODE = "4242";
	public static final String CUSTOMER_STREET = "myStreet";
	public static final String CUSTOMER_CREATE_USER = "myCreateUser";
	
	// _______________________________________________
	// Helper methods
	// -----------------------------------------------
	
	public static CustomerEntity createCustomerEntity() {
		
		return CustomerEntity.builder().city(CUSTOMER_CITY).country(CUSTOMER_COUNTRY)
				.dateOfBirth(CUSTOMER_DATE_OF_BIRTH).emailAddress(CUSTOMER_E_MAIL).firstName(CUSTOMER_FIRST_NAME)
				.houseNumber(CUSTOMER_HOUSE_NUMBER).lastName(CUSTOMER_LAST_NAME).phoneNumber(CUSTOMER_PHONE_NUMBER)
				.postalCode(CUSTOMER_POSTAL_CODE).street(CUSTOMER_STREET).build();
	}
	
	public static CustomerDto createCustomerDto() {
		
		final CustomerDto customerDto = CustomerDto.builder().city(CUSTOMER_CITY).country(CUSTOMER_COUNTRY)
				.dateOfBirth(CUSTOMER_DATE_OF_BIRTH).emailAddress(CUSTOMER_E_MAIL).firstName(CUSTOMER_FIRST_NAME)
				.houseNumber(CUSTOMER_HOUSE_NUMBER).lastName(CUSTOMER_LAST_NAME).phoneNumber(CUSTOMER_PHONE_NUMBER)
				.postalCode(CUSTOMER_POSTAL_CODE).street(CUSTOMER_STREET).build();
		
		customerDto.setId(CUSTOMER_ID);
		
		return customerDto;
	}
}
