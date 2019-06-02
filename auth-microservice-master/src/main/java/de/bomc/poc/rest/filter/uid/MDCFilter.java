/**
 * Project: MY_POC
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2015
 */
package de.bomc.poc.rest.filter.uid;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;

/**
 * This filter adds the {@link MDC} properties X-BOMC-REQUEST-ID,
 * X-BOMC-BASE-URI, host.
 * <p/>
 * If you are using logback or log4j, you can append this to your pattern to
 * append the properties to each log entry:
 * <code>R:[%X{X-BOMC-REQUEST-ID}] U:[%X{X-BOMC-BASE-URI}]</code>
 *
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
@Provider
@ServerInterceptor
// Smaller numbers are first in the chain.
@Priority(value = Priorities.AUTHORIZATION -200)
public class MDCFilter implements ContainerRequestFilter, ContainerResponseFilter {

	private static final Logger LOGGER = Logger.getLogger(MDCFilter.class);
	public static final String HEADER_REQUEST_ID_ATTR = "X-BOMC-REQUEST-ID";
	private static final String MDC_REQUEST_BASE_URI = "X-BOMC-BASE-URI";

	/** 
	 * Filter method called after a response has been provided for a request
	 * (either by a {@link ContainerRequestFilter request filter} or by a
	 * matched resource method.
	 */
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		LOGGER.debug("MDCFilter#filter [requestContext.absolutePath=" + requestContext.getUriInfo().getAbsolutePath()
				+ ", responseContext.status=" + responseContext.getStatus() + "]");
		//
		// Remove all MDC's.
		MDC.remove(HEADER_REQUEST_ID_ATTR);
		MDC.remove(MDC_REQUEST_BASE_URI);
	}

	/**
	 * Filter method called before a request has been dispatched to a resource.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void filter(ContainerRequestContext requestContext) throws IOException {
		StringBuilder sb = new StringBuilder();

		if(MDC.getContext() != null) {
			MDC.getContext().forEach((key, value) -> sb.append("MDC.key=").append(key).append(", MDC.value=").append(value).append("\n"));
			LOGGER.debug("MDCFilter#filter(requestContext) [" + sb.toString() + "]");
		}

		String requestId;

		if (requestContext.getHeaders().containsKey(HEADER_REQUEST_ID_ATTR)) {
			//
			// Add request id to the MDC.
			List<String> headerList = requestContext.getHeaders().get(HEADER_REQUEST_ID_ATTR);

			if (headerList.size() == 1) {
				//
				// A request id is available.
				requestId = headerList.iterator().next();
				MDC.put(HEADER_REQUEST_ID_ATTR, requestId);
			} else {
				//
				// The request id is not set, so a new will be added.
				MDC.put(HEADER_REQUEST_ID_ATTR, UUID.randomUUID().toString());
			}
		} else {
			//
			// The request id is not set, so a new will be added.
			MDC.put(HEADER_REQUEST_ID_ATTR, UUID.randomUUID().toString());
		}

		// Add further diagnostic data.
		final String requestBaseUri = requestContext.getUriInfo().getBaseUri().toString();
		final String[] splitRequestBaseUri = requestBaseUri.split("//");
		MDC.put(MDC_REQUEST_BASE_URI, ">>" + splitRequestBaseUri[1]);
	}
}

