package de.bomc.poc.events.command;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.config.ConfigurationPropertiesConstants;
import de.bomc.poc.config.qualifier.PropertiesConfigTypeQualifier;
import de.bomc.poc.config.type.PropertiesConfigTypeEnum;
import de.bomc.poc.events.control.EventConsumer;
import de.bomc.poc.events.entity.AppEvent;
import de.bomc.poc.events.entity.CreateEvent;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

/**
 * An event handler that reacts to consumed events and sends new events.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
@Startup
@Singleton
public class EventHandler {

	// _______________________________________________
	// Constants
	// -----------------------------------------------
	private static final String LOG_PREFIX = "EventHandler#";
	private static final String GROUP_ID_VALUE = "bomc-handler";
	// _______________________________________________
	// Member variables.
	// -----------------------------------------------
	@Inject
	@LoggerQualifier
	private Logger logger;
	private EventConsumer eventConsumer;
	@Resource
	private ManagedExecutorService managedExecutorService;
	@Inject
	@PropertiesConfigTypeQualifier(type = PropertiesConfigTypeEnum.KAFKA_CONFIG)
	private Properties kafkaProperties;
	@Inject
	private Event<AppEvent> appEvents;
	@Inject
	private EventCommandService eventCommandService;

	@PostConstruct
	private void initConsumer() {
		this.logger.debug(LOG_PREFIX + "initConsumer");

		kafkaProperties.put(ConfigurationPropertiesConstants.KAFKA_GROUP_ID_KEY, GROUP_ID_VALUE);
		final String bomcTopic = kafkaProperties.getProperty(ConfigurationPropertiesConstants.KAFKA_BOMC_TOPIC_PROPERTY_KEY);

		this.logger.debug(LOG_PREFIX + "initConsumer [topic=" + bomcTopic + ", " + ConfigurationPropertiesConstants.KAFKA_GROUP_ID_KEY + "=" + GROUP_ID_VALUE + "]");

		eventConsumer = new EventConsumer(kafkaProperties, ev -> {
			logger.info(LOG_PREFIX + "initConsumer#firing = " + ev);
			appEvents.fire(ev);
		}, bomcTopic);

		managedExecutorService.execute(eventConsumer);
	}

	@PreDestroy
	public void closeConsumer() {
		this.logger.debug(LOG_PREFIX + "closeConsumer");

		// Cleanup resources.
		eventConsumer.stop();
	}
	
	/**
	 * Observes the events that are fired by the EventConsumer via CDI. The
	 * fired method, see EventHandler.initConsumer -> fire(CreateEvent).
	 * 
	 * @param createEvent
	 *            the incoming event.
	 */
	public void handle(@Observes CreateEvent createEvent) {
		this.logger
				.debug(LOG_PREFIX + "handle incoming event [eventId=" + createEvent.getEventInfo().getEventId() + "]");

		eventCommandService.doReceiveEvent(createEvent);
	}
}
