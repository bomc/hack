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
package de.bomc.poc.axon.interfaces.rest;

import org.apache.log4j.Logger;
import org.axonframework.commandhandling.callbacks.LoggingCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.interceptors.EventLoggingInterceptor;

import de.bomc.poc.axon.domain.model.shared.ProductInfo;
import de.bomc.poc.axon.infrastructure.events.order.FileOrderCommand;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import java.lang.invoke.MethodHandles;
import java.util.UUID;

/**
 * This REST endpoint is the boundary of the service.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
@Path("/api")
@Consumes("application/xml")
@Produces("text/plain")
public class OrderControllerEndpoint {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "OrderControllerEndpoint#";
	@SuppressWarnings("unused")
	private static final String FAIL_FLAG_SHIPMENT = "failShipment";
	@SuppressWarnings("unused")
	private static final String DELAY_FLAG_SHIPMENT = "delayShipment";
	@SuppressWarnings("unused")
	private static final String REGULAR_FLAG = "testShipment";
	@SuppressWarnings("unused")
	private static final String DELAY_FLAG_INVOICE = "delayInvoice";
	@SuppressWarnings("unused")
	private static final String FAIL_FLAG_INVOICE = "failInvoice";
	@Inject
	private EventBus eventBus;
	@Inject
	private CommandGateway commandGateway;

	@PostConstruct
	public void init() {
		LOGGER.debug(LOG_PREFIX + "init - Initializing application, and sending event.");

		eventBus.registerDispatchInterceptor(new EventLoggingInterceptor());
	}

	/**
	 * Create a order by the given product id.
	 * 
	 * @param productId
	 *            the given product id.
	 * @return the order id.
	 */
	@GET
	@Path("/order/{productId}")
	public String createOrder(@PathParam("productId") String productId) {
		LOGGER.debug(LOG_PREFIX + "createOrder [pathParam.productId=" + productId + "]");

		// Create a unique order id.
		final String orderId = UUID.randomUUID().toString();

		// Create a order command.
		final ProductInfo productInfo = new ProductInfo(productId, "enchilada", 100);
		final FileOrderCommand fileOrderCommand = new FileOrderCommand(orderId, productInfo);

		// Send the command to the command bus.
		commandGateway.send(fileOrderCommand, LoggingCallback.INSTANCE);

		LOGGER.info(LOG_PREFIX + "createOrder finished - [productId=" + productId + ", orderId=" + orderId + "]");

		return "OrderAggregate posted - [productId=" + productId + ", orderId=" + orderId + "]";
	}
}
