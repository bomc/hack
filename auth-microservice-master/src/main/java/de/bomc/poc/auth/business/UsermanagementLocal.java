/**
 * Project: MY_POC_MICROSERVICE
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
package de.bomc.poc.auth.business;

import de.bomc.poc.api.mapper.transfer.RoleDTO;
import de.bomc.poc.auth.model.usermanagement.Role;

import java.util.List;

/**
 * A business interface for user management actions.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public interface UsermanagementLocal {

    /**
     * Find all {@link Role}s by given username.
     * @param username the given username.
     * @return A list with all roles or null if no roles are assigned to the user.
     */
    List<RoleDTO> findAllRolesByUsername(String username);
}
