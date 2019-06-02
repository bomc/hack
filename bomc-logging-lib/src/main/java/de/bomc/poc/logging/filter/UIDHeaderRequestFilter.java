package de.bomc.poc.logging.filter;

import org.apache.log4j.Logger;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.util.UUID;

/**
 * A filter (used by a REST client) to add the header attribute requestId. This filter checks every request if a requestId as a header parameter is set. If the parameter is set the parameter will be adopted, otherwise a
 * new requestId will be generated as {@link UUID}. The parameter HEADER_REQUEST_ID_ATTR is defined in {@link MDCFilter}.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @since 19.07.2016
 */
public class UIDHeaderRequestFilter implements ClientRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(UIDHeaderRequestFilter.class);
    private static final String LOG_PREFIX = "UIDHeaderRequestFilter#";
    private String requestId;

    /**
     * Creates a new instance of <code>UIDHeaderRequestFilter</code> default#co.
     */
    public UIDHeaderRequestFilter() {
        //
    }

    /**
     * Creates a new instance of <code>UIDHeaderRequestFilter</code>.
     * @param requestId the given requestId, comes with client request.
     */
    public UIDHeaderRequestFilter(final String requestId) {
        LOGGER.debug(LOG_PREFIX + "co [requestId=" + requestId + "]");

        this.requestId = requestId;
    }

    /**
     * Filters the header for a requestId. If a requestId is available, the given requestId is added to the header otherwise a new requestId is generated and added to the header.
     * @param requestContext the request context.
     */
    @Override
    public void filter(final ClientRequestContext requestContext) {
        LOGGER.debug(LOG_PREFIX + "filter [requestId=" + this.requestId + "]");

        final MultivaluedMap<String, Object> headers = requestContext.getHeaders();

        if (this.requestId == null || this.requestId.isEmpty()) {
            headers.add(MDCFilter.HEADER_REQUEST_ID_ATTR, UUID.randomUUID()
                                                              .toString());
        } else {
            headers.add(MDCFilter.HEADER_REQUEST_ID_ATTR, this.requestId);
        }
    }
}
