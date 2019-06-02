#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.application.config.qualifier;

import ${package}.application.config.ConfigKeys;

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
 * <pre>
 * Use it:
 *
 * &${symbol_pound}064;Inject
 * &${symbol_pound}064;ConfigQualifier(key = ConfigKeys.PRODUCER)
 * private String host;
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Documented
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ConfigQualifier {

    @Nonbinding ConfigKeys key();
}
