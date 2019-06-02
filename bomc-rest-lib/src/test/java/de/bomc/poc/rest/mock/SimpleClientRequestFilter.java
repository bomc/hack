package de.bomc.poc.rest.mock;

import org.apache.log4j.Logger;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

/**
 * A simple <code>ClientRequestFilter</code>.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: 7051 $ $Author: tzdbmm $ $Date: 2016-07-22 10:57:35 +0200 (Fr, 22 Jul 2016) $
 * @since 19.07.2016
 */
public class SimpleClientRequestFilter implements ClientRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(SimpleClientRequestFilter.class);
    private static final String LOG_PREFIX = "SimpleClientRequestFilter#";

    /**
     * Creates a new instance of <code>SimpleClientRequestFilter</code> default#co.
     */
    public SimpleClientRequestFilter() {
        LOGGER.debug(LOG_PREFIX + "co - I'm here... and do the whole enchilada");
    }

    /**
     * Methode is invoked by the request.
     * @param requestContext the request context.
     */
    @Override
    public void filter(final ClientRequestContext requestContext) {
        LOGGER.debug(LOG_PREFIX + "filter - I'm here... and do the whole enchilada");
    }
}
