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
package de.bomc.poc.axon.domain.model.shipment;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;
import static org.axonframework.commandhandling.model.AggregateLifecycle.markDeleted;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.log4j.Logger;
import org.axonframework.cdi.stereotype.Aggregate;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;

import de.bomc.poc.axon.application.rest.shipment.ShipmentHystrixCommand;
import de.bomc.poc.axon.application.rest.shipment.ShipmentHystrixResult;
import de.bomc.poc.axon.application.rest.shipment.UndoShipmentHystrixCommand;
import de.bomc.poc.axon.infrastructure.events.shipment.CompensateShipmentCommand;
import de.bomc.poc.axon.infrastructure.events.shipment.PrepareShipmentCommand;
import de.bomc.poc.axon.infrastructure.events.shipment.ShipmentCompensatedEvent;
import de.bomc.poc.axon.infrastructure.events.shipment.ShipmentPreparationFailedEvent;
import de.bomc.poc.axon.infrastructure.events.shipment.ShipmentPreparedEvent;
import de.bomc.poc.axon.interfaces.rest.OrderControllerServiceEndpoint;

/**
 * Aggregate for managing the shipment domain.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
@Entity
@Aggregate
public class ShipmentAggregate {

	private static final Logger LOGGER = Logger.getLogger(ShipmentAggregate.class);
	private static final String LOG_PREFIX = "ShipmentAggregate#";
	@Id
	@AggregateIdentifier
	private String shipmentId;
	private String orderId;
	private String productName;
	private int price;

	public ShipmentAggregate() {
		// Used by JPA-Provider.
	}

	/**
	 * This aggregate is created when <code>PrepareShipmentCommand</code> is
	 * created. A <code>ShipmentPreparedEvent</code> will be produced.
	 * 
	 * @param command
	 * @throws InterruptedException
	 */
	@CommandHandler
	public ShipmentAggregate(final PrepareShipmentCommand prepareShipmentCommand) throws InterruptedException {
		LOGGER.info(LOG_PREFIX + "co#CommandHandler - received PrepareShipmentCommand [prepareShipmentCommand="
				+ prepareShipmentCommand + "]");

		this.shipmentId = prepareShipmentCommand.getShipmentId();
		this.orderId = prepareShipmentCommand.getOrderId();
		this.productName = prepareShipmentCommand.getProductInfo().getProductName();

		// ___________________________________________
		// Invoke the remote service.
		// -------------------------------------------
		final ShipmentHystrixCommand shipmentHystrixCommand = new ShipmentHystrixCommand();
		shipmentHystrixCommand.setPayload(prepareShipmentCommand.getProductInfo().getProductId());
		final ShipmentHystrixResult shipmentHystrixResult = shipmentHystrixCommand.execute();

		LOGGER.debug(LOG_PREFIX + "co - [shipmentHystrixResult=" + shipmentHystrixResult + "]");

		if (shipmentHystrixResult.getStatus().equals("42")) {
			//
			// A correct response from shipment service is received.
			apply(new ShipmentPreparedEvent(shipmentId, this.orderId, this.productName,
					Integer.valueOf(shipmentHystrixResult.getStatus())));
		} else {
			//
			// Start simulating a saga compensating.
			apply(new ShipmentPreparationFailedEvent(this.shipmentId, prepareShipmentCommand.getOrderId(),
					"simulated saga fail"));
		}
	}

	@CommandHandler
	public void handle(final CompensateShipmentCommand compensateShipmentCommand) {
		LOGGER.debug(LOG_PREFIX + "handle - received CompensateShipmentCommand command [compensateShipmentCommand="
				+ compensateShipmentCommand + "]");

		markDeleted();

		// Compute shipment by remote service.
		final UndoShipmentHystrixCommand undoShipmentHystrixCommand = new UndoShipmentHystrixCommand();
		undoShipmentHystrixCommand.setPayload(OrderControllerServiceEndpoint.FAIL_FLAG_SHIPMENT);
		final ShipmentHystrixResult shipmentHystrixResult = undoShipmentHystrixCommand.execute();

		LOGGER.info(LOG_PREFIX + "co - shipment undo service returns. [shipment=" + shipmentHystrixResult.getStatus()
				+ "]");

		apply(new ShipmentCompensatedEvent(shipmentId, orderId, compensateShipmentCommand.getCause()));
	}

	@EventSourcingHandler
	public void on(final ShipmentPreparedEvent shipmentPreparedEvent) {
		LOGGER.debug(LOG_PREFIX + "on#EventSourcingHandler + [shipmentPreparedEvent=" + shipmentPreparedEvent + "]");

		this.shipmentId = shipmentPreparedEvent.getShipmentId();
		this.orderId = shipmentPreparedEvent.getOrderId();
		this.price = shipmentPreparedEvent.getPrice();
		this.productName = shipmentPreparedEvent.getProductName();
	}

	@EventSourcingHandler
	public void on(final ShipmentPreparationFailedEvent shipmentPreparationFailedEvent) {
		LOGGER.debug(LOG_PREFIX + "on#EventSourcingHandler [shipmentPreparationFailedEvent="
				+ shipmentPreparationFailedEvent + "]");

		this.shipmentId = shipmentPreparationFailedEvent.getShipmentId();
		this.orderId = shipmentPreparationFailedEvent.getOrderId();
	}

	public String getId() {
		return this.shipmentId;
	}

	public void setId(String id) {
		this.shipmentId = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
		result = prime * result + price;
		result = prime * result + ((productName == null) ? 0 : productName.hashCode());
		result = prime * result + ((shipmentId == null) ? 0 : shipmentId.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final ShipmentAggregate other = (ShipmentAggregate) obj;
		if (orderId == null) {
			if (other.orderId != null) {
				return false;
			}
		} else if (!orderId.equals(other.orderId)) {
			return false;
		}

		if (price != other.price) {
			return false;
		}

		if (productName == null) {
			if (other.productName != null) {
				return false;
			}
		} else if (!productName.equals(other.productName)) {
			return false;
		}

		if (shipmentId == null) {
			if (other.shipmentId != null) {
				return false;
			}
		} else if (!shipmentId.equals(other.shipmentId)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "ShipmentAggregate [shipmentId=" + shipmentId + ", orderId=" + orderId + ", productName=" + productName
				+ ", price=" + price + "]";
	}
}
