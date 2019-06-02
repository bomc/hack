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
package de.bomc.poc.controller.infrastructure.rest.cache.qualifier;

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
    
    public boolean isPrivate() default true;
    
    public boolean noCache() default false;
    
    public boolean noStore() default false;
    
    public boolean noTransform() default true;
    
    public boolean mustRevalidate() default true;
    
    public boolean proxyRevalidate() default false;
    
    public int maxAge() default 0;
    
    public int sMaxAge() default 0;
    
}
