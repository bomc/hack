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
package de.bomc.poc.test.auth.arq;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.bomc.poc.api.mapper.transfer.GrantDTO;
import de.bomc.poc.api.mapper.transfer.RoleDTO;

/**
 * A class that creates dto transfer data.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class TransferDTOMockData {

    /**
     * @return a list with <code>RoleDTO</code>s. The id's are set, but the instances are not stored in db.
     */
    public static List<RoleDTO> getNotPersistedRoleDTOsWithId() {
        final GrantDTO grantDTO1 = new GrantDTO("read", "read access");
        grantDTO1.setId(1l);
        final GrantDTO grantDTO2 = new GrantDTO("write", "write access");
        grantDTO2.setId(2l);
        final GrantDTO grantDTO3 = new GrantDTO("delete", "delete access");
        grantDTO3.setId(3l);

        final RoleDTO roleDTO1 = new RoleDTO("SystemUser", "A system user");
        roleDTO1.setId(1l);
        final RoleDTO roleDTO2 = new RoleDTO("ExternalUser", "a external user");
        roleDTO2.setId(2l);

        roleDTO1.setGrantDTOList(Arrays.asList(grantDTO1, grantDTO2, grantDTO3));
        roleDTO2.setGrantDTOList(Collections.singletonList(grantDTO1));

        return Arrays.asList(roleDTO1, roleDTO2);
    }
}
