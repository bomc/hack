package de.bomc.poc.exception.core.handler;

import java.util.Map;

import de.bomc.poc.exception.core.web.WebRuntimeException;

/**
 * Exception handler for exceptions.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 *
 */
public interface ExceptionHandler<T> {

	/**
	 * Handle the exception. Create a application exception, log it, if it is
	 * set and throw it.
	 *
	 * @param apiError
	 *            the error description {@link ApiError}.
	 * @param isLogged
	 *            if true, the error is logged here, otherwise not.
	 * @param propertyMap
	 *            a map that describes the error more specific.
	 * @return a created instance of {@link WebRuntimeException}.
	 */
	WebRuntimeException handleException(T apiError, boolean isLogged, Map<String, String> propertyMap);
}
