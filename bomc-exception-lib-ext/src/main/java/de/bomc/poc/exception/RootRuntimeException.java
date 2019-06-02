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
package de.bomc.poc.exception;

import de.bomc.poc.exception.core.BasisErrorCodeEnum;
import de.bomc.poc.exception.core.ErrorCode;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * The root for all exceptions.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
public abstract class RootRuntimeException extends RuntimeException {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 6732997308902960162L;
	/**
	 * Separates the custom exception info from the stacktrace.
	 */
	public static final String SEPARATOR = "-------------------------------";
	/**
	 * Print the full stacktrace.
	 */
	public static final Integer FULL = Integer.MAX_VALUE;
	/*
	 * Mark the exception is already logged (true means exception is already
	 * logged).
	 */
	private boolean isLogged = false;
	/**
	 * Describes the error that uniquely identifies the occurred error.
	 */
	private ErrorCode errorCode = BasisErrorCodeEnum.UNEXPECTED_10000;
	/**
	 * Holds additional information to the exception as properties.
	 */
	private final Map<String, String> properties = new TreeMap<>();
	/**
	 * Identifier for exception.
	 */
	private String uuid = UUID.randomUUID().toString();

	/**
	 * Creates a new instance of <code>RootRuntimeException</code>.
	 * 
	 * @param errorCode the error description.
	 */
	public RootRuntimeException(final ErrorCode errorCode) {
		if (errorCode != null) {
			this.errorCode = errorCode;
		}
	}

	/**
	 * Creates a new instance of <code>RootRuntimeException</code>.
	 * 
	 * @param message   the specified detail message.
	 * @param errorCode the error description.
	 */
	public RootRuntimeException(final String message, final ErrorCode errorCode) {
		super(message);

		if (errorCode != null) {
			this.errorCode = errorCode;
		}
	}

	/**
	 * Creates a new instance of <code>RootRuntimeException</code>.
	 * 
	 * @param cause     the <code>Throwable</code> of the exception.
	 * @param errorCode the error description.
	 */
	public RootRuntimeException(final Throwable cause, final ErrorCode errorCode) {
		super(cause);

		if (errorCode != null) {
			this.errorCode = errorCode;
		}
	}

	/**
	 * Creates a new instance of <code>AppRuntimeException</code>.
	 * 
	 * @param message   the specified detail message.
	 * @param cause     the <code>Throwable</code> of the exception.
	 * @param errorCode the error description.
	 */
	public RootRuntimeException(final String message, final Throwable cause, final ErrorCode errorCode) {
		super(message, cause);

		if (errorCode != null) {
			this.errorCode = errorCode;
		}
	}

	/**
	 * @return true the exception is already logged otherwise false.
	 */
	public boolean isLogged() {
		return this.isLogged;
	}

	/**
	 * Mark the exception for the logging state.
	 * 
	 * @param isLogged true the exception is already logged otherwise false.
	 */
	public void setIsLogged(final boolean isLogged) {
		this.isLogged = isLogged;
	}

	/**
	 * @return the error description.
	 */
	public ErrorCode getErrorCode() {
		return this.errorCode;
	}

