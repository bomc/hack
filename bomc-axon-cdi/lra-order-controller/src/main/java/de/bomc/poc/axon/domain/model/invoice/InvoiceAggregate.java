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

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;
import static org.axonframework.commandhandling.model.AggregateLifecycle.markDeleted;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.log4j.Logger;
import org.axonframework.cdi.stereotype.Aggregate;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;

import de.bomc.poc.axon.application.rest.invoice.InvoiceHystrixCommand;
import de.bomc.poc.axon.application.rest.invoice.InvoiceHystrixResult;
import de.bomc.poc.axon.application.rest.invoice.UndoInvoiceHystrixCommand;
import de.bomc.poc.axon.infrastructure.events.invoice.CompensateInvoiceCommand;
import de.bomc.poc.axon.infrastructure.events.invoice.InvoiceCompensatedEvent;
import de.bomc.poc.axon.infrastructure.events.invoice.InvoicePreparationFailedEvent;
import de.bomc.poc.axon.infrastructure.events.invoice.InvoicePreparedEvent;
import de.bomc.poc.axon.infrastructure.events.invoice.PrepareInvoiceCommand;
import de.bomc.poc.axon.interfaces.rest.OrderControllerServiceEndpoint;

/**
 * Aggregate for invoice handling.
 * 
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
	private String orderId;

	public InvoiceAggregate() {
		// Used by JPA and CDI-provider
	}

	/**
	 * Creates a new instance <code>InvoiceAggregate</code>.
	 * 
	 * @param prepareInvoiceCommand
	 *            the given command.
	 */
	@CommandHandler
	public InvoiceAggregate(final PrepareInvoiceCommand prepareInvoiceCommand) {
		LOGGER.info(LOG_PREFIX + "co#CommandHandler - received PrepareInvoiceCommand command for order: "
				+ prepareInvoiceCommand.getOrderId());

		this.invoiceId = UUID.randomUUID().toString();
		
		// ___________________________________________
		// Invoke the remote service.
		// -------------------------------------------
		final InvoiceHystrixCommand invoiceHystrixCommand = new InvoiceHystrixCommand();
		invoiceHystrixCommand.setPayload(prepareInvoiceCommand.getProductInfo().getProductId());
		// Invoke rest endpoint via hystrix command.
		final InvoiceHystrixResult invoiceHystrixResult = invoiceHystrixCommand.execute();

		LOGGER.debug(LOG_PREFIX + "co - [invoiceHystrixResult=" + invoiceHystrixResult + "]");

		if (invoiceHystrixResult.getStatus().equals(OrderControllerServiceEndpoint.FAIL_FLAG_INVOICE)) {
			//
			// Start saga compensation.
			LOGGER.info(LOG_PREFIX + "co#CommandHandler - failing generating invoice creation, start a compensating.");
			apply(new InvoicePreparationFailedEvent(this.invoiceId, prepareInvoiceCommand.getOrderId(),
					"start simulating a compensating"));
		} else {
			//
			// Send next event to finish the saga.
			apply(new InvoicePreparedEvent(this.invoiceId, prepareInvoiceCommand.getOrderId(),
					invoiceHystrixResult.getStatus()));
		}
	}

	@CommandHandler
	public void handle(final CompensateInvoiceCommand compensateInvoiceCommand) {
		LOGGER.debug(LOG_PREFIX + "handle#CommandHandler - receivedCompensateInvoiceCommand command" + ", "
				+ this.toString());

		markDeleted();

		// Undo generate invoice.
		final UndoInvoiceHystrixCommand undoInvoiceHystrixCommand = new UndoInvoiceHystrixCommand();
		undoInvoiceHystrixCommand.setPayload(compensateInvoiceCommand.getOrderId());
		// Invoke rest endpoint via hystrix command.
		final InvoiceHystrixResult invoiceHystrixResult = undoInvoiceHystrixCommand.execute();

		LOGGER.debug(LOG_PREFIX + "co - [invoiceHystrixResult=" + invoiceHystrixResult + "]");

		apply(new InvoiceCompensatedEvent(this.invoiceId, this.orderId, compensateInvoiceCommand.getCause()));
	}

	@EventSourcingHandler
	public void on(final InvoicePreparedEvent invoicePreparedEvent) {
		LOGGER.debug(LOG_PREFIX + "on#EventSourcingHandler [invoicePreparedEvent=" + invoicePreparedEvent + ", "
				+ this.toString() + "]");

		this.invoiceId = invoicePreparedEvent.getInvoiceId();
		this.orderId = invoicePreparedEvent.getOrderId();
	}

	@EventSourcingHandler
	public void on(final InvoicePreparationFailedEvent invoicePreparationFailedEvent) {
		LOGGER.debug(LOG_PREFIX + "on#EventSourcingHandler [invoicePreparationFailedEvent="
				+ invoicePreparationFailedEvent + ", " + this.toString() + "]");

		this.invoiceId = invoicePreparationFailedEvent.getInvoiceId();
		this.orderId = invoicePreparationFailedEvent.getOrderId();
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
