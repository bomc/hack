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
package de.bomc.poc.auth.business.service;

import de.bomc.poc.api.mapper.UserManagementMapper;
import de.bomc.poc.api.mapper.transfer.RoleDTO;
import de.bomc.poc.auth.business.UsermanagementLocal;
import de.bomc.poc.auth.dao.jpa.qualifier.JpaDao;
import de.bomc.poc.auth.dao.jpa.role.JpaRoleManagementDao;
import de.bomc.poc.auth.model.usermanagement.Role;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Stream;

/**
 * A business interface for user management actions.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Stateless
@Local(UsermanagementLocal.class)
public class UsermanagementEJB implements UsermanagementLocal {
    private static final String LOG_PREFIX = "UsermanagementEJB#";
    /**
     * The logger
     */
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Inject
    @JpaDao
    private JpaRoleManagementDao jpaRoleManagementDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RoleDTO> findAllRolesByUsername(final String username) {
        this.logger.debug(LOG_PREFIX + "findAllRolesByUsername [user.username=" + username + "]");

        final Stream<String> streamOfUsernames = Stream.of(username);
        final String[] values = streamOfUsernames.toArray(String[]::new);

        final List<Role> roleList = this.jpaRoleManagementDao.findByPositionalParameters(Role.NQ_FIND_ALL_BY_USERNAME, (Object[])values);

        // DTO object mapping.
        return UserManagementMapper.INSTANCE.map_RoleList_To_DTOList(roleList);
    }
}
