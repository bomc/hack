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
package de.bomc.poc.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;

import de.bomc.poc.logger.qualifier.LoggerQualifier;

/**
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Provider
@ServerInterceptor
public class ConstraintViolationMapper implements ExceptionMapper<ConstraintViolationException> {

    @Inject
    @LoggerQualifier
    private Logger logger;

    @Context
    protected Request request;

    @Override
    public Response toResponse(ConstraintViolationException ex) {
        this.logger.debug("ConstraintViolationMapper#toResponse");

        final Set<ConstraintViolation<?>> constViolations = ex.getConstraintViolations();

        final List<ConstraintViolationEntry> errorList = new ArrayList<>();

        constViolations.forEach(constraintViolation -> {
            ConstraintViolationEntry constraintViolationEntry = new ConstraintViolationEntry(constraintViolation);
            errorList.add(constraintViolationEntry);
        });

        final GenericEntity<List<ConstraintViolationEntry>> entity = new GenericEntity<List<ConstraintViolationEntry>>(errorList) {};

        return Response.status(Response.Status.PRECONDITION_FAILED)
                       .entity(entity)
                       .build();
    }
}
