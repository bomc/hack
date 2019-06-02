package de.bomc.poc.model.test;

import de.bomc.poc.model.account.User;
import de.bomc.poc.model.mock.SingleAlternateKeyObjectMother;

/**
 * A pattern for creating test instances by the object mother pattern.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
public class UserMother extends SingleAlternateKeyObjectMother<User, String, UserMother> {

	private static final String ALTERNATE_ATTRIBUTE_VALUE = "username";
	
    /**
     * Creates a new <code>UserMother</code>.
     * @param entityManagerProvider the given entityManagerProvider
     */
    public UserMother(final EntityManagerProvider entityManagerProvider, final String usernameValue) {
        super(entityManagerProvider, User.class, usernameValue, ALTERNATE_ATTRIBUTE_VALUE);
    }

    @Override
    protected void configureInstance(final User user) {
    	// Fill here instance with default data.
        user.setUsername(user.getUsername());
    }

    @Override
    protected User createInstance() {
        final User user = new User(super.getAlternateKey());

        return user;
    }
}
