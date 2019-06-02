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
package de.bomc.poc.axon.domain.model.invoice;

import org.apache.log4j.Logger;
import org.axonframework.cdi.stereotype.Aggregate;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;

import de.bomc.poc.axon.infrastructure.events.invoice.CompensateInvoiceCommand;
import de.bomc.poc.axon.infrastructure.events.invoice.InvoiceCompensatedEvent;
import de.bomc.poc.axon.infrastructure.events.invoice.InvoicePreparationFailedEvent;
import de.bomc.poc.axon.infrastructure.events.invoice.InvoicePreparedEvent;
import de.bomc.poc.axon.infrastructure.events.invoice.PrepareInvoiceCommand;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;
import static org.axonframework.commandhandling.model.AggregateLifecycle.markDeleted;

/**
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
@Entity
@Aggregate
public class InvoiceAggregate {

	private static final Logger LOGGER = Logger.getLogger(InvoiceAggregate.class);
	private static final String LOG_PREFIX = "InvoiceAggregate#";
	@Id
	@AggregateIdentifier
	private String invoiceId;
	// @AggregateIdentifier
	private String orderId;

	public InvoiceAggregate() {
		// Used by CDI-Provider.
	}

	@CommandHandler
	public InvoiceAggregate(final PrepareInvoiceCommand prepareInvoiceCommand) throws InterruptedException {
		LOGGER.info(LOG_PREFIX + "co#CommandHandler - received PrepareInvoiceCommand command for order: "
				+ prepareInvoiceCommand.getOrderId());

		this.invoiceId = UUID.randomUUID().toString();

		if (prepareInvoiceCommand.getProductInfo().getProductId().equals("failInvoice")) {
			// Simulate saga compensation.
			LOGGER.info(LOG_PREFIX + "co#CommandHandler - failing invoice creation");
			apply(new InvoicePreparationFailedEvent(this.invoiceId, prepareInvoiceCommand.getOrderId(),
					"simulated saga fail"));
		} else {
			if (prepareInvoiceCommand.getProductInfo().getProductId().equals("delayInvoice")) {
				// Simulate a delay.
				final long delay = 30000L;
				LOGGER.info(LOG_PREFIX + "co#CommandHandler - delayInvoice [delay=" + delay + "]");
				Thread.sleep(delay);
			}

			// Generate invoice.
			final String invoice = generateInvoice();

			apply(new InvoicePreparedEvent(this.invoiceId, prepareInvoiceCommand.getOrderId(), invoice));
		}
	}

	private String generateInvoice() {
		return "This is just the invoice stub";
	}

	@CommandHandler
	public void handle(final CompensateInvoiceCommand command) {
		LOGGER.debug(LOG_PREFIX + "handle#CommandHandler - receivedCompensateInvoiceCommand command");

		markDeleted();

		apply(new InvoiceCompensatedEvent(this.invoiceId, this.orderId, command.getCause()));
	}

	@EventSourcingHandler
	public void on(final InvoicePreparedEvent invoicePreparedEvent) {
		LOGGER.debug(LOG_PREFIX + "on#EventSourcingHandler [invoicePreparedEvent=" + invoicePreparedEvent + "]");

		this.invoiceId = invoicePreparedEvent.getInvoiceId();
		this.orderId = invoicePreparedEvent.getOrderId();
	}

	@EventSourcingHandler
	public void on(final InvoicePreparationFailedEvent invoicePreparationFailedEvent) {
		LOGGER.debug(LOG_PREFIX + "on#EventSourcingHandler [invoicePreparationFailedEvent="
				+ invoicePreparationFailedEvent + "]");

		this.invoiceId = invoicePreparationFailedEvent.getInvoiceId();
		this.orderId = invoicePreparationFailedEvent.getOrderId();
	}

	public InvoiceAggregate(final String invoiceId, final String orderId) {
		LOGGER.debug(LOG_PREFIX + "co [invoiceId=" + invoiceId + ", orderId=" + orderId + "]");

		this.invoiceId = invoiceId;
		this.orderId = orderId;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

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

		final InvoiceAggregate other = (InvoiceAggregate) obj;
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
		return "InvoiceAggregate [invoiceId=" + invoiceId + ", orderId=" + orderId + "]";
	}
}
