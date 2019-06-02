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
public class ShipmentServiceEndpoint {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "ShipmentServiceEndpoint#";
	private static final String FAIL_FLAG_SHIPMENT = "failShipment";
	private static final String DELAY_FLAG_SHIPMENT = "delayShipment";
	@SuppressWarnings("unused")
	private static final String REGULAR_FLAG = "testShipment";

	/**
	 * Compute the shipment.
	 * 
	 * @return the computed shipment.
	 */
	@GET
	@Path("/compute/{option}")
	public List<String> computeShipment(@PathParam("option") String option) {
		LOGGER.debug(LOG_PREFIX + "computeShipment ***** ##### ***** [option=" + option + "] ***** ##### *****");

		final List<String> shipmentList = new ArrayList<String>(1);
		
		// Set computed return value.
		String shipment = "42";

		if (option.equals(FAIL_FLAG_SHIPMENT)) {
			//
			// Return the option to simulate a failed saga.
			shipment = option;
		} else if (option.equals(DELAY_FLAG_SHIPMENT)) {
			//
			// Sleep for a while...
			try {
				TimeUnit.SECONDS.sleep(30000L);
			} catch (final InterruptedException e) {
				// Ignore
			}

			// Return the option to simulate a delayed saga. 
			shipment = option;
		}

		shipmentList.add(shipment);
		
		return shipmentList;
	}
	
	/**
	 * Compute the shipment.
	 * 
	 * @return the computed shipment.
	 */
	@DELETE
	@Path("/undo/compute/{option}")
	public List<String> undoComputeShipment(@PathParam("option") String option) {
		LOGGER.debug(LOG_PREFIX + "undoComputeShipment ***** ***** [option=" + option + "] ***** *****");

		final List<String> shipmentList = new ArrayList<String>(1);
		
		// Set computed return value.
		final String shipment = "undo shipment";

		shipmentList.add(shipment);
		
		return shipmentList;
	}
}