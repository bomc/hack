package de.bomc.poc.logging.filter.mock;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import de.bomc.poc.logging.filter.MDCFilter;

import javax.ws.rs.core.Response;

/**
 * This endpoint works as a mock and is the implementation of the {@link MockResourceInterface}.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: 7096 $ $Author: tzdbmm $ $Date: 2016-08-03 14:59:36 +0200 (Mi, 03 Aug 2016) $
 * @since 12.07.2016
 */
public class MockResource implements MockResourceInterface {

    private static final Logger LOGGER = Logger.getLogger(MockResource.class);
    private static final String LOG_PREFIX = "MockResource#";

    @Override
    public Response logToMe(final Long id) {
        LOGGER.debug(LOG_PREFIX + "logToMe [id=" + id + "]");

        final String retVal;

        final Object objRequestID = MDC.get(MDCFilter.HEADER_REQUEST_ID_ATTR);

        if (objRequestID != null) {
            retVal = objRequestID.toString();
        } else {
            retVal = String.valueOf(id);
        }

        return Response.status(Response.Status.OK)
                       .entity(retVal)
                       .build();
    }
}
