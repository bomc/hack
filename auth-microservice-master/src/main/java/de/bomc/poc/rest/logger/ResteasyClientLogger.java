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
package de.bomc.poc.rest.logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.apache.log4j.Logger;

/**
 * A client logger for Resteasy to log the traffic.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class ResteasyClientLogger implements /*ContainerRequestFilter,*/ ClientRequestFilter, /*ContainerResponseFilter,*/
		ClientResponseFilter, WriterInterceptor {

	private static final Logger LOGGER = Logger.getLogger(ResteasyClientLogger.class.getName());

	private static final String NOTIFICATION_PREFIX = "* ";

	private static final String REQUEST_PREFIX = "> ";

	private static final String RESPONSE_PREFIX = "< ";

	private static final String ENTITY_LOGGER_PROPERTY = ResteasyClientLogger.class.getName() + ".entityLogger";

	/**
	 * <pre>
	 * 	private static final Comparator<Map.Entry<String, List<String>> COMPARATOR = (o1, o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()));
	 * </pre>
	 */
	private static final Comparator<Map.Entry<String, List<String>>> COMPARATOR = new Comparator<Map.Entry<String, List<String>>>() {

		@Override
		public int compare(final Map.Entry<String, List<String>> o1, final Map.Entry<String, List<String>> o2) {
			return o1.getKey().compareToIgnoreCase(o2.getKey());
		}
	};

	private static final int DEFAULT_MAX_ENTITY_SIZE = 8 * 1024;

	private final Logger logger;

	private final AtomicLong aid = new AtomicLong(0);

	private final Boolean printEntity;

	private final int maxEntitySize;

	/**
	 * Create a logging filter logging the request and response to a default JDK
	 * logger, named as the fully qualified class name of this class. Entity
	 * logging is turned off by default.
	 */
	public ResteasyClientLogger() {
		this(LOGGER, false);
	}

	/**
	 * Create a logging filter with custom logger and custom settings of entity
	 * logging.
	 *
	 * @param logger
	 *            the logger to log requests and responses.
	 * @param printEntity
	 *            if true, entity will be logged as well up to the default
	 *            maxEntitySize, which is 8KB
	 */
	public ResteasyClientLogger(final Logger logger, final Boolean printEntity) {
		this.logger = logger;
		this.printEntity = printEntity;
		this.maxEntitySize = DEFAULT_MAX_ENTITY_SIZE;
	}

	/**
	 * Creates a logging filter with custom logger and entity logging turned on,
	 * but potentially limiting the size of entity to be buffered and logged.
	 *
	 * @param logger
	 *            the logger to log requests and responses.
	 * @param maxEntitySize
	 *            maximum number of entity bytes to be logged (and buffered) -
	 *            if the entity is larger, logging filter will print (and buffer
	 *            in memory) only the specified number of bytes and print
	 *            "...more..." string at the end.
	 */
	public ResteasyClientLogger(final Logger logger, final int maxEntitySize) {
		this.logger = logger;
		this.printEntity = true;
		this.maxEntitySize = maxEntitySize;
	}

	private void log(final StringBuilder b) {
		if (logger != null) {
			logger.debug(b.toString());
		}
	}

	private StringBuilder prefixId(final StringBuilder b, final long id) {
		b.append(Long.toString(id)).append(" ");
		return b;
	}

	private void printRequestLine(final StringBuilder b, final String note, final long id, final String method,
			final URI uri) {
		prefixId(b, id).append(NOTIFICATION_PREFIX).append(note).append(" on thread ")
				.append(Thread.currentThread().getName()).append("\n");
		prefixId(b, id).append(REQUEST_PREFIX).append(method).append(" ").append(uri.toASCIIString()).append("\n");
	}

	private void printResponseLine(final StringBuilder b, final String note, final long id, final int status) {
		prefixId(b, id).append(NOTIFICATION_PREFIX).append(note).append(" on thread ")
				.append(Thread.currentThread().getName()).append("\n");
		prefixId(b, id).append(RESPONSE_PREFIX).append(Integer.toString(status)).append("\n");
	}

	private void printPrefixedHeaders(final StringBuilder b, final long id, final String prefix,
			final MultivaluedMap<String, String> headers) {
		for (final Map.Entry<String, List<String>> headerEntry : getSortedHeaders(headers.entrySet())) {
			final List<?> val = headerEntry.getValue();
			final String header = headerEntry.getKey();

			if (val.size() == 1) {
				prefixId(b, id).append(prefix).append(header).append(": ").append(val.get(0)).append("\n");
			} else {
				final StringBuilder sb = new StringBuilder();
				Boolean add = false;
				for (final Object s : val) {
					if (add) {
						sb.append(',');
					}
					add = true;
					sb.append(s);
				}
				prefixId(b, id).append(prefix).append(header).append(": ").append(sb.toString()).append("\n");
			}
		}
	}

	private Set<Map.Entry<String, List<String>>> getSortedHeaders(final Set<Map.Entry<String, List<String>>> headers) {
		final TreeSet<Map.Entry<String, List<String>>> sortedHeaders = new TreeSet<Map.Entry<String, List<String>>>(
				COMPARATOR);
		sortedHeaders.addAll(headers);
		return sortedHeaders;
	}

	private InputStream logInboundEntity(final StringBuilder b, InputStream stream) throws IOException {
		if (!stream.markSupported()) {
			stream = new BufferedInputStream(stream);
		}
		stream.mark(maxEntitySize + 1);
		final byte[] entity = new byte[maxEntitySize + 1];
		final int entitySize = Math.max(0, stream.read(entity));
		b.append(new String(entity, 0, Math.min(entitySize, maxEntitySize)));
		if (entitySize > maxEntitySize) {
			b.append("...more...");
		}
		b.append('\n');
		stream.reset();
		return stream;
	}

	@Override
	public void filter(final ClientRequestContext context) throws IOException {
		final long id = aid.incrementAndGet();
		final StringBuilder b = new StringBuilder();

		printRequestLine(b, "ResteasyClientLogger#filter - Sending client request", id, context.getMethod(), context.getUri());
		printPrefixedHeaders(b, id, REQUEST_PREFIX, context.getStringHeaders());

		if (printEntity && context.hasEntity()) {
			final OutputStream stream = new LoggingStream(b, context.getEntityStream());
			context.setEntityStream(stream);
			context.setProperty(ENTITY_LOGGER_PROPERTY, stream);
			// not calling log(b) here - it will be called by the interceptor
		} else {
			log(b);
		}
	}

	@Override
	public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext)
			throws IOException {
		final long id = aid.incrementAndGet();
		final StringBuilder b = new StringBuilder();

		printResponseLine(b, "Client response received", id, responseContext.getStatus());
		printPrefixedHeaders(b, id, RESPONSE_PREFIX, responseContext.getHeaders());

		if (printEntity && responseContext.hasEntity()) {
			responseContext.setEntityStream(logInboundEntity(b, responseContext.getEntityStream()));
		}

		log(b);
	}

