package de.bomc.poc.exception.test.unit;

import de.bomc.poc.exception.core.web.WebRuntimeException;
import de.bomc.poc.exception.test.TestApiError;

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
public class WebRuntimeExceptionTest {

    private static final String LOG_PREFIX = "WebRuntimeExceptionTest#";
    private static final Logger LOGGER = Logger.getLogger(WebRuntimeExceptionTest.class);
    private static final String ERROR_MESSAGE = "java.lang.ArithmeticException: / by zero";
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void test001_createSimpleWebRuntimeException_Pass() {
        LOGGER.debug(LOG_PREFIX + "test001_createSimpleWebRuntimeException_Pass");

        try {
            final WebRuntimeException webRuntimeException = new WebRuntimeException(TestApiError.TEST_API_00101);

            throw webRuntimeException;
        } catch (final WebRuntimeException sRE) {
            assertThat(sRE.getErrorCode(), is(equalTo(TestApiError.TEST_API_00101)));
            assertThat(sRE.getUuid(), notNullValue());
            assertThat(sRE.isLogged(), is(equalTo(false)));
        }
    }

    @Test
    public void test010_createWebRuntimeExceptionWithProperties_Pass() {
        LOGGER.debug(LOG_PREFIX + "test010_createWebRuntimeExceptionWithProperties_Pass");

        final String PROPERTY_NAME_1 = "propertyName_1";
        final String PROPERTY_VALUE_1 = "propertyValue_1";
        final String PROPERTY_NAME_2 = "propertyName_2";
        final String PROPERTY_VALUE_2 = "propertyValue_2";

        try {
            final WebRuntimeException webRuntimeException = new WebRuntimeException(TestApiError.TEST_API_00101);
            webRuntimeException.set(PROPERTY_NAME_1, PROPERTY_VALUE_1).set(PROPERTY_NAME_2, PROPERTY_VALUE_2);

            throw webRuntimeException;
        } catch (final WebRuntimeException sRE) {
            assertThat(sRE.getErrorCode(), is(equalTo(TestApiError.TEST_API_00101)));
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
    public void test020_createWebRuntimeExceptionWithMessage_Pass() {
        LOGGER.debug(LOG_PREFIX + "test020_createWebRuntimeExceptionWithMessage_Pass");

        final String MESSAGE = "my custom message";

        try {
            final WebRuntimeException webRuntimeException = new WebRuntimeException(MESSAGE, TestApiError.TEST_API_00101);

            throw webRuntimeException;
        } catch (final WebRuntimeException sRE) {
            assertThat(sRE.getErrorCode(), is(equalTo(TestApiError.TEST_API_00101)));
            assertThat(sRE.getMessage(), is(equalTo(MESSAGE)));
            assertThat(sRE.getUuid(), notNullValue());
            assertThat(sRE.isLogged(), is(equalTo(false)));
            assertThat(sRE.stackTraceToString(), containsString(MESSAGE));
        }
    }

    @Test
    public void test030_createWebRuntimeExceptionWithThrowable_Pass() {
        LOGGER.debug(LOG_PREFIX + "test030_createWebRuntimeExceptionWithThrowable_Pass");

        try {
            this.helperMethodCreatesWebRuntimeExceptionWithErrorCode();
        } catch (final WebRuntimeException sRE) {
            assertThat(sRE.getErrorCode(), is(equalTo(TestApiError.TEST_API_00101)));
            assertThat(sRE.getMessage(), is(equalTo(ERROR_MESSAGE)));
        }
    }

    @Test
    public void test040_createWebRuntimeExceptionWithMessageThrowableErrorCode_Pass() {
        LOGGER.debug(LOG_PREFIX + "test040_createWebRuntimeExceptionWithMessageThrowableErrorCode_Pass");

        try {
            this.helperMethodCreatesWebRuntimeExceptionWithMessageThrowableErrorCode();
        } catch (final WebRuntimeException sRE) {
            assertThat(sRE.getErrorCode(), is(equalTo(TestApiError.TEST_API_00101)));
            assertThat(sRE.getMessage(), is(equalTo(ERROR_MESSAGE)));
        }
    }

    @Test
    public void test050_stackTrace_Pass() {
        LOGGER.debug(LOG_PREFIX + "test050_stackTrace_Pass");

        try {
            this.helperMethodCreatesWebRuntimeExceptionWithErrorCode();
        } catch (final WebRuntimeException sRE) {
            assertThat(sRE.getErrorCode(), is(equalTo(TestApiError.TEST_API_00101)));
            assertThat(sRE.getMessage(), is(equalTo(ERROR_MESSAGE)));

            // Get a stacktrace with depth = 1;
            String stackTrace = sRE.stackTraceToString(1);
            final String[] stackTraceSplittedBySeparatedLineDepth1 = stackTrace.split(System.lineSeparator());
            List<Integer> separatorPosList = IntStream.range(0, stackTraceSplittedBySeparatedLineDepth1.length)
                                                      .filter(i -> stackTraceSplittedBySeparatedLineDepth1[i].contains(WebRuntimeException.SEPARATOR))
                                                      .mapToObj(i -> i)
                                                      .collect(Collectors.toList());
            assertThat(stackTraceSplittedBySeparatedLineDepth1.length, is(greaterThan(separatorPosList.get(0))));

            // Get a stacktrace with depth = 2;
            separatorPosList.clear();
            stackTrace = sRE.stackTraceToString(2);
            final String[] stackTraceSplittedBySeperatedLineDepth2 = stackTrace.split(System.lineSeparator());
            separatorPosList = IntStream.range(0, stackTraceSplittedBySeperatedLineDepth2.length)
                                        .filter(i -> stackTraceSplittedBySeperatedLineDepth2[i].contains(WebRuntimeException.SEPARATOR))
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
            this.helperMethodCreatesWebRuntimeExceptionWithErrorCodeNull();
        } catch (final WebRuntimeException sRE) {
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
            this.helperMethodCreatesWebRuntimeExceptionWithErrorCode();
        } catch (final WebRuntimeException nPE) {
            final WebRuntimeException s0 = null;
            final WebRuntimeException s1 = new WebRuntimeException(s0, TestApiError.TEST_API_00101);

            final String fullStackTrace = s1.stackTraceToString();

            assertThat(fullStackTrace, not(containsString("cause")));
        }
    }

    @Test
    public void test080_wrapThrowable_Pass() {
        LOGGER.debug(LOG_PREFIX + "test080_wrapThrowable_Pass");

        try {
            // Catch a NullPointerException and wrap them to WebRuntimeException.
            this.helperMethodCreatesNullPointerException();
        } catch (final NullPointerException nPE) {
            final WebRuntimeException webRuntimeException = WebRuntimeException.wrap(nPE);

            assertThat(webRuntimeException.getCause(), is(instanceOf(NullPointerException.class)));
        }
    }

    @Test
    public void test090_wrapThrowableWithErrorCode_Pass() {
        LOGGER.debug(LOG_PREFIX + "test090_wrapThrowableWithErrorCode_Pass");

        try {
            // Catch a NullPointerException and wrap them to WebRuntimeException with a ErrorCode.
            this.helperMethodCreatesNullPointerException();
        } catch (final NullPointerException nPE) {
            final WebRuntimeException webRuntimeException = WebRuntimeException.wrap(nPE, TestApiError.TEST_API_00101);

            assertThat(webRuntimeException.getCause(), is(instanceOf(NullPointerException.class)));
            assertThat(webRuntimeException.getErrorCode(), is(equalTo(TestApiError.TEST_API_00101)));
        }
    }

    /**
     * Tests the overwriting of the <code>ErrorCode</code> from the catched Exception.
     */
    @Test
    public void test100_wrapThrowableThatOverwritesCatchedErrorCode_Pass() {
        LOGGER.debug(LOG_PREFIX + "test100_wrapThrowableThatOverwritesCatchedErrorCode_Pass");

        try {
            this.helperMethodCreatesWebRuntimeExceptionWithErrorCode();
        } catch (final WebRuntimeException sRE) {
            assertThat(sRE.getErrorCode(), is(equalTo(TestApiError.TEST_API_00101)));

            final WebRuntimeException webRuntimeException = WebRuntimeException.wrap(sRE, TestApiError.TEST_API_00102);

            assertThat(webRuntimeException.getCause(), is(instanceOf(WebRuntimeException.class)));
            assertThat(webRuntimeException.getErrorCode(), is(equalTo(TestApiError.TEST_API_00102)));
        }
    }

    @Test
    public void test110_wrapWebRuntimeExceptionWithoutErrorCode_Pass() {
        LOGGER.debug(LOG_PREFIX + "test110_wrapWebRuntimeExceptionWithoutErrorCode_Pass");

        try {
            this.helperMethodCreatesWebRuntimeExceptionWithErrorCodeNull();
        } catch (final WebRuntimeException nPE) {
            final WebRuntimeException webRuntimeException = WebRuntimeException.wrap(nPE, null);

            assertThat(webRuntimeException, is(instanceOf(WebRuntimeException.class)));
        }
    }

    @Test
    public void test120_wrapWebRuntimeExceptionWithErrorCode_Pass() {
        LOGGER.debug(LOG_PREFIX + "test120_wrapWebRuntimeExceptionWithErrorCode_Pass");

        try {
            this.helperMethodCreatesWebRuntimeExceptionWithErrorCode();
        } catch (final WebRuntimeException nPE) {
            final WebRuntimeException webRuntimeException = WebRuntimeException.wrap(nPE, TestApiError.TEST_API_00101);

            assertThat(webRuntimeException, is(instanceOf(WebRuntimeException.class)));
        }
    }

    @Test
    public void test130_wrapsWebRuntimeExceptionInWebRuntimeExceptionWithErrorCode_Pass() {
        LOGGER.debug(LOG_PREFIX + "test130_wrapsWebRuntimeExceptionInWebRuntimeExceptionWithErrorCode_Pass");

        try {
            this.helperMethodCreatesWebRuntimeExceptionWithErrorCode();
        } catch (final WebRuntimeException sRE) {
            final WebRuntimeException webRuntimeException = WebRuntimeException.wrap(sRE);

            assertThat(webRuntimeException, is(instanceOf(WebRuntimeException.class)));
            assertThat(webRuntimeException.getErrorCode(), is(equalTo(TestApiError.TEST_API_00101)));

            LOGGER.debug(LOG_PREFIX + "test130_wrapsWebRuntimeExceptionInWebRuntimeExceptionWithErrorCode_Pass" + webRuntimeException.stackTraceToString());
        }
    }

    @Test
    @SuppressWarnings("unused")
    public void test140_wrapWithRootCauseNull_Fail() {
        LOGGER.debug(LOG_PREFIX + "test140_wrapWithRootCauseNull_Fail");

        this.thrown.expect(NullPointerException.class);

        final NullPointerException nPE = null;
        final WebRuntimeException webRuntimeException = WebRuntimeException.wrap(nPE, TestApiError.TEST_API_00101);
    }

    @Test
    public void test150_isLogged_Pass() {
        LOGGER.debug(LOG_PREFIX + "test150_isLogged_Pass");

        // Create a exception with a cause and a root cause.
        final WebRuntimeException webRuntimeException = new WebRuntimeException(TestApiError.TEST_API_00102);
        webRuntimeException.setIsLogged(true);

        assertThat(true, is(equalTo(webRuntimeException.isLogged())));
    }

    /**
     * A helper method that throws a <code>WebRuntimeException</code>. The application exception contains a <code>Throwable</code>, 'null' as <code>ErrorCode</code> and additional properties.
     */
    @SuppressWarnings("unused")
	private void helperMethodCreatesWebRuntimeExceptionWithErrorCodeNull() {
        try {
            // Create a thrown exception.
            // This throws an java.lang.ArithmeticException.
            final int i = 10 / 0;
        } catch (final Throwable ex) {
            final WebRuntimeException webRuntimeException = new WebRuntimeException(ex, null);
            webRuntimeException.set("name1", "value1")
                               .set("name2", "value2");
            throw webRuntimeException;
        }
    }

    /**
     * A helper method that throws a <code>WebRuntimeException</code>. The application exception contains a <code>Throwable</code>, a <code>ErrorCode</code> and additional properties.
     */
    @SuppressWarnings("unused")
	private void helperMethodCreatesWebRuntimeExceptionWithErrorCode() {
        try {
            // Create a thrown exception.
            // This throws an java.lang.ArithmeticException.
            final int i = 10 / 0;
        } catch (final Throwable ex) {
            final WebRuntimeException webRuntimeException = new WebRuntimeException(ex, null);
            webRuntimeException.set("name1", "value1")
                               .set("name2", "value2");
            webRuntimeException.setErrorCode(TestApiError.TEST_API_00101);

            throw webRuntimeException;
        }
    }

    /**
     * A helper method that throws a <code>WebRuntimeException</code>. The application exception contains a error message as String, a<code>Throwable</code> and a <code>ErrorCode</code>.
     */
    @SuppressWarnings("unused")
	private void helperMethodCreatesWebRuntimeExceptionWithMessageThrowableErrorCode() {
        try {
            // Create a thrown exception.
            // This throws an java.lang.ArithmeticException.
            final int i = 10 / 0;
        } catch (final Throwable ex) {
            final WebRuntimeException webRuntimeException = new WebRuntimeException(ERROR_MESSAGE, ex, TestApiError.TEST_API_00101);
            webRuntimeException.set("name1", "value1")
                               .set("name2", "value2");

            throw webRuntimeException;
        }
    }

    /**
     * A helper method that throws a <code>NullPointerException</code>.
     */
    @SuppressWarnings({ "null", "unused" })
	private void helperMethodCreatesNullPointerException() {
        final Long i = null;
        // Create a thrown exception.
        // This throws an NullPointerException.
        final Long result = 100l / i;
    }
}
