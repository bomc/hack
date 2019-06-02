/**
 * <pre>
 *
 * Last change:
 *
 *  by: $Author$
 *
 *  date: $Date$
 *
 *  revision: $Revision$
 *
 *    © Bomc 2018
 *
 * </pre>
 */
package de.bomc.poc.hystrix.command;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixEventType;

import de.bomc.poc.rest.api.HystrixDTO;
import de.bomc.poc.rest.endpoints.v1.HystrixVersionRESTResource;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;

/**
 * Hystrix command.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 17.01.2018
 */
public class HystrixVersionCommand extends HystrixCommand<HystrixDTO> {

	private static final String LOG_PREFIX = "HystrixVersionCommand#";
	private static final int TIMEOUT_OFFSET = 500;
	private static final int HYSTRIX_SOCKET_TIMEOUT = 4500;
	private static final long ESTABLISH_CONNECTION_TIMEOUT = 2500L;
	private static final boolean REQUEST_LOG_ENABLED = true;
	private static final int CIRCUIT_BREAKER_REQUEST_VOLUME = 10;
	private final String baseUri;
	private final String requestId;
	private Logger logger;

	/**
	 * Creates a new instance of <code>HystrixVersionCommand</code>.
	 * 
	 * @param logger
	 *            the logger from the invoker.
	 * @param baseUri
	 *            the uri of the requested endpoint.
	 * @param requestId
	 *            the request id.
	 */
	public HystrixVersionCommand(final Logger logger, final String baseUri, final String requestId) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(HystrixVersionCommand.class.getName()))
				.andCommandKey(HystrixCommandKey.Factory.asKey("getVersion")).andCommandPropertiesDefaults(
						HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(HYSTRIX_SOCKET_TIMEOUT)
								.withCircuitBreakerRequestVolumeThreshold(CIRCUIT_BREAKER_REQUEST_VOLUME)
								.withRequestLogEnabled(REQUEST_LOG_ENABLED).withFallbackEnabled(true)));
		this.logger = logger;
		this.baseUri = baseUri;
		this.requestId = requestId;
	}

	/**
	 * Is invoked by hystrix in the execute method.
	 */
	@Override
	protected HystrixDTO run() throws Exception {
		this.logger.debug(LOG_PREFIX + "run [requestId=" + this.requestId + "]");

		try {
			// Create the rest client.
			final ResteasyClient resteasyClient = this.createRestClient();
			final ResteasyWebTarget resteasyWebTarget = resteasyClient.target(baseUri);
			// Invoke the endpoint.
			final Response response = resteasyWebTarget.proxy(HystrixVersionRESTResource.class).getVersion();
			// Evaluate the response.
			final Response.StatusType statusInfo = response.getStatusInfo();

			final HystrixDTO hystrixDTO = response.readEntity(HystrixDTO.class);
			this.logger.debug(LOG_PREFIX + "run - receive response - [statusInfo=" + statusInfo + ", hystrixDTO="
					+ hystrixDTO + "]");

			if (statusInfo.getFamily() == Response.Status.Family.SUCCESSFUL) {
				// _______________________________________________________
				// Send a regular answer.
				return hystrixDTO;
			} else {
				// Do some useful things, that indicates that response status is
				// not successful.
				return hystrixDTO;
			}

		} catch (final Exception ex) {
			this.logger.error(LOG_PREFIX + "run [requestId=" + this.requestId + ", ex.message=" + ex.getMessage() + "]",
					ex);

			final HystrixDTO hystrixDTO = new HystrixDTO();
			hystrixDTO.setErrorMsg("Unexpected error [" + ex.getMessage() + "]");
			hystrixDTO.setFallback(false);

			if (ex.getCause() != null && ex.getCause() instanceof ConnectTimeoutException
					// NoRouteToHostException is handled by
					// org.apache.http.impl.client.DefaultHttpClient
					|| ex.getCause() instanceof NoRouteToHostException || ex.getCause() instanceof ConnectException
					|| ex.getCause() instanceof UnknownHostException && !super.isCircuitBreakerOpen()) {
				final String errMsg = LOG_PREFIX + "run - do the retry [isCircuitBreakerOpen="
						+ super.isCircuitBreakerOpen() + ", exception=" + ex.getCause().getClass().getName() + "]";

				// Do the retry.
				this.logger.info(errMsg);

				hystrixDTO.setRetry(true);
			} else {
				// Rethrow exception for fallback handling.
				throw ex;
			}

			return hystrixDTO;
		}
	}

	/**
	 * Do the fallback.
	 */
	@Override
	protected HystrixDTO getFallback() {
		this.logger.error(LOG_PREFIX + "getFallback [executionResult=" + executionResult + "]");

		final HystrixDTO hystrixDTO = new HystrixDTO();
		hystrixDTO.setFallback(true);
		hystrixDTO.setRetry(false);

		final Exception exception = executionResult.getException();

		if (exception != null) {
			this.logger.error(LOG_PREFIX + "getFallback [exception.message=" + exception.getMessage() + "]");

			hystrixDTO.setErrorMsg("exception.name=" + exception.getClass().getName() + ", exception.message="
					+ exception.getMessage() + ", hystrix.events=" + this.extractHystrixEventsFromExecutionResult());
		} else {
			hystrixDTO.setErrorMsg("hystrix.events=" + this.extractHystrixEventsFromExecutionResult());
		}

		return hystrixDTO;
	}

	/**
	 * Extract hystrix events from execution result.
	 * 
	 * @return a joined hystrix event string.
	 */
	private String extractHystrixEventsFromExecutionResult() {
		final List<HystrixEventType> hystrixEventTypeList = executionResult.getOrderedList();
		final String eventAsString = hystrixEventTypeList.stream().map(hystrixEventType -> hystrixEventType.name())
				.collect(Collectors.joining());

		return eventAsString;

	}

	/**
	 * Create a REST {@link Client} with the given socket timeout.
	 * 
	 * @return a initialized resteasy client.
	 */
	private ResteasyClient createRestClient() {
		this.logger.debug(LOG_PREFIX + "createRestClient [requestId=" + this.requestId + ", establishConnectionTimeout="
				+ ESTABLISH_CONNECTION_TIMEOUT + ", socketTimeout=" + (HYSTRIX_SOCKET_TIMEOUT + TIMEOUT_OFFSET) + "]");

		final ResteasyClient resteasyClient = new ResteasyClientBuilder()
				// The connection timeout denotes the maximum time elapsed
				// before the connection is established or an error occurs.
				.establishConnectionTimeout(ESTABLISH_CONNECTION_TIMEOUT + (long) TIMEOUT_OFFSET, TimeUnit.MILLISECONDS)
				// The socket timeout defines the maximum period of inactivity
				// between two consecutive data packets arriving on the client
				// side after a connection has been established.
				.socketTimeout(HYSTRIX_SOCKET_TIMEOUT + TIMEOUT_OFFSET, TimeUnit.MILLISECONDS)
				.register(new ResteasyClientLogger(this.logger, true)).build();

		return resteasyClient;
	}

}
