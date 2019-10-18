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

import java.io.Serializable;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A SystemUserEntity is granted with all privileges and omits all defined security
 * constraints. Whenever a SystemUserEntity logs in, the SystemUserEntity is assigned to a
 * virtual <code>RoleEntity</code> with the name ROLE_SYSTEM. Furthermore this kind of
 * <code>RoleEntity</code> is immutable and it is not allowed for the SystemUserEntity to
 * change the <code>UserDetailsEntity</code> or <code>UserPasswordEntity</code>. Changing
 * the <code>UserPasswordEntity</code> has to be done in the application configuration
 * when the project is setup.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
// JPA
@Entity
public class SystemUserEntity extends UserEntity implements Serializable {

	private static final String LOG_PREFIX = "SystemUserEntity#";
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemUserEntity.class.getName());
	private static final long serialVersionUID = -7575215406745881912L;

	/**
	 * The defined fullname of the system user. Default {@value} .
	 */
	public static final String SYSTEM_USERNAME = "BOMC_SYSTEM_USER";
	/**
	 * The virtual <code>RoleEntity</code> of the SystemUserEntity.
	 */
	public static final String SYSTEM_ROLE_NAME = "ROLE_SYSTEM";

	/**
	 * Create a new SystemUserEntity.
	 */
	protected SystemUserEntity() {
		// Used by JPA provider.
		//
		LOGGER.debug(LOG_PREFIX + "co");
	}

	/**
	 * Create a new SystemUserEntity.
	 * 
	 * @param username
	 *            SystemUserEntity's username
	 * @param password
	 *            SystemUserEntity's password
	 */
	public SystemUserEntity(final String username, @NotBlank final String password) {
		super(username, password);

		LOGGER.debug(LOG_PREFIX + "co [username=" + username + ", password=" + password + ", SYSTEM_USERNAME="
				+ SYSTEM_USERNAME + "]");

		setFullname(SYSTEM_USERNAME);
	}

	/**
	 * Check whether <code>user</code> is the system user.
	 * 
	 * @param userEntity
	 *            The user to check
	 * @return <code>true</code> if user is the system user, otherwise
	 *         <code>false</code>
	 */
	public static boolean isSuperUser(final UserEntity userEntity) {
		LOGGER.debug("SystemUserEntity.isSuperUser [isSuperUser=" + (userEntity instanceof SystemUserEntity) + "]");

		return (userEntity instanceof SystemUserEntity);
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
