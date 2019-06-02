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
 * Copyright (c): BOMC, 2015
 */
package de.bomc.poc.logger.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * <pre>
 * Use it:
 * ------_
 * at-Inject
 * at-LoggerQualifier(logPrefix = "#test#unterbau#ccc#KlassenName#")
 * private de.bomc.poc.kdm.log.Logger log;
 * ----------------------------------------------------------------------------------
 * </pre>
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@Documented
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
public @interface LoggerQualifier {

    String DEFAULT_PREFIX = "#";

    @Nonbinding String logPrefix() default LoggerQualifier.DEFAULT_PREFIX;
}
