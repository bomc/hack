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
package de.bomc.poc.hrm.application.service;

import de.bomc.poc.hrm.interfaces.mapper.UserDto;

/**
 * The service handles the business logic for users.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 20.09.2019
 */
public interface UserService {

	/**
	 * Creates a user with his or her corresponding roles and permissions.
	 * 
	 * @param userDto the given user data.
	 * @return the created user with created id (technical id).
	 */
	UserDto createUser(UserDto userDto);

	/**
	 * Find user by id (technical id).
	 * 
	 * @param id the given technical id.
	 * @return a user that corresponds to the given id.
	 */
	UserDto findById(Long id);

	/**
	 * Find user by username (email).
	 * 
	 * @param username the given username.
	 * @return a user that corresponds to the given username.
	 */
	UserDto findByUsername(final String username);

	/**
	 * Check if the user has permission.
	 * 
	 * @param username   the unique username.
	 * @param permission the given permission to check.
	 * 
	 * @return true the user has permission otherwise false.
	 */
	Boolean hasUserPermission(String username, String permission);

}
