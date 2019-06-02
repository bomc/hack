/**
 * Project: MY_POC
 * <p/>
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
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.auth.unit.model.mock;

import java.time.LocalDateTime;

import de.bomc.poc.auth.model.usermanagement.User;
import de.bomc.poc.test.auth.unit.EntityManagerProvider;

/**
 * A pattern for creating test instances by the object mother pattern.
 * <p/>
 * http://martinfowler.com/bliki/ObjectMother.html
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class UserMother extends SingleAlternateKeyObjectMother<User, String, UserMother> {

	/**
	 * Creates a new <code>UserMother</code>.
	 * 
	 * @param entityManagerProvider
	 *            the given entityManagerProvider
	 */
	public UserMother(final EntityManagerProvider entityManagerProvider, final String usernameValue) {
		super(entityManagerProvider, User.class, usernameValue, "username");
	}

	@Override
	protected void configureInstance(final User user) {
		user.setEnabled(true);
		user.setExpirationDate(LocalDateTime.now().plusDays(3l));
		user.setExternalUser(false);
		user.setFullname("my_fullname");
		user.setLocked(false);
		user.setNewPassword("tzdbmm$1234");
	}

	@Override
	protected User createInstance() {
		final User user = new User(super.getAlternateKey());

		return user;
	}
}
