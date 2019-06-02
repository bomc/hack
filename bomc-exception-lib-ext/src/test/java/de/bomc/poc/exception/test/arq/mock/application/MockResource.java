/**
 * Project: bomc-exception-lib-ext
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
package de.bomc.poc.exception.test.arq.mock.application;

import javax.enterprise.event.Observes;
import javax.interceptor.Interceptors;
import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintDefinitionException;
import javax.validation.ConstraintViolationException;
import javax.validation.GroupDefinitionException;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.exception.cdi.qualifier.ExceptionHandlerQualifier;
import de.bomc.poc.exception.core.BasisErrorCodeEnum;
import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.exception.core.event.ExceptionLogEvent;
import de.bomc.poc.exception.core.web.WebRuntimeException;

/**
 * This endpoint works as a mock and is the implementation of the
 * {@link MockResourceInterface}.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
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
			final ErrorCode errorCode = BasisErrorCodeEnum.UNEXPECTED_10000;

			throw new WebRuntimeException(errorCode);
		}

		return Response.status(Response.Status.OK).entity(String.valueOf(id)).build();
	}

	@Override
	public Response getExceptionWithStatusAndErrorCode(final Long id) {
		LOGGER.debug(LOG_PREFIX + "getExceptionWithStatusAndErrorCode [id=" + id + "]");

		if (id % 2 == 0) {
			throw new WebRuntimeException(BasisErrorCodeEnum.UNEXPECTED_10000);
		}

		return Response.status(Response.Status.OK).entity(String.valueOf(id)).build();
	}

	@Override
	public Response getExceptionWithWrappedException(final Long id) {
		LOGGER.debug(LOG_PREFIX + "getExceptionWithWrappedException [id=" + id + "]");

		if (id % 2 == 0) {
			throw new WebRuntimeException(
					new IllegalArgumentException("A app exception with a IllegalArgumentException."),
					BasisErrorCodeEnum.UNEXPECTED_10000);
		}

		return Response.status(Response.Status.OK).entity(String.valueOf(id)).build();
	}

	@Override
	public Response getWrappedExceptionInARuntimeException(final Long id) {
		LOGGER.debug(LOG_PREFIX + "getWrappedExceptionInARuntimeException [id=" + id + "]");

		if (id % 2 == 0) {
			throw new IllegalArgumentException(new WebRuntimeException(BasisErrorCodeEnum.UNEXPECTED_10000));
		}

		return Response.status(Response.Status.OK).entity(String.valueOf(id)).build();
	}

	@Override
	public Response getNotAWebRuntimeException(final Long id) {
		LOGGER.debug(LOG_PREFIX + "getNotAWebRuntimeException [id=" + id + "]");

		if (id % 2 == 0) {
			throw new IllegalArgumentException("This is not a app runtime-exception");
		}

		return Response.status(Response.Status.OK).entity(String.valueOf(id)).build();
	}

	@Override
	public Response getConstraintValidationException(final Long id) {
		LOGGER.debug(LOG_PREFIX + "getConstraintValidationException [id=" + id + "]");

		if (id == 0) {
			throw new ConstraintViolationException("ConstraintViolationException validation failed!", null);
		} else if (id == 1) {
			throw new ConstraintDefinitionException("ConstraintDefinitionException validation failed!", null);
		} else if (id == 2) {
			throw new ConstraintDeclarationException("ConstraintDeclarationException validation failed!", null);
		} else if (id == 3) {
			throw new GroupDefinitionException("GroupDefinitionException validation failed!", null);
		} else if (id == 4) {
			throw new NullPointerException("Constraint validation failed!");
		}

		return Response.status(Response.Status.OK).entity(String.valueOf(id)).build();
	}

	@Override
	public Response getAppRuntimeException(final Long id) {
		LOGGER.debug(LOG_PREFIX + "getAppRuntimeException [id=" + id + "]");

		if (id % 2 == 0) {
			throw new AppRuntimeException("My custom message", BasisErrorCodeEnum.UNEXPECTED_10000);
		}

		return Response.status(Response.Status.OK).entity(String.valueOf(id)).build();
	}

	@Override
	public Response getConstraintValidationOnSignatureException(final Long id, final String text) {
		LOGGER.debug(LOG_PREFIX + "getConstraintValidationOnSignatureException");

		return Response.status(Response.Status.OK).entity(id + "," + text).build();
	}

	@Override
	public Response doDownloadWithException() {
		LOGGER.debug(LOG_PREFIX + "doDownloadWithException");

		throw new WebRuntimeException(BasisErrorCodeEnum.UNEXPECTED_10000);
	}
	
	public void logException(@Observes final ExceptionLogEvent exceptionLogEvent) {
		LOGGER.info(LOG_PREFIX + "logException - " + exceptionLogEvent);
	} 
}
