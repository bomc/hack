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
package de.bomc.poc.axon.application;

import java.lang.invoke.MethodHandles;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.interceptors.EventLoggingInterceptor;

/**
 * Initializing the application at startup.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
@Singleton
@Startup
public class AccountApplication {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "AccountApplication#";
	@Inject
	private EventBus eventBus;

	@PostConstruct
	public void init() {
		LOGGER.info(LOG_PREFIX + "init - Initializing application, and sending event.");

		eventBus.registerDispatchInterceptor(new EventLoggingInterceptor());

		LOGGER.info(LOG_PREFIX + "init - finished.");
	}
}
