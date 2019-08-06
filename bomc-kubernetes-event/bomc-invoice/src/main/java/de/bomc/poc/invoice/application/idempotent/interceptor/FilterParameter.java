/**
 * Project: bomc-invoice
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
package de.bomc.poc.invoice.application.idempotent.interceptor;

import java.util.regex.Pattern;

/**
 * A helper class for extracting the messageId from the method signatur.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class FilterParameter {

	// RegEx constants.
	protected static final String UUID_REG = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";
	public static final String POSTFIX_REG = "iid";
	protected static final String MESSAGE_ID_REG = POSTFIX_REG + "_" + UUID_REG;
	// RegEx patterns.
	protected static final Pattern UUID_PATTERN = Pattern.compile(UUID_REG);
	protected static final Pattern MESSAGE_ID_PATTERN = Pattern.compile(MESSAGE_ID_REG);

	/**
	 * Extract message id from parameter object.
	 * 
	 * @param parameter the given parameter object.
	 * @return the idempotentId or "" if the parameter does not match the regex
	 *         pattern
	 */
	public String findIdempotentId(final Object parameter) {

		if (parameter != null && MESSAGE_ID_PATTERN.matcher(parameter.toString()).matches()) {
			return parameter.toString();
		}

		return "";
	}

	/**
	 * Extract user id from parameter object.
	 * 
	 * @param parameter the given parameter object.
	 * @return the userId or "" if the parameter does not match the regex pattern
	 */
	public String findUserId(final Object parameter) {

		if (parameter != null && UUID_PATTERN.matcher(parameter.toString()).matches()) {
			return parameter.toString();
		}

		return "";
	}
}
