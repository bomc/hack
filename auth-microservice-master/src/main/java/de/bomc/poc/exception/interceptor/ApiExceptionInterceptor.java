/**
 * Project: MY_POC_MICROSERVICE
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
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.exception.interceptor;

import de.bomc.poc.exception.qualifier.ApiExceptionQualifier;
import de.bomc.poc.exception.web.WebAuthRuntimeException;

import javax.annotation.PostConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import java.util.Locale;

/**
 * This interceptor handles a unified exception handling for REST invocations.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Interceptor
@ApiExceptionQualifier
public class ApiExceptionInterceptor {

    // @Inject
    private Locale locale;

    @PostConstruct
    public void init() {
        this.locale = Locale.getDefault();
    }

    @AroundInvoke
    public Object handleException(InvocationContext context) {
        Object proceedResponse;

        try {
            // Handles a invocation without an error.
            proceedResponse = context.proceed();
        } catch (Exception ex) {
            //
            // Handles a invocation with an error.
            // Processing a unified exception handling.
            Response errorResponse;

            if (ex instanceof WebAuthRuntimeException) {
                errorResponse = ((WebAuthRuntimeException)ex).getHttpResponse();
            } else if (ex.getCause() instanceof WebAuthRuntimeException) {
                errorResponse = ((WebAuthRuntimeException)ex.getCause()).getHttpResponse();
            } else if (ex.getCause() instanceof ConstraintViolationException) {
                // ---> this exception is handled via the ConstraintViolationMapper.
                System.out.println("ApiExceptionInterceptor#handleException " + ex.getCause().toString());

                throw (ConstraintViolationException)ex.getCause();
            } else {
                errorResponse = new WebAuthRuntimeException(ex, this.locale).getHttpResponse();
            }

            return errorResponse;
        }

        return proceedResponse;
    }
}
