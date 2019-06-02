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
package de.bomc.poc.axon.infrastructure.events.invoice;

import java.lang.invoke.MethodHandles;

import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
public class InvoicePreparationFailedEvent {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "InvoicePreparationFailedEvent#";
	private String orderId;
	private String invoiceId;
	private String cause;

	public InvoicePreparationFailedEvent() {
		// Used by CDI.
	}

	/**
	 * @param orderId
	 * @param invoiceId
	 * @param cause
	 */
	public InvoicePreparationFailedEvent(final String invoiceId, final String orderId, final String cause) {
		LOGGER.debug(LOG_PREFIX + "co - [invoiceId=" + invoiceId + ", orderId=" + orderId + ", cause=" + cause + "]");

		this.orderId = orderId;
		this.invoiceId = invoiceId;
		this.cause = cause;
	}

	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(final String orderId) {
		this.orderId = orderId;
	}

	public String getInvoiceId() {
		return this.invoiceId;
	}

	public void setInvoiceId(final String invoiceId) {
		this.invoiceId = invoiceId;
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
		result = prime * result + ((invoiceId == null) ? 0 : invoiceId.hashCode());
		result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());

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

		final InvoicePreparationFailedEvent other = (InvoicePreparationFailedEvent) obj;

		if (cause == null) {
			if (other.cause != null) {
				return false;
			}
		} else if (!cause.equals(other.cause)) {
			return false;
		}

		if (invoiceId == null) {
			if (other.invoiceId != null) {
				return false;
			}
		} else if (!invoiceId.equals(other.invoiceId)) {
			return false;
		}

		if (orderId == null) {
			if (other.orderId != null) {
				return false;
			}
		} else if (!orderId.equals(other.orderId)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "InvoicePreparationFailedEvent [orderId=" + this.orderId + "invoiceId=" + this.invoiceId + ", cause="
				+ this.cause + "]";
	}
}
