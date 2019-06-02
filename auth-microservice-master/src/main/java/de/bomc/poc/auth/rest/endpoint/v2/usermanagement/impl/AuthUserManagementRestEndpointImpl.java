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
package de.bomc.poc.auth.rest.endpoint.v2.usermanagement.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.bomc.poc.api.mapper.transfer.RoleDTO;
import de.bomc.poc.auth.business.UsermanagementLocal;
import de.bomc.poc.auth.rest.endpoint.v2.usermanagement.AuthUserManagementRestEndpoint;
import de.bomc.poc.exception.interceptor.ApiExceptionInterceptor;
import de.bomc.poc.exception.qualifier.ApiExceptionQualifier;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.monitor.qualifier.PerformanceTrackingQualifier;

/**
 * The entry point for user management actions.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@ApiExceptionQualifier
@Interceptors(ApiExceptionInterceptor.class)
@PerformanceTrackingQualifier
public class AuthUserManagementRestEndpointImpl implements AuthUserManagementRestEndpoint {

    /**
     * The logger.
     */
    @Inject
    @LoggerQualifier
    private Logger logger;
    @EJB
    private UsermanagementLocal userManagementEJB;

    /**
     * {@inheritDoc}
     */
    @Override
    public Response findAllRolesByUsername(final String username) {
        this.logger.debug("AuthUserManagementRestEndpointImpl#findAllRolesByUsername [pathParam.username=" + username + "]");

        final List<RoleDTO> roleList = this.userManagementEJB.findAllRolesByUsername(username);

        final GenericEntity<List<RoleDTO>> entity = new GenericEntity<List<RoleDTO>>(roleList) {};

        return Response.status(Response.Status.OK)
                       .entity(entity)
                       .build();
    }
}
