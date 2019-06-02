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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * The implementation that reads the current version of this project from
 * 'version.properties'-file.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
public class HealthRestEndpointImpl implements HealthRestEndpoint {

	private static final String LOG_PREFIX = "VersionRestEndpointImpl#";
	private static final String VERSION_KEY_NAME = "version.version";
	private static final String BUILD_DATE_KEY_NAME = "version.build.date";
	private static final String DEFAULT_VALUE = "not set in 'microprofile-config.properties'";
	// The logger
	public static final Logger LOGGER = Logger.getLogger(HealthRestEndpointImpl.class.getSimpleName());
	@Inject
	@ConfigProperty(name = VERSION_KEY_NAME, defaultValue = DEFAULT_VALUE)
	private String version;
	@Inject
	@ConfigProperty(name = BUILD_DATE_KEY_NAME, defaultValue = DEFAULT_VALUE)
	private String buildDate;

	@Override
	public Response getLiveness() {
		LOGGER.log(Level.INFO, LOG_PREFIX + "getLiveness");

		final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		jsonObjectBuilder.add(VERSION_KEY_NAME, version);
		jsonObjectBuilder.add(BUILD_DATE_KEY_NAME, buildDate);

		return Response.ok().entity(jsonObjectBuilder.build()).build();
	}
	

	@Override
	public Response getReadiness() {
		LOGGER.log(Level.INFO, LOG_PREFIX + "getReadiness");

		final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		jsonObjectBuilder.add(VERSION_KEY_NAME, version);
		jsonObjectBuilder.add(BUILD_DATE_KEY_NAME, buildDate);

		return Response.ok().entity(jsonObjectBuilder.build()).build();
	}

}
