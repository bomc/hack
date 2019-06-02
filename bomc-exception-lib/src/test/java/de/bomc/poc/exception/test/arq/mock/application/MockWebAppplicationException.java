package de.bomc.poc.exception.test.arq.mock.application;

import javax.ejb.ApplicationException;

import de.bomc.poc.exception.core.web.ApiError;
import de.bomc.poc.exception.core.web.WebRuntimeException;

/**
 * Describes the MockResource REST endpoint.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 */
@ApplicationException(rollback=true)
public class MockWebAppplicationException extends WebRuntimeException {
	
    /**
	 * The serial UUID.
	 */
	private static final long serialVersionUID = -1885804629842768196L;

	/**
     * Creates a new instance of <code>WebRuntimeException</code>.
     * @param apiError the error description.
     */
    public MockWebAppplicationException(final ApiError apiError) {
        super(apiError);
    }
}
