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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import de.bomc.poc.exception.core.ExceptionUtil;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.invoice.application.controller.InvoiceComposition;
import de.bomc.poc.invoice.application.internal.AppErrorCodeEnum;
import de.bomc.poc.invoice.application.internal.ApplicationUserEnum;
import de.bomc.poc.invoice.application.log.LoggerQualifier;
import de.bomc.poc.invoice.application.order.OrderDTO;
import de.bomc.poc.invoice.interfaces.rest.client.OrderRestEndpoint;
import de.bomc.poc.invoice.interfaces.rest.filter.RestClientHeaderIfModifiedSinceFilter;
import de.bomc.poc.invoice.interfaces.rest.filter.ResteasyClientLogger;
import de.bomc.poc.invoice.interfaces.rest.filter.UIDHeaderRequestFilter;
import io.smallrye.restclient.RestClientProxy;

/**
 * A service that invokes the remote order-service for new orders. java -jar
 * Thorntail..-xy.jar
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class OrderSchedulerService {

	private static final String LOG_PREFIX = "OrderSchedulerService#";
	@Inject
	@LoggerQualifier
	private Logger logger;
	// _______________________________________________
	// Config constants
	// -----------------------------------------------
	private static final String CONTEXT_ROOT_ORDER_SERVICE_KEY = "context.root.order.service";
	private static final String DEFAULT_CONTEXT_ROOT_ORDER_SERVICE = "bomc-order";
	private static final String ROOT_PATH_ORDER_SERVICE_KEY = "root.path.order.service";
	private static final String DEFAULT_ROOT_PATH_ORDER_SERVICE = "rest";
	private static final String ENV_HOST_KEY = "env.order.service.host.key";
	private static final String DEFAULT_ENV_HOST_KEY = "BOMC_ORDER_SERVICE_HOST";
	private static final String ENV_PORT_KEY = "env.order.service.port.key";
	private static final String DEFAULT_ENV_PORT_KEY = "BOMC_ORDER_SERVICE_PORT";
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
	@Inject
	@ConfigProperty(name = ENV_HOST_KEY, defaultValue = DEFAULT_ENV_HOST_KEY)
	private String envHostKey;
	@Inject
	@ConfigProperty(name = ENV_PORT_KEY, defaultValue = DEFAULT_ENV_PORT_KEY)
	private String envPortKey;
	// Contains host and port from system environment.
	private String orderEndpointHost = null;
	private String orderEndpointPort = null;
	//
	// Other member variables.
	@Inject
	public InvoiceComposition invoiceController; 
	
	@PostConstruct
	public void init() {
		this.logger.log(Level.FINE, LOG_PREFIX + "init");

		orderEndpointHost = System.getenv(envHostKey);
		orderEndpointPort = System.getenv(envPortKey);

		if (orderEndpointHost == null || orderEndpointPort == null) {
			this.logger.log(Level.WARNING, LOG_PREFIX
					+ "init - ### BOMC_ORDER_SERVICE_HOST and BOMC_ORDER_SERVICE_PORT are null. Set environment variables! ### ");

			throw new IllegalArgumentException(LOG_PREFIX + "init - host or port could not be injected");
		}

		this.logger.log(Level.INFO, LOG_PREFIX + "init [host=" + orderEndpointHost + ", port=" + orderEndpointPort + "]");
	}

	/**
	 * Invokes the order service and check for new orders.
	 * 
	 * <pre>
	 *  
	 * NOTE:
	 * CircuitBreaker: 
	 * This means if 75% requests fail in a rolling window of 4 requests , then the 
	 * circuit will be in open state and the monitored endpoint is not executed for 
	 * 1000ms. After the 1000ms delay, the circuit is placed to half-open. At this 
	 * point, trial calls will probe the destination and after 10 consecutive successes, 
	 * the circuit will be placed back to closed
	 * Fallback:
	 * Provides alternative execution path , in case of failures.
	 * Retry:
	 * Specifies the number of times a specific endpoint will be retried in case of 
	 * failures.
	 * Bulkhead: Limits the number of concurrent requests.
	 * </pre>
	 */
	public String doWork(final String lastModifiedDate) {
		this.logger.log(Level.FINE, LOG_PREFIX + "doWork [lastModifiedDate=" + lastModifiedDate /*+ ", requestId="
				+ MDC.get(MDCFilter.HEADER_REQUEST_ID_ATTR)*/ + "]");

		Response response = null;
		OrderRestEndpoint orderRestClient = null;

		// do work
		try {
			final URI apiUri = new URI("http://" + orderEndpointHost + ":" + orderEndpointPort + "/"
					+ contextRootOrderService + "/" + rootPathOrderService);

			RestClientHeaderIfModifiedSinceFilter restClientHeaderIfModifiedSinceFilter;

			if (lastModifiedDate != null && !lastModifiedDate.isEmpty()) {
				//
				// Set 'If-Modified-Since'-header.
				this.logger.log(Level.INFO,
						LOG_PREFIX + "doWork - invoke remote order-service *** WITH *** 'If-Modified-Since' header.");

				restClientHeaderIfModifiedSinceFilter = new RestClientHeaderIfModifiedSinceFilter(lastModifiedDate);
				orderRestClient = RestClientBuilder.newBuilder().register(new UIDHeaderRequestFilter())
						.register(restClientHeaderIfModifiedSinceFilter).register(OrderRestClientExceptionMapper.class)
						.register(new ResteasyClientLogger(this.logger, true)).baseUri(apiUri)
						.build(OrderRestEndpoint.class);
			} else {
				//
				// Startup request without 'If-Modified-Since' header.
				this.logger.log(Level.INFO, LOG_PREFIX
						+ "doWork - invoke remote order-service *** WITHOUT *** 'If-Modified-Since' header.");
				orderRestClient = RestClientBuilder.newBuilder().register(new UIDHeaderRequestFilter())
						.register(OrderRestClientExceptionMapper.class).register(new ResteasyClientLogger(this.logger, true))
						.baseUri(apiUri).build(OrderRestEndpoint.class);
			}

			response = orderRestClient.getLatestModifiedDate(ApplicationUserEnum.SYSTEM_USER.name());

			if (response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				final Date retLastModifiedDate = response.getLastModified();

				final List<OrderDTO> oderDtoList = response.readEntity(new GenericType<List<OrderDTO>>() {
				});

				// ___________________________________
				// Create here the invoice.
				// -----------------------------------
				this.logger.log(Level.INFO, LOG_PREFIX + "doWork - create and send the invoice. [oderDtoList.size="
						+ oderDtoList.size() + "]");

				if(oderDtoList.size() > 0) {
					invoiceController.createInvoice(oderDtoList);
				}
				
				if (retLastModifiedDate != null) {
					return this.convertToRfc1132DateAsString(retLastModifiedDate);
				} else {
					this.logger.log(Level.SEVERE,
							LOG_PREFIX + "doWork - get response and extracted last-modified-date is null!]");
				}
			}	
		} catch (final Exception exception) {
			this.logger.log(Level.SEVERE, LOG_PREFIX + "doWork - remote endpoint invocation failed!", exception);

			if (ExceptionUtil.is(exception, java.net.ConnectException.class)) {
				//
				// Do Fallback...
				final String errMsg = LOG_PREFIX + "timerRunningOff - invocation to remote endpoint failed!";
				final AppRuntimeException appRuntimeException = new AppRuntimeException(errMsg,
						AppErrorCodeEnum.APP_REST_CLIENT_FAILURE_10600);
				this.logger.log(Level.SEVERE, appRuntimeException.stackTraceToString());

				throw appRuntimeException;
			} else {
				//
				this.logger.log(Level.SEVERE, LOG_PREFIX + "timerRunningOff - remote endpoint invocation failed!");
			}
		} finally {
			//
			// Cleanup resources.
			if (response != null) {
				response.close();
			}

			if (orderRestClient != null) {
				((RestClientProxy) orderRestClient).close();
			}
		}

		return lastModifiedDate;
	}

	/**
	 * Convert java.util.Date to LocalDateTime in RFC1123 format.
	 * 
	 * @param lastModifiedDate the given date to format.
	 * @return the formatted string.
	 */
	private String convertToRfc1132DateAsString(final Date lastModifiedDate) {
		this.logger.log(Level.FINE, LOG_PREFIX + "convertToRfc1132DateAsString [lastModifiedDate=" + lastModifiedDate + "]");

		// Convert java.util.Date to LocalDateTime in RFC1123.

		final ZoneId zoneIdEuropeBerlin = ZoneId.of(ZONE_ID_EUROPE_BERLIN);
		final Instant instant = lastModifiedDate.toInstant();
		final ZonedDateTime zonedDateTimeRfc1132 = instant.atZone(zoneIdEuropeBerlin);

		final String strZonedDateTimeRfc1132 = zonedDateTimeRfc1132.format(DateTimeFormatter.RFC_1123_DATE_TIME);

		return strZonedDateTimeRfc1132;
	}
}
