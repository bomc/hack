/**
 * Project: hrm
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
package de.bomc.poc.hrm.application.exception.core;

/**
 * Collection of general utility methods with respect to working with
 * exceptions.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 20.09.2019
 */
public final class ExceptionUtil {

	// public header codes.
	public static final String HTTP_HEADER_X_EXCEPTION_UUID = "X-EXCEPTION-UUID";
	public static final String HTTP_HEADER_X_ERROR_CODE = "X-ERROR-CODE";

	private ExceptionUtil() {
		// Prevents instantiation.
	}

	/**
	 * Unwrap the nested causes of given exception as long as until it is not an
	 * instance of the given type. If the given exception is already an instance
	 * of the given type, then it will directly be returned. Or if the
	 * exception, unwrapped or not, does not have a nested cause anymore and is
	 * not of given type, null is returned. This is particularly useful if you
	 * want to unwrap the real root cause out of a nested hierarchy.
	 * 
	 * @param <T>
	 *            the exception that has to be unwrapped.
	 * @param exception
	 *            The exception to be unwrapped.
	 * @param type
	 *            The type which needs to be unwrapped.
	 * @return The unwrapped root cause of given type, or null if no root cause
	 *         of given type found.
	 */
	public static <T extends Throwable> T unwrap(Throwable exception, Class<T> type) {
		while (exception != null && !type.isInstance(exception) && exception.getCause() != null) {
			exception = exception.getCause();
		}

		if (type.isInstance(exception)) {
			return type.cast(exception);
		}

		return null;
	}

	/**
	 * Returns <code>true</code> if the given exception or one of its nested
	 * causes is an instance of the given type.
	 * 
	 * @param <T>
	 *            the exception that has to be unwrapped.
	 * @param exception
	 *            The exception to be checked.
	 * @param type
	 *            The type to be compared to.
	 * @return <code>true</code> if the given exception or one of its nested
	 *         causes is an instance of the given type.
	 */
	public static <T extends Throwable> boolean is(Throwable exception, final Class<T> type) {
		for (; exception != null; exception = exception.getCause()) {
			if (type.isInstance(exception)) {
				return true;
			}
		}

		return false;
	}

}