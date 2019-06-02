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
 * Reads the current version of this project from 'version.properties'-file.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 09.12.2017
 */
@Path("version")
@Consumes({ VersionRESTResource.MEDIA_TYPE_JSON_V })
@Produces({ VersionRESTResource.MEDIA_TYPE_JSON_V })
@Api(value = "/version", produces = "application/vnd.health-v1+json", consumes = "application/vnd.health-v1+json")
@SwaggerDefinition(
		info = @Info(
		  description = "Provides the current version of this api.",
		  version = "1.00.00-SNAPSHOT",
		  title = "The Version API",
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
		consumes = {"application/vnd.version-v1+json"},
		produces = {"application/vnd.version-v1+json"},
		schemes = {SwaggerDefinition.Scheme.HTTP},
		tags = {
				@Tag(name = "current-version", description = "Return the current version in json format.")
		}, 
		externalDocs = @ExternalDocs(value = "Documentation", url = "http://confluence.org/bomc-swagger.html")
	)
public interface VersionRESTResource {

	String MEDIA_TYPE_JSON_V = "application/vnd.version-v1+json";

	/**
	 * <pre>
	 *  http://localhost:8080/bomc-swagger/rest/version/current-version
	 * </pre>
	 * 
	 * @return available heap as JsonObject.
	 */
	@GET
	@Path("/current-version")
	@ApiOperation(notes = "Return the current version in json format.", value = "getVersion", httpMethod = "GET", nickname = "getVersion", position = 1)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful retrieval of current version.", response = String.class),
			@ApiResponse(code = 500, message = "Request failed", response = String.class) })
	Response getVersion();
}
