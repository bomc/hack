package de.bomc.poc.zk.services.registry.producer;

import de.bomc.poc.zk.config.env.EnvConfigKeys;
import de.bomc.poc.zk.config.env.qualifier.EnvConfigQualifier;
import de.bomc.poc.zk.curator.qualifier.CuratorFrameworkQualifier;
import de.bomc.poc.zk.exception.AppZookeeperException;
import de.bomc.poc.zk.services.InstanceMetaData;
import de.bomc.poc.zk.services.registry.qualifier.ZookeeperServiceRegistryQualifier;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.log4j.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * A producer for a service discovery instance.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
public class ZookeeperServiceRegistryProducer {

    // The logger.
    private static final Logger LOGGER = Logger.getLogger(ZookeeperServiceRegistryProducer.class);
    private static final String LOG_PREFIX = "ZookeeperServiceRegistryProducer#";
    // The curatorFramework that wraps the zookeeper curator, is injected by constructor.
    private final CuratorFramework curatorFramework;
    // The root zNode.
    private final String zNodeBasePath;
    // Handles the discovery.
    private ServiceDiscovery<InstanceMetaData> serviceDiscovery;

    /**
     * Creates a new bean of <code>ZookeeperServiceRegistryProducer</code> via bean constructor injection.
     * @param curatorFramework the curator client which wraps the zookeeper client.
     * @param zNodeBasePath        the path under which the instance will be registered.
     */
    @Inject
    public ZookeeperServiceRegistryProducer(@CuratorFrameworkQualifier final CuratorFramework curatorFramework, @EnvConfigQualifier(key = EnvConfigKeys.Z_NODE_BASE_PATH) final String zNodeBasePath) {
        LOGGER.debug(LOG_PREFIX + "co");

        this.curatorFramework = curatorFramework;
        this.zNodeBasePath = zNodeBasePath;
    }

    @Produces
    @ApplicationScoped
    @ZookeeperServiceRegistryQualifier
    public ServiceDiscovery<InstanceMetaData> getServiceDiscovery() {
        LOGGER.debug(LOG_PREFIX + "getServiceDiscovery [zNodeBasePath=" + this.zNodeBasePath + "]");

        if (null == this.serviceDiscovery) {
            this.init();
        }

        return this.serviceDiscovery;
    }

    @PreDestroy
    public void cleanup() {
        LOGGER.debug(LOG_PREFIX + "cleanup");

        CloseableUtils.closeQuietly(this.serviceDiscovery);
    }

    private void init() {
        LOGGER.debug(LOG_PREFIX + "init");

        final JsonInstanceSerializer<InstanceMetaData> serializer = new JsonInstanceSerializer<>(InstanceMetaData.class);

        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceMetaData.class)
                                                  .client(this.curatorFramework)
                                                  .serializer(serializer)
                                                  .basePath(this.zNodeBasePath)
                                                  .build();
        try {
            this.serviceDiscovery.start();
        } catch (final Exception e) {
            LOGGER.error(LOG_PREFIX + "init - Error starting service discovery", e);

            throw new AppZookeeperException(LOG_PREFIX + "init - Error starting service discovery", e);
        }
    }
}
