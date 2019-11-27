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

import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import de.bomc.poc.hrm.application.exception.AppErrorCodeEnum;
import de.bomc.poc.hrm.application.exception.AppRuntimeException;
import de.bomc.poc.hrm.application.exception.core.ExceptionUtil;
import de.bomc.poc.hrm.interfaces.ApiErrorResponseObject;
import lombok.extern.slf4j.Slf4j;

/**
 * This class handles centralized exception handling for standardized and custom
 * exceptions.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Slf4j
@ControllerAdvice
public class ResponseEntityExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

	private static final String LOG_PREFIX = "ResponseEntityExceptionHandlerAdvice#";
    
	// _______________________________________________
	// Constants
	// -----------------------------------------------
	// @TODO see {@link TraceHeaderFilter.class}
	private static final String X_B3_TRACE_ID_HEADER = "X-B3-TraceId";

	public ResponseEntityExceptionHandlerAdvice() {
		super();
	}

	// API

	// 400

//	@Override
//	protected ResponseEntity<Object> handleBadRequest(final HttpMessageNotReadableException ex,
//			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
//		final String bodyOfResponse = "This should be application specific";
//		// ex.getCause() instanceof JsonMappingException, JsonParseException // for
//		// additional information later on
//		return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.BAD_REQUEST, request);
//	}

//	@Override
//	protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
//			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
//		final String bodyOfResponse = "This should be application specific";
//		// ex.getCause() instanceof JsonMappingException, JsonParseException // for
//		// additional information later on
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

	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<Object> handleInternal(final ConstraintViolationException ex, final WebRequest request) {
		final String errorUuid = UUID.randomUUID().toString();

		log.error(LOG_PREFIX + "handleInternal (ConstraintViolationException) [ex=" + ex + ", request=" + request
				+ ", traceId=" + MDC.get(X_B3_TRACE_ID_HEADER) + ", errorUuid= " + errorUuid + "]");

		final ApiErrorResponseObject apiErrorResponseObject = ApiErrorResponseObject.builder()
				.shortErrorCodeDescription(
						AppErrorCodeEnum.JPA_PERSISTENCE_CONSTRAINT_VIOLATION_10403.getShortErrorCodeDescription())
				.errorCode(AppErrorCodeEnum.JPA_PERSISTENCE_CONSTRAINT_VIOLATION_10403.toString()).uuid(errorUuid)
				.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		return handleExceptionInternal(ex, apiErrorResponseObject, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler({ DataIntegrityViolationException.class })
	public ResponseEntity<Object> handleInternal(final DataIntegrityViolationException ex, final WebRequest request) {
		final String errorUuid = UUID.randomUUID().toString();

		log.error(LOG_PREFIX + "handleInternal (DataIntegrityViolationException) [ex=" + ex + ", request=" + request
				+ ", traceId=" + MDC.get(X_B3_TRACE_ID_HEADER) + ", errorUuid= " + errorUuid + "]");

		final ApiErrorResponseObject apiErrorResponseObject = ApiErrorResponseObject.builder()
				.shortErrorCodeDescription(
						AppErrorCodeEnum.JPA_PERSISTENCE_DATA_INTEGRITY_VIOLATION_10404.getShortErrorCodeDescription())
				.errorCode(AppErrorCodeEnum.JPA_PERSISTENCE_DATA_INTEGRITY_VIOLATION_10404.toString()).uuid(errorUuid)
				.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		return handleExceptionInternal(ex, apiErrorResponseObject, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	/**
	 * NOTE: A ExceptionHandler for this type of exception -
	 * 'MethodArgumentNotValidException' already exists. So the method must be only
	 * overwritten.
	 */
	@Override
	public ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, final WebRequest request) {
		final String errorUuid = UUID.randomUUID().toString();

		log.error(LOG_PREFIX + "handleInternal (MethodArgumentNotValidException) [ex=" + ex + ", status=" + status
				+ ", request=" + request + ", traceId=" + MDC.get(X_B3_TRACE_ID_HEADER) + ", errorUuid= " + errorUuid
				+ "]");

		final ApiErrorResponseObject apiErrorResponseObject = ApiErrorResponseObject.builder()
				.shortErrorCodeDescription(AppErrorCodeEnum.APP_VALDIDATION_ERROR_10610.getShortErrorCodeDescription())
				.errorCode(AppErrorCodeEnum.APP_VALDIDATION_ERROR_10610.toString()).uuid(errorUuid)
				.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		return handleExceptionInternal(ex, apiErrorResponseObject, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler({ AppRuntimeException.class })
	public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
		log.error(LOG_PREFIX + "handleInternal [ex=" + ex + ", request=" + request + ", traceId="
				+ MDC.get(X_B3_TRACE_ID_HEADER) + "]");

		final AppRuntimeException appRuntimeException = ExceptionUtil.unwrap(ex, AppRuntimeException.class);

		if (!appRuntimeException.isLogged()) {
			logger.error(LOG_PREFIX + "handleInternal" + appRuntimeException.stackTraceToString());
		}

		final ApiErrorResponseObject apiErrorResponseObject = ApiErrorResponseObject.builder()
				.shortErrorCodeDescription(appRuntimeException.getMessage()) //
				.errorCode(appRuntimeException.getErrorCode().toString()) //
				.uuid(appRuntimeException.getUuid()) //
				.status(HttpStatus.INTERNAL_SERVER_ERROR) //
				.build();

		return handleExceptionInternal(ex, apiErrorResponseObject, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR,
				request);
	}

}
