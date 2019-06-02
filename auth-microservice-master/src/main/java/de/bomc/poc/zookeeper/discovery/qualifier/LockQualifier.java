/**
 * Project: MY_POC
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
package de.bomc.poc.zookeeper.discovery.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ejb.LockType;
import javax.interceptor.InterceptorBinding;

import de.bomc.poc.zookeeper.discovery.interceptor.LockInterceptor;

/**
 * A qualifier for using the {@link LockInterceptor}.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
@Inherited
@Documented
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface LockQualifier {
	LockType value() default LockType.WRITE;
}
