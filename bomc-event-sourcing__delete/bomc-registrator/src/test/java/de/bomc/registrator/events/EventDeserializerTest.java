package de.bomc.registrator.events;

import de.bomc.registrator.events.control.EventDeserializer;
import de.bomc.registrator.events.entity.CreateEvent;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventDeserializerTest {

	private static final String LOG_PREFIX = "EventDeserializerTest#";
	private static final Logger LOGGER = Logger.getLogger(EventDeserializerTest.class);

	@Parameterized.Parameter
	public String data;

	@Parameterized.Parameter(1)
	public CreateEvent expected;

	private EventDeserializer cut = new EventDeserializer();

	@Test
	public void test010_deserialize_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_deserialize_pass");

		final CreateEvent actual = (CreateEvent) cut.deserialize(null, data.getBytes(StandardCharsets.UTF_8));

		assertEventEquals(actual, expected);
	}

	private void assertEventEquals(final CreateEvent actual, final CreateEvent expected) {
		assertThat(expected, equalTo(actual));
	}

	@Parameterized.Parameters
	public static Collection<Object[]> testData() {
		return TestData.eventTestData();
	}

}
