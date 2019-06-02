package de.bomc.poc.logging.qualifier;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A qualifier for the {@link de.bomc.poc.logging.interceptor.ThreadTrackerInterceptor}.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @since 19.07.2016
 */
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target({TYPE})
public @interface ThreadTrackerQualifier {}
