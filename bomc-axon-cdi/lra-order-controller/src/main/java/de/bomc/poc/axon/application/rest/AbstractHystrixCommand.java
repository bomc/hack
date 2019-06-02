/**
 * Project: cdi-axon
 * <pre>
 *
 * Last change:
 *
 *  by:       $Author$
 *
 *  date:     $Date$
 *
 *  revision: $Revision$
 *
 *  © Bomc 2018
 *
 * </pre>
 */
package de.bomc.poc.axon.application.rest;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

import de.bomc.poc.axon.application.rest.invoice.InvoiceHystrixCommand;

/**
 * A abstract class for all {@link HystrixCommand}s implementations.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
public abstract class AbstractHystrixCommand<T> extends HystrixCommand<T> {

	private static final Logger LOGGER = Logger.getLogger(AbstractHystrixCommand.class);
	private static final String LOG_PREFIX = "AbstractHystrixCommand#";
	private static final ConnectionParameter CONNECTION_PARAMETER = new ConnectionParameter();
	private final String logPrefix;
	private final String serviceName;

	/**
	 * Create a new instance of <code>AbstractHystrixCommand</code>.
	 * 
	 * See for hystrix configuration: https://www.baeldung.com/introduction-to-hystrix
	 * 
	 * @param logPrefix
	 *            the given log prefix of the implemented command instance.
	 * @param serviceName
	 *            name of the remote service.
	 */
	protected AbstractHystrixCommand(final String logPrefix, final String serviceName) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(InvoiceHystrixCommand.class.getName()))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionTimeoutInMilliseconds(CONNECTION_PARAMETER.getExecutionTimeoutInMilliseconds())
						.withCircuitBreakerSleepWindowInMilliseconds(CONNECTION_PARAMETER.getCircuitBreakerSleepWindowInMilliseconds())
						.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
						.withCircuitBreakerEnabled(true)
						.withCircuitBreakerRequestVolumeThreshold(CONNECTION_PARAMETER.getCircuitBreakerRequestVolume())
						.withRequestLogEnabled(CONNECTION_PARAMETER.isRequestLogEnabled())));

		this.logPrefix = logPrefix;
		this.serviceName = serviceName;
	}

	@SuppressWarnings("deprecation")
	public ResteasyClient createRestClient() {
		LOGGER.debug(LOG_PREFIX + logPrefix + "createRestClient [serviceName=" + this.serviceName
				+ ", connectionParameter=" + CONNECTION_PARAMETER + "]");

		final ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
		clientBuilder.establishConnectionTimeout(CONNECTION_PARAMETER.getEstablishConnectionTimeout(),
				TimeUnit.MILLISECONDS);
		clientBuilder.socketTimeout(
				CONNECTION_PARAMETER.getExecutionTimeoutInMilliseconds() + CONNECTION_PARAMETER.getSocketTimeoutOffset(),
				TimeUnit.MILLISECONDS);
		clientBuilder.maxPooledPerRoute(CONNECTION_PARAMETER.getMaxConnections());
		clientBuilder.connectionPoolSize(CONNECTION_PARAMETER.getMaxConnections());

		return clientBuilder.build();
	}

}
