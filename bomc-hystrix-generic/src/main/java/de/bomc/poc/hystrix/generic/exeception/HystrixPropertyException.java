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
 * This exception indicates a property configuration error.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public class HystrixPropertyException extends RuntimeException {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -411341471870772550L;

	/**
	 * Default constructor.
	 */
	public HystrixPropertyException() {
		//
	}

	public HystrixPropertyException(final Throwable cause) {
		super(cause);
	}

	public HystrixPropertyException(final String message, final Throwable cause) {
		super(message, cause);

		//
	}
}
