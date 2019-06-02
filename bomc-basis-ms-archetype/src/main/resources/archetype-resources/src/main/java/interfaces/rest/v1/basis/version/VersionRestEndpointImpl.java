#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.interfaces.rest.v1.basis.version;

import java.io.IOException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;

import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;

import ${package}.application.basis.performance.qualifier.PerformanceTrackingQualifier;
import ${package}.application.basis.version.VersionSingletonEJB;
import ${package}.interfaces.rest.v1.basis.VersionRestEndpoint;

/**
 * The implementation that reads the current version of this project from
 * 'version.properties'-file.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@PerformanceTrackingQualifier
@Interceptors({ExceptionHandlerInterceptor.class})
public class VersionRestEndpointImpl implements VersionRestEndpoint {

    private static final String LOG_PREFIX = "VersionRestEndpointImpl${symbol_pound}";
    private static final String VERSION_KEY_NAME = "version";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @EJB
    private VersionSingletonEJB versionSingetonEJB;

    /**
     * Read the svn version from 'version.properties' file.
     * @return the svn version from 'version.properties' file.
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

        return Response.ok()
                       .entity(jsonObjectBuilder.build())
                       .build();
    }
}
