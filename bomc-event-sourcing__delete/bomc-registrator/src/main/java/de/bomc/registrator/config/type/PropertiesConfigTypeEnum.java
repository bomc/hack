package de.bomc.registrator.config.type;

import java.util.stream.Stream;

/**
 * Defines the different configuration types.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
public enum PropertiesConfigTypeEnum {

	KAFKA_CONFIG, VERSION_CONFIG, PRODUCER;

	public static PropertiesConfigTypeEnum fromString(final String name) {
		return Stream.of(values()).filter(v -> v.name().equalsIgnoreCase(name)).findAny().orElse(null);
	}

}