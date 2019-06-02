package de.bomc.poc.prometheus.collector.control;

import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.bomc.poc.prometheus.collector.entity.Metric;
import de.bomc.poc.prometheus.configuration.boundary.ConfigurationStore;

/**
 * This ejb reads the metrics from a endpoint. This endpoint must be available
 * in cahce by name.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 02.06.2017
 */
@Stateless
public class DataCollectorEJB {

	private static final String LOG_PREFIX = "DataCollector#";
	private static final Logger LOGGER = Logger.getLogger(DataCollectorEJB.class);

	private Client client;

	@Inject
	private ConfigurationStore configurationStore;

	private static final String URI = "uri";

	@PostConstruct
	public void initClient() {
		LOGGER.debug(LOG_PREFIX + "initClient");

		this.client = ClientBuilder.newClient();
	}

	/**
	 * Fetch the metrics from the remote service
	 * 
	 * @param metricsName
	 *            the name of the remote service. Which is the key in the store.
	 * @return a Metric entity.
	 */
	public Optional<Metric> fetchRemoteMetrics(final String metricsName) {
		LOGGER.debug(LOG_PREFIX + "fetchRemoteMetrics [metricName=" + metricsName + "]");

		// Reads the uri from store. The store acts as a cache.
		final Optional<JsonObject> optionalConfiguration = this.configurationStore.getConfiguration(metricsName);
		final JsonObject configuration = optionalConfiguration
				.orElseThrow(() -> new MetricNotConfiguredException(metricsName));
		final String extractedUri = extractUri(configuration);

		LOGGER.debug(LOG_PREFIX + "fetchRemoteMetrics - send metric request to [extractedUri=" + extractedUri + "]");

		// Start a request to the service to get the metrics.
		final Response response = client.target(extractedUri).request(MediaType.APPLICATION_JSON).get();
		if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
			return Optional.empty();
		}

		// Get the response and transform it to a Metric entity.
		final JsonObject applicationData = response.readEntity(JsonObject.class);

		return Optional.of(new Metric(configuration, applicationData));
	}

	/**
	 * Extract the uiri from the given configuration.
	 * 
	 * @param configuration
	 *            the given configuration with the uri, that has to be
	 *            extracted.
	 * @return the uri.
	 */
	public String extractUri(final JsonObject configuration) {
		LOGGER.debug(LOG_PREFIX + "extractUri [configuration=" + configuration.toString() + "]");

		return configuration.getString(URI);
	}
}
