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

import de.bomc.poc.exception.core.BasisErrorCodeEnum;
import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.exception.core.handler.WebRuntimeExceptionHandler;
import de.bomc.poc.exception.core.web.WebRuntimeException;
import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the exception handler.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExceptionHandlerTest {

	private static final Logger LOGGER = Logger.getLogger(ExceptionHandlerTest.class);
	private static final String LOG_PREFIX = "ExceptionHandlerTest#";

	@Test
	public void test010_exceptionHandler() {
		LOGGER.debug(LOG_PREFIX + "test010_exceptionHandler");

		final ErrorCode errorCode = BasisErrorCodeEnum.CONNECTION_FAILURE_10500;
		final boolean isLogged = false;
		final Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put("bomcKey", "bomcValue");
		final String errMsg = "error Message";

		final WebRuntimeException webRuntimeException = WebRuntimeExceptionHandler
				.handleException(LOG_PREFIX + "test010_exceptionHandler", errMsg, errorCode, isLogged, propertyMap);

		assertThat(webRuntimeException.getResponseStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR));
		assertThat(webRuntimeException.get("bomcKey"), equalTo("bomcValue"));
		assertThat(webRuntimeException.getUuid(), notNullValue());
		assertThat(webRuntimeException.getErrorCode(), equalTo(BasisErrorCodeEnum.CONNECTION_FAILURE_10500));
		assertThat(webRuntimeException.getProperties().size(), equalTo(1));
		assertThat(webRuntimeException.stackTraceToString(), notNullValue());
		assertThat(webRuntimeException.getMessage(), equalTo(errMsg));
	}

	@Test
	public void test020_exceptionHandler() {
		LOGGER.debug(LOG_PREFIX + "test020_exceptionHandler");

		try {
			forceNpe();
		} catch (final NullPointerException npe) {
			final AppRuntimeException appRuntimeException = new AppRuntimeException(npe.getMessage(),
					BasisErrorCodeEnum.CONNECTION_FAILURE_10500);
			appRuntimeException.setUuid("myUuid");
			appRuntimeException.setIsLogged(false);
			appRuntimeException.set("bomcKey", "bomcValue");
			appRuntimeException.setStackTrace(npe.getStackTrace());

			final WebRuntimeException webRuntimeException = WebRuntimeExceptionHandler
					.handleException(LOG_PREFIX + "test020_exceptionHandler", appRuntimeException);

			assertThat(webRuntimeException.getResponseStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR));
			assertThat(webRuntimeException.get("bomcKey"), equalTo("bomcValue"));
			assertThat(webRuntimeException.getUuid(), equalTo("myUuid"));
			assertThat(webRuntimeException.getErrorCode(),
					equalTo(BasisErrorCodeEnum.CONNECTION_FAILURE_10500));
			assertThat(webRuntimeException.getProperties().size(), equalTo(1));
			assertThat(webRuntimeException.stackTraceToString(), notNullValue());
		}
	}

	@Test
	public void test030_exceptionHandler() {
		LOGGER.debug(LOG_PREFIX + "test030_exceptionHandler");

		try {
			forceNpe();
		} catch (final NullPointerException npe) {

			final WebRuntimeException webRuntimeException = WebRuntimeExceptionHandler
					.handleException(LOG_PREFIX + "test030_exceptionHandler", npe);

			assertThat(webRuntimeException.getResponseStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR));
			assertThat(webRuntimeException.getErrorCode(), equalTo(BasisErrorCodeEnum.UNEXPECTED_10000));
			assertThat(webRuntimeException.getProperties().isEmpty(), equalTo(true));
			assertThat(webRuntimeException.stackTraceToString(), notNullValue());
		}
	}

	@Test
	public void test040_exceptionHandler() {
		LOGGER.debug(LOG_PREFIX + "test040_exceptionHandler");

		try {
			forceNpe();
		} catch (final NullPointerException npe) {
			final AppRuntimeException appRuntimeException = new AppRuntimeException(npe.getMessage(),
					BasisErrorCodeEnum.CONNECTION_FAILURE_10500);
			appRuntimeException.setUuid("myUuid");
			appRuntimeException.setIsLogged(false);
			appRuntimeException.set("bomcKey", "bomcValue");
			appRuntimeException.setStackTrace(npe.getStackTrace());

			final Map<String, String> propertiesMap = new HashMap<>();
			propertiesMap.put("bomcKey2", "bomcValue2");

			final WebRuntimeException webRuntimeException = WebRuntimeExceptionHandler
					.handleException(LOG_PREFIX + "test040_exceptionHandler", appRuntimeException, propertiesMap);

			assertThat(webRuntimeException.getResponseStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR));
			assertThat(webRuntimeException.get("bomcKey"), equalTo("bomcValue"));
			assertThat(webRuntimeException.get("bomcKey2"), equalTo("bomcValue2"));
			assertThat(webRuntimeException.getUuid(), equalTo("myUuid"));
			assertThat(webRuntimeException.getErrorCode(),
					equalTo(BasisErrorCodeEnum.CONNECTION_FAILURE_10500));
			assertThat(webRuntimeException.getProperties().size(), equalTo(2));
			assertThat(webRuntimeException.stackTraceToString(), notNullValue());
		}
	}

	@Test
	public void test050_exceptionHandler() {
		LOGGER.debug(LOG_PREFIX + "test050_exceptionHandler");

		try {
			forceNpe();
		} catch (final NullPointerException npe) {

			final Map<String, String> propertiesMap = new HashMap<>();
			propertiesMap.put("bomcKey2", "bomcValue2");

			final WebRuntimeException webRuntimeException = WebRuntimeExceptionHandler
					.handleException(LOG_PREFIX + "test050_exceptionHandler", npe, propertiesMap);

			assertThat(webRuntimeException.getResponseStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR));
			assertThat(webRuntimeException.get("bomcKey2"), equalTo("bomcValue2"));
			assertThat(webRuntimeException.getErrorCode(), equalTo(BasisErrorCodeEnum.UNEXPECTED_10000));
			assertThat(webRuntimeException.getProperties().size(), equalTo(1));
			assertThat(webRuntimeException.stackTraceToString(), notNullValue());
		}
	}

	/* --------------------- Helper Methods ------------------------------ */
	@SuppressWarnings("null")
	private void forceNpe() {
		final StringBuffer stringBuffer = null;
		stringBuffer.append("NullPointerException");
	}
}
