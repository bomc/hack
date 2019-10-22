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
package de.bomc.poc.hrm.domain.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.application.exception.AppRuntimeException;
import de.bomc.poc.hrm.objmother.PermissionMother;
import de.bomc.poc.hrm.objmother.RoleMother;
import de.bomc.poc.hrm.objmother.UserDetailsMother;

/**
 * Tests the customer entity.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserEntityTest extends AbstractBaseUnit {

	private static final String LOG_PREFIX = "UserEntityTest#";
	private static final Logger LOGGER = LoggerFactory.getLogger(UserEntityTest.class);

	// _______________________________________________
	// Test constants
	// -----------------------------------------------

	// _______________________________________________
	// Member variables
	// -----------------------------------------------

	// _______________________________________________
	// Test preparation
	// -----------------------------------------------
	@Before
	public void initialize() {
		LOGGER.debug(LOG_PREFIX + "initialize");

		this.entityManager1 = this.emProvider1.getEntityManager();

		assertThat(this.entityManager1, notNullValue());
	}

	// _______________________________________________
	// Test methods
	// -----------------------------------------------
	@Test
	public void test010_createUser_pass() {
		LOGGER.info(LOG_PREFIX + "test010_createUser_pass");

		// GIVEN
		final UserEntity userEntity = createNewUserEntityWithUsernameAndPassword(USER_USER_NAME, USER_PASSWORD);
		userEntity.setCreateUser(USER_USER_NAME);

		// WHEN
		this.emProvider1.tx().begin();

		this.entityManager1.persist(userEntity);

		this.emProvider1.tx().commit();

		// THEN
		assertThat(userEntity.getId(), notNullValue());
	}

	@Test
	public void test020_createUserWithUserDetails_pass() {
		LOGGER.info(LOG_PREFIX + "test020_createUserWithUserDetails_pass");

		// GIVEN
		final UserDetailsEntity userDetailsEntity = UserDetailsMother.instance();

		final UserEntity userEntity = new UserEntity(USER_USER_NAME);
		userEntity.setNewPassword(USER_PASSWORD);
		userEntity.setUserDetails(userDetailsEntity);

		// WHEN
		this.emProvider1.tx().begin();

		this.entityManager1.persist(userEntity);

		this.emProvider1.tx().commit();

		// THEN
		assertThat(userEntity.getId(), notNullValue());
		assertThat(userEntity.getUserDetails(), notNullValue());
		assertThat(userEntity.getUserDetails().getComment(), equalTo(USER_DETAILS_COMMENT));
	}

	@Test
	public void test030_registerUser_pass() {
		LOGGER.info(LOG_PREFIX + "test030_registerUser_pass");

		// 1. Create a user with username and password.
		final UserEntity userEntity = new UserEntity(USER_USER_NAME, USER_PASSWORD);

		this.emProvider1.tx().begin();
		this.entityManager1.persist(userEntity);
		this.emProvider1.tx().commit();

		assertThat(userEntity.getId(), notNullValue());

		// 2. Add user details.
		final UserDetailsEntity userDetailsEntity = UserDetailsMother.instance();

		this.emProvider1.tx().begin();
		userEntity.setUserDetails(userDetailsEntity);
		final UserEntity retUserEntity = this.entityManager1.merge(userEntity);
		this.emProvider1.tx().commit();
		
		assertThat(retUserEntity.getUserDetails(), notNullValue());

		// 3. Create role and permission.
		final PermissionMother permissionMother = new PermissionMother(this.emProvider1, PERMISSION_NAME);
		final PermissionEntity permissionEntity = permissionMother.instance();

		final RoleMother roleMother = new RoleMother(this.emProvider1, ROLE_NAME);
		final RoleEntity roleEntity = roleMother.instance();

		this.emProvider1.tx().begin();
		roleEntity.addPermission(permissionEntity);
		this.emProvider1.tx().commit();

		assertThat(roleEntity.getPermissions().stream().count(), equalTo(1L));

		// 4. Add role to user.
		this.emProvider1.tx().begin();
		retUserEntity.addRole(roleEntity);
		this.entityManager1.persist(retUserEntity);
		this.entityManager1.flush();
		this.emProvider1.tx().commit();

		// Do asserts.
		assertThat(retUserEntity.getRoles().stream().count(), equalTo(1L));
	}

	@Test
	public void test040_isExpired_pass() {
		LOGGER.info(LOG_PREFIX + "test040_isExpired_pass");

		final UserEntity userEntity = new UserEntity(USER_USER_NAME, USER_PASSWORD);

		this.emProvider1.tx().begin();
		this.entityManager1.persist(userEntity);
		this.emProvider1.tx().commit();

		assertThat(userEntity.getExpirationDate(),
				lessThan(LocalDateTime.now().plusDays(UserEntity.EXPIRATION_OFFSET_DAYS + 1)));
	}

	@Test
	public void test050_passwordFlow_pass() {
		LOGGER.info(LOG_PREFIX + "test050_passwordFlow_pass");
		
		this.thrown.expect(AppRuntimeException.class);
		
		final UserEntity userEntity = new UserEntity(USER_USER_NAME, USER_PASSWORD);

		this.emProvider1.tx().begin();
		this.entityManager1.persist(userEntity);
		this.emProvider1.tx().commit();

		// Check last password change.
		assertThat(userEntity.getLastPasswordChange(), nullValue());
		
		// Set new password.
		userEntity.setNewPassword(USER_NEW_PASSWORD);
		assertThat(userEntity.getLastPasswordChange(), notNullValue());
		
		// Set same password.
		try {
			userEntity.setNewPassword(USER_NEW_PASSWORD);
			
			fail("Should not be reached.");
		} catch(final AppRuntimeException ex) {
			LOGGER.info(LOG_PREFIX + "test050_passwordFlow_pass");
		}
		
		final int countOfPasswordChanges = 5;
		
		// Set 5 times password.
		for (int i = 0; i < countOfPasswordChanges; i++) {
			userEntity.setNewPassword(USER_NEW_PASSWORD + i);
		}
		
		// Set a already available password in the ring buffer.
		userEntity.setNewPassword(USER_NEW_PASSWORD + (countOfPasswordChanges - 1));
	}

}
