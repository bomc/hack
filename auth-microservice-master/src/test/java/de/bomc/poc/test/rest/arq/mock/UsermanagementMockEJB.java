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
package de.bomc.poc.test.rest.arq.mock;

import de.bomc.poc.api.mapper.transfer.RoleDTO;
import de.bomc.poc.auth.business.UsermanagementLocal;

import javax.ejb.Local;
import javax.ejb.Stateless;
import java.util.Collections;
import java.util.List;

/**
 * Mocks the <code>UsermanagementEJB</code>.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@Stateless
@Local(UsermanagementLocal.class)
public class UsermanagementMockEJB implements UsermanagementLocal {

    @Override
    public List<RoleDTO> findAllRolesByUsername(final String username) {
        // Create a persisted object.
        final RoleDTO roleDTO = new RoleDTO("myRole", "myDescription");
        roleDTO.setId(1L);

        return Collections.singletonList(roleDTO);
    }
}
