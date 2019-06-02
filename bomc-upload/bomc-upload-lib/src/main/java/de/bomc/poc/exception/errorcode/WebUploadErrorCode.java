/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-23 13:46:51 +0100 (Mo, 23 Jan 2017) $
 *
 *  revision: $Revision: 9964 $
 *
 * </pre>
 */
package de.bomc.poc.exception.errorcode;

import javax.ws.rs.core.Response;

import de.bomc.poc.exception.core.web.ApiError;

/**
 * Describes the available error codes for handling with api.
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 03.08.2016
 */
public enum WebUploadErrorCode implements ApiError {

    API_BOMC_00100(Response.Status.INTERNAL_SERVER_ERROR, "Not defined exception"),
    API_BOMC_00200(Response.Status.INTERNAL_SERVER_ERROR, "Error during REST handling"),
    API_BOMC_00300(Response.Status.BAD_REQUEST, "Multipart content is corrupt");
    private final Response.Status status;
    private final String shortErrorCodeDescription;

    /**
     * Creates a new instance of <code>ApiRuntimeError</code>.
     * @param status                    the rest status.
     * @param shortErrorCodeDescription a short description of the error.
     */
    WebUploadErrorCode(final Response.Status status, final String shortErrorCodeDescription) {
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
