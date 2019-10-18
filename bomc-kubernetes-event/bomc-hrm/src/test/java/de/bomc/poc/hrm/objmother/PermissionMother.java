/**
 * Project: hrm
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
package de.bomc.poc.hrm.objmother;

import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.EntityManagerProvider;
import de.bomc.poc.hrm.domain.model.PermissionEntity;

/**
 * A pattern for creating test instances by the object mother pattern.
 * <p/>
 * http://martinfowler.com/bliki/ObjectMother.html
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class PermissionMother extends SingleAlternateKeyObjectMother<PermissionEntity, String, PermissionMother> {

	/**
	 * Creates a new <code>PermissionMother</code>.
	 * 
	 * @param entityManagerProvider
	 *            the given entityManagerProvider
	 */
	public PermissionMother(final EntityManagerProvider entityManagerProvider, final String permissionNameValue) {
		super(entityManagerProvider, PermissionEntity.class, permissionNameValue, "name");
	}

	@Override
	protected void configureInstance(final PermissionEntity permissionEntity) {
		permissionEntity.setDescription(AbstractBaseUnit.PERMISSION_DESCRIPTION);
	}

	@Override
	protected PermissionEntity createInstance() {

		return new PermissionEntity(super.getAlternateKey());
	}
}
