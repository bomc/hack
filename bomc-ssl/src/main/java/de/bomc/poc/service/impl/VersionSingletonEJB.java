package de.bomc.poc.service.impl;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A startup singleton EJB that returns the current project version.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@Startup
@Singleton
public class VersionSingletonEJB {
	public static final String VERSION_PROPERTIES_FILE = "version.properties";
	private static final String VERSION_PROPERTY_VALUE = "version";
	private static final String LOG_PREFIX = "VersionSingletonEJB#";
	@Inject
	@LoggerQualifier
	private Logger logger;

	@PostConstruct
	public void init() {
		this.logger.debug(LOG_PREFIX + "init");
	}

	/**
	 * Read the svn version from version.properties file.
	 * 
	 * @return the svn version from version.properties file.
	 * @throws IOException
	 *             if reading of version.properties file failed.
	 */
	@PermitAll
	public String readVersionFromClasspath() throws IOException {
		this.logger.debug(LOG_PREFIX + "readVersionFromClasspath");

		String versionPropertyValue;

		// Get the inputStream
		final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(VERSION_PROPERTIES_FILE);

		final Properties properties = new Properties();

		// Load the inputStream using the properties.
		properties.load(inputStream);
		// Get the value of the property.
		versionPropertyValue = properties.getProperty(VERSION_PROPERTY_VALUE);

		this.logger.debug(LOG_PREFIX + "readVersionFromClasspath [version=" + versionPropertyValue + "]");

		return versionPropertyValue;
	}
}
