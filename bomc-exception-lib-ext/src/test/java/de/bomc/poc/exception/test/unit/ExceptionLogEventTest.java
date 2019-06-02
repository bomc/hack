/**
 * Project: bomc-exception-lib-ext
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.exception.test.unit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.bomc.poc.exception.core.event.ExceptionLogEvent;

/**
 * Tests the {@link ExceptionLogEvent} builder.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExceptionLogEventTest {

	private static final Logger LOGGER = Logger.getLogger(ExceptionLogEventTest.class);
	private static final String LOG_PREFIX = "ExceptionLogEventTest#";

	private final String SHORT_ERROR_CODE_DESCRIPTION = "shortErrorCodeDescription";
	private final String RESPONSE_STATUS = "responseStatus";
	private final String UUID = "uuid";
	private final String CATEGORY = "category";

	@Test
	public void test010_exceptionBuilder() {
		LOGGER.debug(LOG_PREFIX + "test010_exceptionBuilder");

		final ExceptionLogEvent e = ExceptionLogEvent.category(CATEGORY)
				.shortErrorCodeDescription(SHORT_ERROR_CODE_DESCRIPTION).responseStatus(RESPONSE_STATUS).uuid(UUID)
				.build();

		LOGGER.debug(LOG_PREFIX + "test010_exceptionBuilder - " + e);

		// Test the builder implementation.
		assertThat(e.getCategory(), equalTo(CATEGORY));
		assertThat(e.getResponseStatus(), equalTo(RESPONSE_STATUS));
		assertThat(e.getShortErrorCodeDescription(), equalTo(SHORT_ERROR_CODE_DESCRIPTION));
		assertThat(e.getUuid(), equalTo(UUID));

		final ExceptionLogEvent e2 = ExceptionLogEvent.category(CATEGORY)
				.shortErrorCodeDescription(SHORT_ERROR_CODE_DESCRIPTION).responseStatus(RESPONSE_STATUS).uuid(UUID)
				.build();

		// Checks equals.
		assertThat(e2.equals(e), equalTo(true));

		final Set<ExceptionLogEvent> map = new HashSet<ExceptionLogEvent>(1);
		map.add(e);
		map.add(e2);
				
		assertThat(map.size(), equalTo(1));
	}
}
