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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bomc.poc.hrm.application.exception.AppErrorCodeEnum;
import de.bomc.poc.hrm.application.exception.AppRuntimeException;

/**
 * This enum describes the roles in the system..
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
public enum SecurityObjectRolesEnum {

	ADMIN_USER(1000, "ADMIN_USER", "A user with full access. "),
	SYSTEM_USER(2000, "SYSTEM_USER", "A user with restricted privileges. "),
	APPLICATION_USER(3000, "APPLICATION_USER", "A user with string restricted privileges. "),
	TEST_USER(4000, "TEST_USER", "A user that should be used for tests. ");
	//
	// Role description.
	private final int code;
	private final String roleStr;
	private final String shortDescription;
	// Constants
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityObjectRolesEnum.class);
	private static final String LOG_PREFIX = "SecurityObjectRolesEnum#";
	
	private SecurityObjectRolesEnum(final int code, final String roleStr, final String shortDescription) {
		this.code = code;
		this.roleStr = roleStr;
		this.shortDescription = shortDescription;
	}

	/**
	 * Returns the numerical value for this role description.
	 *
	 * @return the code as an unique {@code int} value.
	 */
	public int getCode() {
		return this.code;
	}

	public String getRoleString() {
		return this.roleStr;
	}
	
	public String getShortDescription() {
		return this.shortDescription;
	}

	/**
	 * Returns the <code>SecurityObjectRolesEnum</code> to the depending int value.
	 * 
	 * @param code the given int value.
	 * @return the <code>SecurityObjectRolesEnum</code> to the depending int value.
	 */
	public static SecurityObjectRolesEnum fromInt(final int code) {
		final SecurityObjectRolesEnum[] enums = values();

		for (int i = 0; i < enums.length; i++) {
			final SecurityObjectRolesEnum securityObjectRolesEnum = enums[i];
			if (securityObjectRolesEnum.getCode() == code) {
				return securityObjectRolesEnum;
			}
		}

		final String errMsg = LOG_PREFIX + "fromInt - could map code to enum: " + code
				+ ", errorCode: " + AppErrorCodeEnum.APP_ROLE_HANDLING_MAPPING_10609;
		
		final AppRuntimeException appRuntimeException = new AppRuntimeException(errMsg, AppErrorCodeEnum.APP_ROLE_HANDLING_MAPPING_10609);
		LOGGER.error(appRuntimeException.stackTraceToString());
		
		throw appRuntimeException;
	}

	/**
	 * Returns the <code>SecurityObjectRolesEnum</code> on the depending string value.
	 * 
	 * @param roleStr the given string value.
	 * @return the <code>SecurityObjectRolesEnum</code> on the depending string value.
	 */
	public static SecurityObjectRolesEnum fromRoleString(final String roleStr) {
		try {
			return Enum.valueOf(SecurityObjectRolesEnum.class, roleStr);
		} catch (final IllegalArgumentException e) {
			
			final String errMsg = LOG_PREFIX + "fromRoleString - could not parse roleStr to code: " + roleStr
					+ ", set errorCode: " + AppErrorCodeEnum.APP_ROLE_HANDLING_MAPPING_10609;
			
			final AppRuntimeException appRuntimeException = new AppRuntimeException(errMsg, AppErrorCodeEnum.APP_ROLE_HANDLING_MAPPING_10609);
			LOGGER.error(appRuntimeException.stackTraceToString());
			
			throw appRuntimeException;
		}
	}
	
	@Override
	public String toString() {
		// Do not overwrite this method. It has an impact to the enum.name()
		// method.
		return super.toString();
	}
}
