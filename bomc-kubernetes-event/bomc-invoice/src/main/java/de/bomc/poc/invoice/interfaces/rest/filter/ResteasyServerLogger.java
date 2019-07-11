/**
 * Project: bomc-invoice
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.invoice.interfaces.rest.filter;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Universal logging filter on server.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 17.04.2019
 */
@Provider
//Smaller numbers are first in the chain.
@Priority(value = Priorities.AUTHORIZATION + 100)
public class ResteasyServerLogger implements ContainerRequestFilter, ContainerResponseFilter, WriterInterceptor {

    private static final String LOG_PREFIX = "ResteasyServerLogger#";
    private static final String NOTIFICATION_PREFIX = "* ";
    private static final String REQUEST_PREFIX = "> ";
    private static final String RESPONSE_PREFIX = "< ";
    private static final String ENTITY_LOGGER_PROPERTY = ResteasyServerLogger.class.getName() + ".entityLogger";
    /**
     * Logger.
     */
    private final Logger logger;
    /**
     * <pre>
     *  private static final Comparator<Map.Entry<String, List<String>> COMPARATOR = (o1, o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()));
     * </pre>
     */
    private static final Comparator<Map.Entry<String, List<String>>> COMPARATOR = new Comparator<Map.Entry<String, List<String>>>() {
        @Override
        public int compare(final Map.Entry<String, List<String>> o1, final Map.Entry<String, List<String>> o2) {
            return o1.getKey()
                     .compareToIgnoreCase(o2.getKey());
        }
    };
    private static final int DEFAULT_MAX_ENTITY_SIZE = 8 * 1024;
    private final AtomicLong aid = new AtomicLong(0);
    private final Boolean printEntity = true;
    private final int maxEntitySize = DEFAULT_MAX_ENTITY_SIZE;

    /**
     * Create a logging filter logging the request and response to a default JDK logger, named as the fully qualified class name of this class. Entity logging is turned off by default.
     */
    public ResteasyServerLogger() {
        this.logger = Logger.getLogger(ResteasyServerLogger.class.getSimpleName());
    }

    private void log(final StringBuilder b) {
        this.logger.log(Level.INFO, LOG_PREFIX + "log:\n" + b.toString());
    }

    private StringBuilder prefixId(final StringBuilder b, final long id) {
        b.append(Long.toString(id))
         .append(" ");
        return b;
    }

    private void printRequestLine(final StringBuilder b, final String note, final long id, final String method, final URI uri) {
        this.prefixId(b, id)
            .append(NOTIFICATION_PREFIX)
            .append(note)
            .append(" on thread ")
            .append(Thread.currentThread()
                          .getName())
            .append("\n");
        this.prefixId(b, id)
            .append(REQUEST_PREFIX)
            .append(method)
            .append(" ")
            .append(uri.toASCIIString())
            .append("\n");
    }

    private void printResponseLine(final StringBuilder b, final String note, final long id, final int status) {
        this.prefixId(b, id)
            .append(NOTIFICATION_PREFIX)
            .append(note)
            .append(" on thread ")
            .append(Thread.currentThread()
                          .getName())
            .append("\n");
        this.prefixId(b, id)
            .append(RESPONSE_PREFIX)
            .append(Integer.toString(status))
            .append("\n");
    }

    private void printPrefixedHeaders(final StringBuilder b, final long id, final String prefix, final MultivaluedMap<String, String> headers) {
        for (final Map.Entry<String, List<String>> headerEntry : this.getSortedHeaders(headers.entrySet())) {
            final List<?> val = headerEntry.getValue();
            final String header = headerEntry.getKey();

            if (val.size() == 1) {
                this.prefixId(b, id)
                    .append(prefix)
                    .append(header)
                    .append(": ")
                    .append(val.get(0))
                    .append("\n");
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
                this.prefixId(b, id)
                    .append(prefix)
                    .append(header)
                    .append(": ")
                    .append(sb.toString())
                    .append("\n");
            }
        }
    }

    private Set<Map.Entry<String, List<String>>> getSortedHeaders(final Set<Map.Entry<String, List<String>>> headers) {
        final Set<Map.Entry<String, List<String>>> sortedHeaders = new TreeSet<>(COMPARATOR);
        sortedHeaders.addAll(headers);
        return sortedHeaders;
    }

    private InputStream logInboundEntity(final StringBuilder b, InputStream stream) throws IOException {
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        stream.mark(this.maxEntitySize + 1);
        final byte[] entity = new byte[this.maxEntitySize + 1];
        final int entitySize = Math.max(0, stream.read(entity));
        b.append(new String(entity, 0, Math.min(entitySize, this.maxEntitySize), "UTF-8"));
        if (entitySize > this.maxEntitySize) {
            b.append("...more...");
        }
        b.append('\n');
        stream.reset();
        return stream;
    }

    @Override
    public void filter(final ContainerRequestContext context) throws IOException {
        final long id = this.aid.incrementAndGet();
        final StringBuilder b = new StringBuilder();

        this.printRequestLine(b, "Server has received a request", id, context.getMethod(), context.getUriInfo()
                                                                                                  .getRequestUri());
        this.printPrefixedHeaders(b, id, REQUEST_PREFIX, context.getHeaders());

        if (this.printEntity && context.hasEntity()) {
            context.setEntityStream(this.logInboundEntity(b, context.getEntityStream()));
        }

        this.log(b);
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
        final long id = this.aid.incrementAndGet();
        final StringBuilder b = new StringBuilder();

        this.printResponseLine(b, "Server responded with a response", id, responseContext.getStatus());
        this.printPrefixedHeaders(b, id, RESPONSE_PREFIX, responseContext.getStringHeaders());

        if (this.printEntity && responseContext.hasEntity()) {
            final OutputStream stream = new LoggingStream(b, responseContext.getEntityStream());
            responseContext.setEntityStream(stream);
            requestContext.setProperty(ENTITY_LOGGER_PROPERTY, stream);
            // not calling log(b) here - it will be called by the interceptor
        } else {
            this.log(b);
        }
    }

    /**
     * For message body writer interceptors that wrap around calls to MessageBodyWriter.writeTo.
     * @param writerInterceptorContext the invocation context.
     * @throws IOException             if stream operations failed.
     * @throws WebApplicationException rest operation failed.
     */
    @Override
    public void aroundWriteTo(final WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
        final LoggingStream stream = (LoggingStream)writerInterceptorContext.getProperty(ENTITY_LOGGER_PROPERTY);
        writerInterceptorContext.proceed();
        if (stream != null) {
            this.log(stream.getStringBuilder());
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

        StringBuilder getStringBuilder() throws IOException {
            // write entity to the builder
            final byte[] entity = this.baos.toByteArray();

            this.b.append(new String(entity, 0, Math.min(entity.length, ResteasyServerLogger.this.maxEntitySize), "UTF-8"));
            if (entity.length > ResteasyServerLogger.this.maxEntitySize) {
                this.b.append("...more...");
            }
            this.b.append('\n');

            return this.b;
        }

        @Override
        public void write(final int i) throws IOException {
            if (this.baos.size() <= ResteasyServerLogger.this.maxEntitySize) {
                this.baos.write(i);
            }
            this.inner.write(i);
        }
    }
}

