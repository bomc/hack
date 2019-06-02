/**
 * Project: MY_POC_MICROSERVICE
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
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.os.version;

import de.bomc.poc.exception.app.AppInitializationException;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A startup singleton EJB that returns the current project version.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Startup
@Singleton
public class VersionSingletonEJB {
    /**
     * Logger.
     */
    @Inject
    @LoggerQualifier
    private Logger logger;
    public static final String VERSION_PROPERTIES_FILE = "version.properties";
    private static final String VERSION_PROPERTY_VALUE = "version";
    private static final String MAVEN_BUILD_TIMESTAMP_PROPERTY_VALUE = "maven.build.timestamp";
    private final Properties properties = new Properties();

    @PostConstruct
    public void init() {
        try {
            // Get the inputStream
            final InputStream
                inputStream =
                this.getClass()
                    .getClassLoader()
                    .getResourceAsStream(VERSION_PROPERTIES_FILE);

            // Load the inputStream using <code>Properties</code>.
            this.properties.load(inputStream);
        } catch(IOException ioException) {
            this.logger.debug("VersionSingletonEJB#init - ", ioException);
            throw new AppInitializationException("VersionSingletonEJB#init - loading version.properties failed!");
        }
    }

    /**
     * @return the maven version of this artefact.
     */
    public String getMavenVersion() {
        this.logger.debug("VersionSingletonEJB#getMavenVersion");

        // Get the value of the property.
        final String versionPropertyValue = properties.getProperty(VERSION_PROPERTY_VALUE);

        this.logger.debug("VersionSingletonEJB#getVersion [version=" + versionPropertyValue + "]");

        return versionPropertyValue;
    }

    /**
     * @return the maven version of this artefact.
     */
    public String getMavenBuildTimestamp() {
        this.logger.debug("VersionSingletonEJB#getMavenBuildTimestamp");

        // Get the value of the property.
        final String mavenBuildTimestampPropertyValue = properties.getProperty(MAVEN_BUILD_TIMESTAMP_PROPERTY_VALUE);

        this.logger.debug("VersionSingletonEJB#getVersion [mavenBuildTimestamp=" + mavenBuildTimestampPropertyValue + "]");

        return mavenBuildTimestampPropertyValue;
    }
}
