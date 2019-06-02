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
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.zookeeper.registry.services;

import de.bomc.poc.exception.app.AppZookeeperException;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.zookeeper.curator.qualifier.CuratorFrameworkQualifier;
import de.bomc.poc.zookeeper.registry.services.qualifier.ZookeeperServicesQualifier;
import de.bomc.poc.zookeeper.InstanceMetaData;
import de.bomc.poc.zookeeper.registry.ServiceRegistry;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.log4j.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;

/**
 * This bean initializes the curatorFramework to connect to zookeeper. The only
 * thing the application has to know, is the address of the zookeeper instance.
 * The zookeeper address is defined in the zookeeper.properties file. This file
 * is at runtime part of the classpath.
 * 
 * <pre>
 * 1. the container calls the bean constructor (the default constructor or
 * 	  the one annotated -at Inject), to obtain an instance of the bean. 
 * 2. the container initializes the values of all injected fields of the bean. 
 * 3. the container calls all initializer methods of bean (the call order is not
 * 	  portable, don’t rely on it). 
 * 4. finally, the -at PostConstruct method, if any, is called.
 * </pre>
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
@ApplicationScoped
@ZookeeperServicesQualifier
public class ZookeeperServiceRegistry implements ServiceRegistry {
	// The logger is injected by constructor.
	private Logger logger;
	// The curatorFramework that wraps the zookeeper curator, is injected by constructor.
	private CuratorFramework curatorFramework;
	// The root zNode.
	private String rootZNode;
	// Definition of this microservice that has to be registered to the service registry.
	private ServiceInstance<InstanceMetaData> serviceInstance;
	// Handles the discovery.
	private ServiceDiscovery<InstanceMetaData> serviceDiscovery;

	/**
	 * Creates a new bean of <code>ZookeeperServiceRegistry</code>. This constructor is used by the cdi container to get this class proxied.
	 *
	 */
	public ZookeeperServiceRegistry() {
		// Used by CDI container.
	}

	/**
	 * Creates a new bean of <code>ZookeeperServiceRegistry</code> via bean constructor injection.
	 *
	 */
	@Inject
	public ZookeeperServiceRegistry(final @CuratorFrameworkQualifier CuratorFramework curatorFramework, final @LoggerQualifier Logger logger) {
		logger.debug("ZookeeperServiceRegistry#co");

		this.logger = logger;
		this.curatorFramework = curatorFramework;
	}

	@PreDestroy
	public void cleanup() {
		this.logger.debug("ZookeeperServiceRegistry#cleanup");

	}

	/**
	 * Register service in zookeeper with structure:
	 * 
	 * <pre>
	 * 										----------
	 * 										| /local |
	 * 										----------		
	 *										/		 \
	 * 								----------   
	 * 								| /node1 |		 ...
	 * 								----------		
	 * 								/        \
	 * 					-------------		-------------
	 * 					| /services |		| /config	|
	 * 					-------------		-------------
	 * 					/		\
	 * 	    ---------------		---------
	 * 		| /management |		| /task |
	 * 		---------------		---------
	 * </pre>
	 */
	@Override
	public void registerService(final InstanceMetaData instanceMetaData, final String baseRootZnode) {
		this.logger.debug("ZookeeperServiceRegistry#registerService [instanceMetaData=" + instanceMetaData.toString() + ", baseRootZnode=" + baseRootZnode + "]");

		this.rootZNode = baseRootZnode;

		final JsonInstanceSerializer<InstanceMetaData> serializer = new JsonInstanceSerializer<InstanceMetaData>(InstanceMetaData.class);

		final StringBuilder sbUri = new StringBuilder();
		sbUri.append("{scheme}://{address}:{port}/").append(instanceMetaData.getContextRoot()).append("/").append(instanceMetaData.getApplicationPath()).append("/");

		try {
			this.serviceInstance = ServiceInstance.<InstanceMetaData>builder()
				.uriSpec(new UriSpec(sbUri.toString()))
				.address(instanceMetaData.getHostAdress())
				.port(instanceMetaData.getPort())
				.name(instanceMetaData.getServiceName())
				.payload(instanceMetaData)
				.serviceType(ServiceType.DYNAMIC) // Corresponds to CreateMode.EPHEMERAL
				.build();

				this.logger.info("ZookeeperServiceRegistry#registerService [serviceInstance=" + this.serviceInstance.toString() + "]");

			// Creates a serviceDiscovery instance and registers this auth-service in zookeeper.
			this.serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceMetaData.class)
														   .client(curatorFramework)
														   .serializer(serializer)
														   .thisInstance(serviceInstance)
														   .basePath(baseRootZnode)
														   .build();
			// Start service discovery instance.
			serviceDiscovery.start();
		} catch(Exception ex) {
			this.logger.error("ZookeeperServiceRegistry#registerService - registering of service failed! ", ex);
			throw new AppZookeeperException("ZookeeperServiceRegistry#registerService - service registry failed! ");
		}
	}

	/**
	 * Unregister the given URI by zookeeper.
	 * 
	 * @throws AppZookeeperException
	 *             if the service could not removed from zookeeper.
	 */
	@Override
	public void unregisterService() {
		this.logger.debug("ZookeeperServiceRegistry#unregisterService");

		try {
			if(serviceDiscovery != null && this.isConnected()) {
				serviceDiscovery.unregisterService(serviceInstance);
			}
		} catch (Exception e) {
			final String errorMessage = "ZookeeperServiceRegistry#unregisterService - failed! [serviceName=" + serviceInstance.getName() + "] ";
			
			this.logger.error(errorMessage, e);
		}
	}

	/**
	 * Return all registered instances by the discovery instance. To use a discovsery, a <code>ServiceProvider</code> instance has to be created.
	 * @param serviceName the name of the searched service.
	 * @return a <code>Collection</code> with all registered instances.
	 */
	@Override
	public Collection<ServiceInstance<InstanceMetaData>> getService(final String serviceName) {
		this.logger.debug("ZookeeperServiceRegistry#getService [serviceName=" + serviceName + "]");

		Collection<ServiceInstance<InstanceMetaData>> instances;

		try {
			instances = Collections.unmodifiableCollection(this.serviceDiscovery.queryForInstances(serviceName));

			return instances;
		} catch (Exception e) {
			final String errorMessage = "ZookeeperServiceRegistry#getService - Error while getting registered instances [serviceName=" + serviceName + "]";
			this.logger.error(errorMessage, e);

			throw new AppZookeeperException(errorMessage);
		}
	}

	/**
	 * Checks if the curator framework is running.
	 *
	 * @return true if the instance is connected, otherwise false.
	 */
	@Override
	public boolean isConnected() {
		boolean isConnected = false;

		if (this.curatorFramework != null) {
			isConnected = this.curatorFramework.getZookeeperClient().isConnected();
		}

		this.logger.debug("ZookeeperServiceRegistry#isConnected [isConnected=" + isConnected + "]");

		return isConnected;
	}

	@Override
	public String getRootZNode() {
		this.logger.debug("ZookeeperServiceRegistry#getRootZNode [rootZNode=" + this.rootZNode + "]");

		return this.rootZNode;
	}
}
