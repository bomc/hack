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
package de.bomc.poc.hrm.application;

import de.bomc.poc.hrm.interfaces.mapper.UserDto;

/**
 * The service handles the business logic for users.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 20.09.2019
 */
public interface UserService {

	UserDto createUser(UserDto userDto);
}
