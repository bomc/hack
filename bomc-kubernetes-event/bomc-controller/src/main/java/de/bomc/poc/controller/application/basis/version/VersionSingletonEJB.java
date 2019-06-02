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
package de.bomc.poc.controller.application.basis.version;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;

import de.bomc.poc.controller.application.internal.AppErrorCodeEnum;
/**
 * A startup singleton EJB that returns the current project version.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@Startup
@Singleton
public class VersionSingletonEJB {

    private static final String LOG_PREFIX = "VersionSingletonEJB#";
    public static final String VERSION_PROPERTIES_FILE = "version.properties";
    private static final String VERSION_PROPERTY_VALUE = "version";
    @Inject
    @LoggerQualifier
    private Logger logger;

    /**
     * Read the svn version from version.properties file.
     * @return the svn version from version.properties file.
     * @throws IOException if reading of version.properties file failed.
     */
    public String readVersionFromClasspath() throws IOException {
        this.logger.debug(LOG_PREFIX + "readVersionFromClasspath");

        try {
            // Get the inputStream
            final InputStream
                inputStream =
                this.getClass()
                    .getClassLoader()
                    .getResourceAsStream(VERSION_PROPERTIES_FILE);

            final Properties properties = new Properties();

            // Load the inputStream using the Properties
            properties.load(inputStream);
            // Get the value of the property
            final String versionPropertyValue = properties.getProperty(VERSION_PROPERTY_VALUE);

            this.logger.debug(LOG_PREFIX + "readVersionFromClasspath [version=" + versionPropertyValue + "]");

            return versionPropertyValue;
        } catch (final Exception exception) {
            final String errMsg = LOG_PREFIX + "readVersionFromClasspath - reading version failed! ";

            final AppRuntimeException appRuntimeException = new AppRuntimeException(errMsg, exception, AppErrorCodeEnum.APP_READ_VERSION_FAILED_10602);
            this.logger.error(appRuntimeException.stackTraceToString());

            throw appRuntimeException;
        }
    }
}
