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
package de.bomc.poc.auth.rest.endpoint.v1.runtime.impl;

import de.bomc.poc.auth.rest.endpoint.v1.runtime.RuntimeRestEndpoint;
import de.bomc.poc.exception.interceptor.ApiExceptionInterceptor;
import de.bomc.poc.exception.qualifier.ApiExceptionQualifier;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.monitor.qualifier.PerformanceTrackingQualifier;
import de.bomc.poc.os.runtime.RuntimeInfoSingletonEJB;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;

/**
 * A rest endpoint that delivers some runtime data.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@ApiExceptionQualifier
@Interceptors(ApiExceptionInterceptor.class)
@PerformanceTrackingQualifier
public class RuntimeRestEndpointImpl implements RuntimeRestEndpoint {

    private static final String LOG_PREFIX = "RuntimeRestEndpointImpl#";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @EJB
    private RuntimeInfoSingletonEJB runtimeInfoSingletonEJB;

    @Override
    public Response availableHeap() {
        this.logger.debug("RuntimeRestEndpointImpl#availableHeap");

        final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("Available memory in mb", this.runtimeInfoSingletonEJB.availableMemoryInMB())
                         .add("Used memory in mb", this.runtimeInfoSingletonEJB.usedMemoryInMb());

        return Response.ok()
                       .entity(jsonObjectBuilder.build())
                       .build();
    }

    @Override
    public Response osInfo() {
        this.logger.debug(LOG_PREFIX + "osInfo");

        final JsonObject jsonObject = this.runtimeInfoSingletonEJB.osInfo();

        return Response.ok()
                       .entity(jsonObject)
                       .build();
    }

    @Override
    public Response nodeName() {
        this.logger.debug(LOG_PREFIX + "nodeName");

        final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("node.name", this.runtimeInfoSingletonEJB.getNodeName());

        return Response.ok()
                       .entity(jsonObjectBuilder.build())
                       .build();
    }
}
