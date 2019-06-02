/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.order.application.config.qualifier;

import de.bomc.poc.order.application.config.ConfigKeys;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A qualifier for injecting configuration properties.
 * <p>
 * 
 * <pre>
 * Use it:
 *
 * &#064;Inject
 * &#064;ConfigQualifier(key = ConfigKeys.PRODUCER)
 * private String host;
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Documented
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ConfigQualifier {

    @Nonbinding
    ConfigKeys key();
}
