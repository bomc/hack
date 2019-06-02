package de.bomc.poc.core.rest.endpoints;

import java.util.Properties;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.bomc.poc.core.config.ConfigurationPropertiesConstants;
import de.bomc.poc.core.config.qualifier.PropertiesConfigTypeQualifier;
import de.bomc.poc.core.config.type.PropertiesConfigTypeEnum;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Reads the current version of this project from 'version.properties'-file.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @since 14.03.2016
 */
@Path(VersionEndpoint.VERSION_ENDPOINT_PATH)
@Api(value = VersionEndpoint.VERSION_ENDPOINT_PATH)
public class VersionEndpoint {
	// _______________________________________________
	// Public constants.
	// -----------------------------------------------
	public static final String VERSION_ENDPOINT_PATH = "/health";
	// _______________________________________________
	// Public constants.
	// -----------------------------------------------
	private static final String LOG_PREFIX = "VersionEndpoint#";
	private static final String JSON_VERSION_KEY_NAME = "version";
	// _______________________________________________
	// Member variables.
	// -----------------------------------------------
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	@PropertiesConfigTypeQualifier(type = PropertiesConfigTypeEnum.VERSION_CONFIG)
	private Properties versionProperties;

	/**
	 * Read the svn version from version.properties file.
	 * 
	 * @return the svn version from version.properties file.
	 */
	@GET
	@Path("/version")
	@Produces("application/json")
	@ApiOperation(notes = "Used to verify the health of the service.", value = "Return the version.", nickname = "version", httpMethod = "GET")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully finished.", response = String.class) })
	public Response getVersion() {
		final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		jsonObjectBuilder.add(JSON_VERSION_KEY_NAME,
				this.versionProperties.getProperty(ConfigurationPropertiesConstants.VERSION_VERSION_PROPERTY_KEY));
		final JsonObject jsonObject = jsonObjectBuilder.build();

		this.logger.debug(LOG_PREFIX + "getVersion [version=" + jsonObject.toString() + "]");

		return Response.ok().entity(jsonObject).build();
	}
}
