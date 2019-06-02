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
package de.bomc.poc.test.auth.arq.usermanagement.mock;

import de.bomc.poc.api.mapper.transfer.RoleDTO;
import de.bomc.poc.auth.business.UsermanagementLocal;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.test.auth.arq.TransferDTOMockData;
import org.apache.log4j.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

/**
 * A business ejb for user management mock actions.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Stateless
@Local(UsermanagementLocal.class)
public class UsermanagementMockEJB implements UsermanagementLocal {

    /**
     * The logger
     */
    @Inject
    @LoggerQualifier
    private Logger logger;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RoleDTO> findAllRolesByUsername(final String username) {
        this.logger.debug("UsermanagementEJB#findAllRolesByUsername [user.username=" + username + "]");

        return TransferDTOMockData.getNotPersistedRoleDTOsWithId();
    }
}
