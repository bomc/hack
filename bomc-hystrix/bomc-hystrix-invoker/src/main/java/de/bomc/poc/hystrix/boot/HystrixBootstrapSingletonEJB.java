package de.bomc.poc.hystrix.boot;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.strategy.HystrixPlugins;

import de.bomc.poc.hystrix.boot.strategy.WildflyHystrixConcurrencyStrategy;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

/**
 * Handling for start and stop hystrix component.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@Startup
@Singleton
public class HystrixBootstrapSingletonEJB {

	private static final String LOG_PREFIX = "HystrixBootstrapSingletonEJB#";

	@Inject
	@LoggerQualifier
	private Logger logger;

	@Resource
	private ManagedThreadFactory threadFactory;

	@PostConstruct
	public void onStartup() {
		this.logger.debug(LOG_PREFIX + "onStrartup - initializing hystrix ...");

		HystrixPlugins.getInstance().registerConcurrencyStrategy(new WildflyHystrixConcurrencyStrategy(threadFactory));
	}

	@PreDestroy
	public void onShutdown() {
		this.logger.debug(LOG_PREFIX + "onShutDown - shutting down hystrix ...");

		Hystrix.reset(1, TimeUnit.SECONDS);
	}
}
