package de.bomc.registrator.events;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import de.bomc.registrator.events.entity.CreateEvent;
import de.bomc.registrator.events.entity.EventInfo;
import de.bomc.registrator.events.entity.EventType;

/**
 * Provides some test data.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
public final class TestData {

	private TestData() {
		//
		// Prevents instantiation.
	}

	public static List<Object[]> eventTestData() {
		return Arrays.<Object[]>asList(

				new Object[] {
						"{\"class\":\"de.bomc.registrator.events.entity.CreateEvent\",\"data\":{\"instant\":\"2018-07-11T13:36:46.870Z\",\"eventInfo\":{\"eventId\":\"333d930a-f056-40ac-880e-8ed5b826d3f0\",\"payload\":\"bomc_send_event_payload\",\"type\":\"BOMC_EVENT_A\"}}}",
						new CreateEvent(
								new EventInfo(UUID.fromString("333d930a-f056-40ac-880e-8ed5b826d3f0"),
										EventType.BOMC_EVENT_A, "bomc_send_event_payload"),
								Instant.parse("2018-07-11T13:36:46.870Z")) }

		// Add here new test data
		// ...
		);
	}

}
