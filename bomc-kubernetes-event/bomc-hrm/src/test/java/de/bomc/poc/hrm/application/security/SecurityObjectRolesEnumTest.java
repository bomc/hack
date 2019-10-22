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
package de.bomc.poc.hrm.application.security;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bomc.poc.hrm.application.exception.AppRuntimeException;

/**
 * Test the {@link SecurityObjectRolesEnum} handling.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 07.05.2019
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SecurityObjectRolesEnumTest {
	
	private static final String LOG_PREFIX = "SecurityObjectRolesEnumTest#";
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityObjectRolesEnumTest.class.getName());

	/* --------------------- member variables ----------------------- */
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void test010_createEnum_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_createEnum_pass");
		
		final SecurityObjectRolesEnum e = SecurityObjectRolesEnum.ADMIN_USER;
		
		assertThat(e.getCode(), equalTo(1000));
		assertThat(e.getRoleString(), equalTo("ADMIN_USER"));
		assertThat(e.getShortDescription(), equalTo("A user with full access. "));
	}
	
	@Test
	public void test020_fromInt_pass() {
		LOGGER.debug(LOG_PREFIX + "test020_fromInt_pass");
		
		final SecurityObjectRolesEnum e = SecurityObjectRolesEnum.fromInt(SecurityObjectRolesEnum.ADMIN_USER.getCode());
		
		assertThat(e, equalTo(SecurityObjectRolesEnum.ADMIN_USER));
	}
	
	@Test
	public void test030_fromInt_fail() {
		LOGGER.debug(LOG_PREFIX + "test030_fromInt_fail");
		
		thrown.expect(AppRuntimeException.class);
		
		SecurityObjectRolesEnum.fromInt(1);
	}
	
	@Test
	public void test040_fromRoleString_pass() {
		LOGGER.debug(LOG_PREFIX + "test040_fromRoleString_pass");
		
		final SecurityObjectRolesEnum e = SecurityObjectRolesEnum.fromRoleString(SecurityObjectRolesEnum.ADMIN_USER.getRoleString());
		
		assertThat(e, equalTo(SecurityObjectRolesEnum.ADMIN_USER));
	}
	
	@Test
	public void test050_fromRoleString_fail() {
		LOGGER.debug(LOG_PREFIX + "test050_fromRoleString_fail");
		
		thrown.expect(AppRuntimeException.class);
		
		SecurityObjectRolesEnum.fromRoleString("xy");
	}
}
