package de.bomc.poc.events.command;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.events.control.EventProducer;
import de.bomc.poc.events.entity.CreateEvent;
import de.bomc.poc.events.entity.EventInfo;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

/**
 * A command that uses the {@link EventProducer} to send a message to kafka.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
public class EventCommandService {

	private static final String LOG_PREFIX = "EventCommandService#";

	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	private EventProducer eventProducer;

	public void doEvent(final EventInfo eventInfo) {
		this.logger.debug(LOG_PREFIX + "doEvent [eventInfo.eventId=" + eventInfo.getEventId() + "]");

		eventProducer.publish(new CreateEvent(eventInfo));
	}
	
	public void doReceiveEvent(final CreateEvent createInfo) {
		this.logger.debug(LOG_PREFIX + "doReceiveEvent [createInfo=" + createInfo + "]");
		
		// Do here something with the event.
		// ...
	}

}
