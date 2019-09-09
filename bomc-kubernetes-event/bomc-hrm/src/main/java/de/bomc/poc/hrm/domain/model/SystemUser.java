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
 * A SystemUser is granted with all privileges and omits all defined security
 * constraints. Whenever a SystemUser logs in, the SystemUser is assigned to a
 * virtual <code>Role</code> with the name ROLE_SYSTEM. Furthermore this kind of
 * <code>Role</code> is immutable and it is not allowed for the SystemUser to
 * change the <code>UserDetails</code> or <code>UserPassword</code>. Changing
 * the <code>UserPassword</code> has to be done in the application configuration
 * when the project is setup.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
//JPA
@Entity
public class SystemUser extends User implements Serializable {

	private static final String LOG_PREFIX = "SystemUser#";
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemUser.class.getName());
	private static final long serialVersionUID = -7575215406745881912L;

	/**
	 * The defined fullname of the system user. Default {@value} .
	 */
	public static final String SYSTEM_USERNAME = "BOMC_SYSTEM_USER";
	/**
	 * The virtual <code>Role</code> of the SystemUser.
	 */
	public static final String SYSTEM_ROLE_NAME = "ROLE_SYSTEM";

	/**
	 * Create a new SystemUser.
	 */
	protected SystemUser() {
		// Used by JPA provider.
		//
		LOGGER.debug(LOG_PREFIX + "co");
	}

	/**
	 * Create a new SystemUser.
	 * 
	 * @param username
	 *            SystemUser's username
	 * @param password
	 *            SystemUser's password
	 */
	public SystemUser(final String username, @NotBlank final String password) {
		super(username, password);

		LOGGER.debug(LOG_PREFIX + "co [username=" + username + ", password=" + password + ", SYSTEM_USERNAME="
				+ SYSTEM_USERNAME + "]");

		setFullname(SYSTEM_USERNAME);
	}

	/**
	 * Check whether <code>user</code> is the system user.
	 * 
	 * @param user
	 *            The user to check
	 * @return <code>true</code> if user is the system user, otherwise
	 *         <code>false</code>
	 */
	public static boolean isSuperUser(final User user) {
		LOGGER.debug("SystemUser.isSuperUser [isSuperUser=" + (user instanceof SystemUser) + "]");

		return (user instanceof SystemUser);
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
