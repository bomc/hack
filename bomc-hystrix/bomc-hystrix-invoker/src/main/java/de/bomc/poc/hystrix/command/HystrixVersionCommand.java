package de.bomc.poc.hystrix.command;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

import de.bomc.poc.rest.api.HystrixDTO;
import de.bomc.poc.rest.endpoints.v1.HystrixVersionRESTResource;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;

/**
 * Hystrix command.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
public class HystrixVersionCommand extends HystrixCommand<HystrixDTO> {

	private static final String LOG_PREFIX = "HystrixVersionCommand#";
	private static final int SOCKET_TIMEOUT_OFFSET = 10000;
	private static final int HYSTRIX_SOCKET_TIMEOUT = 7500;
	private static final long ESTABLISH_CONNECTION_TIMEOUT = 10l;
	private static final boolean REQUEST_LOG_ENABLED = true;
	private static final int CIRCUIT_BREAKER_REQUEST_VOLUME = 5;
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
				.andCommandPropertiesDefaults(
						HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(HYSTRIX_SOCKET_TIMEOUT)
								.withCircuitBreakerRequestVolumeThreshold(CIRCUIT_BREAKER_REQUEST_VOLUME)
								.withRequestLogEnabled(REQUEST_LOG_ENABLED)));
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
			this.logger.debug(LOG_PREFIX + "run - [statusInfo=" + statusInfo + ", hystrixDTO=" + hystrixDTO + "]");

			if (statusInfo.getFamily() == Response.Status.Family.SUCCESSFUL) {
				// _______________________________________________________
				// Send a regular answer.
				return hystrixDTO;
			} else {
				// Do some useful things...
				return hystrixDTO;
			}
		} catch (Exception ex) {
			this.logger.error(LOG_PREFIX + "run [requestId=" + this.requestId + "]", ex);

			final HystrixDTO hystrixDTO = new HystrixDTO();
			hystrixDTO.setErrorMsg("Unexpected error [" + ex.getMessage() + "]");
			hystrixDTO.setFallback(true);

			return hystrixDTO;
		}
	}

	@Override
	protected HystrixDTO getFallback() {
		final String message = this.getExecutionException().getMessage();

		this.logger.warn(
				LOG_PREFIX + "getFallback - [requestId=" + this.requestId + ", exception.message=" + message + "]");

		final HystrixDTO hystrixDTO = new HystrixDTO();
		hystrixDTO.setErrorMsg(message);
		hystrixDTO.setFallback(true);

		return hystrixDTO;
	}

	/**
	 * Create a REST {@link Client} with the given socket timeout
	 * 
	 * @return a initialized resteasy client.
	 */
	private ResteasyClient createRestClient() {
		this.logger.debug(LOG_PREFIX + "createRestClient [requestId=" + this.requestId + ", establishConnectionTimeout="
				+ ESTABLISH_CONNECTION_TIMEOUT + ", socketTimeout=" + (HYSTRIX_SOCKET_TIMEOUT + SOCKET_TIMEOUT_OFFSET)
				+ "]");

		final ResteasyClient resteasyClient = new ResteasyClientBuilder()
				.establishConnectionTimeout(ESTABLISH_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
				.socketTimeout(HYSTRIX_SOCKET_TIMEOUT + SOCKET_TIMEOUT_OFFSET, TimeUnit.MILLISECONDS)
				.register(new ResteasyClientLogger(this.logger, true))
				.build();
				
		return resteasyClient;
	}

}
