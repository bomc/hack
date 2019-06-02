package de.bomc.poc.exception.core;

import org.apache.log4j.Logger;

public enum BasisErrorCodeEnum implements ErrorCode {

	// MAJOR /general response errors (10000-10499)
	UNEXPECTED_10000(10000, Category.ERROR, "This error should not happen! "),

	// Indicates resilience error (10500-10599)
	CONNECTION_FAILURE_10500(10500, Category.FATAL, "Connection timed out! ");

	private final static  String LOG_PREFIX = "BasisErrorCodeEnum#";
	private final static Logger LOGGER = Logger.getLogger(BasisErrorCodeEnum.class);
	//
	// Error code description.
	private final int code;
	private final Category category;
	private String shortErrorCodeDescription = "no description available";

	BasisErrorCodeEnum(final int errorCode) {
		this(errorCode, Category.ERROR);
	}

	BasisErrorCodeEnum(final int errorCode, final Category category) {
		this.code = errorCode;
		this.category = category;
	}

	BasisErrorCodeEnum(final int errorCode, final Category category, final String shortErrorCodeDescription) {
		this.code = errorCode;
		this.category = category;
		this.shortErrorCodeDescription = shortErrorCodeDescription;
	}

	/**
	 * Returns the numerical value for this error code.
	 *
	 * @return the error code as an unique {@code int} value.
	 */
	@Override
	public int intValue() {
		return this.code;
	}

	@Override
	public String getShortErrorCodeDescription() {
		return this.shortErrorCodeDescription;
	}

	/**
	 * Returns the <code>ErrorCode</code> on the depending int value.
	 * 
	 * @param errorCode
	 *            the given int value.
	 * @return the <code>ErrorCode</code> on the depending int value.
	 */
	public static ErrorCode fromInt(final int errorCode) {
		final ErrorCode[] errorCodes = values();

		for (int i = 0; i < errorCodes.length; i++) {
			final ErrorCode error = errorCodes[i];
			if (error.intValue() == errorCode) {
				return error;
			}
		}

		throw new IllegalArgumentException("Unknown error code: " + errorCode);
	}
	
	/**
	 * Returns the <code>ErrorCode</code> on the depending string value.
	 * 
	 * @param errorCodeStr
	 *            the given string value.
	 * @return the <code>ErrorCode</code> on the depending string value.
	 */
	public static ErrorCode errorCodeFromString(final String errorCodeStr) {
		try {
			return Enum.valueOf(BasisErrorCodeEnum.class, errorCodeStr);
		} catch (final IllegalArgumentException e) {
			LOGGER.warn(LOG_PREFIX + "errorCodefromString - could not parse error code to Integer: " + errorCodeStr + ", set new errorCode: "
					+ BasisErrorCodeEnum.UNEXPECTED_10000);
			return BasisErrorCodeEnum.UNEXPECTED_10000;
		}
	}
	
	/**
	 * Returns the getCategory of the error.
	 * <p>
	 * Errors come in 2 categories: ERROR, FATAL
	 * <p>
	 * ERROR: A temporary error.
	 * <p>
	 * FATAL: The flow has to be aborted.
	 * <p>
	 *
	 * @return the severity of the error.
	 */
	@Override
	public Category getCategory() {
		return this.category;
	}

	@Override
	public String toString() {
		// Do not overwrite this method. It has an impact to the enum.name()
		// method.
		return super.toString();
	}
}
