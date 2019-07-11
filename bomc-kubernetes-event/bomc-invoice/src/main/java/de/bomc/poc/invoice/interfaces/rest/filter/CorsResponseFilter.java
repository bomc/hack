package de.bomc.poc.invoice.interfaces.rest.filter;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsResponseFilter implements ContainerResponseFilter {

	public static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
	public final static int MAX_AGE = 42 * 60 * 60;
	public final static String DEFAULT_ALLOWED_HEADERS = "origin,accept,content-type";
	public final static String DEFAULT_EXPOSED_HEADERS = "location,info";

	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
			throws IOException {
		
		final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Headers", getRequestedAllowedHeaders(requestContext));
		headers.add("Access-Control-Expose-Headers", getRequestedExposedHeaders(requestContext));
		headers.add("Access-Control-Allow-Credentials", "true");
		headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
		headers.add("Access-Control-Max-Age", MAX_AGE);
		headers.add("x-responded-by", "cors-response-filter");
	}

	String getRequestedAllowedHeaders(final ContainerRequestContext responseContext) {
		
		final List<String> headers = responseContext.getHeaders().get("Access-Control-Allow-Headers");
		
		return createHeaderList(headers, DEFAULT_ALLOWED_HEADERS);
	}

	String getRequestedExposedHeaders(final ContainerRequestContext responseContext) {
		
		final List<String> headers = responseContext.getHeaders().get("Access-Control-Expose-Headers");
		
		return createHeaderList(headers, DEFAULT_EXPOSED_HEADERS);
	}

	String createHeaderList(final List<String> headers, final String defaultHeaders) {
		
		if (headers == null || headers.isEmpty()) {
			return defaultHeaders;
		}
		
		StringBuilder retVal = new StringBuilder();
		
		for (int i = 0; i < headers.size(); i++) {
			String header = (String) headers.get(i);
			retVal.append(header);
			retVal.append(',');
		}
		
		retVal.append(defaultHeaders);
		
		return retVal.toString();
	}

}
