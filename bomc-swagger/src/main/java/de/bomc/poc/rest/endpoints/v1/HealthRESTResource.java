/**
 * Project: bomc-swagger
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
 */
package de.bomc.poc.rest.endpoints.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

/**
 * Reads some health data from running system.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 09.12.2017
 */
@Path("health")
@Consumes({ HealthRESTResource.MEDIA_TYPE_JSON_V })
@Produces({ HealthRESTResource.MEDIA_TYPE_JSON_V })
@Api(value = "/health", produces = "application/vnd.health-v1+json", consumes = "application/vnd.health-v1+json")
@SwaggerDefinition(
		info = @Info(
		  description = "Reads some health data from running system.",
		  version = "1.00.00-SNAPSHOT",
		  title = "The Health API",
		  termsOfService = "http://localhost:8180/bomc-swagger",
		  contact = @Contact(
				name = "bomc", 
				email = "bomc@bomc.org", 
				url = "http://bomc.org"
		  ),
		  license = @License(
				name = "Apache 2.0", 
				url = "http://www.apache.org/licenses/LICENSE-2.0"
		  )
		),
		consumes = {"application/vnd.health-v1+json"},
		produces = {"application/vnd.health-v1+json"},
		schemes = {SwaggerDefinition.Scheme.HTTP},
		tags = {
				@Tag(name = "current-memory", description = "Return the heapsize"),
				@Tag(name = "os-info", description = "Provides some os data in json format.")
		},
		externalDocs = @ExternalDocs(value = "Documentation", url = "http://confluence.org/bomc-swagger.html")
	)
public interface HealthRESTResource {

	String MEDIA_TYPE_JSON_V = "application/vnd.health-v1+json";

	/**
	 * <pre>
	 *  http://localhost:8080/bomc-swagger/rest/health/os-info
	 * </pre>
	 * 
	 * @return available heap as JsonObject.
	 */
	@GET
	@Path("current-memory")
	@ApiOperation(notes = "Return the heapsize at runtime in json format.", value = "availableHeap", httpMethod = "GET", nickname = "availableHeap", position = 1)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful retrieval of heap size", response = String.class) })
	Response availableHeap();

	/**
	 * <pre>
	 *  http://localhost:8080/bomc-swagger/rest/health/os-info
	 * </pre>
	 * 
	 * @return os info as JsonObject.
	 */
	@GET
	@Path("/os-info")
	@ApiOperation(notes = "Provides some os data in json format.", value = "osInfo", httpMethod = "GET", nickname = "osInfo", position = 2)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful retrieval of os data", response = String.class) })
	Response osInfo();
}
