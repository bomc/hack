/**
 * Project: Poc-prometheus
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-17 16:25:31 +0100 (Di, 17 Jan 2017) $
 *
 *  revision: $Revision: 9685 $
 *
 * </pre>
 */
package de.bomc.poc.prometheus.rest.endpoints;

import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

/**
 * A sample service wich collects metrics for prometheus.
 *
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@Path("metrics")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class MetricsMockResourceEndpoint {

	private static final String LOG_PREFIX = "MetricsMockResourceEndpoint#";
	private static final Logger LOGGER = Logger.getLogger(MetricsMockResourceEndpoint.class);

	private AtomicLong counter = new AtomicLong();

	/**
	 * <code>
	 * 	curl -v -H "Accept: application/json" -X GET "http://192.168.4.1:8180/sample-service/rest/metrics"
	 * </code>
	 */
	@GET
	public Response metric() {
		LOGGER.debug(LOG_PREFIX + "metric");

		final JsonObject jsonObject = Json.createObjectBuilder().add("application", "sample-service")
				.add("component", "MetricsMockResourceEndpoint").add("units", "request").add("suffix", "total")
				.add("value", String.valueOf(counter.incrementAndGet())).build();

		return Response.ok().entity(jsonObject).build();
	}
}
