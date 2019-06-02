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

import de.bomc.poc.auth.model.usermanagement.Role;

/**
 * A factory class for reverse mapping used by mapstruct. This class creates a
 * <code>Role</code> instance with a dummy name, which is overwritten during
 * mapping phase with value form the <code>RoleDTO</code>. This is necessary
 * because mapstruct is using the default constructor for creating instances.
 * But the default constructor is 'protected' and reflection is not used by
 * mapstruct.
 * </p>
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class RoleMappingFactory {
	// {@link Role} has a constructor with a name-parameter, annotated with
	// a &#064;NotNull constraint. During creation of a Role instance, a
	// 'dummy'-name has to be set to fulfill this constraint.
	private final static String ROLENAME_MAPSTRUCT_INITIALIZER = "rolename_mapstruct_initializer";

	public Role createRole() {
		Role role = new Role(RoleMappingFactory.ROLENAME_MAPSTRUCT_INITIALIZER);

		return role;
	}
}
