/**
 * Project: bomc-invoice
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
package de.bomc.poc.invoice.interfaces.rest.v1.basis;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import de.bomc.poc.invoice.application.util.JaxRsActivator;

/**
 * Reads the current version of this project from 'version.properties'-file.
 * 
 * <pre>
 * see for a example of OpenAPI example:
 * https://labs.consol.de/de/microservices/2018/09/10/eclipse-microprofile.html
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 05.04.2019
 */
@Path(JaxRsActivator.HEALTH_ENDPOINT_PATH)
@Produces({ HealthRestEndpoint.MEDIA_TYPE_JSON_V1 })
@Schema(name = JaxRsActivator.HEALTH_ENDPOINT_PATH)
@Tag(name = "Health - liveness and readiness", description = "Supports healthy for liveness and readiness.")
public interface HealthRestEndpoint {

	/**
	 * A {@code String} constant representing json v1 "{@value #MEDIA_TYPE_JSON_V1}"
	 * media type .
	 */
	String MEDIA_TYPE_JSON_V1 = "application/vnd.health-v1+json";
	
	/**
	 * <pre>
	 *  TODO curl
	 * </pre>
	 */
	@Operation(summary = "Get liveness", description = "Read the current version from 'version.properties' to support the liveness probe, in form '{\"version\":\"1.00.00-SNAPSHOT\"}'.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "The current version as response object that wraps the javax.json.JsonObject as a string.", content = @Content(
                schema = @Schema(implementation = Response.class)
        ))
    })
	@GET
	@Path("/liveness")
	Response getLiveness();

	/**
	 * <pre>
	 *  TODO curl
	 * </pre>
	 */
	@Operation(summary = "Get readiness", description = "Read the current version from 'version.properties' to support the readiness probe, in form '{\"version\":\"1.00.00-SNAPSHOT\"}'.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "The current version as response object that wraps the javax.json.JsonObject as a string.", content = @Content(
                schema = @Schema(implementation = Response.class)
        ))
    })
	@GET
	@Path("/readiness")
	Response getReadiness();
}
