/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.order.interfaces.rest.v1.basis.exception;

import java.io.IOException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

import de.bomc.poc.order.application.internal.AppErrorCodeEnum;
import de.bomc.poc.order.application.basis.log.ExceptionLogController;
import de.bomc.poc.order.application.basis.log.qualifier.AuditLogQualifier;
import de.bomc.poc.order.application.basis.performance.qualifier.PerformanceTrackingQualifier;
import de.bomc.poc.order.interfaces.rest.v1.basis.ExceptionLogRestEndpoint;

/**
 * A resource implementation that displays a list of max the last 100
 * exceptions.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.12.2018
 */
@AuditLogQualifier
@PerformanceTrackingQualifier
@Interceptors({ ExceptionHandlerInterceptor.class })
public class ExceptionLogRestEndpointImpl implements ExceptionLogRestEndpoint {

    private static final String LOG_PREFIX = "ExceptionLogRestEndpointImpl#";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @EJB
    private ExceptionLogController exceptionLogController;

    /**
     * curl -X GET
     * "http://192.168.99.119:31380/bomc-order/rest/exception/stored" -H
     * "accept: application/vnd.exception-v1+json" -H "X-BOMC_USER_ID: bomc"
     */
    @Override
    public Response getStoredExceptions(final String userId) {

        // Read entities from db.
        String jsonString = "";

        try {
            jsonString = this.exceptionLogController.readStoredExceptions(userId);
        } catch (final IOException iOException) {
            final String errMsg = LOG_PREFIX + "getStoredExceptions - mapping from list to json string failed! ";
            final AppRuntimeException appRuntimeException = new AppRuntimeException(errMsg, iOException,
                    AppErrorCodeEnum.APP_JSON_MAPPING_FAILED_10603);

            this.logger.error(appRuntimeException.stackTraceToString());

            throw appRuntimeException;
        }

        return Response.ok().entity(jsonString).build();
    }
}
