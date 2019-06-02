/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-26 15:24:52 +0100 (Do, 26 Jan 2017) $
 *
 *  revision: $Revision: 10039 $
 *
 * </pre>
 */
package de.bomc.poc.upload.ping;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;
import de.bomc.poc.upload.rest.api.v1.LagacyUploadResource;
import de.bomc.poc.upload.rest.api.v1.ResponseObject;
import de.bomc.poc.upload.rest.client.RestClientBuilder;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Do the pinger work.
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 21.12.2016
 */
@Singleton
public class PingWorkerSingletonEJB {

    private static final String LOG_PREFIX = "PingWorkerSingletonEJB#";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Inject
    private RestClientBuilder restClientBuilder;
    private final AtomicBoolean busy = new AtomicBoolean(false);

    @Lock(LockType.READ)
    public void doPing(final String requestId) throws InterruptedException {
        this.logger.debug(LOG_PREFIX + "doTimerWork [requestId=" + requestId + "]");

        if (!this.busy.compareAndSet(false, true)) {
            return;
        }

        final long time = System.currentTimeMillis();

        final Response response;

        try {
            // Do pinger work.
            //
            // Create query param.
            final MultivaluedMap<String, Object> multivaluedQueryMap = new MultivaluedHashMap<>(1);
            multivaluedQueryMap.add(LagacyUploadResource.QUERY_PARAM_REQUEST_ID, requestId);

            // Get webTarget from restClientBuilder.
            final ResteasyWebTarget resteasyWebTarget = this.restClientBuilder.buildUploadRestClient();
            // Register logger on client.
            resteasyWebTarget.register(new ResteasyClientLogger(this.logger, true));

            response = resteasyWebTarget.path("/" + LagacyUploadResource.UPLOAD_LAGACY_REST_RESOURCE_PATH + "/ping")
                                        .queryParams(multivaluedQueryMap)
                                        .request()
                                        .accept(MediaType.APPLICATION_JSON)
                                        .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final ResponseObject responseObject = response.readEntity(ResponseObject.class);

                this.logger.info(LOG_PREFIX + "doPing [duration (full in ms)=" + (System.currentTimeMillis() - time) + ", response=" + responseObject + "]");

                // TODO: do here additional work for monitoring
            } else {
                this.logger.info(LOG_PREFIX + "doPing - get unexpected HTTP response code. [duration (in ms)=" + (System.currentTimeMillis() - time) + "]");
            }
        } catch (Exception ex) {
            this.logger.error(LOG_PREFIX + "doPing - could not invoke ping service! + [message=" + ex.getMessage() + "]");
        } finally {
            this.busy.set(false);
        }
    }
}
