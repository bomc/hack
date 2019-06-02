package de.bomc.poc.exception.test;

import javax.ws.rs.core.Response;

import de.bomc.poc.exception.core.web.ApiError;

/**
 * Kommentar.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 6790 $ $Author: tzdbmm $ $Date: 2016-07-19 09:06:34 +0200 (Di, 19 Jul 2016) $
 * @since 12.07.2016
 */
public enum TestApiError implements ApiError {

    TEST_API_00101(Response.Status.INTERNAL_SERVER_ERROR, "Invalid arguments"),
    TEST_API_00102(Response.Status.INTERNAL_SERVER_ERROR, "Invalid parameter");

    private final Response.Status status;
    private final String shortErrorCodeDescription;

    /**
     * Creates a new instance of <code>ApiRuntimeError</code>.
     * @param status     the rest status.
     */
    TestApiError(final Response.Status status, final String shortErrorCodeDescription) {
        this.status = status;
        this.shortErrorCodeDescription = shortErrorCodeDescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response.Status getStatus() {
        return this.status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortErrorCodeDescription() {
        return this.shortErrorCodeDescription;
    }
}
