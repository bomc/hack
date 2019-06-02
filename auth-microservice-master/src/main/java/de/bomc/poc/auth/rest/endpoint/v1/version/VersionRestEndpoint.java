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
package de.bomc.poc.auth.rest.endpoint.v1.version;

import de.bomc.poc.auth.rest.application.JaxRsActivator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Reads the current version of this project from 'version.properties'-file.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Path(JaxRsActivator.VERSION_ENDPOINT_PATH)
@Produces({VersionRestEndpoint.MEDIA_TYPE_JSON_V1})
public interface VersionRestEndpoint {
    /**
     * A {@code String} constant representing json v1 "{@value #MEDIA_TYPE_JSON_V1}" media type .
     */
    String MEDIA_TYPE_JSON_V1 = "application/vnd.version-v1+json";
    /**
     * A {@link MediaType} constant representing json v1 "{@value #MEDIA_TYPE_JSON_V1}" media type.
     */
    MediaType MEDIA_TYPE_JSON_V1_TYPE = new MediaType("application", "vnd.version-v1+json");

    /**
     * <pre>
     *	http://127.0.0.1:8180/auth-microservice/auth-api/version/current-version
     *  curl -v -H "Accept: application/vnd.version-v1+json" -H "Content-Type: application/vnd.version-v1+json" -H "X-BOMC-AUTHORIZATION: BOMC_USER" -H "X-BOMC-REQUEST-ID: SET-BY-CURL-123" -X GET "127.0.0.1:8180/auth-microservice/auth-api/version/current-version"
     * </pre>
     * Returns the current maven version of the artefact.
     * @description Returns the current maven version of the artefact.
     * responseType javax.json.JsonObject
     * @responseMessage 200 A Response object that wraps the javax.json.JsonObject.
     * @responseMessage 400 Invalid Request.
     * @responseMessage 401 Unauthorized. API key does not need access privileges.
     * @responseMessage 404 Endpoint not found.
     *
     * @return version as JsonObject.
     */
    @GET
    @Path("/current-version")
    Response getVersion();
}
