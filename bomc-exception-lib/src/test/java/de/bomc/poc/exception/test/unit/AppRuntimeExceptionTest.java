package de.bomc.poc.exception.test.unit;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.exception.test.TestErrorCode;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsNot.not;

/**
 * Tests the <code>RootRuntimeException</code>.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: 6790 $ $Author: tzdbmm $ $Date: 2016-07-19 09:06:34 +0200 (Di, 19 Jul 2016) $
 * @since 07.07.2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppRuntimeExceptionTest {

    private static final String LOG_PREFIX = "AppRuntimeExceptionTest#";
    private static final Logger LOGGER = Logger.getLogger(AppRuntimeExceptionTest.class);
    private static final String ERROR_MESSAGE = "java.lang.ArithmeticException: / by zero";
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void test001_createSimpleAppRuntimeException_Pass() {
        LOGGER.debug(LOG_PREFIX + "test001_createSimpleAppRuntimeException_Pass");

        try {
            final AppRuntimeException appRuntimeException = new AppRuntimeException(TestErrorCode.TEST_00101);

            throw appRuntimeException;
        } catch (final AppRuntimeException sRE) {
            assertThat(sRE.getErrorCode(), is(equalTo(TestErrorCode.TEST_00101)));
            assertThat(sRE.getUuid(), notNullValue());
            assertThat(sRE.isLogged(), is(equalTo(false)));
        }
    }

    @Test
    public void test010_createAppRuntimeExceptionWithProperties_Pass() {
        LOGGER.debug(LOG_PREFIX + "test010_createAppRuntimeExceptionWithProperties_Pass");

        final String PROPERTY_NAME_1 = "propertyName_1";
        final String PROPERTY_VALUE_1 = "propertyValue_1";
        final String PROPERTY_NAME_2 = "propertyName_2";
        final String PROPERTY_VALUE_2 = "propertyValue_2";

        try {
            final AppRuntimeException appRuntimeException = new AppRuntimeException(TestErrorCode.TEST_00101);
            appRuntimeException.set(PROPERTY_NAME_1, PROPERTY_VALUE_1).set(PROPERTY_NAME_2, PROPERTY_VALUE_2);

            throw appRuntimeException;
        } catch (final AppRuntimeException sRE) {
            assertThat(sRE.getErrorCode(), is(equalTo(TestErrorCode.TEST_00101)));
            assertThat(sRE.getUuid(), notNullValue());
            assertThat(sRE.isLogged(), is(equalTo(false)));
            assertThat(sRE.get(PROPERTY_NAME_1), is(equalTo(PROPERTY_VALUE_1)));
            assertThat(sRE.get(PROPERTY_NAME_2), is(equalTo(PROPERTY_VALUE_2)));

            final Map<String, String> propertyMap = sRE.getProperties();
            final List<String>
                valueList =
                propertyMap.entrySet()
                           .stream()
                           .map(entry -> entry.getValue()
                                              .toString())
                           .collect(Collectors.toList());
            assertThat(valueList.get(0), is(equalTo(PROPERTY_VALUE_1)));
            assertThat(valueList.get(1), is(equalTo(PROPERTY_VALUE_2)));
        }
    }

    @Test
    public void test020_createAppRuntimeExceptionWithMessage_Pass() {
        LOGGER.debug(LOG_PREFIX + "test020_createAppRuntimeExceptionWithMessage_Pass");

        final String MESSAGE = "my custom message";

        try {
            final AppRuntimeException appRuntimeException = new AppRuntimeException(MESSAGE, TestErrorCode.TEST_00101);

            throw appRuntimeException;
        } catch (final AppRuntimeException sRE) {
            assertThat(sRE.getErrorCode(), is(equalTo(TestErrorCode.TEST_00101)));
            assertThat(sRE.getMessage(), is(equalTo(MESSAGE)));
            assertThat(sRE.getUuid(), notNullValue());
            assertThat(sRE.isLogged(), is(equalTo(false)));
            assertThat(sRE.stackTraceToString(), containsString(MESSAGE));
        }
    }

    @Test
    public void test030_createAppRuntimeExceptionWithThrowable_Pass() {
        LOGGER.debug(LOG_PREFIX + "test030_createAppRuntimeExceptionWithThrowable_Pass");

        try {
            this.helperMethodCreatesAppRuntimeExceptionWithErrorCode();
        } catch (final AppRuntimeException sRE) {
            assertThat(sRE.getErrorCode(), is(equalTo(TestErrorCode.TEST_00101)));
            assertThat(sRE.getMessage(), is(equalTo(ERROR_MESSAGE)));
        }
    }

    @Test
    public void test040_createAppRuntimeExceptionWithMessageThrowableErrorCode_Pass() {
        LOGGER.debug(LOG_PREFIX + "test040_createAppRuntimeExceptionWithMessageThrowableErrorCode_Pass");

        try {
            this.helperMethodCreatesAppRuntimeExceptionWithMessageThrowableErrorCode();
        } catch (final AppRuntimeException sRE) {
            assertThat(sRE.getErrorCode(), is(equalTo(TestErrorCode.TEST_00101)));
            assertThat(sRE.getMessage(), is(equalTo(ERROR_MESSAGE)));
        }
    }

    @Test
    public void test050_stackTrace_Pass() {
        LOGGER.debug(LOG_PREFIX + "test050_stackTrace_Pass");

        try {
            this.helperMethodCreatesAppRuntimeExceptionWithErrorCode();
        } catch (final AppRuntimeException sRE) {
            assertThat(sRE.getErrorCode(), is(equalTo(TestErrorCode.TEST_00101)));
            assertThat(sRE.getMessage(), is(equalTo(ERROR_MESSAGE)));

            // Get a stacktrace with depth = 1;
            String stackTrace = sRE.stackTraceToString(1);
            final String[] stackTraceSplittedBySeparatedLineDepth1 = stackTrace.split(System.lineSeparator());
            List<Integer> separatorPosList = IntStream.range(0, stackTraceSplittedBySeparatedLineDepth1.length)
                                                      .filter(i -> stackTraceSplittedBySeparatedLineDepth1[i].contains(AppRuntimeException.SEPARATOR))
                                                      .mapToObj(i -> i)
                                                      .collect(Collectors.toList());
            assertThat(stackTraceSplittedBySeparatedLineDepth1.length, is(greaterThan(separatorPosList.get(0))));

            // Get a stacktrace with depth = 2;
            separatorPosList.clear();
            stackTrace = sRE.stackTraceToString(2);
            final String[] stackTraceSplittedBySeperatedLineDepth2 = stackTrace.split(System.lineSeparator());
            separatorPosList = IntStream.range(0, stackTraceSplittedBySeperatedLineDepth2.length)
                                        .filter(i -> stackTraceSplittedBySeperatedLineDepth2[i].contains(AppRuntimeException.SEPARATOR))
                                        .mapToObj(i -> i)
                                        .collect(Collectors.toList());
            assertThat(stackTraceSplittedBySeperatedLineDepth2.length, is(greaterThan(separatorPosList.get(0))));
            assertThat(stackTraceSplittedBySeperatedLineDepth2.length, is(equalTo(stackTraceSplittedBySeparatedLineDepth1.length + 1)));

            // StackTrace 0 corresponds to FULL stacktrace output.
            final String stackTraceMaxValue = sRE.stackTraceToString(0);
            final String stackTraceFull = sRE.stackTraceToString();
            assertThat(stackTraceMaxValue, is(equalTo(stackTraceFull)));
        }
    }

    /**
     * Tests the condition 'this.errorCode != null' with ErrorCode = null. If no <code>ErrorCode</code> is set, so no information is print out.
     */
    @Test
    public void test060_exceptionWithErrorCodeNull_Pass() {
        LOGGER.debug(LOG_PREFIX + "test060_exceptionWithErrorCodeNull_Pass");

        try {
            this.helperMethodCreatesAppRuntimeExceptionWithErrorCodeNull();
        } catch (final AppRuntimeException sRE) {
            final String stackTraceReduced = sRE.stackTraceToString(1);
            // Should not contains information about 'TestErrorCode'.
            assertThat(stackTraceReduced, not(containsString("TestErrorCode")));
        }
    }

    /**
     * Tests the print out of a exception when root cause is null.
     */
    @Test
    public void test070_stackTraceWithRootCauseNull_Pass() {
        LOGGER.debug(LOG_PREFIX + "test070_stackTraceWithRootCauseNull_Pass");

        try {
            this.helperMethodCreatesAppRuntimeExceptionWithErrorCode();
        } catch (final AppRuntimeException nPE) {
            final AppRuntimeException s0 = null;
            final AppRuntimeException s1 = new AppRuntimeException(s0, TestErrorCode.TEST_00101);

            final String fullStackTrace = s1.stackTraceToString();

            assertThat(fullStackTrace, not(containsString("cause")));
        }
    }

    @Test
    public void test080_wrapThrowable_Pass() {
        LOGGER.debug(LOG_PREFIX + "test080_wrapThrowable_Pass");

        try {
            // Catch a NullPointerException and wrap them to AppRuntimeException.
            this.helperMethodCreatesNullPointerException();
        } catch (final NullPointerException nPE) {
            final AppRuntimeException appRuntimeException = AppRuntimeException.wrap(nPE);

            assertThat(appRuntimeException.getCause(), is(instanceOf(NullPointerException.class)));
        }
    }

    @Test
    public void test090_wrapThrowableWithErrorCode_Pass() {
        LOGGER.debug(LOG_PREFIX + "test090_wrapThrowableWithErrorCode_Pass");

        try {
            // Catch a NullPointerException and wrap them to AppRuntimeException with a ErrorCode.
            this.helperMethodCreatesNullPointerException();
        } catch (final NullPointerException nPE) {
            final AppRuntimeException appRuntimeException = AppRuntimeException.wrap(nPE, TestErrorCode.TEST_00101);

            assertThat(appRuntimeException.getCause(), is(instanceOf(NullPointerException.class)));
            assertThat(appRuntimeException.getErrorCode(), is(equalTo(TestErrorCode.TEST_00101)));
        }
    }

    /**
     * Tests the overwriting of the <code>ErrorCode</code> from the catched Exception.
     */
    @Test
    public void test100_wrapThrowableThatOverwritesCatchedErrorCode_Pass() {
        LOGGER.debug(LOG_PREFIX + "test100_wrapThrowableThatOverwritesCatchedErrorCode_Pass");

        try {
            this.helperMethodCreatesAppRuntimeExceptionWithErrorCode();
        } catch (final AppRuntimeException sRE) {
            assertThat(sRE.getErrorCode(), is(equalTo(TestErrorCode.TEST_00101)));

            final AppRuntimeException appRuntimeException = AppRuntimeException.wrap(sRE, TestErrorCode.TEST_00102);

            assertThat(appRuntimeException.getCause(), is(instanceOf(AppRuntimeException.class)));
            assertThat(appRuntimeException.getErrorCode(), is(equalTo(TestErrorCode.TEST_00102)));
        }
    }

    @Test
    public void test110_wrapAppRuntimeExceptionWithoutErrorCode_Pass() {
        LOGGER.debug(LOG_PREFIX + "test110_wrapAppRuntimeExceptionWithoutErrorCode_Pass");

        try {
            this.helperMethodCreatesAppRuntimeExceptionWithErrorCodeNull();
        } catch (final AppRuntimeException nPE) {
            final AppRuntimeException appRuntimeException = AppRuntimeException.wrap(nPE, null);

            assertThat(appRuntimeException, is(instanceOf(AppRuntimeException.class)));
        }
    }

    @Test
    public void test120_wrapAppRuntimeExceptionWithErrorCode_Pass() {
        LOGGER.debug(LOG_PREFIX + "test120_wrapAppRuntimeExceptionWithErrorCode_Pass");

        try {
            this.helperMethodCreatesAppRuntimeExceptionWithErrorCode();
        } catch (final AppRuntimeException nPE) {
            final AppRuntimeException appRuntimeException = AppRuntimeException.wrap(nPE, TestErrorCode.TEST_00101);

            assertThat(appRuntimeException, is(instanceOf(AppRuntimeException.class)));
        }
    }

    @Test
    public void test130_wrapsAppRuntimeExceptionInAppRuntimeExceptionWithErrorCode_Pass() {
        LOGGER.debug(LOG_PREFIX + "test130_wrapsAppRuntimeExceptionInAppRuntimeExceptionWithErrorCode_Pass");

        try {
            this.helperMethodCreatesAppRuntimeExceptionWithErrorCode();
        } catch (final AppRuntimeException sRE) {
            final AppRuntimeException appRuntimeException = AppRuntimeException.wrap(sRE);

            assertThat(appRuntimeException, is(instanceOf(AppRuntimeException.class)));
            assertThat(appRuntimeException.getErrorCode(), is(equalTo(TestErrorCode.TEST_00101)));

            LOGGER.debug(appRuntimeException.stackTraceToString());
        }
    }

    @Test
    @SuppressWarnings("unused")
    public void test140_wrapWithRootCauseNull_Fail() {
        LOGGER.debug(LOG_PREFIX + "test140_wrapWithRootCauseNull_Fail");

        this.thrown.expect(NullPointerException.class);

        final NullPointerException nPE = null;
        // This throws a NullPointerException.
        final AppRuntimeException appRuntimeException = AppRuntimeException.wrap(nPE, TestErrorCode.TEST_00101);
    }

    @Test
    public void test150_isLogged_Pass() {
        LOGGER.debug(LOG_PREFIX + "test150_isLogged_Pass");

        // Create a exception with a cause and a root cause.
        final AppRuntimeException appRuntimeException = new AppRuntimeException(TestErrorCode.TEST_00102);

        assertThat(false, is(equalTo(appRuntimeException.isLogged())));

        appRuntimeException.setIsLogged(true);

        assertThat(true, is(equalTo(appRuntimeException.isLogged())));
    }

    /**
     * A helper method that throws a <code>AppRuntimeException</code>. The application exception contains a <code>Throwable</code>, 'null' as <code>ErrorCode</code> and additional properties.
     */
    @SuppressWarnings("unused")
	private void helperMethodCreatesAppRuntimeExceptionWithErrorCodeNull() {
        try {
            // Create a thrown exception.
            // This throws an java.lang.ArithmeticException.
            final int i = 10 / 0;
        } catch (final Throwable ex) {
            final AppRuntimeException appRuntimeException = new AppRuntimeException(ex, null);
            appRuntimeException.set("name1", "value1")
                               .set("name2", "value2");
            throw appRuntimeException;
        }
    }

    /**
     * A helper method that throws a <code>AppRuntimeException</code>. The application exception contains a <code>Throwable</code>, a <code>ErrorCode</code> and additional properties.
     */
    @SuppressWarnings("unused")
	private void helperMethodCreatesAppRuntimeExceptionWithErrorCode() {
        try {
            // Create a thrown exception.
            // This throws an java.lang.ArithmeticException.
            final int i = 10 / 0;
        } catch (final Throwable ex) {
            final AppRuntimeException appRuntimeException = new AppRuntimeException(ex, null);
            appRuntimeException.set("name1", "value1")
                               .set("name2", "value2");
            appRuntimeException.setErrorCode(TestErrorCode.TEST_00101);

            throw appRuntimeException;
        }
    }

    /**
     * A helper method that throws a <code>AppRuntimeException</code>. The application exception contains a error message as String, a<code>Throwable</code> and a <code>ErrorCode</code>.
     */
    @SuppressWarnings("unused")
	private void helperMethodCreatesAppRuntimeExceptionWithMessageThrowableErrorCode() {
        try {
            // Create a thrown exception.
            // This throws an java.lang.ArithmeticException.
            final int i = 10 / 0;
        } catch (final Throwable ex) {
            final AppRuntimeException appRuntimeException = new AppRuntimeException(ERROR_MESSAGE, ex, TestErrorCode.TEST_00101);
            appRuntimeException.set("name1", "value1")
                               .set("name2", "value2");

            throw appRuntimeException;
        }
    }

    /**
     * A helper method that throws a <code>NullPointerException</code>.
     */
    @SuppressWarnings({ "unused", "null" })
	private void helperMethodCreatesNullPointerException() {
        final Long i = null;
        // Create a thrown exception.
        // This throws an NullPointerException.
        final Long result = 100l / i;
    }
}
