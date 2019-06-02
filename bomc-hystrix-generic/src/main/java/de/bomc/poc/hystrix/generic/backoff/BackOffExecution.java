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
package de.bomc.poc.hystrix.generic.backoff;

/**
 * <pre>
 * Represent a particular back-off execution.
 *
 * Implementations do not need to be thread safe.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public interface BackOffExecution {

    /**
     * @return value of {@link #nextBackOff()} that indicates that the operation should not be retried.
     */
    long STOP = -1;

    /**
     * @return the number of milliseconds to wait before retrying the operation or {@link #STOP} ({@value #STOP}) to indicate that no further attempt should be made for the operation.
     */
    long nextBackOff();

    /**
     *  @return the retryCount.
     */
    int getRetryCount();
}
