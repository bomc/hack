/**
 * Project: cdi-axon
 * <pre>
 *
 * Last change:
 *
 *  by:       $Author$
 *
 *  date:     $Date$
 *
 *  revision: $Revision$
 *
 *  © Bomc 2018
 *
 * </pre>
 */
package de.bomc.poc.axon.application.rest.shipment;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;

import javax.json.Json;
import javax.json.JsonArray;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;

import de.bomc.poc.axon.application.rest.AbstractHystrixCommand;

/**
 * Undo a shipment in shipment-service.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
public class UndoShipmentHystrixCommand extends AbstractHystrixCommand<ShipmentHystrixResult> {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "UndoShipmentHystrixCommand#";
	private static final String SERVICE_NAME = "lra-shipment";
	private static final String BASE_URL = "http://127.0.0.1:8180/%s";
	private static final String API_PATH = "/rest/api/undo/compute/";
	private String option;

	/**
	 * Creates a new instance of <code>UndoShipmentHystrixCommand</code>.
	 * 
	 */
	public UndoShipmentHystrixCommand() {
		super(LOG_PREFIX, SERVICE_NAME);
	}

	@Override
	protected ShipmentHystrixResult run() throws Exception {
		LOGGER.debug(LOG_PREFIX + "run [payload/option=" + this.option + ", serviceName=" + SERVICE_NAME + "]");

		// Check if pay load is set.
		checkPayload();

		final String url = String.format(BASE_URL, SERVICE_NAME);

		final ResteasyClient resteasyClient = createRestClient();
		final Response response = resteasyClient.target(UriBuilder.fromUri(url).path(API_PATH + this.option).build())
				.request().accept(MediaType.APPLICATION_JSON).delete();

		final Response.StatusType statusInfo = response.getStatusInfo();

		LOGGER.info(LOG_PREFIX + "run - Receive response from shipment-service. [http.status.family="
				+ statusInfo.getFamily() + ", serviceName=" + SERVICE_NAME + "]");

		if (statusInfo.getFamily() == Response.Status.Family.SUCCESSFUL) {
			final JsonArray jsonArray = Json.createReader(new StringReader(response.readEntity(String.class)))
					.readArray();

			jsonArray.forEach(
					e -> LOGGER.info(LOG_PREFIX + "run [element=" + e + ", serviceName=" + SERVICE_NAME + "]"));

			return new ShipmentHystrixResult(jsonArray.getString(jsonArray.size() - 1), true);
		} else {
			LOGGER.error(LOG_PREFIX + "run - Receive response from invoice-service fails! [http.status.family="
					+ statusInfo.getFamily() + ", serviceName=" + SERVICE_NAME + "]");

			throw new RuntimeException(statusInfo.getReasonPhrase());
		}
	}

	/**
	 * Set the payload, here the option is set.
	 * 
	 * @param option
	 *            the given options 'failShipment'.
	 */
	public void setPayload(final String option) {
		LOGGER.debug(LOG_PREFIX + "setPayLoad [serviceName=" + SERVICE_NAME + ", option=" + option + "]");

		this.option = option;
	}
	
	private void checkPayload() {
		LOGGER.debug(LOG_PREFIX + "checkPayload [option=" + this.option + ", serviceName=" + SERVICE_NAME + "]");

		if (this.option == null) {
			throw new IllegalArgumentException(LOG_PREFIX + "checkPayload [serviceName=" + SERVICE_NAME
					+ "] - option is null, this is not allowed, use setter method for setting payload!");
		}
	}

	@Override
	protected ShipmentHystrixResult getFallback() {
		String message;

		if (this.executionResult != null && this.executionResult.getExecutionException() != null) {
			message = this.executionResult.getExecutionException().toString();

			LOGGER.error(
					LOG_PREFIX + "getFallback [serviceName=" + SERVICE_NAME + ", execution.exception=" + message + "]");
		} else {
			message = LOG_PREFIX + "getFallback - [serviceName=" + SERVICE_NAME + "] generating invoice failed!";

			LOGGER.error(message);
		}

		return new ShipmentHystrixResult(message, false);
	}
}
