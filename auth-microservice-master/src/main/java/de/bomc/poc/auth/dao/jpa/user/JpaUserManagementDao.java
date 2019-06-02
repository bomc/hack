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
package de.bomc.poc.auth.dao.jpa.user;

import de.bomc.poc.auth.dao.jpa.generic.JpaGenericDao;
import de.bomc.poc.auth.model.usermanagement.User;
import de.bomc.poc.auth.model.usermanagement.UserPassword;

/**
 * An JpaUserManagementDao offers functionality regarding {@link User} entity classes.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
public interface JpaUserManagementDao extends JpaGenericDao<User> {

    /**
     * Find an {@link User} by his userName and password.
     * @param userPassword Stores the userName and password.
     * @return The {@link User} if found, otherwise might be <code>null</code>
     */
    User findByNameAndPassword(UserPassword userPassword);
}
