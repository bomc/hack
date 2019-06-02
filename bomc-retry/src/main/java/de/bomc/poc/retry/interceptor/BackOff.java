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
package de.bomc.poc.retry.interceptor;

/**
 * <pre>
 * Provide a {@link BackOffExecution} that indicates the rate at which
 * an operation should be retried.
 *
 * Users of this interface are expected to use it like this:
 *
 * <pre class="code">
 * BackOffExecution exec = backOff.start();
 *
 * // In the operation recovery/retry loop:
 * long waitInterval = exec.nextBackOff();
 *
 * if (waitInterval == BackOffExecution.STOP) {
 *      // do not retry operation
 * } else {
 *      // sleep, e.g. Thread.sleep(waitInterval)
 *      // retry operation
 * }
 * </pre>
 * 
 * Once the underlying operation has completed successfully, the execution
 * instance can be simply discarded.
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 13.01.2018
 */
public interface BackOff {

	/**
	 * Start a new back off execution.
	 * 
	 * @return a fresh {@link BackOffExecution} ready to be used.
	 */
	BackOffExecution start();
}
