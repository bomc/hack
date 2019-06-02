package de.bomc.poc.rest.endpoints.impl;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.endpoints.v1.SslRESTResource;
import de.bomc.poc.service.impl.VersionSingletonEJB;

import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Reads the current version of this project from 'version.properties'-file.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
public class SslRESTResourceImpl implements SslRESTResource {
	private static final String LOG_PREFIX = "SslRESTResourceImpl#";
	private static final String VERSION_KEY_NAME = "Version";
	@Inject
	@LoggerQualifier
	private Logger logger;
	@EJB
	private VersionSingletonEJB versionSingetonEJB;
	
	/**
	 * <pre>
	 *  curl -v -H "Accept: application/vnd.version-v1+json" -H "Content-Type: application/vnd.version-v1+json" -X GET "192.168.4.1:8180/ssl/rest/version/current-version"
	 *	http://192.168.4.1:8180/ssl/version/current-version
	 * </pre>
	 */
	@Override
	public Response getVersion() {
		this.logger.debug(LOG_PREFIX + "getVersion");

		final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

		try {
			jsonObjectBuilder.add(VERSION_KEY_NAME, this.versionSingetonEJB.readVersionFromClasspath());
		} catch (final IOException ioex) {
			this.logger.error(LOG_PREFIX + "getVersion - failed!", ioex);

			jsonObjectBuilder.add(VERSION_KEY_NAME, "Reading version from property file failed!");
		}

		return Response.ok().entity(jsonObjectBuilder.build()).build();
	}
}
