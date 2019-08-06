/**
 * Project: bomc-invoice
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
package de.bomc.poc.invoice.application.idempotent.qualifier;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Binding for the
 * {@link de.bomc.poc.invoice.application.idempotent.interceptor.IdempotentFilterInterceptor}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Retention(RUNTIME)
@Target({ METHOD, TYPE })
@InterceptorBinding
public @interface IdempotentFilterQualifier {

}
