/**
 * Project: lra-shipment
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
package de.bomc.poc.axon.interfaces.rest;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

/**
 * This REST endpoint is the boundary of the service.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
@Path("/api")
@Consumes("application/json")
@Produces("application/json")
public class InvoiceServiceEndpoint {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "InvoiceServiceEndpoint#";
	private static final String FAIL_FLAG_INVOICE = "failInvoice";
	private static final String DELAY_FLAG_INVOICE = "delayInvoice";
	@SuppressWarnings("unused")
	private static final String REGULAR_FLAG = "testInvoice";

	/**
	 * Generate invoice..
	 * 
	 * @return the computed shipment.
	 */
	@GET
	@Path("/generate/{option}")
	public List<String> generateInvoice(@PathParam("option") String option) {
		LOGGER.debug(LOG_PREFIX + "generateInvoice ***** ##### ******[option=" + option + "] ***** ##### *****");

		final List<String> invoiceList = new ArrayList<>();
		
		// Set computed return value.
		String invoice = "42";

		if (option.equals(FAIL_FLAG_INVOICE)) {
			//
			// Return the option to simulate a failed saga.
			invoice = option;
		} else if (option.equals(DELAY_FLAG_INVOICE)) {
			//
			// Sleep for a while...
			try {
				TimeUnit.SECONDS.sleep(30000L);
			} catch (final InterruptedException e) {
				// Ignore
			}

			// Return the option to simulate a delayed saga.
			invoice = option;
		}

		invoiceList.add(invoice);
		
		return invoiceList;
	}
	
	/**
	 * Undo the invoice.
	 * 
	 * @return the undo invoice.
	 */
	@DELETE
	@Path("/undo/generate/{option}")
	public List<String> undoGenerateInvoice(@PathParam("option") String option) {
		LOGGER.debug(LOG_PREFIX + "undoGenerateInvoice ***** ***** [option=" + option + "] ***** *****");

		final List<String> invoiceList = new ArrayList<>();

		final String invoice = "Undo invoice.";
		invoiceList.add(invoice);
		
		return invoiceList;
	}
}