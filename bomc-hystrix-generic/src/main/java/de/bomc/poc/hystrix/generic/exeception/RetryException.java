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

/**
 * Indicates a retry has to be performed.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public class RetryException extends RuntimeException {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 1224470447251955541L;

	/**
	 * Default constructor.
	 */
	public RetryException() {
		//
	}

	public RetryException(final String message) {
		super(message);
	}

	public RetryException(final String message, final Throwable cause) {
		super(message, cause);

		//
	}
}
