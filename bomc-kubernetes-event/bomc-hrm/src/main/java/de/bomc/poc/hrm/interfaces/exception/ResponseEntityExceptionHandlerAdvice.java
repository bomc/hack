/**
 * Project: POC PaaS
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
package de.bomc.poc.hrm.interfaces.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import de.bomc.poc.hrm.application.exception.AppRuntimeException;
import de.bomc.poc.hrm.application.exception.core.ExceptionUtil;
import de.bomc.poc.hrm.interfaces.ApiErrorResponseObject;

/**
 * This class handles centralized exception handling for standardized and custom
 * exceptions.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@ControllerAdvice
public class ResponseEntityExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

	private static final String LOG_PREFIX = "ResponseEntityExceptionHandlerAdvice#";
	private static Logger LOGGER = LoggerFactory.getLogger(ResponseEntityExceptionHandlerAdvice.class);
	
	// _______________________________________________
	// Constants
	// -----------------------------------------------
	// @TODO see {@link TraceHeaderFilter.class}
	private static final String X_B3_TraceId_HEADER = "X-B3-TraceId";
	
	public ResponseEntityExceptionHandlerAdvice() {
		super();
	}

	// API

	// 400

	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<Object> handleBadRequest(final ConstraintViolationException ex, final WebRequest request) {
		final String bodyOfResponse = "This should be application specific";
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

//	@ExceptionHandler({ DataIntegrityViolationException.class })
//	public ResponseEntity<Object> handleBadRequest(final DataIntegrityViolationException ex, final WebRequest request) {
//		final String bodyOfResponse = "This should be application specific";
//		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//	}
//
//	@Override
//	protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
//			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
//		final String bodyOfResponse = "This should be application specific";
//		// ex.getCause() instanceof JsonMappingException, JsonParseException // for
//		// additional information later on
//		return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.BAD_REQUEST, request);
//	}
//
//	@Override
//	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
//			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
//		final String bodyOfResponse = "This should be application specific";
//		return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.BAD_REQUEST, request);
//	}

	// 404

//	@ExceptionHandler(value = { EntityNotFoundException.class /*, AppRuntimeException.class*/ })
//	protected ResponseEntity<Object> handleNotFound(final RuntimeException ex, final WebRequest request) {
//		final String bodyOfResponse = "This should be application specific";
//		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
//	}

	// 409

//	@ExceptionHandler({ InvalidDataAccessApiUsageException.class, DataAccessException.class })
//	protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
//		final String bodyOfResponse = "This should be application specific";
//		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
//	}

	// 412

	// 500

	@ExceptionHandler({ AppRuntimeException.class })
	public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
		LOGGER.error(LOG_PREFIX + "handleInternal [ex=" + ex + ", request=" + request + ", traceId=" + MDC.get(X_B3_TraceId_HEADER) + "]");
		
		final AppRuntimeException appRuntimeException = ExceptionUtil.unwrap(ex, AppRuntimeException.class);
		
		if(!appRuntimeException.isLogged()) {
			logger.error(LOG_PREFIX + "handleInternal" + appRuntimeException.stackTraceToString());
		}
		
		final ApiErrorResponseObject apiErrorResponseObject = ApiErrorResponseObject.builder().shortErrorCodeDescription(appRuntimeException.getMessage())
		.errorCode(appRuntimeException.getErrorCode().toString()).uuid(appRuntimeException.getUuid()).status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		
		return handleExceptionInternal(ex, apiErrorResponseObject, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR,
				request);
	}

}
