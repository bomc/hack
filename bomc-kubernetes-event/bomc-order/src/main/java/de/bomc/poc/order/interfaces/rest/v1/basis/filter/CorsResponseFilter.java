/**
 * Project: bomc-order
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
package de.bomc.poc.order.interfaces.rest.v1.basis.filter;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * See http://www.w3.org/TR/cors/
 * <p>
 *
 * @author <a href="mailto:michael.boerner@bs.ch">bomc</a>
 * @since 08.08.2019
 */
@Provider
public class CorsResponseFilter implements ContainerResponseFilter {

    public static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    public static final int MAX_AGE = 42 * 60 * 60;
    public static final String DEFAULT_ALLOWED_HEADERS = "origin,accept,content-type";
    public static final String DEFAULT_EXPOSED_HEADERS = "location,info";

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
            throws IOException {
        final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Headers", getRequestedAllowedHeaders(requestContext));
        headers.add("Access-Control-Expose-Headers", getRequestedExposedHeaders(requestContext));
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
        headers.add("Access-Control-Max-Age", MAX_AGE);
        headers.add("x-responded-by", "cors-response-filter");
    }

    String getRequestedAllowedHeaders(final ContainerRequestContext responseContext) {
        final List<String> headers = responseContext.getHeaders().get("Access-Control-Allow-Headers");
        
        return createHeaderList(headers, DEFAULT_ALLOWED_HEADERS);
    }

    String getRequestedExposedHeaders(final ContainerRequestContext responseContext) {
        final List<String> headers = responseContext.getHeaders().get("Access-Control-Expose-Headers");
        
        return createHeaderList(headers, DEFAULT_EXPOSED_HEADERS);
    }

    String createHeaderList(final List<String> headers, final String defaultHeaders) {
        if (headers == null || headers.isEmpty()) {
            return defaultHeaders;
        }
        
        final StringBuilder retVal = new StringBuilder();
        
        for (int i = 0; i < headers.size(); i++) {
            final String header = (String) headers.get(i);
            retVal.append(header);
            retVal.append(',');
        }
        
        retVal.append(defaultHeaders);
        
        return retVal.toString();
    }

}