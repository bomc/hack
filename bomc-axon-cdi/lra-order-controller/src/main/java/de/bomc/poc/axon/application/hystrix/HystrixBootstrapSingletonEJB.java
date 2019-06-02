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
package de.bomc.poc.axon.application.hystrix;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedThreadFactory;
//import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.strategy.HystrixPlugins;

import de.bomc.poc.axon.application.hystrix.strategy.WildflyHystrixConcurrencyStrategy;

/**
 * Handling for start and stop hystrix component.
 *
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
@Startup
@Singleton
public class HystrixBootstrapSingletonEJB {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "HystrixBootstrapSingletonEJB#";

//	@Inject
//	private WildflyHystrixConcurrencyStrategy wildflyHystrixConcurrencyStrategy;
	@Resource
	private ManagedThreadFactory threadFactory;
	
	@PostConstruct
	public void onStartup() {
		LOGGER.debug(LOG_PREFIX + "onStartup - initializing hystrix ...");
		
		WildflyHystrixConcurrencyStrategy wildflyHystrixConcurrencyStrategy = new WildflyHystrixConcurrencyStrategy(threadFactory);
		HystrixPlugins.getInstance().registerConcurrencyStrategy(wildflyHystrixConcurrencyStrategy);
	}

	@PreDestroy
	public void onShutdown() {
		LOGGER.debug(LOG_PREFIX + "onShutDown - shutting down hystrix ...");

		// Shutdown all thread pools; waiting a little time for shutdown.
		Hystrix.reset(1, TimeUnit.SECONDS);
	} // end shutdown
}
