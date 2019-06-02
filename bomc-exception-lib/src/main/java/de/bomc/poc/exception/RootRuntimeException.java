package de.bomc.poc.exception;

import org.apache.commons.lang.exception.ExceptionUtils;

import de.bomc.poc.exception.core.ErrorCode;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * The root exception
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 6790 $ $Author: tzdbmm $ $Date: 2016-07-19 09:06:34 +0200
 *          (Di, 19 Jul 2016) $
 * @since 13.07.2016
 */
public abstract class RootRuntimeException extends RuntimeException {

	/**
	 * The serial UUID.
	 */
	private static final long serialVersionUID = -6545655784862054093L;
	/**
	 * Separates the custom exception info from the stacktrace.
	 */
	public static final String SEPARATOR = "-------------------------------";
	/**
	 * Print the full stacktrace.
	 */
	public static final Integer FULL = Integer.MAX_VALUE;
	/**
	 * Mark the exception is already logged.
	 */
	private boolean isLogged = false;
	/**
	 * Describes the error that uniquely identifies the occurred error.
	 */
	private ErrorCode errorCode;
	/**
	 * Holds additional information to the exception as properties.
	 */
	private final Map<String, String> properties = new TreeMap<>();
	/**
	 * Identifier for exception.
	 */
	private final String uuid = UUID.randomUUID().toString();

	/**
	 * Creates a new instance of <code>RootRuntimeException</code>.
	 * 
	 * @param errorCode
	 *            the error description.
	 */
	public RootRuntimeException(final ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Creates a new instance of <code>RootRuntimeException</code>.
	 * 
	 * @param message
	 *            the specified detail message.
	 * @param errorCode
	 *            the error description.
	 */
	public RootRuntimeException(final String message, final ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * Creates a new instance of <code>RootRuntimeException</code>.
	 * 
	 * @param cause
	 *            the <code>Throwable</code> of the exception.
	 * @param errorCode
	 *            the error description.
	 */
	public RootRuntimeException(final Throwable cause, final ErrorCode errorCode) {
		super(cause);
		this.errorCode = errorCode;
	}

	/**
	 * Creates a new instance of <code>AppRuntimeException</code>.
	 * 
	 * @param message
	 *            the specified detail message.
	 * @param cause
	 *            the <code>Throwable</code> of the exception.
	 * @param errorCode
	 *            the error description.
	 */
	public RootRuntimeException(final String message, final Throwable cause, final ErrorCode errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
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
	 * @param isLogged
	 *            true the exception is already logged otherwise false.
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
	 * @param errorCode
	 *            the error description.
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
	 * @return the additional properties.
	 */
	public Map<String, String> getProperties() {
		return this.properties;
	}

	/**
	 * Return a property to the given name.
	 * 
	 * @param name
	 *            the given key.
	 * @param <T>
	 *            the type of the return value.
	 * @return a property to the given name.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(final String name) {
		return (T) this.properties.get(name);
	}

	/**
	 * Set a additional information to the exception.
	 * 
	 * @param name
	 *            the name of the property.
	 * @param value
	 *            the value of the property.
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
	 * @param depth
	 *            the stacktrace depth.
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
					.append(this.errorCode.getShortErrorCodeDescription()).append(")").append(": ")
					.append(this.errorCode.getClass().getName());
		}

		for (Map.Entry<String, String> entry : this.properties.entrySet()) {
			sb.append(System.lineSeparator()).append(entry.getKey()).append("=[").append(entry.getValue()).append("]");
		}

		sb.append(System.lineSeparator()).append(SEPARATOR).append(System.lineSeparator());

		int stackTraceDepth = depth;

		if (depth == 0) {
			stackTraceDepth = FULL;
		}

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
			String element = printableStacktrace[i];
//			element = element.replaceAll("\t", "");

			if (i > 0) {
				sb.append(System.lineSeparator());
			}

			sb.append(element);
		}

		return sb.toString();
	}
}
