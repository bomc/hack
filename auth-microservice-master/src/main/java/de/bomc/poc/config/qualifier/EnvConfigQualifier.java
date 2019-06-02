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
package de.bomc.poc.config.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import de.bomc.poc.config.EnvConfigKeys;

/**
 * A qualifier for injecting environment parameter.
 * 
 * <pre>
 * Use it:
 * 
 * -at Inject 
 * -at EnvConfigQualifier(key = EnvConfigKeys.BIND_ADDRESS)
 * private String bindAddress;
 * </pre>
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
@Documented
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface EnvConfigQualifier {

	@Nonbinding EnvConfigKeys key();
}
