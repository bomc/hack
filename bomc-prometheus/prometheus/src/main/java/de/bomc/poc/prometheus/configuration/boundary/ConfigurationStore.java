package de.bomc.poc.prometheus.configuration.boundary;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.log4j.Logger;

import de.bomc.poc.prometheus.configuration.control.EnvironmentVariables;

/**
 * A store that holds config data to access the remote service for requesting
 * the metrics.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 02.06.2017
 */
@ApplicationScoped
public class ConfigurationStore {

	private static final String LOG_PREFIX = "ConfigurationStore#";
	private static final Logger LOGGER = Logger.getLogger(ConfigurationStore.class);

	private ConcurrentHashMap<String, JsonObject> configurationStore;

	@PostConstruct
	public void init() {
		LOGGER.debug(LOG_PREFIX + "init");

		this.configurationStore = new ConcurrentHashMap<>();
	}

	/**
	 *
	 * @param name
	 *            metrics / configuration name
	 * @param configuration
	 *            metrics configuration
	 * @return true = update, false = save
	 */
	public boolean saveOrUpdate(final String name, final JsonObject configuration) {
		LOGGER.debug(LOG_PREFIX + "saveOrUpdate [name=" + name + ", configuration=" + configuration + "]");

		return this.configurationStore.put(name, configuration) != null;
	}

	public Optional<String> getValue(final String configuration, final String key) {
		LOGGER.debug(LOG_PREFIX + "getValue [configuration=" + configuration + ", key=" + key + "]");

		final Optional<String> environment = EnvironmentVariables.getValue(configuration, key);

		if (environment.isPresent()) {
			return environment;
		}

		return getConfiguration(key).map(o -> o.getString(key, null));
	}

	public Optional<JsonObject> getConfiguration(final String key) {
		LOGGER.debug(LOG_PREFIX + "getConfiguration [key=" + key + "]");

		final Optional<JsonObject> environment = EnvironmentVariables.getConfiguration(key);

		if (environment.isPresent()) {
			return environment;
		}

		return Optional.ofNullable(this.configurationStore.get(key));
	}

	JsonObject getAllConfigurations() {
		LOGGER.debug(LOG_PREFIX + "getAllConfigurations");

		final JsonObjectBuilder retVal = Json.createObjectBuilder();
		final Optional<JsonObject> environmentEntries = EnvironmentVariables.getAllConfigurations();

		if (environmentEntries.isPresent()) {
			final JsonObject environment = environmentEntries.get();

			LOGGER.info(LOG_PREFIX + "getAllConfigurations [environment= " + environment + "]");

			environment.keySet().stream().forEach(key -> retVal.add(key, environment.getJsonObject(key)));
		}

		this.configurationStore.entrySet().forEach(e -> retVal.add(e.getKey(), e.getValue()));

		return retVal.build();
	}

	public void remove(String name) {
		LOGGER.debug(LOG_PREFIX + "remove [name=" + name + "]");

		this.configurationStore.remove(name);
	}

}
