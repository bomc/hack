package de.bomc.poc.zk.services.discovery;

import de.bomc.poc.zk.concurrent.qualifier.ManagedThreadFactoryQualifier;
import de.bomc.poc.zk.config.env.EnvConfigKeys;
import de.bomc.poc.zk.config.env.qualifier.EnvConfigQualifier;
import de.bomc.poc.zk.curator.qualifier.CuratorFrameworkQualifier;
import de.bomc.poc.zk.exception.AppZookeeperException;
import de.bomc.poc.zk.services.InstanceMetaData;
import de.bomc.poc.zk.services.lock.interceptor.LockInterceptor;
import de.bomc.poc.zk.services.lock.qualifier.LockQualifier;
import de.bomc.poc.zk.services.registry.qualifier.ZookeeperServiceRegistryQualifier;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LockType;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This class should be used for discovery actions.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
@ApplicationScoped
@Interceptors({LockInterceptor.class})
public class ZookeeperServiceDiscoveryProvider {

    private static final Logger LOGGER = Logger.getLogger(ZookeeperServiceDiscoveryProvider.class);
    private static final String LOG_PREFIX = "ZookeeperServiceDiscoveryProvider#";
    @Inject
    @CuratorFrameworkQualifier
    private CuratorFramework client;
    @Inject
    @ZookeeperServiceRegistryQualifier
    private ServiceDiscovery<InstanceMetaData> serviceDiscovery;
    @Inject
    @EnvConfigQualifier(key = EnvConfigKeys.Z_NODE_BASE_PATH)
    private String zNodeBasePath;
    private final Map<String, ServiceProvider<InstanceMetaData>> providers = Maps.newHashMap();
    private final List<Closeable> closeableList = Lists.newArrayList();
    @Inject
    @ManagedThreadFactoryQualifier
    private ManagedThreadFactory defaultManagedThreadFactory;

    public ZookeeperServiceDiscoveryProvider() {
        //
        // Used by CDI container.
    }

    /**
     * Creates a new instance of <code>ZookeeperServiceDiscoveryProvider</code>.
     */
    @PostConstruct
    public void init() {
        LOGGER.debug(LOG_PREFIX + "init");
    }

    @PreDestroy
    public void cleanup() {
        LOGGER.debug(LOG_PREFIX + "cleanup");

        this.close();
    }

    /**
     * Return the service instance from zookeeper by name.
     * @param serviceName the name of the service.
     * @return the service instance from zookeeper by name.
     * @throws AppZookeeperException if getting service instance, or starting of provider failed.
     */
    @LockQualifier(LockType.WRITE)
    public ServiceInstance<InstanceMetaData> getServiceInstance(@NotNull @Size(min = 3) final String serviceName) {
		try {
            ServiceProvider<InstanceMetaData> provider = this.providers.get(serviceName);

            LOGGER.debug(LOG_PREFIX + "getServiceInstance [serviceName=" + serviceName + ", provider=" + provider + "]");

            if (provider == null) {
                LOGGER.debug(LOG_PREFIX + "getServiceInstance - create a new serviceProvider. [serviceName=" + serviceName + "]");

                provider =
                    this.serviceDiscovery.serviceProviderBuilder()
                                         .serviceName(serviceName)
                                         .providerStrategy(new RoundRobinStrategy<InstanceMetaData>())
                                         .threadFactory(this.defaultManagedThreadFactory)
                                         .build();
                provider.start();

                LOGGER.debug(LOG_PREFIX + "getServiceInstance - start a new provider.");

                this.closeableList.add(provider);
                this.providers.put(serviceName, provider);

                LOGGER.debug(LOG_PREFIX + "getServiceInstance - finished [providers.size=" + this.providers.size() + "]");
            }
    
            return provider.getInstance();
        } catch (final Exception ex) {
            final String errorMessage = LOG_PREFIX + "getServiceInstance - if getting service instance, or starting of provider failed! ";
            LOGGER.error(errorMessage, ex);
            throw new AppZookeeperException(errorMessage, ex);
        }
    }

    /**
     * Cleanup all resources.
     */
    public void close() {
    	LOGGER.debug(LOG_PREFIX + "close");
    	
    	if(this.serviceDiscovery != null) {
    		try {
				this.serviceDiscovery.close();
			} catch (IOException e) {
				LOGGER.error(LOG_PREFIX + "close - Error stopping serviceDuscovery instance! ", e);
			}
    	}
    	
        this.closeableList.forEach(CloseableUtils::closeQuietly);
    }
}
