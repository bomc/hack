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
package de.bomc.poc.auth.rest.endpoint.v1.version.impl;

import de.bomc.poc.exception.handling.ApiVersionError;
import de.bomc.poc.exception.interceptor.ApiExceptionInterceptor;
import de.bomc.poc.exception.qualifier.ApiExceptionQualifier;
import de.bomc.poc.exception.web.WebJsonProcessingException;
import de.bomc.poc.os.version.VersionSingletonEJB;
import de.bomc.poc.auth.rest.endpoint.v1.version.VersionRestEndpoint;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.monitor.qualifier.PerformanceTrackingQualifier;

import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import java.util.Locale;

/**
 * Reads the current version of this project from 'version.properties'-file.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@ApiExceptionQualifier
@Interceptors(ApiExceptionInterceptor.class)
@PerformanceTrackingQualifier
public class VersionRestEndpointImpl implements VersionRestEndpoint {

    @Inject
    @LoggerQualifier
    private Logger logger;
    @EJB
    private VersionSingletonEJB versionSingetonEJB;

    @Override
    public Response getVersion() {
        this.logger.debug("VersionRESTResourceImpl#getVersion");

        final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

        try {
            jsonObjectBuilder.add("Version", this.versionSingetonEJB.getMavenVersion());
            jsonObjectBuilder.add("MavenBuildTimestamp", this.versionSingetonEJB.getMavenBuildTimestamp());
        } catch (Exception ex) {
            this.logger.error("VersionRESTResourceImpl#getVersion - Reading version and mavenBuildTimestamp failed!", ex);

            throw new WebJsonProcessingException(ApiVersionError.O10001, Locale.getDefault());
        }

        return Response.ok().entity(jsonObjectBuilder.build()).build();
    }
}
