package de.bomc.registrator.events.control;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import org.apache.log4j.Logger;

import de.bomc.registrator.events.entity.AppEvent;

/**
 * Produces a event, the event will be sent to the kafka middleware.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
public class EventJsonbSerializer implements JsonbSerializer<AppEvent> {
	
	private static final String LOG_PREFIX = "EventJsonbSerializer#";
	
	private Logger logger = Logger.getLogger(EventJsonbSerializer.class);
	
	@Override
	public void serialize(final AppEvent event, final JsonGenerator generator, final SerializationContext ctx) {
		this.logger.debug(LOG_PREFIX + "serialize");

		generator.writeStartObject();
        generator.write("class", event.getClass().getCanonicalName());
        ctx.serialize("data", event, generator);
        generator.writeEnd();
        generator.close();
    }

}
