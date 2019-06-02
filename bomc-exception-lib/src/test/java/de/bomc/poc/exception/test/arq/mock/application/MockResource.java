package de.bomc.poc.exception.test.arq.mock.application;

import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.exception.cdi.qualifier.ExceptionHandlerQualifier;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.exception.core.web.WebRuntimeException;
import de.bomc.poc.exception.test.TestApiError;
import de.bomc.poc.exception.test.TestErrorCode;

import org.apache.log4j.Logger;

import javax.interceptor.Interceptors;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;

/**
 * This endpoint works as a mock and is the implementation of the {@link MockResourceInterface}.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 */
@ExceptionHandlerQualifier
@Interceptors(ExceptionHandlerInterceptor.class)
public class MockResource implements MockResourceInterface {

    private static final Logger LOGGER = Logger.getLogger(MockResource.class);
    private static final String LOG_PREFIX = "MockResource#";

    @Override
    public Response getExceptionWithApiError(final Long id) {
        LOGGER.debug(LOG_PREFIX + "getExceptionWithApiError [id=" + id + "]");

        if (id % 2 == 0) {
            final TestApiError testApiError = TestApiError.TEST_API_00101;

            throw new WebRuntimeException(testApiError);
        }

        return Response.status(Response.Status.OK)
                       .entity(String.valueOf(id))
                       .build();
    }

    @Override
    public Response getExceptionWithStatusAndErrorCode(final Long id) {
        LOGGER.debug(LOG_PREFIX + "getExceptionWithStatusAndErrorCode [id=" + id + "]");

        if (id % 2 == 0) {
            throw new WebRuntimeException(TestApiError.TEST_API_00102);
        }

        return Response.status(Response.Status.OK)
                       .entity(String.valueOf(id))
                       .build();
    }

    @Override
    public Response getExceptionWithWrappedException(final Long id) {
        LOGGER.debug(LOG_PREFIX + "getExceptionWithWrappedException [id=" + id + "]");

        if (id % 2 == 0) {
            throw new WebRuntimeException(new IllegalArgumentException("A app exception with a IllegalArgumentException."), TestApiError.TEST_API_00101);
        }

        return Response.status(Response.Status.OK)
                       .entity(String.valueOf(id))
                       .build();
    }

    @Override
    public Response getWrappedExceptionInARuntimeException(final Long id) {
        LOGGER.debug(LOG_PREFIX + "getWrappedExceptionInARuntimeException [id=" + id + "]");

        if (id % 2 == 0) {
            throw new IllegalArgumentException(new WebRuntimeException(TestApiError.TEST_API_00101));
        }

        return Response.status(Response.Status.OK)
                       .entity(String.valueOf(id))
                       .build();
    }

    @Override
    public Response getNotAWebRuntimeException(final Long id) {
        LOGGER.debug(LOG_PREFIX + "getNotAWebRuntimeException [id=" + id + "]");

        if (id % 2 == 0) {
            throw new IllegalArgumentException("This is not a app runtime-exception");
        }

        return Response.status(Response.Status.OK)
                       .entity(String.valueOf(id))
                       .build();
    }

    @Override
    public Response getConstraintValidationException(final Long id) {
        LOGGER.debug(LOG_PREFIX + "getConstraintValidationException [id=" + id + "]");

        if (id % 2 == 0) {
            throw new ConstraintViolationException("Constraint validation failed!", null);
        }

        return Response.status(Response.Status.OK)
                       .entity(String.valueOf(id))
                       .build();
    }

    @Override
    public Response getAppRuntimeException(final Long id) {
        LOGGER.debug(LOG_PREFIX + "getAppRuntimeException [id=" + id + "]");

        if (id % 2 == 0) {
            throw new AppRuntimeException("My custom message", TestErrorCode.TEST_00101);
        }

        return Response.status(Response.Status.OK)
                       .entity(String.valueOf(id))
                       .build();
    }
}
