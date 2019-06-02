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
public class InvoiceCompensatedEvent {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "InvoiceCompensatedEvent#";
	private String invoiceId;
	private String orderId;
	private String description;

	public InvoiceCompensatedEvent() {
		// Used by CDI.
	}

	/**
	 * @param invoiceId
	 * @param orderId
	 * @param description
	 */
	public InvoiceCompensatedEvent(final String invoiceId, final String orderId, final String description) {
		LOGGER.debug(LOG_PREFIX + "co - [invoiceId=" + invoiceId + ", orderId=" + orderId + ", description="
				+ description + "]");

		this.invoiceId = invoiceId;
		this.orderId = orderId;
		this.description = description;
	}

	public String getInvoiceId() {
		return this.invoiceId;
	}

	public void setInvoiceId(final String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(final String orderId) {
		this.orderId = orderId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((description == null) ? 0 : description.hashCode());
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

		final InvoiceCompensatedEvent other = (InvoiceCompensatedEvent) obj;

		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
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
		return "InvoiceCompensatedEvent [invoiceId=" + this.invoiceId + "orderId=" + this.orderId + ", description="
				+ this.description + "]";
	}
}
