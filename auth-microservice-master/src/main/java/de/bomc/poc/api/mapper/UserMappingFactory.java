/**
 * Project: MY_POC
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.api.mapper;

import de.bomc.poc.auth.model.usermanagement.User;

/**
 * A factory class for reverse mapping used by mapstruct. This class creates a
 * <code>User</code> instance with a dummy username, which is overwritten during
 * mapping phase with value form the <code>UserDTO</code>. This is necessary
 * because mapstruct is using the default constructor for creating instances.
 * But the default constructor is 'protected' and reflection is not used by
 * mapstruct.
 * </p>
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class UserMappingFactory {
	// {@link User} has a constructor with a username-parameter, annotated with
	// a &#064;NotNull constraint. During creation of a User instance, a
	// 'dummy'-username has to be set to fulfill this constraint.
	private final static String USERNAME_MAPSTRUCT_INITIALIZER = "username_mapstruct_initializer";

	public User createUser() {
		User user = new User(UserMappingFactory.USERNAME_MAPSTRUCT_INITIALIZER);

		return user;
	}
}
