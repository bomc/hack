package de.bomc.poc.logging.filter;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 *  This filter adds the {@link MDC} properties X-EGOV-REQUEST-ID, X-EGOV-BASE-URI, host.
 * </pre>
 * 
 * Append this to log pattern to append the properties to each log entry:
 * <code>R:[%X{X-EGOV-REQUEST-ID}] U:[%X{X-EGOV-BASE-URI}]</code>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @since 19.07.2016
 */
@Provider
// @ServerInterceptor
// Smaller numbers are first in the chain.
@Priority(value = Priorities.AUTHORIZATION - 200)
public class MDCFilter implements ContainerRequestFilter, ContainerResponseFilter {

	private static final Logger LOGGER = Logger.getLogger(MDCFilter.class);
	private static final String LOG_PREFIX = "MDCFilter#";
	public static final String HEADER_REQUEST_ID_ATTR = "x-bomc-request-id";
	private static final String MDC_REQUEST_BASE_URI = "x-bomc-base-uri";

	/**
	 * Filter method called after a response has been provided for a request
	 * (either by a {@link ContainerRequestFilter request filter} or by a
	 * matched resource method.
	 */
	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
			throws IOException {
		LOGGER.debug(LOG_PREFIX + "filter - after a response has been provided. [requestContext.absolutePath="
				+ requestContext.getUriInfo().getAbsolutePath() + ", responseContext.status="
				+ responseContext.getStatus() + "]");
		//
		// Remove all MDC's.
		MDC.remove(HEADER_REQUEST_ID_ATTR);
		MDC.remove(MDC_REQUEST_BASE_URI);
	}

	/**
	 * Filter method called before a request has been dispatched to a resource.
	 */
	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		if (MDC.getContext() != null) {
			final String requestId;

			if (requestContext.getHeaders().containsKey(HEADER_REQUEST_ID_ATTR)) {
				//
				// Add request id to the MDC.
				final List<String> headerList = requestContext.getHeaders().get(HEADER_REQUEST_ID_ATTR);

				if (headerList.size() == 1) {
					//
					// A request id is available.
					requestId = headerList.iterator().next();
					MDC.put(HEADER_REQUEST_ID_ATTR, requestId);
				} else {
					//
					// The list size has unexpected size, expected is 1.
					LOGGER.warn(LOG_PREFIX + "filter - the header '" + HEADER_REQUEST_ID_ATTR
							+ "' has unexpected size, must be 1. Generate a new one. [size=" + headerList.size() + "]");
					MDC.put(HEADER_REQUEST_ID_ATTR, UUID.randomUUID().toString());
				}
			} else {
				//
				// The request id is not set, so a new will be added.
				LOGGER.info(LOG_PREFIX + "filter - no requestId is set, a own will be created.");
				MDC.put(HEADER_REQUEST_ID_ATTR, UUID.randomUUID().toString());
			}

			// Add further diagnostic data.
			final String requestBaseUri = requestContext.getUriInfo().getBaseUri().toString();
			final String[] splitRequestBaseUri = requestBaseUri.split("//");
			MDC.put(MDC_REQUEST_BASE_URI, ">>" + splitRequestBaseUri[1]);
		} else {
			LOGGER.warn(LOG_PREFIX + "#filter - MDC.getContext is null.");
		}
	}
}
