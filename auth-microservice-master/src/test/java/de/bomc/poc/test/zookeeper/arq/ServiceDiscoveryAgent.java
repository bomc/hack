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
package de.bomc.poc.test.zookeeper.arq;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * A helper class for discovery.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
public class ServiceDiscoveryAgent {
    private static final Logger LOGGER = Logger.getLogger(ServiceDiscoveryAgent.class);
    private final ServiceDiscovery<InstanceMetaData> serviceDiscovery;
    private final Map<String, ServiceProvider<InstanceMetaData>> providers = Maps.newHashMap();
    private final List<Closeable> closeableList = Lists.newArrayList();
    private final Object lock = new Object();

    /**
     * Creates a new instance of <code>ServiceDiscoveryProvider</code>.
     * @param client   a initialized and started curator (zookeeper) curator.
     * @param basePath the path in to the service instance in zookeeper.
     * @throws Exception
     */
    public ServiceDiscoveryAgent(final CuratorFramework client, final String basePath) throws Exception {
        final JsonInstanceSerializer<InstanceMetaData> serializer = new JsonInstanceSerializer<InstanceMetaData>(InstanceMetaData.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceMetaData.class)
                                                       .client(client)
                                                       .basePath(basePath)
                                                       .serializer(serializer)
                                                       .build();
        this.serviceDiscovery.start();
    }

    /**
     * Return the service instance from zookeeper by name.
     * @param serviceName the name of the service.
     * @return the service instance from zookeeper by name.
     * @throws Exception
     */
    public ServiceInstance<InstanceMetaData> getServiceInstance(final String serviceName) throws Exception {
        ServiceProvider<InstanceMetaData> provider = this.providers.get(serviceName);
        if (provider == null) {
            synchronized (this.lock) {
                LOGGER.debug("ServiceDiscoveryProvider#getServiceInstance - create a new serviceProvider. [serviceName=" + serviceName + "]");

                provider = this.providers.get(serviceName);

                if (provider == null) {
                    provider =
                        this.serviceDiscovery.serviceProviderBuilder()
                                             .serviceName(serviceName)
                                             .providerStrategy(new RoundRobinStrategy<InstanceMetaData>())
                                             .build();
                    provider.start();
                    this.closeableList.add(provider);
                    this.providers.put(serviceName, provider);
                }
            }
        }

        return provider.getInstance();
    }

    /**
     * Cleanup all resources.
     */
    public synchronized void close() {
        for (Closeable closeable : this.closeableList) {
            CloseableUtils.closeQuietly(closeable);
        }
    }
}
