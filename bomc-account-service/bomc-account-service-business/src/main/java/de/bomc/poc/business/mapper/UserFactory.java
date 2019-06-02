package de.bomc.poc.business.mapper;

import de.bomc.poc.model.account.User;

/**
 * <pre>
 * A factory for creating a <code>Person</code> instance.
 * This is necessary, because the default constructor of Person is not visible.
 * So a instance with the given constructor has to be created. 
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
public class UserFactory {
	
	private static final String DEFAULT_USER_NAME = "DEFAULT_USER_NAME";

	public static User createUser() {

		final User user = new User(DEFAULT_USER_NAME);

		return user;
	}
}
