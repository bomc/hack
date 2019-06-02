package de.bomc.poc.config;

/**
 * A class with global project constants.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
public interface ConfigurationPropertiesConstants {

	// _______________________________________________
	// Configuration key properties.
	// -----------------------------------------------
	// Version
	public static final String VERSION_PROPERTIES_FILE = "version.properties";
	public static final String VERSION_VERSION_PROPERTY_KEY = "version";
	// Kafka
	public static final String KAFKA_PROPERTIES_FILE = "kafka.properties";
	public static final String KAFKA_BOMC_TOPIC_PROPERTY_KEY = "bomc-app.topic";
	public static final String KAFKA_GROUP_ID_KEY = "group.id";
	public static final String KAFKA_TRANSACTIONAL_ID_KEY = "transactional.id";
}
