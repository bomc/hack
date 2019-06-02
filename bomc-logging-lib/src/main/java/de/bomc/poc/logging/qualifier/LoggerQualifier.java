package de.bomc.poc.logging.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * <pre>
 * ________
 * Use it:
 * --------
 * &#064;Inject
 * &#064;LoggerQualifier(logPrefix = "#my-custom-prefix")
 * private Logger logger;
 * ----------------------------------------------------------------------------------
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @since 19.07.2016
 */
@Documented
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
public @interface LoggerQualifier {

    String DEFAULT_PREFIX = "#";

    @Nonbinding String logPrefix() default LoggerQualifier.DEFAULT_PREFIX;
}
