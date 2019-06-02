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
package de.bomc.poc.exception.core.handler;

import de.bomc.poc.exception.RootRuntimeException;
import de.bomc.poc.exception.core.BasisErrorCodeEnum;
import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.ExceptionUtil;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.exception.core.web.WebRuntimeException;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * A handler for web exceptions.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
public class WebRuntimeExceptionHandler {

	private static final Logger LOGGER = Logger.getLogger(WebRuntimeExceptionHandler.class);
	private static final String LOG_PREFIX = "WebRuntimeExceptionHandler#";

	/**
	 * Handles a {@link WebRuntimeException} by the given parameter.
	 * 
	 * @param source
	 *            the source of the exception is: 'class#method'.
	 * @param exception
	 *            an exception.
	 * @return an initialized {@link WebRuntimeException}.
	 */
	public static WebRuntimeException handleException(final String source, final Exception exception) {
		LOGGER.debug(LOG_PREFIX + "handleException [source=" + source + ", exception]");
		return handleException(source, exception, null);
	}

	/**
	 * Handles a {@link WebRuntimeException} by the given parameter.
	 * 
	 * @param source
	 *            the source of the exception is: 'class#method'.
	 * @param exception
	 *            an exception.
	 * @param propertyMap
	 *            additional parameter to describe the exception, could be null.
	 * @return an initialized {@link WebRuntimeException}.
	 */
	public static WebRuntimeException handleException(final String source, final Exception exception,
			final Map<String, String> propertyMap) {
		LOGGER.debug(LOG_PREFIX + "handleException [source=" + source + ", exception, propertyMap]");

		AppRuntimeException unwrappedAppRuntimeException = ExceptionUtil.unwrap(exception, AppRuntimeException.class);

		if (unwrappedAppRuntimeException != null) {
			// handle wrapped app runtime exception.
			WebRuntimeException webRuntimeException = handleException(source, unwrappedAppRuntimeException);
			setupProperties(propertyMap, webRuntimeException);
			return webRuntimeException;
		} else {
			// no wrapped app runtime exception found. trigger alternative
			// default behavior.
			return handleException(source, exception.getMessage(), BasisErrorCodeEnum.UNEXPECTED_10000, false, propertyMap);
		}
	}

	/**
	 * Handles a {@link WebRuntimeException} by the given parameter.
	 * 
	 * @param source
	 *            the source of the is exception: 'class#method'.
	 * @param appRuntimeException
	 *            a appRuntimeException.
	 * @return an initialized {@link WebRuntimeException}.
	 */
	public static WebRuntimeException handleException(final String source,
			final AppRuntimeException appRuntimeException) {
		LOGGER.debug(LOG_PREFIX + "handleException [source=" + source + ", appRuntimeException]");

		final WebRuntimeException webRuntimeException = new WebRuntimeException(appRuntimeException.getMessage(),
				appRuntimeException.getErrorCode());
		webRuntimeException.setIsLogged(appRuntimeException.isLogged());
		webRuntimeException.setUuid(appRuntimeException.getUuid());
		webRuntimeException.setStackTrace(appRuntimeException.getStackTrace());

		setupProperties(appRuntimeException.getProperties(), webRuntimeException);
		logException(webRuntimeException);

		return webRuntimeException;
	}

	/**
	 * Handles a {@link WebRuntimeException} by the given parameter.
	 * 
	 * @param source
	 *            the source of the is exception: 'class#method'.
	 * @param errMsg
	 *            the given error message.
	 * @param errorCode
	 *            the given errorCode is mandatory.
	 * @param isLogged
	 *            false the exception will be logged in method, otherwise not
	 * @param propertyMap
	 *            additional parameter to describe the exception, could be null.
	 * @return an initialized {@link WebRuntimeException}.
	 */
	public static WebRuntimeException handleException(final String source, final String errMsg,
			final ErrorCode errorCode, final boolean isLogged, final Map<String, String> propertyMap) {
		LOGGER.debug(LOG_PREFIX + "handleException [source=" + source + ", errMsg=" + errMsg + ", errorCode="
				+ errorCode + ", isLogged=" + isLogged + ", propertyMap=" + propertyMap + "]");

		final WebRuntimeException webRuntimeException = new WebRuntimeException(errMsg, errorCode);
		webRuntimeException.setIsLogged(isLogged);

		setupProperties(propertyMap, webRuntimeException);
		logException(webRuntimeException);

		return webRuntimeException;
	}

	/* --------------------- Helper Methods ------------------------------ */
	private static void setupProperties(final Map<String, String> propertyMap,
			final WebRuntimeException webRuntimeException) {
		LOGGER.debug(LOG_PREFIX + "extractProperties [propertyMap=" + propertyMap + "]");

		if (propertyMap != null) {
			propertyMap.forEach((name, value) -> {
				webRuntimeException.set(name, value);
			});
		}
	}

	private static void logException(final WebRuntimeException webRuntimeException) {
		LOGGER.debug(LOG_PREFIX + "logException");

		if (!webRuntimeException.isLogged()) {
			LOGGER.debug(
					LOG_PREFIX + "logException - " + webRuntimeException.stackTraceToString(RootRuntimeException.FULL));
		}
	}
}
