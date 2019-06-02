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
package de.bomc.poc.invoice.application.scheduler;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.MDC;

import de.bomc.poc.exception.core.ExceptionUtil;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.invoice.application.internal.AppErrorCodeEnum;
import de.bomc.poc.invoice.application.internal.ApplicationUserEnum;
import de.bomc.poc.invoice.interfaces.rest.client.OrderRestEndpoint;
import de.bomc.poc.invoice.interfaces.rest.filter.MDCFilter;
import de.bomc.poc.invoice.interfaces.rest.filter.RestClientHeaderIfModifiedSinceFilter;

/**
 * A service that invokes the remote order-service for new orders. java -jar
 * Thorntail..-xy.jar
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class OrderSchedulerService {

	private static final String LOG_PREFIX = "OrderSchedulerService#";
	private static final Logger LOGGER = Logger.getLogger(OrderSchedulerService.class.getName());
	// _______________________________________________
	// Config constants
	// -----------------------------------------------
	private static final long REST_CLIENT_TIMEOUT = 1500;
	private static final int REST_CLIENT_RETRY_MAX_RETRIES = 3;
	private static final long REST_CLIENT_RETRY_DELAY = 150;
	private static final String CONTEXT_ROOT_ORDER_SERVICE_KEY = "context.root.order.service";
	private static final String DEFAULT_CONTEXT_ROOT_ORDER_SERVICE = "bomc-order";
	private static final String ROOT_PATH_ORDER_SERVICE_KEY = "root.path.order.service";
	private static final String DEFAULT_ROOT_PATH_ORDER_SERVICE = "rest";
	private static final String ENV_HOST_KEY = "BOMC_ORDER_SERVICE_HOST";
	private static final String ENV_PORT_KEY = "BOMC_ORDER_SERVICE_PORT";
	private static final String ZONE_ID_EUROPE_BERLIN = "Europe/Berlin";
	// _______________________________________________
	// Member variables
	// -----------------------------------------------
	@Inject
	@ConfigProperty(name = CONTEXT_ROOT_ORDER_SERVICE_KEY, defaultValue = DEFAULT_CONTEXT_ROOT_ORDER_SERVICE)
	private String contextRootOrderService;
	@Inject
	@ConfigProperty(name = ROOT_PATH_ORDER_SERVICE_KEY, defaultValue = DEFAULT_ROOT_PATH_ORDER_SERVICE)
	private String rootPathOrderService;
	// Contains host and port from system environment.
	private String host = null;
	private String port = null;
//	@Inject
//	@RestClient
//	private OrderRestEndpoint orderRestClient;

	@PostConstruct
	public void init() {
		LOGGER.log(Level.INFO, LOG_PREFIX + "init");

		host = System.getenv(ENV_HOST_KEY);
		port = System.getenv(ENV_PORT_KEY);

		if (host == null || port == null) {
			throw new IllegalArgumentException(
					LOG_PREFIX + "init - host or port could not be injected [host=" + host + ", port=" + port + "]");
		}

		LOGGER.log(Level.INFO, LOG_PREFIX + "init [host=" + host + ", port=" + port + "]");
	}

	/**
	 * Invokes the order service and check for new orders.
	 */
//	@Timeout(value = REST_CLIENT_TIMEOUT)
//	@Fallback(fallbackMethod = "doWorkFallback")
//	@Retry(maxRetries = REST_CLIENT_RETRY_MAX_RETRIES, delay = REST_CLIENT_RETRY_DELAY, retryOn = {
//			AppRuntimeException.class })
	public String doWork(final String lastModifiedDate) {
		LOGGER.log(Level.INFO, LOG_PREFIX + "doWork [lastModifiedDate=" + lastModifiedDate + ", requestId=" + MDC.get(MDCFilter.HEADER_REQUEST_ID_ATTR) + "]");

		Response response = null;

		// do work
		try {
			final URI apiUri = new URI(
					"http://" + host + ":" + port + "/" + contextRootOrderService + "/" + rootPathOrderService);

			RestClientHeaderIfModifiedSinceFilter restClientHeaderIfModifiedSinceFilter;
			OrderRestEndpoint orderRestClient;
			
//			if (lastModifiedDate != null && !lastModifiedDate.isEmpty()) {
//				// 
//				// Set 'If-Modified-Since' header.
//				LOGGER.log(Level.INFO, LOG_PREFIX + "doWork - invoke remote order-service *** WITH *** 'If-Modified-Since' header.");
				
				restClientHeaderIfModifiedSinceFilter = new RestClientHeaderIfModifiedSinceFilter("lastModifiedDate");
				orderRestClient = RestClientBuilder.newBuilder().register(restClientHeaderIfModifiedSinceFilter).baseUri(apiUri).build(OrderRestEndpoint.class);
//			} else {
//				//
//				// Start request without 'If-Modified-Since' header.
//				
//				LOGGER.log(Level.INFO, LOG_PREFIX + "doWork - invoke remote order-service *** WITHOUT *** 'If-Modified-Since' header.");
//				orderRestClient = RestClientBuilder.newBuilder().baseUri(apiUri).build(OrderRestEndpoint.class);
//			}

			response = orderRestClient.getLatestModifiedDate(ApplicationUserEnum.SYSTEM_USER.name());

			if (response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				final Date retLastModifiedDate = response.getLastModified();

				if (lastModifiedDate != null) {
					return this.convertToRfc1132DateAsString(retLastModifiedDate);

				}
			}
		} catch (final Exception exception) {

			if (ExceptionUtil.is(exception, java.net.ConnectException.class)) {
				//
				// Do Fallback...
				final String errMsg = LOG_PREFIX + "timerRunningOff - invocation to remote endpoint failed!";
				final AppRuntimeException appRuntimeException = new AppRuntimeException(errMsg,
						AppErrorCodeEnum.APP_REST_CLIENT_FAILURE_10600);
				LOGGER.log(Level.SEVERE, appRuntimeException.stackTraceToString());

				throw appRuntimeException;
			} else {
				//
				LOGGER.log(Level.SEVERE, LOG_PREFIX + "timerRunningOff - remote endpoint invocation failed!");
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}

		return lastModifiedDate;
	}

	public String doWorkFallback(final String lastModifiedDate) {
		LOGGER.log(Level.INFO,
				LOG_PREFIX + "doWorkFallback - a error occurs during remote endpoint invocation. [lastModifiedDate="
						+ lastModifiedDate + "]");

		return lastModifiedDate;
	}

	/**
	 * Convert java.util.Date to LocalDateTime in RFC1123 format.
	 * 
	 * @param lastModifiedDate the given date to format.
	 * @return the formatted string.
	 */
	private String convertToRfc1132DateAsString(final Date lastModifiedDate) {
		LOGGER.log(Level.INFO, LOG_PREFIX + "convertToRfc1132DateAsString [lastModifiedDate=" + lastModifiedDate + "]");
		
		// Convert java.util.Date to LocalDateTime in RFC1123.

		final ZoneId zoneIdEuropeBerlin = ZoneId.of(ZONE_ID_EUROPE_BERLIN);
		final Instant instant = lastModifiedDate.toInstant();
		final ZonedDateTime zonedDateTimeRfc1132 = instant.atZone(zoneIdEuropeBerlin);

		final String strZonedDateTimeRfc1132 = zonedDateTimeRfc1132.format(DateTimeFormatter.RFC_1123_DATE_TIME);

		return strZonedDateTimeRfc1132;
	}
}
