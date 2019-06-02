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

import de.bomc.poc.auth.model.usermanagement.Grant;

/**
 * A factory class for reverse mapping used by mapstruct. This class creates a
 * <code>Grant</code> instance with a dummy name, which is overwritten during
 * mapping phase with value form the <code>GrantDTO</code>. This is necessary
 * because mapstruct is using the default constructor for creating instances.
 * But the default constructor is 'protected' and reflection is not used by
 * mapstruct.
 * </p>
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class GrantMappingFactory {
	// {@link Grant} has a constructor with a name-parameter, annotated with
	// a &#064;NotNull constraint. During creation of a Grant instance, a
	// 'dummy'-name has to be set to fulfill this constraint.
	private final static String GRANTNAME_MAPSTRUCT_INITIALIZER = "grantname_mapstruct_initializer";

	public Grant createGrant() {

		return new Grant(GrantMappingFactory.GRANTNAME_MAPSTRUCT_INITIALIZER);
	}
}
