package de.bomc.poc.exception.test.arq.mock.application;

import java.util.Map;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.RootRuntimeException;
import de.bomc.poc.exception.core.handler.ExceptionHandler;
import de.bomc.poc.exception.core.web.WebRuntimeException;
import de.bomc.poc.exception.test.TestApiError;

/**
 * A handler for handling application exception.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 */
public class MockExceptionHandler<T> implements ExceptionHandler<T> {

	public static final String LOG_PREFIX = "MockExceptionHandler#";
	public static final Logger LOGGER = Logger.getLogger(MockExceptionHandler.class);
	
	@Override
	public WebRuntimeException handleException(final T apiError, final boolean isLogged, final Map<String, String> propertyMap) {
		final TestApiError testApiError = (TestApiError)apiError;

		LOGGER.debug(
				LOG_PREFIX + "handleException - [testApiError=" + testApiError.name() + ", isLogged=" + isLogged + ", propertyMap]");

		final WebRuntimeException webRuntimeException = new WebRuntimeException(testApiError);
		webRuntimeException.setIsLogged(isLogged);

		if (propertyMap != null) {
			propertyMap.forEach((name, value) -> {
				webRuntimeException.set(name, value);
			});
		}

		if (isLogged) {
			LOGGER.debug(LOG_PREFIX + "handleException - "
					+ webRuntimeException.stackTraceToString(RootRuntimeException.FULL));
		}

		return webRuntimeException;
	}
}
