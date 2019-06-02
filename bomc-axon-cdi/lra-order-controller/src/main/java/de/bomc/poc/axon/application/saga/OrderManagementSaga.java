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
package de.bomc.poc.axon.application.saga;

import java.lang.invoke.MethodHandles;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.axonframework.cdi.stereotype.Saga;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;

import de.bomc.poc.axon.domain.model.shared.ProductInfo;
import de.bomc.poc.axon.infrastructure.events.invoice.CompensateInvoiceCommand;
import de.bomc.poc.axon.infrastructure.events.invoice.InvoiceCompensatedEvent;
import de.bomc.poc.axon.infrastructure.events.invoice.InvoicePreparationFailedEvent;
import de.bomc.poc.axon.infrastructure.events.invoice.InvoicePreparedEvent;
import de.bomc.poc.axon.infrastructure.events.invoice.PrepareInvoiceCommand;
import de.bomc.poc.axon.infrastructure.events.order.OrderCompletedCommand;
import de.bomc.poc.axon.infrastructure.events.order.OrderCreateEvent;
import de.bomc.poc.axon.infrastructure.events.shipment.CompensateShipmentCommand;
import de.bomc.poc.axon.infrastructure.events.shipment.PrepareShipmentCommand;
import de.bomc.poc.axon.infrastructure.events.shipment.ShipmentCompensatedEvent;
import de.bomc.poc.axon.infrastructure.events.shipment.ShipmentPreparationFailedEvent;
import de.bomc.poc.axon.infrastructure.events.shipment.ShipmentPreparedEvent;

