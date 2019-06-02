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
package de.bomc.poc.auth.rest.endpoint.v1.overview;

import de.bomc.poc.auth.rest.application.JaxRsActivator;
import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.core.Dispatcher;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A resource that displays a list of available endpoints.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Path(JaxRsActivator.OVERVIEW_ENDPOINT_PATH)
public interface ApiOverviewRestEndpoint {
    /**
     * A {@code String} constant representing json v1 "{@value #MEDIA_TYPE_JSON_V1}" media type .
     */
    String MEDIA_TYPE_JSON_V1 = "application/vnd.overview-v1+json";
    /**
     * A {@link MediaType} constant representing json v1 "{@value #MEDIA_TYPE_JSON_V1}" media type.
     */
    MediaType MEDIA_TYPE_JSON_V1_TYPE = new MediaType("application", "vnd.overview-v1+json");

    /**
     * <pre>
     *  http://127.0.0.1:8180/auth-microservice/auth-api/overview/endpoints
     *  curl -v -H "Accept: application/vnd.overview-v1+json" -H "Content-Type: application/vnd.overview-v1+json" -H "X-BOMC-REQUEST-ID: SET-BY-CURL-123" -H "X-BOMC-AUTHORIZATION: BOMC_USER" -X GET "127.0.0.1:8180/auth-microservice/auth-api/overview/endpoints"
     * </pre>
     * @return JSON response for all available endpoints.
     * @description List all available endpoints. responseType javax.json.JsonObject
     * @responseMessage 200 A Response object that wraps the javax.json.JsonObject.
     * @responseMessage 400 Invalid Request.
     * @responseMessage 401 Unauthorized. API key does not need access privileges.
     * @responseMessage 404 Endpoint not found.
     */
    @GET
    @Path("/endpoints")
    @Produces(ApiOverviewRestEndpoint.MEDIA_TYPE_JSON_V1)
    @Cache
    Response getAvailableEndpoints(@Context final Dispatcher dispatcher);
}
