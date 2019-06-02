package de.bomc.poc.events.entity;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import javax.json.JsonObject;

/**
 * A specific event that performs a specific task
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
public class CreateEvent extends AppEvent {

	private final EventInfo eventInfo;

	public CreateEvent(final EventInfo eventInfo) {
		this.eventInfo = eventInfo;
	}

	public CreateEvent(final EventInfo eventInfo, final Instant instant) {
		super(instant);
		this.eventInfo = eventInfo;
	}

	public CreateEvent(final JsonObject jsonObject) {
		this(new EventInfo(jsonObject.getJsonObject("eventInfo")), Instant.parse(jsonObject.getString("instant")));
	}

	public EventInfo getEventInfo() {
		return eventInfo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = super.hashCode();
		result = prime * result + ((eventInfo == null) ? 0 : eventInfo.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		CreateEvent other = (CreateEvent) obj;
		if (eventInfo == null) {
			if (other.eventInfo != null) {
				return false;
			}
		} else if (!eventInfo.equals(other.eventInfo)) {
			return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		return "CreateEvent [" + eventInfo + ", instant=" + DateTimeFormatter.ISO_INSTANT.format(instant) + "]";
	}

}
