package de.bomc.poc.core.config.producer;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.core.config.ConfigurationPropertiesConstants;
import de.bomc.poc.core.config.qualifier.PropertiesConfigTypeQualifier;
import de.bomc.poc.core.config.type.PropertiesConfigTypeEnum;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

/**
 * Loads the application properties from config files.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
@ApplicationScoped
public class PropertiesConfiguratorProducer {

	// _______________________________________________
	// Class constants.
	// -----------------------------------------------
	private static final String LOG_PREFIX = "PropertiesConfigurator#";
	// _______________________________________________
	// Member variables.
	// -----------------------------------------------
	@Inject
	@LoggerQualifier
	private Logger logger;
	private Properties versionProperties;

	@PostConstruct
	public void initProperties() {
		this.logger.debug(LOG_PREFIX + "initProperties");

		this.readVersionProperties();
	}

	private void readVersionProperties() {
		this.logger.debug(LOG_PREFIX + "initVersionProperties");

		try {
			this.versionProperties = new Properties();
			this.versionProperties
					.load(PropertiesConfiguratorProducer.class.getResourceAsStream("/" + ConfigurationPropertiesConstants.VERSION_PROPERTIES_FILE));
			
			this.printoutProperties(PropertiesConfigTypeEnum.VERSION_CONFIG, this.versionProperties);
		} catch (final IOException e) {
			this.logger.error(LOG_PREFIX + "initVersionProperties - failed [message=" + e.getMessage() + "]");

			throw new IllegalStateException(e);
		}
	}

	/**
	 * Print out properties.
	 * 
	 * @param propertiesConfigTypeEnum
	 *            the given parameter to print out.
	 */
	private void printoutProperties(final PropertiesConfigTypeEnum propertiesConfigTypeEnum, final Properties properties) {
		this.logger.debug(LOG_PREFIX + "printoutProperties");
		
		final StringBuffer sb = new StringBuffer();
		
		sb.append("\n");
		
		for (final String key : properties.stringPropertyNames()) {
			final String value = properties.getProperty(key);

			sb.append(LOG_PREFIX + "printoutProperties - " + propertiesConfigTypeEnum.name() + "." + key + " = " + value + "\n");
		}
		
		this.logger.info(sb.toString());

	}

	/**
	 * Produces the configuration parameter depending on the annotated type.
	 * 
	 * @param injectionPoint
	 *            the given injection point.
	 * @return the parameter depends on the key, or a IllegalStateException if a
	 *         wrong or no key is defined.
	 */
	@Produces
	@SuppressWarnings("incomplete-switch")
	@PropertiesConfigTypeQualifier(type = PropertiesConfigTypeEnum.PRODUCER)
	public Properties produceConfigurationProperties(final InjectionPoint injectionPoint) {

		final Annotated annotated = injectionPoint.getAnnotated();
		final PropertiesConfigTypeQualifier annotation = annotated.getAnnotation(PropertiesConfigTypeQualifier.class);

		if (annotation != null) {
			final PropertiesConfigTypeEnum key = annotation.type();
			switch (key) {

			case VERSION_CONFIG:
				return this.versionProperties;
			}
		}

		throw new IllegalStateException(
				LOG_PREFIX + "produceConfigurationProperties - No key for injection point: " + injectionPoint);
	}
}
