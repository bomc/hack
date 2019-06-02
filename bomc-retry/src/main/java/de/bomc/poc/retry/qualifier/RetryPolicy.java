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
package de.bomc.poc.retry.qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Qualilfier for retry mechanism.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a> created
 *         10.01.2018
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryPolicy {

	/**
	 * The default initial interval (in ms).
	 */
	public static final long DEFAULT_INITIAL_INTERVAL = 2000L;
	/**
	 * The default multiplier (increases the interval by 50%).
	 */
	public static final double DEFAULT_MULTIPLIER = 1.5d;
	/**
	 * The default maximum back off time (in ms).
	 */
	public static final long DEFAULT_MAX_INTERVAL = 30000L;
	/**
	 * The default maximum elapsed time (in ms).
	 */
	public static final long DEFAULT_MAX_ELAPSED_TIME = 10000L;
	/**
	 * The default maximum retry count.
	 */
	public static final int DEFAULT_MAX_RETRY_COUNT = 10;

	public long initialInterval() default RetryPolicy.DEFAULT_INITIAL_INTERVAL;

	public double multiplier() default RetryPolicy.DEFAULT_MULTIPLIER;

	public int maxRetryCount() default RetryPolicy.DEFAULT_MAX_RETRY_COUNT;
}
