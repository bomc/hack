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
package de.bomc.poc.auth.rest.endpoint.v1.runtime;

import de.bomc.poc.auth.rest.application.JaxRsActivator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A REST endpoint that delivers some runtime informations.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Path(JaxRsActivator.RUNTIME_ENDPOINT_PATH)
@Produces({RuntimeRestEndpoint.MEDIA_TYPE_JSON_V1})
public interface RuntimeRestEndpoint {
    /**
     * A {@code String} constant representing json v1 "{@value #MEDIA_TYPE_JSON_V1}" media type .
     */
    String MEDIA_TYPE_JSON_V1 = "application/vnd.runtime-v1+json";
    /**
     * A {@link MediaType} constant representing json v1 "{@value #MEDIA_TYPE_JSON_V1}" media type.
     */
    MediaType MEDIA_TYPE_JSON_V1_TYPE = new MediaType("application", "vnd.runtime-v1+json");

    /**
     * <pre>
     *  http://127.0.0.1:8180/auth-microservice/auth-api/runtime/available-heap
     *  curl -v -H "Accept: application/vnd.runtime-v1+json" -H "Content-Type: application/vnd.runtime-v1+json" -H "X-BOMC-REQUEST-ID: SET-BY-CURL-123" -H "X-BOMC-AUTHORIZATION: BOMC_USER" -X GET "127.0.0.1:8180/auth-microservice/auth-api/runtime/available-heap"
     * </pre>
     * Returns the available heap from the vm.
     * @description Returns the available heap from the vm.
     * responseType javax.json.JsonObject
     * @responseMessage 200 A Response object that wraps the javax.json.JsonObject.
     * @responseMessage 400 Invalid Request.
     * @responseMessage 401 Unauthorized. API key does not need access privileges.
     * @responseMessage 404 Endpoint not found.
     * @return available heap as JsonObject.
     */
    @GET
    @Path("/available-heap")
    Response availableHeap();

    /**
     * <pre>
     *  http://127.0.0.1:8180/auth-microservice/auth-api/runtime/os-info
     *  curl -v -H "Accept: application/vnd.runtime-v1+json" -H "Content-Type: application/vnd.runtime-v1+json" -H "X-BOMC-REQUEST-ID: SET-BY-CURL-123" -H "X-BOMC-AUTHORIZATION: BOMC_USER" -X GET "127.0.0.1:8180/auth-microservice/auth-api/runtime/os-info"
     * </pre>
     * @description Returns some informations from the running operating system.
     * responseType javax.json.JsonObject
     * @responseMessage 200 A Response object that wraps the javax.json.JsonObject.
     * @responseMessage 400 Invalid Request.
     * @responseMessage 401 Unauthorized. API key does not need access privileges.
     * @responseMessage 404 Endpoint not found.
     * @return os info as JsonObject.
     */
    @GET
    @Path("/os-info")
    Response osInfo();

    /**
     * <pre>
     *  http://127.0.0.1:8180/auth-microservice/auth-api/runtime/node-name
     *  curl -v -H "Accept: application/vnd.runtime-v1+json" -H "Content-Type: application/vnd.runtime-v1+json" -H "X-BOMC-REQUEST-ID: SET-BY-CURL-123" -H "X-BOMC-AUTHORIZATION: BOMC_USER" -X GET "127.0.0.1:8180/auth-microservice/auth-api/runtime/node-name"
     * </pre>
     * @description Returns the node name of the application server.
     * responseType javax.json.JsonObject
     * @responseMessage 200 A Response object that wraps the javax.json.JsonObject.
     * @responseMessage 400 Invalid Request.
     * @responseMessage 401 Unauthorized. API key does not need access privileges.
     * @responseMessage 404 Endpoint not found.
     * @return node.name of the application server as JsonObject.
     */
    @GET
    @Path("/node-name")
    Response nodeName();
}
