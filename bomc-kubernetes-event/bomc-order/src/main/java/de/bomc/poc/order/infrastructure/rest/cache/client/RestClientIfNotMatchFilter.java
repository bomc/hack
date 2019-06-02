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
 * A client filter for handling 'If-Not-Match' caching.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 11.02.2019
 */
public class RestClientIfNotMatchFilter implements ClientRequestFilter {

    private final String ifNotMatch;

    /**
     * Creates a new instance of <code>RestClientIfNotMatchFilter</code>.
     * 
     * @param ifNotMatch
     *            ifNotMatch tag.
     */
    public RestClientIfNotMatchFilter(final String ifNotMatch) {
        this.ifNotMatch = ifNotMatch;

    }

    @Override
    public void filter(final ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().putSingle(HttpHeaders.IF_NONE_MATCH, this.ifNotMatch);

    }
}
