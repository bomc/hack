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
package de.bomc.poc.order.infrastructure.rest.cache.producer;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.ws.rs.core.CacheControl;

import de.bomc.poc.order.infrastructure.rest.cache.qualifier.CacheControlConfigQualifier;

/**
 * A producer for cache control.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
public class CacheControlProducer {
    
    @Produces
    public CacheControl get(final InjectionPoint ip) {
        
        final CacheControlConfigQualifier cacheControlConfig = ip.getAnnotated()
                .getAnnotation(CacheControlConfigQualifier.class);
        CacheControl cacheControl = null;
        
        if (cacheControlConfig != null) {
            cacheControl = new CacheControl();
            cacheControl.setMaxAge(cacheControlConfig.maxAge());
            cacheControl.setMustRevalidate(cacheControlConfig.mustRevalidate());
            cacheControl.setNoCache(cacheControlConfig.noCache());
            cacheControl.setNoStore(cacheControlConfig.noStore());
            cacheControl.setNoTransform(cacheControlConfig.noTransform());
            cacheControl.setPrivate(cacheControlConfig.isPrivate());
            cacheControl.setProxyRevalidate(cacheControlConfig.proxyRevalidate());
            cacheControl.setSMaxAge(cacheControlConfig.sMaxAge());
        }
        
        return cacheControl;
    }
}
