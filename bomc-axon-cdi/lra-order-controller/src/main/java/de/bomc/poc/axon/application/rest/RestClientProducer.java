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

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

/**
 * Produces a resteasy-client.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
@ApplicationScoped
public class RestClientProducer {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "RestClientProducer#";
	private ResteasyClient jaxRsClient;
	@Inject
	@ConnectionParameterQualifier
	private ConnectionParameter connectionParameter;
	
	/**
	 * Create a REST {@link ResteasyClient} with the given socket timeout
	 * 
	 * @return a initialized resteasy client.
	 */
	@Produces
	@RestClientQualifier
	@SuppressWarnings("deprecation")
	private ResteasyClient createRestClient() {
		LOGGER.debug(LOG_PREFIX + "createRestClient [" + this.connectionParameter + "]");

		// String apiHostUrl = String.format("%s://%s:%d", useHttps ? "https" :
		// "http", hostname, port);

		final ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
		clientBuilder.establishConnectionTimeout(this.connectionParameter.getEstablishConnectionTimeout(),
				TimeUnit.MILLISECONDS);
		clientBuilder.socketTimeout(
				this.connectionParameter.getEstablishConnectionTimeout() + this.connectionParameter.getSocketTimeoutOffset(),
				TimeUnit.MILLISECONDS);
		clientBuilder.maxPooledPerRoute(this.connectionParameter.getMaxConnections());
		clientBuilder.connectionPoolSize(this.connectionParameter.getMaxConnections());

		this.jaxRsClient = clientBuilder.build();
		
		return jaxRsClient;
	}
	
	@PreDestroy
	public void destroy() {
		LOGGER.debug(LOG_PREFIX + "destroy - close rest client!");
		
		if(this.jaxRsClient != null) {
			try {
				this.jaxRsClient.close();
			} catch(final Exception exception) {
				LOGGER.error(LOG_PREFIX + "destroy - closing rest client failed!");
			}
		}
	}

}