/**
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
@Saga
public class OrderManagementSaga {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "OrderManagementSaga#";
	private final OrderProcessing orderProcessing = new OrderProcessing();
	private final OrderCompensationProcessing compensationProcessing = new OrderCompensationProcessing();
	private boolean isCompensating = false;
	private String orderId;
	private String shipmentId;
	private String invoiceId;
	private ProductInfo productInfo;
	@Inject
	private CommandGateway commandGateway;
	
	public OrderManagementSaga() {
		// Used by CDI.
	}
	
	/**
	 * Starts the order process with a <code>OrderCreateEvent</code>.
	 * 
	 * @param orderFiledEvent
	 *            the given orderFiledEvent.
	 */
	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void on(final OrderCreateEvent orderFiledEvent) {
		LOGGER.info(LOG_PREFIX + "on - STARTING SAGA - [orderId=" + orderFiledEvent.getOrderId() + "]");

		this.orderId = orderFiledEvent.getOrderId();
		this.productInfo = orderFiledEvent.getProductInfo();

		// Create a shipment id and start a 'PrepareShipmentCommand', target is the ShipmentAggregate.
		shipmentId = UUID.randomUUID().toString();
		this.commandGateway.send(new PrepareShipmentCommand(this.orderId, shipmentId, this.productInfo));
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void on(final ShipmentPreparedEvent event) {
		LOGGER.info(LOG_PREFIX + "on - 'ShipmentPreparedEvent' [orderId=" + event.getOrderId() + "]");

		if (!this.isCompensating) {
			//
			// Not compensating.
			LOGGER.debug(LOG_PREFIX
					+ "on - 'ShipmentPreparedEvent' is successfully finished, start 'PrepareInvoiceCommand' - [isCompensating="
					+ this.isCompensating + " ('true' means a compensating action is performed)]");

			// Set shipment process to finished.
			this.orderProcessing.setShipmentProcessed(true);
			// Generate a new invoice id and start a 'PrepareInvoiceCommand'.
			invoiceId = UUID.randomUUID().toString();
			this.commandGateway.send(new PrepareInvoiceCommand(this.orderId, invoiceId, this.productInfo));
		}
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void on(final InvoicePreparedEvent event) {
		LOGGER.info(LOG_PREFIX + "on - 'InvoicePreparedEvent' - [invoideId=" + event.getInvoiceId() + "]");

		if (!this.isCompensating) {
			//
			// Not compensating.
			LOGGER.debug(LOG_PREFIX + "on - 'InvoicePreparedEvent' - [isCompensating=" + this.isCompensating
					+ " ('true' means a compensating action is performed)]");

			this.orderProcessing.setInvoiceProcessed(true);

			this.checkSagaCompleted();
		}
	}

	private void checkSagaCompleted() {
		LOGGER.info(LOG_PREFIX + "checkSagaCompleted");

		if (this.orderProcessing.isDone()) {
			LOGGER.debug(LOG_PREFIX + "checkSagaCompleted - saga executed successfully");

			this.endSaga();

			this.commandGateway.send(new OrderCompletedCommand(orderId, productInfo));
		}
	}
	
	@EndSaga
	private void endSaga() {
		LOGGER.info(LOG_PREFIX + "endSage - ENDING SAGA");
	}
	
	// _______________________________________________
	// Compensations
	// -----------------------------------------------

	@SagaEventHandler(associationProperty = "orderId")
	public void on(final ShipmentPreparationFailedEvent shipmentPreparationFailedEvent) {
		LOGGER.info(LOG_PREFIX + "on - [shipmentPreparationFailedEvent=" + shipmentPreparationFailedEvent + "]");

		this.compensateSagaShipment(shipmentPreparationFailedEvent.getShipmentId(),
				shipmentPreparationFailedEvent.getCause());
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void on(final InvoicePreparationFailedEvent invoicePreparationFailedEvent) {
		LOGGER.info(LOG_PREFIX + "#on - [invoicePreparationFailedEvent=" + invoicePreparationFailedEvent + "]");

		this.compensateSagaInvoice(invoicePreparationFailedEvent.getInvoiceId(),
				invoicePreparationFailedEvent.getCause());
	}

	private void compensateSagaShipment(final String shipmentId, final String cause) {
		LOGGER.info(String.format(
				LOG_PREFIX + "compensateSagaShipment - compensation of saga for model [%s] with cause - '%s'",
				shipmentId, cause));

		this.isCompensating = true;
		
		// Set invoice compensating to true, because no hanlding for invoice is necessary. 
		this.compensationProcessing.setInvoiceCompensated(true);
		
		this.commandGateway.send(new CompensateShipmentCommand(shipmentId, cause));
	}

	private void compensateSagaInvoice(final String invoiceId, final String cause) {
		LOGGER.info(String.format(
				LOG_PREFIX + "compensateSagaInvoice - compensation of saga for model [%s] with cause - '%s'", invoiceId,
				cause));

		this.isCompensating = true;

		// Compensate shipment and invoice.
		this.commandGateway.send(new CompensateInvoiceCommand(invoiceId, cause));
		this.commandGateway.send(new CompensateShipmentCommand(this.shipmentId, cause));
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void on(ShipmentCompensatedEvent event) {
		LOGGER.info(LOG_PREFIX + "on - ShipmentCompensatedEvent");

		this.compensationProcessing.setShipmentCompensated(true);

		this.checkSagaCompensated();
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void on(InvoiceCompensatedEvent event) {
		LOGGER.info(LOG_PREFIX + "on - InvoiceCompensatedEvent");

		this.compensationProcessing.setInvoiceCompensated(true);

		this.checkSagaCompensated();
	}

	private void checkSagaCompensated() {
		LOGGER.info(LOG_PREFIX + "checkSagaCompensated");

		if (this.compensationProcessing.isCompensated()) {
			LOGGER.info(LOG_PREFIX + "checkSagaCompensated - saga fully compensated");

			this.endSaga();
		}
	}

	// _______________________________________________
	// Inner class
	// -----------------------------------------------

	private static class OrderProcessing {

		private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
		private static final String LOG_PREFIX = "OrderProcessing#";
		private boolean shipmentProcessed;
		private boolean invoiceProcessed;

		public OrderProcessing() {
		}

		public void setShipmentProcessed(boolean shipmentProcessed) {
			this.shipmentProcessed = shipmentProcessed;
		}

		public void setInvoiceProcessed(boolean invoiceProcessed) {
			this.invoiceProcessed = invoiceProcessed;
		}

		public boolean isDone() {
			LOGGER.debug(LOG_PREFIX + "isDone - [shipmentProcessed=" + this.shipmentProcessed
					+ " && invoiceProcessed=" + this.invoiceProcessed + " == "
					+ (this.shipmentProcessed && this.invoiceProcessed) + "]");

			return this.shipmentProcessed && this.invoiceProcessed;
		}
	}

	private static class OrderCompensationProcessing {

		private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
		private static final String LOG_PREFIX = "OrderCompensationProcessing#";
		private boolean shipmentCompensated;
		private boolean invoiceCompensated;

		public void setShipmentCompensated(boolean shipmentCompensated) {
			this.shipmentCompensated = shipmentCompensated;
		}

		public void setInvoiceCompensated(boolean invoiceCompensated) {
			this.invoiceCompensated = invoiceCompensated;
		}

		public boolean isCompensated() {
			LOGGER.debug(LOG_PREFIX + "isCompensated - [shipmentCompensated="
					+ this.shipmentCompensated + " && invoiceCompensated=" + this.invoiceCompensated + " == "
					+ (this.shipmentCompensated && this.invoiceCompensated) + "]");

			return shipmentCompensated && invoiceCompensated;
		}
	}
}
