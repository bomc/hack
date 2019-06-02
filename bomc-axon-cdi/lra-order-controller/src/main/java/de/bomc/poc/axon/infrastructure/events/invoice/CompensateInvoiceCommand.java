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

import org.apache.log4j.Logger;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.lang.invoke.MethodHandles;

/**
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
public class CompensateInvoiceCommand {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "CompensateInvoiceCommand#";
	@TargetAggregateIdentifier
	private String orderId;
	private String cause;

	/**
	 *
	 */
	public CompensateInvoiceCommand() {
		// Used by CDI
	}

	/**
	 * @param orderId
	 * @param cause
	 */
	public CompensateInvoiceCommand(final String orderId, final String cause) {
		LOGGER.debug(LOG_PREFIX + "co [orderId=" + orderId + ", cause=" + cause + "]");

		this.orderId = orderId;
		this.cause = cause;
	}

	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(final String orderId) {
		this.orderId = orderId;
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

		final CompensateInvoiceCommand other = (CompensateInvoiceCommand) obj;

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
		return "CompensateInvoiceCommand [orderId=" + this.orderId + ", cause=" + this.cause + "]";
	}
}
