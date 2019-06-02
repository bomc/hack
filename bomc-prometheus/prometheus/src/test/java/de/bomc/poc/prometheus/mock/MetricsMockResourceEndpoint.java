package de.bomc.poc.prometheus.mock;

import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

/**
 *
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@ApplicationScoped
public class MetricsMockResourceEndpoint implements MetricsMockResource {

	private static final String LOG_PREFIX = "MetricsMockResourceEndpoint#";
	private static final Logger LOGGER = Logger.getLogger(MetricsMockResourceEndpoint.class);
	
	private AtomicLong counter = new AtomicLong();

	public Response metric() {
		LOGGER.debug(LOG_PREFIX + "metric");
		
		final JsonObject jsonObject = Json.createObjectBuilder().add("application", "sample-service").add("component", "MetricsMockResourceEndpoint")
				.add("units", "request").add("suffix", "total").add("value", String.valueOf(counter.incrementAndGet()))
				.build();
		
		return Response.ok().entity(jsonObject).build();
		
	}
}
