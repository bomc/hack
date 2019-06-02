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

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * RegEx testing for pattern.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegexPatternTest {

	private static final String LOG_PREFIX = "RegexPatternTest";
	private static final Logger LOGGER = Logger.getLogger(RegexPatternTest.class);

	@Test
	public void test010_mediaType() {
		LOGGER.debug(LOG_PREFIX + "test010_mediaType");

		final String mediaType = "application/vnd.hoko-zid-v1+json";

		String patternString = "^application.*json$";
		assertThat(this.doMatcher(patternString, mediaType), equalTo(true));

		patternString = "^Aapplication.*json$";
		assertThat(this.doMatcher(patternString, mediaType), equalTo(false));

		patternString = "^application.*jsons$";
		assertThat(this.doMatcher(patternString, mediaType), equalTo(false));

		patternString = "^.*jsons$";
		assertThat(this.doMatcher(patternString, mediaType), equalTo(false));

		patternString = "^application.*$";
		assertThat(this.doMatcher(patternString, mediaType), equalTo(true));
	}

	@Test
	public void test011_mediaType() {
		LOGGER.debug(LOG_PREFIX + "test011_mediaType");

		final String mediaType = "application/vnd.hoko-zid-v1+json";

		String patternString = ".*json$";
		assertThat(this.doMatcher(patternString, mediaType), equalTo(true));

		patternString = "^application.*";
		assertThat(this.doMatcher(patternString, mediaType), equalTo(true));
	}

	@Test
	public void test020_uuid() {
		LOGGER.debug(LOG_PREFIX + "test020_uuid");

		// A valid uuid generated by 'UUID.randomUUID().toString()'.
		final String uuid = "9a0c98fc-e0e5-4851-adff-3bed2a2862d4";

		String patternString = "[0-9a-fA-F]{8}-?[0-9a-fA-F]{4}-?4[0-9a-fA-F]{3}-?[89abAB][0-9a-fA-F]{3}-?[0-9a-fA-F]{12}";
		assertThat(this.doMatcher(patternString, uuid), equalTo(true));

		patternString = "";
		assertThat(this.doMatcher(patternString, uuid), equalTo(false));

		patternString = null;
		try {
			this.doMatcher(patternString, uuid);
		} catch (final NullPointerException nPex) {
			LOGGER.error(LOG_PREFIX + "test020_uuid - ignore, catch a expected NullPointerException.");
		}
	}

	private boolean doMatcher(final String patternString, final String theMatcherString) {
		final Pattern pattern = Pattern.compile(patternString);

		final Matcher matcher = pattern.matcher(theMatcherString);
		return matcher.matches();
	}
}
