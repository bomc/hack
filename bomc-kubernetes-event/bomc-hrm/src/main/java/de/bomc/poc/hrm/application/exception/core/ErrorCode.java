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
 * This class enumerate all potential errors related to using the Object Server
 * or synchronizing data.
 *
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 20.09.2019
 */
public interface ErrorCode {

	public enum Category {
		ERROR, // Still possible to recover the session by either rebinding or
				// providing the required information.
		FATAL, // Abort session as soon as possible
	}

	/**
	 * Returns the numerical value for this error code.
	 *
	 * @return the error code as an unique {@code int} value.
	 */
	int intValue();

	String getShortErrorCodeDescription();

	Category getCategory();

	String toString();

}
