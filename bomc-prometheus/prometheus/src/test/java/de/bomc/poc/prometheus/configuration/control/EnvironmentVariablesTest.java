package de.bomc.poc.prometheus.configuration.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.json.JsonObject;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.bomc.poc.prometheus.configuration.control.EnvironmentVariables;

/**
 * Tests the extracting of properties from environment properties.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
public class EnvironmentVariablesTest {

	private static final Logger LOGGER = Logger.getLogger(EnvironmentVariablesTest.class);
	private static final String LOG_PREFIX = "EnvironmentVariablesTest#";

	@Test
	public void test010_extractConfigurationName_Pass() {
		LOGGER.debug(LOG_PREFIX + "test010_extractConfigurationName_Pass");

		final String configurationName = "bomc";

		// Extracts the configurationName 'bomc' from the given property key
		// 'prometheus.bomc.uri'.
		assertThat(EnvironmentVariables.extractConfigurationName("prometheus." + configurationName + ".uri"),
				is(configurationName));
	}

	@Test
	public void test020_extractKeyName_Pass() {
		LOGGER.debug(LOG_PREFIX + "test020_extractKeyName_Pass");

		final String keyName = "uri";

		assertThat(EnvironmentVariables.extractKeyName("prometheus.bomc." + keyName), is(keyName));
	}

	@Test
	public void test030_configurationNames_Pass() {
		LOGGER.debug(LOG_PREFIX + "test030_configurationNames_Pass");

		final Map<String, String> entries = new HashMap<>();
		entries.put("prometheus.sampleservice.uri", "http://sample-service:8080/sample-service/resources/metrics");
		entries.put("another.sampleservice.uri", "http://another:8080/another/resources/metrics");
		entries.put("prometheus.another.uri", "http://another:8080/another/resources/metrics");

		final List<String> configurationNames = EnvironmentVariables.configurationNames(entries.keySet());

		assertThat(configurationNames.size(), is(2));
		assertThat(configurationNames, hasItems("sampleservice", "another"));
	}

	@Test
	public void test040_getConfiguration_Pass() {
		LOGGER.debug(LOG_PREFIX + "test040_getConfiguration_Pass");

		final Map<String, String> entries = new HashMap<>();
		entries.put("prometheus.sampleservice.uri", "http://sample-service:8080/sample-service/resources/metrics");
		entries.put("another.sampleservice.uri", "http://another:8080/another/resources/metrics");
		entries.put("prometheus.another.uri", "http://another:8080/another/resources/metrics");
		entries.put("prometheus.another.applicationname", "anotherservice");

		// Get the configuration entry with the given name.
		final Optional<JsonObject> optionalConfiguration = EnvironmentVariables.getConfiguration(entries, "another");
		assertTrue(optionalConfiguration.isPresent());

		final JsonObject configuration = optionalConfiguration.get();
		LOGGER.debug(LOG_PREFIX + "test040_getConfiguration_Pass [configuration=" + configuration + "]");
		assertThat(configuration.size(), is(2));
	}
}
