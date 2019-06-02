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
package de.bomc.poc.hystrix.generic.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
/**
 * This annotation used to specify some methods which should be processes as
 * hystrix commands.
 *
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public @interface HystrixCommand {

	/**
	 * The default initial interval (in ms).
	 */
	long DEFAULT_INITIAL_INTERVAL = 100L;
	/**
	 * The default multiplier (increases the interval by 50%).
	 */
	double DEFAULT_MULTIPLIER = 1.75d;
	/**
	 * The default maximum back off time (in ms).
	 */
	long DEFAULT_MAX_INTERVAL = 30000L;
	/**
	 * The default maximum elapsed time (in ms).
	 */
	long DEFAULT_MAX_ELAPSED_TIME = 10000L;
	/**
	 * The default maximum retry count.
	 */
	int DEFAULT_MAX_RETRY_COUNT = 3;

	/**
	 * <p>
	 * The initial interval..
	 * </p>
	 * default to the DEFAULT_INITIAL_INTERVAL
	 * 
	 * @return group key
	 */
	long initialInterval() default DEFAULT_INITIAL_INTERVAL;

	/**
	 * <p>
	 * The default multiplier.
	 * </p>
	 * default to the DEFAULT_MULTIPLIER
	 * 
	 * @return group key
	 */
	double multiplier() default DEFAULT_MULTIPLIER;

	/**
	 * <p>
	 * The maximal retry count.
	 * </p>
	 * default to the DEFAULT_MAX_INTERVAL
	 * 
	 * @return group key
	 */
	int maxRetryCount() default DEFAULT_MAX_RETRY_COUNT;

	/**
	 * <p>
	 * The command group key is used for grouping together commands such as for
	 * reporting, alerting, dashboards or team/library ownership.
	 * </p>
	 * default to the runtime class name of annotated method
	 * 
	 * @return group key
	 */
	String groupKey() default "";

	/**
	 * Hystrix command key.
	 * <p>
	 * default to the name of annotated method. for example:
	 * </p>
	 * <code>
	 * ...
	 * &#64;HystrixCommand
	 * public User getUserById(...)
	 * ...
	 * the command name will be: 'getUserById'
	 * </code>
	 * 
	 * @return command key
	 */
	String commandKey() default "";

	/**
	 * Specifies command properties.
	 * 
	 * @return command properties
	 */
	HystrixProperty[] commandProperties() default {};
}
