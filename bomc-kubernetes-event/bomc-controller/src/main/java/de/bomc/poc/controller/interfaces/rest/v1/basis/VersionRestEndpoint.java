/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.controller.interfaces.rest.v1.basis;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.bomc.poc.controller.application.util.JaxRsActivator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Reads the current version of this project from 'version.properties'-file.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@Path(JaxRsActivator.VERSION_ENDPOINT_PATH)
@Produces({VersionRestEndpoint.MEDIA_TYPE_JSON_V1})
@Api(value = "/version", description = "Show the current version of the deployed service.")
public interface VersionRestEndpoint {

    /**
     * A {@code String} constant representing json v1
     * "{@value #MEDIA_TYPE_JSON_V1}" media type .
     */
    String MEDIA_TYPE_JSON_V1 = "application/vnd.version-v1+json";

    /**
     * <pre>
     *  http://localhost:8180/bomc-controller/rest/version/current-version
     *  curl -v -H "Accept: application/vnd.version-v1+json" -H "Content-Type: application/vnd.version-v1+json" -H "X-BOMC-REQUEST-ID: SET-BY-CURL-123" -H "X-BOMC-AUTHORIZATION: BOMC_USER" -X GET
     * "127.0.0.1:8180/bomc-controller/rest/version/current-version"
     * </pre>
     */
    @ApiOperation(value = "Read the current version from 'version.properties'.", response = String.class)
    @ApiResponses({@ApiResponse(code = 404, message = "Endpoint not found."), @ApiResponse(code = 200, message = "The current version as response object that wraps the javax.json.JsonObject as a string.")})
    @GET
    @Path("/current-version")
    Response getVersion();
}
