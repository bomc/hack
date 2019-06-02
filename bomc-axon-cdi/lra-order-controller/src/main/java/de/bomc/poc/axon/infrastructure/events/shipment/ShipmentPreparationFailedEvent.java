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
package de.bomc.poc.axon.infrastructure.events.shipment;

import java.lang.invoke.MethodHandles;

import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
public class ShipmentPreparationFailedEvent {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "ShipmentPreparationFailedEvent#";
	private String orderId;
	private String shipmentId;
	private String cause;

	public ShipmentPreparationFailedEvent() {
		// Used by CDI.
	}

	/**
	 * @param orderId
	 * @param shipmentId
	 * @param cause
	 */
	public ShipmentPreparationFailedEvent(final String shipmentId, final String orderId, final String cause) {
		LOGGER.debug(LOG_PREFIX + "co - [shipmentId=" + shipmentId + ", orderId=" + orderId + ", cause=" + cause + "]");

		this.orderId = orderId;
		this.shipmentId = shipmentId;
		this.cause = cause;
	}

	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(final String orderId) {
		this.orderId = orderId;
	}

	public String getShipmentId() {
		return this.shipmentId;
	}

	public void setShipmentId(final String shipmentId) {
		this.shipmentId = shipmentId;
	}

	public String getCause() {
		return this.cause;
	}

	public void setCause(final String cause) {
		this.cause = cause;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((cause == null) ? 0 : cause.hashCode());
		result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
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

		final ShipmentPreparationFailedEvent other = (ShipmentPreparationFailedEvent) obj;

		if (cause == null) {
			if (other.cause != null) {
				return false;
			}
		} else if (!cause.equals(other.cause)) {
			return false;
		}

		if (orderId == null) {
			if (other.orderId != null) {
				return false;
			}
		} else if (!orderId.equals(other.orderId)) {
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
		return "ShipmentPreparationFailedEvent [orderId=" + orderId + ", shipmentId=" + shipmentId + ", cause=" + cause
				+ "]";
	}
}