	/**
	 * Set the error code.
	 * 
	 * @param errorCode the error description.
	 */
	public void setErrorCode(final ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the uuid.
	 */
	public String getUuid() {
		return this.uuid;
	}

	/**
	 * The uuid helps exception identification of the thrown exception in the
	 * application stack.
	 * 
	 * @param uuid the given uuid to set
	 */
	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the additional properties.
	 */
	public Map<String, String> getProperties() {
		return this.properties;
	}

	/**
	 * Return a property to the given name.
	 * 
	 * @param name the given key.
	 * @param      <T> the type of the return value.
	 * @return a property to the given name.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(final String name) {
		return (T) this.properties.get(name);
	}

	/**
	 * Set a additional information to the exception.
	 * 
	 * @param name  the name of the property.
	 * @param value the value of the property.
	 * @return the instance, using the fluent pattern.
	 */
	public RootRuntimeException set(final String name, final String value) {
		this.properties.put(name, value);

		return this;
	}

	/**
	 * Return the stackTrace as string.
	 * 
	 * @return the stackTrace as string.
	 */
	public String stackTraceToString() {
		return this.stackTraceToString(FULL);
	}

	/**
	 * Return the stackTrace as string.
	 * 
	 * @param depth the stacktrace depth.
	 * @return the stackTrace as string.
	 */
	public String stackTraceToString(final int depth) {
		this.isLogged = true;
		final StringBuilder sb = new StringBuilder();

		sb.append(System.lineSeparator()).append("_______________________________").append(System.lineSeparator());
		sb.append(this);

		sb.append(System.lineSeparator()).append("uuid=").append(this.uuid);

		if (this.errorCode != null) {
			sb.append(System.lineSeparator()).append(this.errorCode).append("(")
					.append(this.errorCode.getShortErrorCodeDescription()).append(")");
		}

		for (final Map.Entry<String, String> entry : this.properties.entrySet()) {
			sb.append(System.lineSeparator()).append(entry.getKey()).append("=[").append(entry.getValue()).append("]");
		}

		sb.append(System.lineSeparator()).append(SEPARATOR).append(System.lineSeparator());

		int stackTraceDepth = depth;

		if (depth == 0) {
			stackTraceDepth = FULL;
		}

		if (isLogged && stackTraceDepth == FULL) {
			return sb.toString();
		}

		sb.append(System.lineSeparator()).append(SEPARATOR).append(System.lineSeparator());

		final Throwable rootCause = ExceptionUtils.getRootCause(this);
		final String[] printableStacktrace;
		if (rootCause != null && this != rootCause) {
			sb.append(" [cause: ").append(ExceptionUtils.getRootCauseMessage(this)).append("]")
					.append(System.lineSeparator());
			printableStacktrace = ExceptionUtils.getRootCauseStackTrace(this);
		} else {
			printableStacktrace = ExceptionUtils.getStackFrames(this);
		}

		for (int i = 0; i < printableStacktrace.length && i <= stackTraceDepth; i++) {
			final String element = printableStacktrace[i];
			// element = element.replaceAll("\t", "");

			if (i > 0) {
				sb.append(System.lineSeparator());
			}

			sb.append(element);
		}

		// Update isLogged flag, when logging FULL depth.
		if (stackTraceDepth == FULL) {
			this.isLogged = true;
		}

		return sb.toString();
	}

	/**
	 * Utility method to wrap the given {@code exception} in an instance of
	 * {@code target}<br>
	 * <br>
	 * 
	 * In case the given {@code exception} is already of the correct type, only the
	 * {@code errorCode} will be updated, to preserve the {@code UUID}
	 * 
	 * @param target    the expected exception class.
	 * @param exception the actual exception to be wrapped.
	 * @param errorCode the target error code.
	 * @param supplier  supplies a new instance of the target exception <T> the
	 *                  generic expected exception type.
	 * @return an instance of the expected exception.
	 * @throws NullPointerException if either {@code target} or {@code exception} is
	 *                              {@code null}
	 */

	protected static <T extends RootRuntimeException> T wrap(final Class<T> target, final Throwable exception,
			final ErrorCode errorCode, Supplier<T> supplier) {

		if (exception != null) {
			if (target.isInstance(exception)) {
				final T se = target.cast(exception);

				if (errorCode != null && errorCode != se.getErrorCode()) {
					// In order to preserve UUID etc., just update the error code
					se.setErrorCode(errorCode);
				}

				return se;
			} else {
				final T suppliedInstance = supplier.get();

				if (exception instanceof RootRuntimeException) {
					// Preserve the UUID.
					suppliedInstance.setUuid(((RootRuntimeException) exception).getUuid());
				}

				return suppliedInstance;
			}
		} else {
			throw new NullPointerException("RootRuntimeException#wrap - Parameter 'exception' can not be null! ");
		}

	}

}
