package de.bomc.poc.zk.services.registry.qualifier;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A qualifier for injecting zookeeper functionality.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
@Qualifier
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface ZookeeperServiceRegistryQualifier {}
