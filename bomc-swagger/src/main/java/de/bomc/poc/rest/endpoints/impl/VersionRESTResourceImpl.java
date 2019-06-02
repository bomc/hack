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
package de.bomc.poc.rest.endpoints.impl;

import java.io.IOException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.endpoints.v1.VersionRESTResource;
import de.bomc.poc.service.VersionSingletonEJB;

/**
 * Reads the current version of this project from 'version.properties'-file.
 * 
 * @author <a href="mailto:bomc@bomc.org.ch">Michael Boerner</a>
 * @since 09.12.2017
 */
public class VersionRESTResourceImpl implements VersionRESTResource {
	private static final String LOG_PREFIX = "VersionRESTResourceImpl#";
	private static final String VERSION_KEY_NAME = "Version";
	@Inject
	@LoggerQualifier
	private Logger logger;
    @EJB
    private VersionSingletonEJB versionSingetonEJB;

	/**
	 * Read the svn version from version.properties file.
	 * 
	 * @return the svn version from version.properties file.
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
