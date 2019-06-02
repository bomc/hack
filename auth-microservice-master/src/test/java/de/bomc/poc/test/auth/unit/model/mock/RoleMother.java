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

import de.bomc.poc.auth.model.usermanagement.Role;
import de.bomc.poc.test.auth.unit.EntityManagerProvider;

/**
 * A pattern for creating test instances by the object mother pattern.
 * <p/>
 * http://martinfowler.com/bliki/ObjectMother.html
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class RoleMother extends SingleAlternateKeyObjectMother<Role, String, RoleMother> {

	/**
	 * Creates a new <code>RoleMother</code>.
	 * 
	 * @param entityManagerProvider
	 *            the given entityManagerProvider
	 */
	public RoleMother(final EntityManagerProvider entityManagerProvider, final String rolenameValue) {
		super(entityManagerProvider, Role.class, rolenameValue, "name");
	}

	@Override
	protected void configureInstance(final Role role) {
		role.setDescription("my_role_description");
	}

	@Override
	protected Role createInstance() {
		final Role role = new Role(super.getAlternateKey());
		
		return role;
	}
}
