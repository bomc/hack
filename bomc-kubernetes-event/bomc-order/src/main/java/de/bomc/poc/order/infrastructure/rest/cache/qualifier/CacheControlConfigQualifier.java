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
package de.bomc.poc.order.infrastructure.rest.cache.qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Qualifier for handling the REST cache control.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface CacheControlConfigQualifier {
    
    boolean isPrivate() default true;
    
    boolean noCache() default false;
    
    boolean noStore() default false;
    
    boolean noTransform() default true;
    
    boolean mustRevalidate() default true;
    
    boolean proxyRevalidate() default false;
    
    int maxAge() default 0;
    
    int sMaxAge() default 0;
    
}
