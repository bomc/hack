
package de.bomc.poc.prometheus.configuration.control;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.log4j.Logger;

/**
 * This class handles extracting configuration, name from environment
 * properties.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 02.06.2017
 */
public class EnvironmentVariables {

	public static final Logger LOGGER = Logger.getLogger(EnvironmentVariables.class);
	public static final String LOG_PREFIX = "EnvironmentVariables#";
	// Indicates the key-prefix in 'metrics.env'.
	public static final String MANDATORY_KEY_PREFIX = "prometheus.";

	public static Optional<String> getValue(final String configuration, final String key) {
		LOGGER.debug(LOG_PREFIX + "getValue [configuration=" + configuration + ", key=" + key + "]");

		return Optional.ofNullable(System.getenv(MANDATORY_KEY_PREFIX + configuration + "." + key));
	}

	public static Optional<JsonObject> getConfiguration(final String configurationName) {
		LOGGER.debug(LOG_PREFIX + "getConfiguration [configurationName=" + configurationName + "]");

		return getConfiguration(System.getenv(), e -> e.getKey().contains("." + configurationName + "."));
	}

	/**
	 * Extract the configuration from map by given configuration name.
	 * 
	 * @param environment
	 *            the given map with configurations.
	 * @param configurationName
	 *            the name that has to be extracted from the given map.
	 * @return a JsonObject with all given configurations from the given map.
	 */
	static Optional<JsonObject> getConfiguration(final Map<String, String> environment,
			final String configurationName) {
		LOGGER.debug(LOG_PREFIX + "environment=" + environment + "configurationName=" + configurationName + "]");

		return getConfiguration(environment, e -> e.getKey().contains("." + configurationName + "."));
	}

	public static Optional<JsonObject> getAllConfigurations() {
		LOGGER.debug(LOG_PREFIX + "getAllConfigurations");

		final JsonObjectBuilder builder = Json.createObjectBuilder();
		configurationNames().stream().forEach(name -> builder.add(name, getConfiguration(name).get()));
		final JsonObject configurations = builder.build();

		if (configurations.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(configurations);
	}

	public static List<String> configurationNames() {
		LOGGER.debug(LOG_PREFIX + "configurationNames");
		return configurationNames(System.getenv().keySet());
	}

	/**
	 * Extract configuration names from the given set.
	 * 
	 * @param keys
	 *            given key with configuration property.
	 * @return a list with all configuration names.
	 */
	static List<String> configurationNames(final Set<String> keys) {
		LOGGER.debug(LOG_PREFIX + "configurationNames [keys=" + keys + "]");

		return keys.stream().filter(key -> key.startsWith(MANDATORY_KEY_PREFIX))
				.map(key -> extractConfigurationName(key)).distinct().collect(Collectors.toList());
	}

	public static Optional<JsonObject> getConfiguration(Map<String, String> environmentEntries,
			Predicate<Map.Entry<String, String>> filter) {
		LOGGER.debug(LOG_PREFIX + "getConfiguration [environmentEntries, filter]");

		final JsonObjectBuilder builder = Json.createObjectBuilder();
		environmentEntries.entrySet().stream().filter(e -> e.getKey().startsWith(MANDATORY_KEY_PREFIX)).filter(filter)
				.forEach(e -> builder.add(extractKeyName(e.getKey()), e.getValue()));
		final JsonObject retVal = builder.build();

		if (retVal.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(retVal);
		}
	}

	/**
	 * Extracts the configuration name from the given key on slot 1. This means,
	 * given key 'prometheus.bomc.uri', extracts 'bomc' on slot 1.
	 * 
	 * @param key
	 *            the configuration name to extract.
	 * @return the name on slot 1 or the given key, if key equals null or has no
	 *         '.'.
	 */
	static String extractConfigurationName(final String key) {
		LOGGER.debug(LOG_PREFIX + "extractConfigurationName [key=" + key + "]");

		return EnvironmentVariables.extract(key, 1);
	}

	/**
	 * Extracts the key name from the given key on slot 2. This means, given key
	 * 'prometheus.bomc.uri', extracts 'bomc' on slot 2.
	 * 
	 * @param key
	 *            the configuration name to extract.
	 * @return the name on slot 2 or the given key, if key equals null or has no
	 *         '.'.
	 */
	static String extractKeyName(final String key) {
		return EnvironmentVariables.extract(key, 2);
	}

	/**
	 * Extract key on given slot.
	 * 
	 * @param key
	 *            the given where the property has to be extracted.
	 * @param slot
	 *            the position, on which the key has to be extracted.
	 * @return The extracted key or the given key if key equals null or key
	 *         contains no '.'.
	 */
	static String extract(String key, int slot) {
		LOGGER.debug(LOG_PREFIX + "extract [key=" + key + ", slot=" + slot + "]");

		if (key == null || !key.contains(".")) {
			return key;
		}

		final String[] twoSegments = key.split("\\.");

		// 0: prometheus
		// 1: configuration name
		// 2: key name
		return twoSegments[slot];
	}

}
