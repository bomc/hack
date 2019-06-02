/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2016-12-23 14:20:47 +0100 (Fr, 23 Dez 2016) $
 *
 *  revision: $Revision: 9598 $
 *
 * </pre>
 */
package de.bomc.poc.upload.configuration.qualifier;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import de.bomc.poc.upload.configuration.ConfigKeys;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A qualifier for injecting configuration properties.
 *
 * <pre>
 * Use it:
 *
 * &#064;Inject
 * &#064;ConfigQualifier(key = ConfigKeys.LAGACY_SERVICE_HOST)
 * private String host;
 * </pre>
 *
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 21.12.2016
 */
@Documented
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ConfigQualifier {

	@Nonbinding ConfigKeys key();
}
