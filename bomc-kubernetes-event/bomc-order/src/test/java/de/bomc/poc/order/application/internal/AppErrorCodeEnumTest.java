/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.order.application.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import de.bomc.poc.exception.core.BasisErrorCodeEnum;
import de.bomc.poc.exception.core.ErrorCode;
import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import de.bomc.poc.order.CategoryFastUnitTest;

/**
 * Tests the {@link AppErrorCodeEnum}.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppErrorCodeEnumTest {

    private static final Logger LOGGER = Logger.getLogger(AppErrorCodeEnumTest.class);
    private static final String LOG_PREFIX = "AppErrorCodeEnumTest#";
    @Rule
    public final ExpectedException thrownAppErrorCodeEnum = ExpectedException.none();

    @Test
    public void test010_enum_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_enum_pass");

        // Test category, shortErrorDescription and errorCode as int value.
        final AppErrorCodeEnum appErrorCodeEnum = AppErrorCodeEnum.APP_CREATE_LOG_ENTRY_FAILED_10601;
        assertThat(appErrorCodeEnum.getCategory(), notNullValue());
        assertThat(appErrorCodeEnum.getShortErrorCodeDescription(), notNullValue());
        assertThat(appErrorCodeEnum.intValue(), notNullValue());

        // Test method 'errorCodefromString'.
        final ErrorCode errorCode1 = AppErrorCodeEnum.errorCodeFromString(AppErrorCodeEnum.APP_CREATE_LOG_ENTRY_FAILED_10601.name());
        assertThat(errorCode1.toString(), equalTo("APP_CREATE_LOG_ENTRY_FAILED_10601"));

        // Test method 'fromInt'.
        final ErrorCode errorCode2 = AppErrorCodeEnum.fromInt(AppErrorCodeEnum.APP_CREATE_LOG_ENTRY_FAILED_10601.intValue());
        assertThat(errorCode2, equalTo(AppErrorCodeEnum.APP_CREATE_LOG_ENTRY_FAILED_10601));

        // Test method 'valueOf'.
        final AppErrorCodeEnum appErrorCodeEnum2 = AppErrorCodeEnum.valueOf("APP_CREATE_LOG_ENTRY_FAILED_10601");
        assertThat(appErrorCodeEnum, equalTo(appErrorCodeEnum2));

        // Test method 'toString'.
        assertThat(appErrorCodeEnum.toString(), containsString("APP_CREATE_LOG_ENTRY_FAILED_10601"));
    }

    @Test
    public void test020_errorCodefromString_fail() {
        LOGGER.debug(LOG_PREFIX + "test020_errorCodefromString_fail");

        // Tests the behavior when passing an invalid errorCode string.
        final ErrorCode errorCode = AppErrorCodeEnum.errorCodeFromString("no_error_code");

        assertThat(errorCode.intValue(), equalTo(BasisErrorCodeEnum.UNEXPECTED_10000.intValue()));
    }

    @Test
    public void test030_fromInt_fail() {
        LOGGER.debug(LOG_PREFIX + "test030_fromInt_fail");

        thrownAppErrorCodeEnum.expect(IllegalArgumentException.class);

        // Tests the behavior when passing an invalid errorCode int value.
        AppErrorCodeEnum.fromInt(42);
    }
}