//	@Override
//	public void filter(final ContainerRequestContext context) throws IOException {
//		final long id = aid.incrementAndGet();
//		final StringBuilder b = new StringBuilder();
//
//		printRequestLine(b, "Server has received a request", id, context.getMethod(),
//				context.getUriInfo().getRequestUri());
//		printPrefixedHeaders(b, id, REQUEST_PREFIX, context.getHeaders());
//
//		if (printEntity && context.hasEntity()) {
//			context.setEntityStream(logInboundEntity(b, context.getEntityStream()));
//		}
//
//		log(b);
//	}

//	@Override
//	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
//			throws IOException {
//		final long id = aid.incrementAndGet();
//		final StringBuilder b = new StringBuilder();
//
//		printResponseLine(b, "Server responded with a response", id, responseContext.getStatus());
//		printPrefixedHeaders(b, id, RESPONSE_PREFIX, responseContext.getStringHeaders());
//
//		if (printEntity && responseContext.hasEntity()) {
//			final OutputStream stream = new LoggingStream(b, responseContext.getEntityStream());
//			responseContext.setEntityStream(stream);
//			requestContext.setProperty(ENTITY_LOGGER_PROPERTY, stream);
//			// not calling log(b) here - it will be called by the interceptor
//		} else {
//			log(b);
//		}
//	}

	@Override
	public void aroundWriteTo(final WriterInterceptorContext writerInterceptorContext)
			throws IOException, WebApplicationException {
		final LoggingStream stream = (LoggingStream) writerInterceptorContext.getProperty(ENTITY_LOGGER_PROPERTY);
		writerInterceptorContext.proceed();
		if (stream != null) {
			log(stream.getStringBuilder());
		}
	}

	private class LoggingStream extends OutputStream {
		private final StringBuilder b;

		private final OutputStream inner;

		private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		LoggingStream(final StringBuilder b, final OutputStream inner) {
			this.b = b;
			this.inner = inner;
		}

		StringBuilder getStringBuilder() {
			// write entity to the builder
			final byte[] entity = baos.toByteArray();

			b.append(new String(entity, 0, Math.min(entity.length, maxEntitySize)));
			if (entity.length > maxEntitySize) {
				b.append("...more...");
			}
			b.append('\n');

			return b;
		}

		@Override
		public void write(final int i) throws IOException {
			if (baos.size() <= maxEntitySize) {
				baos.write(i);
			}
			inner.write(i);
		}
	}

}
