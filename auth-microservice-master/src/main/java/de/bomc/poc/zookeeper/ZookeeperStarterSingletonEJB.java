/**
 * Project: MY_POC
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2015
 */
package de.bomc.poc.zookeeper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.DependsOn;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.log4j.Logger;

import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.concurrency.qualifier.ManagedScheduledExecutorServiceQualifier;
import de.bomc.poc.config.EnvConfigKeys;
import de.bomc.poc.config.EnvConfigSingletonEJB;
import de.bomc.poc.config.qualifier.EnvConfigQualifier;
import de.bomc.poc.exception.app.AppZookeeperException;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.zookeeper.registry.ServiceRegistry;
import de.bomc.poc.zookeeper.registry.services.qualifier.ZookeeperServicesQualifier;

/**
 * This singleton EJB registers at startup the REST endpoints of this project at
 * zookeeper. This EJB depends on the {@link EnvConfigSingletonEJB} which reads
 * the environment parameter from the running wildfly instance. If the Resteasy
 * curator Framework is used the registered uri has the from
 * 'http://ip:port/context-root/application-path'. The service name should be
 * the endpoint path.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
@Startup
@Singleton
@DependsOn("EnvConfigSingletonEJB")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ZookeeperStarterSingletonEJB {
	private static final String LOG_PREFIX = "ZookeeperStarterSingletonEJB#";
	/**
	 * Logger.
	 */
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	@ManagedScheduledExecutorServiceQualifier
	private ManagedScheduledExecutorService executorService;
	// A future for returned connection state from zookeeper.
	private ScheduledFuture<Boolean> delayTask = null;
	@Inject
	@ZookeeperServicesQualifier
	private ServiceRegistry serviceRegistry;
	// The bind address of the running container (e.g. wildfly) where this
	// artifact is deployed.
	@NotNull
	@Size(min = 4)
	@Inject
	@EnvConfigQualifier(key = EnvConfigKeys.BIND_ADDRESS)
	private String bindAddress;
	// The bind port of the running container (e.g. wildfly) where this artifact
	// is deployed.
	@NotNull
	@Size(min = 2)
	@Inject
	@EnvConfigQualifier(key = EnvConfigKeys.BIND_PORT)
	private String port;
	// The artifact name.
	@NotNull
	@Size(min = 1)
	@Inject
	@EnvConfigQualifier(key = EnvConfigKeys.WEB_ARCHIVE_NAME)
	private String webArchiveName;
	// Relative to this path is the uri added.
	@NotNull
	@Size(min = 4)
	@Inject
	@EnvConfigQualifier(key = EnvConfigKeys.ZNODE_BASE_PATH)
	private String zNodeBasePath;

	/**
	 * <pre>
	 * Register service with structure:
	 * 
	 * 			   / root				     
	 * 				o						 
	 * - 		   / \   			 -------------
	 * 			  /   ...					 
	 * 			 o auth-microservice		root-context, get from jboss-web.xml           
	 * -	    / \   				 -------------
	 * 		   /   ...  					
	 * 		  o dev o test  ...				stage get from node.name (system property)
	 * -     / \    				 -------------
	 * 		/   -----------\				 
	 * 	   o discovery		o config	 	discovery uri's and configs
	 * -  / \  ----------  / \   	 -------------
	 *   /   \			  /	  \			 	 
	 *  o     o			 o	   o			 
	 *  uri1  uri2 ...  config1 ...
	 * </pre>
	 */
	@PostConstruct
	protected void init() {
		this.logger.debug(
				LOG_PREFIX + "init [bindAddress=" + this.bindAddress + ", port=" + this.port + "]");

		// Check if it is a digit.
		final String regex = "\\d+";
		Pattern pattern = Pattern.compile(regex);
		if (!pattern.matcher(this.port).matches()) {
			throw new AppZookeeperException(LOG_PREFIX + "init - port is not numeric! ");
		}

		// Metadata for service registration.
		final InstanceMetaData instanceMetaData = InstanceMetaData.hostAdress(this.bindAddress).port(Integer.parseInt(this.port))
				.serviceName(JaxRsActivator.APPLICATION_PATH).contextRoot(this.webArchiveName)
				.applicationPath(JaxRsActivator.APPLICATION_PATH).description(null).build();

		// Check if the injected CuratorFramwork is already connected to
		// zookeeper.
		if (this.serviceRegistry.isConnected()) {
			//
			// The CuratorFramework is already connected.
			this.serviceRegistry.registerService(instanceMetaData, this.zNodeBasePath);
		} else {
			this.logger.info(LOG_PREFIX + "init - start serviceRegistry with a delay!");
			//
			// The CuratorFramework is not initialized and connected, so wait
			// for a short time and try it again.
			final DelayTaskCallable delayTaskCallable = new DelayTaskCallable(this.serviceRegistry,
					instanceMetaData, this.zNodeBasePath);
			this.delayTask = this.executorService.schedule(delayTaskCallable, 250, TimeUnit.MILLISECONDS);

			try {
				final Boolean isConnected = this.delayTask.get();
				this.logger.error(LOG_PREFIX + "init - [isConnected=" + isConnected + "]");
			} catch (InterruptedException | ExecutionException e) {
				this.logger.error(LOG_PREFIX + "init - Could not connect to zookeeper!", e);

				throw new AppZookeeperException(
						LOG_PREFIX + "init - Could not connect to zookeeper! [" + e.getMessage() + "]");
			}
		}
	}

	@PreDestroy
	protected void unregisterService() {
		this.logger.debug(LOG_PREFIX + "unregisterService");

		this.serviceRegistry.unregisterService();

		this.cleanup();
	}

	/**
	 * Cleanup resources.
	 */
	private void cleanup() {
		this.logger.debug(LOG_PREFIX + "cleanup");

		// Cleanup resources.
		if (this.delayTask != null && !this.delayTask.isCancelled()) {
			final boolean cancel = this.delayTask.cancel(true);

			if (!cancel) {
				this.logger.warn(LOG_PREFIX + "cleanup - Could not cancel 'delayedTask'.");
			}
		}
	}

	/**
	 * @return the serviceRegistry.
	 */
	@Lock(LockType.READ)
	@AccessTimeout(value = 500, unit = TimeUnit.MILLISECONDS)
	public ServiceRegistry getServiceRegistry() {
		this.logger.debug(LOG_PREFIX + "getServiceRegistry");

		return this.serviceRegistry;
	}

	// _____________________________________________________________________________
	// Inner class
	// -----------------------------------------------------------------------------
	class DelayTaskCallable implements Callable<Boolean> {
		private final ServiceRegistry serviceRegistry;
		private final InstanceMetaData instanceMetaData;
		private final String zNodeBasePath;

		public DelayTaskCallable(final ServiceRegistry serviceRegistry, final InstanceMetaData instanceMetaData,
				final String zNodeBasePath) {
			this.serviceRegistry = serviceRegistry;
			this.instanceMetaData = instanceMetaData;
			this.zNodeBasePath = zNodeBasePath;
		}

		@Override
		public Boolean call() throws Exception {
			if (this.serviceRegistry.isConnected()) {
				//
				// So the application is connected to zookeeper, so register
				// services.
				this.serviceRegistry.registerService(instanceMetaData, zNodeBasePath);
				return true;
			} else {
				return false;
			}
		}
	}
	
//    Trigger everyHourTrigger = new Trigger() {
//
//        @Override
//        public Date getNextRunTime(final LastExecution le, Date date) {
//            if (le == null) {
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(date);
//                cal.add(Calendar.HOUR, 1);
//                cal.set(Calendar.SECOND, 0);
//                cal.set(Calendar.MINUTE, 0);
//                cal.set(Calendar.MILLISECOND, 0);
//                logger.fine(() -> "bot first execution at " + cal.getTime());
//                return cal.getTime();
//            }
//            Date next =  new Date(le.getScheduledStart().getTime() + 3600 * 1000);
//            logger.fine(() -> "bot next execution at " + next);
//            return next;
//        }
//
//        @Override
//        public boolean skipRun(LastExecution le, Date date) {
//            return false;
//        }
//    };
//    
//    Runnable command = () -> {
//        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
//     
//    };
//    
//    scheduler.schedule(command, everyHourTrigger);
}
