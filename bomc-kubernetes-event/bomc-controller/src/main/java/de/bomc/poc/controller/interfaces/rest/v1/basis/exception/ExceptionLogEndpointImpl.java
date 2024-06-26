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
package de.bomc.poc.controller.interfaces.rest.v1.basis.exception;

import java.io.IOException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

import de.bomc.poc.controller.application.internal.AppErrorCodeEnum;
import de.bomc.poc.controller.application.basis.log.ExceptionLogController;
import de.bomc.poc.controller.application.basis.performance.qualifier.PerformanceTrackingQualifier;
import de.bomc.poc.controller.interfaces.rest.v1.basis.ExceptionLogEndpoint;

/**
 * A resource implementation that displays a list of max the last 100
 * exceptions.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.12.2018
 */
@PerformanceTrackingQualifier
@Interceptors({ ExceptionHandlerInterceptor.class })
public class ExceptionLogEndpointImpl implements ExceptionLogEndpoint {

    private static final String LOG_PREFIX = "ExceptionLogEndpoint#";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @EJB
    private ExceptionLogController exceptionLogController;

    @Override
    public Response getStoredExceptions() {
        this.logger.debug(LOG_PREFIX + "getStoredExceptions");

        // Read entities from db.
        String jsonString = "";
        
        try {
            jsonString = this.exceptionLogController.readStoredExceptions();
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
