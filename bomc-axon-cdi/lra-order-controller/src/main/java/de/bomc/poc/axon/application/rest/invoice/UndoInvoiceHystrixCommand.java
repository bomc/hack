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
package de.bomc.poc.axon.application.rest.invoice;

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
 * Undo a invoice in invoice-service.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
public class UndoInvoiceHystrixCommand extends AbstractHystrixCommand<InvoiceHystrixResult> {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "UndoInvoiceHystrixCommand#";
	private static final String SERVICE_NAME = "lra-invoice";
	private static final String BASE_URL = "http://127.0.0.1:8180/%s";
	private static final String API_PATH = "/rest/api/undo/generate/";
	private String option;

	/**
	 * Creates a new instance of <code>UndoInvoiceHystrixCommand</code>.
	 *
	 */
	public UndoInvoiceHystrixCommand() {
		super(LOG_PREFIX, SERVICE_NAME);
	}

	@Override
	protected InvoiceHystrixResult run() throws Exception {
		LOGGER.debug(LOG_PREFIX + "run [serviceName=" + SERVICE_NAME + ", payload/option=" + this.option + "]");

		// Check if pay load is set.
		checkPayload();

		final String url = String.format(BASE_URL, SERVICE_NAME);

		final ResteasyClient resteasyClient = createRestClient();
		final Response response = resteasyClient.target(UriBuilder.fromUri(url).path(API_PATH + this.option).build())
				.request().accept(MediaType.APPLICATION_JSON).delete();

		final Response.StatusType statusInfo = response.getStatusInfo();

		LOGGER.info(LOG_PREFIX + "run - Receive response from " + SERVICE_NAME + ". [http.status.family="
				+ statusInfo.getFamily() + "]");

		if (statusInfo.getFamily() == Response.Status.Family.SUCCESSFUL) {
			final JsonArray jsonArray = Json.createReader(new StringReader(response.readEntity(String.class)))
					.readArray();

			jsonArray.forEach(
					e -> LOGGER.info(LOG_PREFIX + "run [element=" + e + ", serviceName=" + SERVICE_NAME + "]"));

			return new InvoiceHystrixResult(jsonArray.getString(jsonArray.size() - 1), true);
		} else {
			LOGGER.error(LOG_PREFIX + "run - Receive response from " + SERVICE_NAME + " fails! [http.status.family="
					+ statusInfo.getFamily() + ", serviceName=" + SERVICE_NAME + "]");

			throw new RuntimeException(statusInfo.getReasonPhrase());
		}
	}

	/**
	 * Set the payload, here the option is set.
	 * 
	 * @param optionthe
	 *            given option 'failInvoice'.
	 */
	public void setPayload(final String option) {
		LOGGER.debug(LOG_PREFIX + "setPayLoad [serviceName=" + SERVICE_NAME + ", option=" + option + "]");

		this.option = option;
	}
	
	private void checkPayload() {
		LOGGER.debug(LOG_PREFIX + "checkPayload [option=" + this.option + "]");

		if (this.option == null) {
			throw new IllegalArgumentException(LOG_PREFIX
					+ "checkPayload - option is null, this is not allowed, use setter method for setting payload!");
		}
	}

	@Override
	protected InvoiceHystrixResult getFallback() {
		String message;

		if (this.executionResult != null && this.executionResult.getExecutionException() != null) {
			message = this.executionResult.getExecutionException().toString();

			LOGGER.error(
					LOG_PREFIX + "getFallback [serviceName=" + SERVICE_NAME + ", execution.exception=" + message + "]");
		} else {
			message = LOG_PREFIX + "getFallback - [serviceName=" + SERVICE_NAME + "] generating invoice failed!";

			LOGGER.error(message);
		}

		return new InvoiceHystrixResult(message, false);
	}
}
