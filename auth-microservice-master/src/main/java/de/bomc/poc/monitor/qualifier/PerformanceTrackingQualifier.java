/**
 * Project: MY_POC_MICROSERVICE
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.monitor.qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

/**
 * A qualifier for performance tracking.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface PerformanceTrackingQualifier {
	//
}
