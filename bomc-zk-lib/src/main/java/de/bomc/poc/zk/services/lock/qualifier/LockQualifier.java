package de.bomc.poc.zk.services.lock.qualifier;

import javax.ejb.LockType;
import javax.interceptor.InterceptorBinding;

import de.bomc.poc.zk.services.lock.interceptor.LockInterceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A qualifier for using the {@link LockInterceptor}.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
@Inherited
@Documented
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface LockQualifier {

    LockType value() default LockType.WRITE;
}
