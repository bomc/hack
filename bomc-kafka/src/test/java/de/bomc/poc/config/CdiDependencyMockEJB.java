package de.bomc.poc.config;

import java.util.Properties;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.config.qualifier.PropertiesConfigTypeQualifier;
import de.bomc.poc.config.type.PropertiesConfigTypeEnum;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

/**
 * A mock for testeing the injected configuration properties.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
@Stateless
public class CdiDependencyMockEJB {

	private static final String LOG_PREFIX = "CdiDependencyMockEJB#";
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	@PropertiesConfigTypeQualifier(type = PropertiesConfigTypeEnum.KAFKA_CONFIG)
	private Properties kafkaProperties;
	@Inject
	@PropertiesConfigTypeQualifier(type = PropertiesConfigTypeEnum.VERSION_CONFIG)
	private Properties versionProperties;

	/**
	 * @return the kafkaProperties
	 */
	public Properties getKafkaProperties() {
		this.logger.debug(LOG_PREFIX + "getKafkaProperties");

		return kafkaProperties;
	}

	/**
	 * @return the versionProperties
	 */
	public Properties getVersionProperties() {
		this.logger.debug(LOG_PREFIX + "getVersionProperties");
		return versionProperties;
	}

}
