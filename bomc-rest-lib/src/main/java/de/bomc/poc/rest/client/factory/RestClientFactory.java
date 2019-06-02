package de.bomc.poc.rest.client.factory;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import de.bomc.poc.rest.logger.client.ResteasyClientLogger;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *  A resteasy client builder.
 *  ____________________________________________________________________________________
 *  NOTE: A pooled rest client has to be instantiated in a container in a singleton ejb.
 *
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 7002 $ $Author: tzdbmm $ $Date: 2016-07-22 09:54:21 +0200 (Fr, 22 Jul 2016) $
 * @since 15.07.2016
 */
public class RestClientFactory<T> {

    private static final int DEFAULT_CONNECTION_TTL = 2000;
    private static final int DEFAULT_ESTABLISHED_CONNECTION_TIMEOUT = 2000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 2000;

    /**
     * <pre>
     *  Creates a simple rest client, with a registered <code>ResteasyClientLogger</code> and setting of the socketTimeout and connectionTTL.
     *  The created instance using a SingleClientConnManager – which of course only makes a single connection available.
     * </pre>
     * @param clazz  the proxy interface.
     * @param uri    of the rest endpoint.
     * @param logger the logger of the using instance.
     * @param <T>    the type of the proxy interface.
     * @return a initialized proxy of the given proxy framework.
     */
    public static <T> T createSimpleRestClient(final Class<T> clazz, final URI uri, final Logger logger, final List<Object> clientFilterList) {
        final ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilder().connectionTTL(DEFAULT_CONNECTION_TTL, TimeUnit.MILLISECONDS)
                                                                         .establishConnectionTimeout(DEFAULT_ESTABLISHED_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                                                                         .socketTimeout(DEFAULT_SOCKET_TIMEOUT, TimeUnit.MILLISECONDS)
                                                                         .register(new ResteasyClientLogger(logger, true));
        if(clientFilterList != null && !clientFilterList.isEmpty()) {
            clientFilterList.forEach(clientFilter -> {
                if(clientFilter instanceof ClientRequestFilter || clientFilter instanceof ClientResponseFilter) {
                    resteasyClientBuilder.register(clientFilter);
                }
            });
        }

        final ResteasyClient resteasyClient = resteasyClientBuilder.build();

        final ResteasyWebTarget target = resteasyClient.target(uri);

        return target.proxy(clazz);
    }
}
