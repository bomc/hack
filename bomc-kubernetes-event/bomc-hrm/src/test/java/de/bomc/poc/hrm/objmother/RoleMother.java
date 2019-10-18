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
package de.bomc.poc.hrm.objmother;

import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.EntityManagerProvider;
import de.bomc.poc.hrm.domain.model.RoleEntity;

/**
 * A pattern for creating test instances by the object mother pattern.
 * <p/>
 * http://martinfowler.com/bliki/ObjectMother.html
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class RoleMother extends SingleAlternateKeyObjectMother<RoleEntity, String, RoleMother> {

	/**
	 * Creates a new <code>RoleMother</code>.
	 * 
	 * @param entityManagerProvider
	 *            the given entityManagerProvider
	 */
	public RoleMother(final EntityManagerProvider entityManagerProvider, final String roleNameValue) {
		super(entityManagerProvider, RoleEntity.class, roleNameValue, "name");
	}
		
	@Override
	protected void configureInstance(final RoleEntity roleEntity) {
		roleEntity.setDescription(AbstractBaseUnit.ROLE_DESCRIPTION);
	}

	@Override
	protected RoleEntity createInstance() {

		return new RoleEntity(super.getAlternateKey());
	}
}
