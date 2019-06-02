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
package de.bomc.poc.order.infrastructure.rest.cache.client;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;

/**
 * A client filter for handling 'Last-Modified' caching.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 11.02.2019
 */
public class RestClientLastModifiedFilter implements ClientRequestFilter {

    private final String lastModifedDate;

    /**
     * Creates a new instance of <code>RestClientLastModifiedFilter</code>.
     * 
     * @param lastModifedDate
     *            the last modifed date.
     */
    public RestClientLastModifiedFilter(final String lastModifedDate) {
        this.lastModifedDate = lastModifedDate;

    }

    @Override
    public void filter(final ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().putSingle(HttpHeaders.IF_MODIFIED_SINCE, this.lastModifedDate);

    }
}
