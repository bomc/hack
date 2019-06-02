package de.bomc.poc.exception.test.unit;

import de.bomc.poc.exception.core.BasisErrorCodeEnum;
import de.bomc.poc.exception.core.ErrorCode;
import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
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
import static org.junit.Assert.fail;

/**
 * Tests functionality from ErrorCode enum.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ErrorCodeEnumTest {

	private static final String LOG_PREFIX = "ErrorCodeEnumTest#";
	private static final Logger LOGGER = Logger.getLogger(ErrorCodeEnumTest.class);

	@Test
	@SuppressWarnings("static-access")
	public void test010_methods_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_methods_pass");

		assertThat(BasisErrorCodeEnum.CONNECTION_FAILURE_10500.getShortErrorCodeDescription(), equalTo("Connection timed out! "));
		assertThat(BasisErrorCodeEnum.CONNECTION_FAILURE_10500.getCategory(), equalTo(ErrorCode.Category.FATAL));
		assertThat(BasisErrorCodeEnum.CONNECTION_FAILURE_10500.intValue(), equalTo(10500));
		assertThat(BasisErrorCodeEnum.CONNECTION_FAILURE_10500.name(), equalTo("CONNECTION_FAILURE_10500"));
		assertThat(BasisErrorCodeEnum.CONNECTION_FAILURE_10500.toString(), equalTo("CONNECTION_FAILURE_10500"));
		assertThat(BasisErrorCodeEnum.fromInt(10500), equalTo(BasisErrorCodeEnum.CONNECTION_FAILURE_10500));

		final BasisErrorCodeEnum basisErrorCodeEnum = BasisErrorCodeEnum.CONNECTION_FAILURE_10500;
		assertThat(basisErrorCodeEnum.valueOf("CONNECTION_FAILURE_10500"), equalTo(BasisErrorCodeEnum.CONNECTION_FAILURE_10500));
	}

	@Test
	public void test020_methods_fail() {
		LOGGER.debug(LOG_PREFIX + "test020_methods_fail");

		try {
			BasisErrorCodeEnum.fromInt(2);
			fail(LOG_PREFIX + "test020_methods_fail - Should not be reached");
		} catch (final IllegalArgumentException ex) {
			LOGGER.debug(LOG_PREFIX + "test020_methods_fail - ok, should be catched.");
		}
	}

	@Test
	public void test030_methods_pass() {
		LOGGER.debug(LOG_PREFIX + "test030_methods_pass");

		final String strErrorCode = BasisErrorCodeEnum.CONNECTION_FAILURE_10500.name();

		assertThat(BasisErrorCodeEnum.valueOf(strErrorCode), equalTo(BasisErrorCodeEnum.CONNECTION_FAILURE_10500));
	}

	@Test
	public void test040_methods_fail() {
		LOGGER.debug(LOG_PREFIX + "test040_methods_fail");

		try {
			BasisErrorCodeEnum.valueOf("strErrorCode");
			fail(LOG_PREFIX + "test040_methods_fail - Should not be reached");
		} catch (final IllegalArgumentException ex) {
			LOGGER.debug(LOG_PREFIX + "test040_methods_fail - ok, should be catched.");
		}
	}

	@Test
	public void test050_transform() {
		LOGGER.debug(LOG_PREFIX + "test040_methods_fail");

		final int intValue = BasisErrorCodeEnum.CONNECTION_FAILURE_10500.intValue();
		assertThat(intValue, equalTo(BasisErrorCodeEnum.CONNECTION_FAILURE_10500.intValue()));

		final ErrorCode errorCode = BasisErrorCodeEnum.fromInt(intValue);
		assertThat(errorCode, equalTo(BasisErrorCodeEnum.CONNECTION_FAILURE_10500));
	}
}
