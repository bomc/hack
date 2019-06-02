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
package de.bomc.poc.zookeeper.discovery;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.bomc.poc.concurrency.qualifier.ManagedThreadFactoryQualifier;
import de.bomc.poc.config.EnvConfigKeys;
import de.bomc.poc.config.qualifier.EnvConfigQualifier;
import de.bomc.poc.exception.app.AppZookeeperException;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.zookeeper.curator.qualifier.CuratorFrameworkQualifier;
import de.bomc.poc.zookeeper.discovery.qualifier.LockQualifier;
import de.bomc.poc.zookeeper.InstanceMetaData;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LockType;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * A helper class for discovery.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@ApplicationScoped
public class ServiceDiscoveryProvider {

	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	@CuratorFrameworkQualifier
	private CuratorFramework client;
	@Inject
	@EnvConfigQualifier(key = EnvConfigKeys.ZNODE_BASE_PATH)
	private String zNodeBasePath;
	private ServiceDiscovery<InstanceMetaData> serviceDiscovery;
	private final Map<String, ServiceProvider<InstanceMetaData>> providers = Maps.newHashMap();
	private final List<Closeable> closeableList = Lists.newArrayList();
	@Inject
	@ManagedThreadFactoryQualifier
	private ManagedThreadFactory defaultManagedThreadFactory;

	public ServiceDiscoveryProvider() {
		//
		// Used by CDI container.
	}

	/**
	 * Creates a new instance of <code>ServiceDiscoveryProvider</code>.
	 */
	@PostConstruct
	public void init() {
		this.logger.debug("ServiceDiscoveryProvider#init");

		final JsonInstanceSerializer<InstanceMetaData> serializer = new JsonInstanceSerializer<>(
				InstanceMetaData.class);
		this.serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceMetaData.class).client(this.client)
				.basePath(this.zNodeBasePath).serializer(serializer).build();
		try {
			this.serviceDiscovery.start();
		} catch (Exception ex) {
			final String logMessage = "ServiceDiscoveryProvider#co - Could not start serviceDiscovery instance";

			this.logger.debug(logMessage, ex);

			throw new AppZookeeperException(logMessage);
		}
	}

	@PreDestroy
	public void cleanup() {
		this.logger.debug("ServiceDiscoveryProvider#cleanup");

		this.close();
	}

	/**
	 * Return the service instance from zookeeper by name.
	 * 
	 * @param serviceName
	 *            the name of the service.
	 * @return the service instance from zookeeper by name.
	 * @throws Exception
	 */
	@LockQualifier(LockType.WRITE)
	public ServiceInstance<InstanceMetaData> getServiceInstance(final String serviceName) throws Exception {
		ServiceProvider<InstanceMetaData> provider = this.providers.get(serviceName);

		this.logger.debug("ServiceDiscoveryProvider#getServiceInstance [serviceName=" + serviceName + ", provider="
				+ provider + "]");

		if (provider == null) {
			this.logger
					.debug("ServiceDiscoveryProvider#getServiceInstance - create a new serviceProvider. [serviceName="
							+ serviceName + "]");

			provider = this.providers.get(serviceName);

			if (provider == null) {
				provider = this.serviceDiscovery.serviceProviderBuilder().serviceName(serviceName)
						.providerStrategy(new RoundRobinStrategy<InstanceMetaData>())
						.threadFactory(this.defaultManagedThreadFactory).build();
				provider.start();

				this.logger.debug("ServiceDiscoveryProvider#getServiceInstance - start a new provider.");

				this.closeableList.add(provider);
				this.providers.put(serviceName, provider);

				this.logger.debug("ServiceDiscoveryProvider#getServiceInstance - finished [providers.size="
						+ providers.size() + "]");
			}
		}

		return provider.getInstance();
	}

	/**
	 * Cleanup all resources.
	 */
	public void close() {
		this.closeableList.forEach(CloseableUtils::closeQuietly);
		
		CloseableUtils.closeQuietly(this.serviceDiscovery);
	}
}
