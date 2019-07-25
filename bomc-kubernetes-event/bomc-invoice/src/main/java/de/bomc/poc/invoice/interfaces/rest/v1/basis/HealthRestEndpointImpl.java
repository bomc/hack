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
package de.bomc.poc.invoice.interfaces.rest.v1.basis;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.invoice.application.internal.AppErrorCodeEnum;
import de.bomc.poc.invoice.application.log.LoggerQualifier;

/**
 * The implementation that reads the current version of this project from
 * 'version.properties'-file.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@Interceptors({ExceptionHandlerInterceptor.class})
public class HealthRestEndpointImpl implements HealthRestEndpoint {

	private static final String LOG_PREFIX = "VersionRestEndpointImpl#";
	private static final String VERSION_KEY_NAME = "version.version";
	private static final String BUILD_DATE_KEY_NAME = "version.build.date";
	private static final String DEFAULT_VALUE = "not set in 'microprofile-config.properties'";
	// _______________________________________________
	// Configuration constants for fault-tolerance handling.
	// -----------------------------------------------
	private static final long TIMEOUT = 500l;
	private static final int RETRY_MAX_RETRIES = 3;
	private static final long RETRY_DELAY = 100l;
	private static final int CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD = 4;
	private static final double CIRCUIT_BREAKER_FAILURE_RATIO = 0.75d;
	private static final long CIRCUIT_BREAKER_DELAY = 1000l;
	private static final int CIRCUIT_BREAKER_SUCCESS_THRESHOLD = 10;
	private static final String FALLBACK_METHOD = "doWorkFallback";
	private static final int BULKHEAD = 3;
	// The logger
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	@ConfigProperty(name = VERSION_KEY_NAME, defaultValue = DEFAULT_VALUE)
	private String version;
	@Inject
	@ConfigProperty(name = BUILD_DATE_KEY_NAME, defaultValue = DEFAULT_VALUE)
	private String buildDate;

	@Override
	@Timeout(value = TIMEOUT)
	@Fallback(fallbackMethod = FALLBACK_METHOD)
	@CircuitBreaker(requestVolumeThreshold = CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, failureRatio = CIRCUIT_BREAKER_FAILURE_RATIO, delay = CIRCUIT_BREAKER_DELAY, successThreshold = CIRCUIT_BREAKER_SUCCESS_THRESHOLD)
	@Retry(maxRetries = RETRY_MAX_RETRIES, delay = RETRY_DELAY, retryOn = { AppRuntimeException.class })
	@Bulkhead(BULKHEAD)
	public Response getLiveness() {
		try {
			final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
			jsonObjectBuilder.add(VERSION_KEY_NAME, version);
			jsonObjectBuilder.add(BUILD_DATE_KEY_NAME, buildDate);

			this.logger.log(Level.INFO, LOG_PREFIX + "getLiveness [response=" + jsonObjectBuilder.build() + "]");
			
			return Response.ok().entity(jsonObjectBuilder.build()).build();
		} catch (final Exception exception) {
			final String errMsg = LOG_PREFIX
					+ "getLiveness - Could not read 'version' and 'build.date from configuration file. ";

			final AppRuntimeException appRuntimeException = new AppRuntimeException(errMsg,
					AppErrorCodeEnum.APP_LIVENESS_READINESS_10605);
			this.logger.log(Level.SEVERE, errMsg + appRuntimeException.stackTraceToString());

			throw appRuntimeException;
		}
	}

	@Override
	@Timeout(value = TIMEOUT)
	@Fallback(fallbackMethod = FALLBACK_METHOD)
	@CircuitBreaker(requestVolumeThreshold = CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, failureRatio = CIRCUIT_BREAKER_FAILURE_RATIO, delay = CIRCUIT_BREAKER_DELAY, successThreshold = CIRCUIT_BREAKER_SUCCESS_THRESHOLD)
	@Retry(maxRetries = RETRY_MAX_RETRIES/*, maxDuration = 1000*/, delay = RETRY_DELAY, retryOn = { AppRuntimeException.class })
	@Bulkhead(BULKHEAD)
	public Response getReadiness() {
		try {
			final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
			jsonObjectBuilder.add(VERSION_KEY_NAME, version);
			jsonObjectBuilder.add(BUILD_DATE_KEY_NAME, buildDate);

			this.logger.log(Level.INFO, LOG_PREFIX + "getLiveness [response=" + jsonObjectBuilder.build() + "]");

			return Response.ok().entity(jsonObjectBuilder.build()).build();
		} catch (final Exception exception) {
			final String errMsg = LOG_PREFIX
					+ "getLiveness - Could not read 'version' and 'build.date from configuration file. ";

			final AppRuntimeException appRuntimeException = new AppRuntimeException(errMsg,
					AppErrorCodeEnum.APP_LIVENESS_READINESS_10605);
			this.logger.log(Level.SEVERE, errMsg + appRuntimeException.stackTraceToString());

			throw appRuntimeException;
		}
	}

	/**
	 * The alternative path in case a {@link ApplicationException} occurs.
	 * 
	 */
	public Response doWorkFallback() {
		this.logger.log(Level.FINE, LOG_PREFIX + "doWorkFallback - a error occurs during method invocation.");

		return Response.serverError().build();
	}
}
