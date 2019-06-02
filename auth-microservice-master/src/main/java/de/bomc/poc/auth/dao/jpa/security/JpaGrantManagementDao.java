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
package de.bomc.poc.auth.dao.jpa.security;

import de.bomc.poc.auth.dao.jpa.generic.JpaGenericDao;
import de.bomc.poc.auth.model.usermanagement.Grant;

/**
 * A GrantManagementDao offers functionality to find and modify {@link Grant} entity classes.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
public interface JpaGrantManagementDao extends JpaGenericDao<Grant> {
    //
}
