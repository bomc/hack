package de.bomc.poc.logging.filter.mock;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.filter.MDCFilter;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * A mock <code>ClientRequestFilter</code>.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 7096 $ $Author: tzdbmm $ $Date: 2016-08-03 14:59:36 +0200 (Mi, 03 Aug 2016) $
 * @since 20.07.2016
 */
public class MockHeaderRequestFilter implements ClientRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(MockHeaderRequestFilter.class);
    private static final String LOG_PREFIX = "MockHeaderRequestFilter#";

    /**
     * Creates a new instance of <code>MockHeaderRequestFilter</code> default#co.
     */
    public MockHeaderRequestFilter() {
        //
    }

    /**
     * Filters the request, adds a null value to HEADER_REQUEST_ID_ATTR.
     * @param requestContext the request context.
     */
    @Override
    public void filter(final ClientRequestContext requestContext) {
        LOGGER.debug(LOG_PREFIX + "filter");

        final String HEADER_REQUEST_ID_ATTR_1 = "X-EGOV-REQUEST-TEST_1";
        final String HEADER_REQUEST_ID_ATTR_2 = "X-EGOV-REQUEST-TEST_2";

        final MultivaluedMap<String, Object> headers = requestContext.getHeaders();

        headers.add(MDCFilter.HEADER_REQUEST_ID_ATTR, HEADER_REQUEST_ID_ATTR_1);
        headers.add(MDCFilter.HEADER_REQUEST_ID_ATTR, HEADER_REQUEST_ID_ATTR_2);
    }
}
