package de.bomc.registrator.events;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import de.bomc.registrator.events.control.EventSerializer;
import de.bomc.registrator.events.entity.CreateEvent;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link EventSerializer}.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventSerializerTest {

	private static final String LOG_PREFIX = "EventSerializerTest#";
	private static final Logger LOGGER = Logger.getLogger(EventSerializerTest.class);
	
	@Parameterized.Parameter
	public String expected;

	@Parameterized.Parameter(1)
	public CreateEvent event;

	private EventSerializer cut = new EventSerializer();

	@Test
	public void test010_serialize_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_serialize_pass");
		
		final String actual = new String(cut.serialize(null, event), StandardCharsets.UTF_8);

		assertThat(expected, equalTo(actual));
	}

	@Parameterized.Parameters
	public static Collection<Object[]> testData() {
		return TestData.eventTestData();
	}

}
