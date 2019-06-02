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

import de.bomc.poc.auth.model.usermanagement.Grant;
import de.bomc.poc.test.auth.unit.EntityManagerProvider;

/**
 * A pattern for creating test instances by the object mother pattern.
 * <p/>
 * http://martinfowler.com/bliki/ObjectMother.html
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class GrantMother extends SingleAlternateKeyObjectMother<Grant, String, GrantMother> {

	/**
	 * Creates a new <code>GrantMother</code>.
	 * 
	 * @param entityManagerProvider
	 *            the given entityManagerProvider
	 */
	public GrantMother(final EntityManagerProvider entityManagerProvider, final String grantnameValue) {
		super(entityManagerProvider, Grant.class, grantnameValue, "name");
	}

	@Override
	protected void configureInstance(final Grant grant) {
		grant.setDescription("my_grant_description");
	}

	@Override
	protected Grant createInstance() {

		return new Grant(super.getAlternateKey());
	}
}
