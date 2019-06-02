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
package de.bomc.poc.auth.dao.jpa.role;

import de.bomc.poc.auth.dao.jpa.generic.JpaGenericDao;
import de.bomc.poc.auth.model.usermanagement.Grant;
import de.bomc.poc.auth.model.usermanagement.Role;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * A RoleDao offers functionality to find and modify {@link Role} entity classes.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
public interface JpaRoleManagementDao extends JpaGenericDao<Role> {

    /**
     * <pre>
     * Remove a collection of {@link Grant}s or <code>Grant</code>s from all Roles.
     * </pre>
     * @param id              The id of the role to remove the grants.
     * @param grants The collection of {@link Grant}s to be unassigned.
     * @return true the {@link Grant}s are successful removed otherwise false.
     */
    boolean removeAllGrantsFromRole(@NotNull Long id, @NotNull Set<? extends Grant> grants);
}
