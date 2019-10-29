/**
 * Project: hrm
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

import javax.persistence.EntityManager;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import de.bomc.poc.hrm.domain.model.CustomerEntity;
import de.bomc.poc.hrm.domain.model.PermissionEntity;
import de.bomc.poc.hrm.domain.model.RoleEntity;
import de.bomc.poc.hrm.domain.model.UserDetailsEntity;
import de.bomc.poc.hrm.domain.model.UserEntity;
import de.bomc.poc.hrm.interfaces.mapper.CustomerDto;

/**
 * A abstract class for unit tests.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 07.05.2019
 */
public abstract class AbstractBaseUnit {

	// _______________________________________________
	// JPA
	// -----------------------------------------------
    public static final String PERSISTENCE_UNIT_NAME = "hrm-pu";
    
    @Rule
    public EntityManagerProvider emProvider1 = EntityManagerProvider.persistenceUnit(PERSISTENCE_UNIT_NAME);
    @Rule
    public EntityManagerProvider emProvider2 = EntityManagerProvider.persistenceUnit(PERSISTENCE_UNIT_NAME);
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    protected EntityManager entityManager1;
    protected EntityManager entityManager2;
    
	// _______________________________________________
	// Constants
	// -----------------------------------------------
	public static final Long CUSTOMER_ID = 42L;
	public static final String CUSTOMER_CITY = "myCity";
	public static final String CUSTOMER_COUNTRY = "CH";
	public static final LocalDate CUSTOMER_DATE_OF_BIRTH = LocalDate.of(2019, Month.JUNE, 3);
	public static final String CUSTOMER_FIRST_NAME = "myFirstName";
	public static final String CUSTOMER_LAST_NAME = "myLastName";
	// _______________________________________________
	// see src/test/resources/META-INF/data-customer-h2.sql
	// -----------------------------------------------
	public static final String CUSTOMER_E_MAIL = "bomc_script_1@bomc.org";
	public static final String CUSTOMER_HOUSE_NUMBER = "42";
	public static final String CUSTOMER_PHONE_NUMBER = "042-424242";
	public static final String CUSTOMER_POSTAL_CODE = "4242";
	public static final String CUSTOMER_STREET = "myStreet";
	public static final String CUSTOMER_CREATE_USER = "myCreateUser";

	public static final Long USER_ID = 42L;
	public static final String USER_USER_NAME = "bomc_user";
	public static final String USER_PASSWORD = "bomc_password";
	public static final String USER_NEW_PASSWORD = "bomc_new_password";
	public static final String USER_FULLNAME = "bomc fullname";
	
	public static final String USER_DETAILS_COMMENT = "userDetailsComment";
	public static final String USER_DETAILS_PHONE_NO = "+49 89 123456";
	public static final byte[] USER_DETAILS_IMAGE = "bomc-image".getBytes();
	public static final UserDetailsEntity.SEX USER_DETAIL_SEX = UserDetailsEntity.SEX.MALE;
	
	public static final String PERMISSION_NAME = "read";
	public static final String PERMISSION_DESCRIPTION = "Describes the permission";
	
	public static final String ROLE_NAME = "admin";
	public static final String ROLE_DESCRIPTION = "Describes the role";
	public static final boolean ROLE_IS_IMMUTABLE = true;
	
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
	
	public static PermissionEntity createPermissionEntity() {
		final PermissionEntity permissionEntity = new PermissionEntity(PERMISSION_NAME, PERMISSION_DESCRIPTION);
		
		return permissionEntity;
	}

	public static RoleEntity createRoleEntity() {
		final RoleEntity roleEntity = new RoleEntity(ROLE_NAME, ROLE_DESCRIPTION);
		
		return roleEntity;
	}
	
	public static UserEntity createNewUserEntityWithUsernameAndPassword(final String username, final String password) {
		final UserEntity user = new UserEntity(username);
		user.setNewPassword(password);
		
		return user;
	}
}
