package de.bomc.registrator.events.entity;

import javax.json.bind.annotation.JsonbProperty;
import java.time.Instant;
import java.util.Objects;

/**
 * The super class for all events.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
public abstract class AppEvent {

	@JsonbProperty
	protected final Instant instant;

	protected AppEvent() {
		// Set creation time of this event.
		instant = Instant.now();
	}

	protected AppEvent(final Instant instant) {
		Objects.requireNonNull(instant);

		this.instant = instant;
	}

	public Instant getInstant() {
		return instant;
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final AppEvent that = (AppEvent) o;

		return instant.equals(that.instant);
	}

	@Override
	public int hashCode() {
		return instant.hashCode();
	}

}
