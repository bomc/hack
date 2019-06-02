package de.bomc.poc.zk.config.env.qualifier;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import de.bomc.poc.zk.config.env.EnvConfigKeys;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

/**
 * A qualifier for injecting environment parameter.
 * <p/>
 * <pre>
 * Use it:
 *
 * &#64;Inject
 * &#64;EnvConfigQualifier(key = EnvConfigKeys.ZNODE_BASE_PATH)
 * private String bindAddress;
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
@Documented
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface EnvConfigQualifier {

    @Nonbinding EnvConfigKeys key();
}
