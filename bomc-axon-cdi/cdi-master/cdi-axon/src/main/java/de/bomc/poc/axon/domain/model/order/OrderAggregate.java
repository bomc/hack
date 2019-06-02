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
package de.bomc.poc.axon.domain.model.order;

import org.apache.log4j.Logger;
import org.axonframework.cdi.stereotype.Aggregate;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;

import de.bomc.poc.axon.infrastructure.events.order.FileOrderCommand;
import de.bomc.poc.axon.infrastructure.events.order.OrderCompletedCommand;
import de.bomc.poc.axon.infrastructure.events.order.OrderCompletedEvent;
import de.bomc.poc.axon.infrastructure.events.order.OrderFiledEvent;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;

import javax.persistence.Entity;
import javax.persistence.Id;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

/**
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
@Entity
@Aggregate
public class OrderAggregate implements Serializable {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -7476142761363128747L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "OrderAggregate#";
	// An Aggregate Root must declare a field that contains the Aggregate
	// identifier. This identifier must be initialized at the latest when the
	// first Event is published. This identifier field must be annotated by the
	// @AggregateIdentifier annotation. If you use JPA and have JPA annotations
	// on the aggregate, Axon can also use the @Id annotation provided by JPA.
	@Id
	@AggregateIdentifier
	private String orderId;
	private boolean completed;

	/**
	 * Creates a new instance of <code>OrderAggregate</code>{@link #hashCode().
	 */
	public OrderAggregate() {
		// Default constructor is needed so Axon can instantiate the Aggregate
		// and apply all sourced events.
		//
		// Also JPA needs the default constructor, if jpa is used.
	}

	/**
	 * CommandHandler: annotation that is put on methods/constructors that
	 * handle commands. When an <code>FileOrderCommand</code> is dispatched,
	 * annotated constructor will be invoked. All business logic / rules are
	 * defined here and all state changes are defined in the
	 * EventSourcingHandlers. The reason for this is when we want to get the
	 * current state of event-sourced Aggregate, we have to apply all sourced
	 * events - we have to invoke EventSourcingHandlers. If the state of our
	 * Aggregate is changed outside of EventSourcingHandlers it will not be
	 * reflected when we do a replay.
	 * 
	 * @param fileOrderCommand
	 *            the given fileOrderCommand.
	 */
	@CommandHandler
	public OrderAggregate(final FileOrderCommand fileOrderCommand) {
		LOGGER.info(LOG_PREFIX + "co#CommandHandler [fileOrderCommand=" + fileOrderCommand + "]");

		// Register events for publication.
		// Invoking apply method will apply method on given
		// aggregate (EventSourcingHandler matching this event will be called
		// on aggregate), and then it will be published to the EventBus, so
		// other components can react upon it.
		apply(new OrderFiledEvent(fileOrderCommand.getOrderId(), fileOrderCommand.getProductInfo()));
	}

	/**
	 * Handle a <code>OrderFieldEvent</code>. By defining an EventHandler
	 * annotated method. The method will be invoked when an EventMessage is
	 * published (before any external handlers are published). State changes in
	 * Event Sourced Aggregates (i.e. any change of a Field value) must be
	 * exclusively performed in an EventSourcingHandler annotated method. This
	 * includes setting the Aggregate Identifier.
	 * 
	 * @param orderFiledEvent
	 *            the given orderFiledEvent
	 */
	@EventSourcingHandler
	public void on(final OrderFiledEvent orderFiledEvent) {
		LOGGER.debug(LOG_PREFIX + "on#EventSourcingHandler [orderFiledEvent=" + orderFiledEvent + "]");

		// Setting the aggregate identifier.
		this.orderId = orderFiledEvent.getOrderId();
	}

	@CommandHandler
	public void handle(final OrderCompletedCommand orderCompletedCommand) {
		LOGGER.debug(LOG_PREFIX + "handle#CommandHandler [orderCompletedCommand=" + orderCompletedCommand + "]");

		apply(new OrderCompletedEvent(orderCompletedCommand.getOrderId(), orderCompletedCommand.getProductInfo()));
	}

	@EventSourcingHandler
	public void on(final OrderCompletedEvent orderCompletedEvent) {
		LOGGER.debug(LOG_PREFIX + "on#EventSourcingHandler [orderCompletedEvent=" + orderCompletedEvent + "]");

		this.completed = true;
	}
	
	// _______________________________________________
	// Getter and setter methods.
	// -----------------------------------------------

	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(final String orderId) {
		this.orderId = orderId;
	}

	public boolean isCompleted() {
		return this.completed;
	}

	public void setCompleted(final boolean completed) {
		this.completed = completed;
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

		final OrderAggregate other = (OrderAggregate) obj;

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
		return "OrderAggregate [orderId=" + orderId + ", completed=" + completed + "]";
	}
}
