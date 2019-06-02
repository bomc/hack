package de.bomc.poc.prometheus.collector.boundary;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.bomc.poc.prometheus.collector.control.DataCollectorEJB;

/**
 * The implementation of the endpoint for requesting prometheus metrics from a jee service.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 02.06.2017
 */
@Stateless
@Local(MetricsResource.class)
public class MetricsResourceEndpoint implements MetricsResource {

	private static final String LOG_PREFIX = "MetricsResourceEndpoint#";
	private static final Logger LOGGER = Logger.getLogger(MetricsResourceEndpoint.class);

	@Inject
	DataCollectorEJB dataCollector;

	/**
	 * <code>
	 * 	curl -v -H "Accept: application/json" -H "Content-Type: application/json" -X PUT -d "{\"uri\":\"http://192.168.4.1:8180/sample-service/rest/metrics\",\"application\":\"sampleservice\"}" "http://192.168.4.1:8180/bomc-prometheus/rest/configurations/sampleservice"
	 * </code>
	 * 
	 * <code>
	 * 	curl -v -H "Accept: text/plain" -X GET "http://192.168.4.1:8180/bomc-prometheus/rest/metrics/sampleservice"
	 * </code>
	 */
	public void metrics(final AsyncResponse response, final String name) {
		LOGGER.debug(LOG_PREFIX + "metrics [name=" + name + "]");
		
		response.setTimeout(1, TimeUnit.SECONDS);
		
		final Optional<String> metric = dataCollector.fetchRemoteMetrics(name).map(m -> m.toMetric());
		
		if (metric.isPresent()) {
			final String strMetrics = metric.get();
			
			LOGGER.debug(LOG_PREFIX + "metrics [strMetrics=" + strMetrics + "]");
			
			response.resume(metric.get());
		} else {
			response.resume(Response.noContent().build());
		}
	}
}
