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
package de.bomc.poc.hrm.application.metrics.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A annotation that enables metrics collection for type 'counter' and 'timer'.
 * The metrics can be turn 'on' and 'off' by the given attributes. Per default all
 * switches are turning 'on'.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 20.11.2019
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD, ElementType.TYPE })
public @interface Metric {

	boolean counter() default true;

	boolean timer() default true;
}
