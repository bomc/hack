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
package de.bomc.poc.hystrix.ejb;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;

import de.bomc.poc.hystrix.command.HystrixVersionCommand;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.api.HystrixDTO;
import de.bomc.poc.retry.interceptor.RetryHandlerInterceptor;
import de.bomc.poc.retry.qualifier.RetryPolicy;

/**
 * EJB invokes hystrix command.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 17.01.2018
 */
@Stateless
public class HystrixCommandInvokerEJB {
	private static final String LOG_PREFIX = "HystrixCommandInvokerEJB#";
	private static final String BASE_URI = "http://192.168.4.1:8180/bomc-hystrix-broken/rest";
	//
	// Member variables
    private static final long INITIAL_INTERVAL = 100L;
    private static final double MULTIPLIER = 1.75d;
    private static final int MAX_RETRY_COUNT = 3;
	@Inject
	@LoggerQualifier
	private Logger logger;

    @Interceptors(RetryHandlerInterceptor.class)
    @RetryPolicy(initialInterval = INITIAL_INTERVAL, multiplier = MULTIPLIER, maxRetryCount = MAX_RETRY_COUNT)
	public HystrixDTO getVersion() {
		this.logger.debug(LOG_PREFIX + "getVersion");

		final String requestId = UUID.randomUUID().toString();
		
		HystrixDTO hystrixDTO = null;
		
		try {
			final HystrixVersionCommand hystrixVersionCommand = new HystrixVersionCommand(this.logger, BASE_URI, requestId);
			hystrixDTO = hystrixVersionCommand.execute();

		} catch (Exception ex) {
			this.logger.debug(LOG_PREFIX + "getVersion - ", ex);
			
			hystrixDTO = new HystrixDTO(null);
			hystrixDTO.setErrorMsg("Unexpected error=" + ex.getMessage());
			hystrixDTO.setFallback(false);
		}
		
		return hystrixDTO;
	}
}
