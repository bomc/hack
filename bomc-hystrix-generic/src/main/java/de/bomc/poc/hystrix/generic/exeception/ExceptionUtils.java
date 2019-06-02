/**
 * <pre>
 *
 * Last change:
 *
 *  by: $Author$
 *
 *  date: $Date$
 *
 *  revision: $Revision$
 *
 *    © Bomc 2018
 *
 * </pre>
 */
package de.bomc.poc.hystrix.generic.exeception;

import com.netflix.hystrix.exception.HystrixBadRequestException;

import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.app.AppRuntimeException;

import org.apache.log4j.Logger;

/**
 * Util class to work with exceptions.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public class ExceptionUtils {

	private static final String LOG_PREFIX = "ExceptionUtils#";
	private static final Logger LOGGER = Logger.getLogger(ExceptionUtils.class);

	/**
	 * Retrieves cause exception and wraps to {@link AppRuntimeException}.
	 * 
	 * @param throwable
	 *            the throwable
	 */
	public static void propagateCause(final Throwable throwable) throws AppRuntimeException, RetryException {
		LOGGER.debug(LOG_PREFIX + "propagateCause [throwable=" + throwable.getClass().getSimpleName() + "]");

		if (throwable instanceof RetryException) {
			// rethrow exception
			throw (RetryException) throwable;
		}

		throw new AppRuntimeException(AppRuntimeException.wrap(throwable.getCause()),
				ErrorCode.RESILIENCE_10500);
	}

	/**
	 * Wraps cause exception to {@link AppRuntimeException}.
	 * 
	 * @param throwable
	 *            the throwable
	 * @return a CommandActionExecutionException that wraps the throwable.
	 */
	public static AppRuntimeException wrapCause(final Throwable throwable) {
		LOGGER.debug(LOG_PREFIX + "wrapCause [throwable=" + throwable.getMessage() + "]");

		return new AppRuntimeException(AppRuntimeException.wrap(throwable.getCause()),
				ErrorCode.RESILIENCE_10500);
	}

	/**
	 * Gets actual exception if it's wrapped in {@link AppRuntimeException} or
	 * {@link HystrixBadRequestException}.
	 * 
	 * @param throwable
	 *            the exception
	 * @return unwrapped
	 */
	public static Throwable unwrapCause(final Throwable throwable) {
		LOGGER.debug(LOG_PREFIX + "unwrapCause [throwable=" + throwable.getClass().getSimpleName() + "]");

		if (throwable instanceof AppRuntimeException) {
			return throwable.getCause();
		}

		if (throwable instanceof HystrixBadRequestException) {
			return throwable.getCause();
		}

		return throwable;
	}
}
