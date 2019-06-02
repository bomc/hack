package de.bomc.poc.zk.services.registry;

import de.bomc.poc.zk.exception.AppZookeeperException;
import de.bomc.poc.zk.services.InstanceMetaData;
import de.bomc.poc.zk.services.registry.qualifier.ZookeeperServiceRegistryQualifier;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;

/**
 * <pre>
 * This bean is used to un/-register services on the service registry.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
@ApplicationScoped
public class ZookeeperServiceRegistryProvider {

    // The logger.
    private static final Logger LOGGER = Logger.getLogger(ZookeeperServiceRegistryProvider.class);
    private static final String LOG_PREFIX = "ZookeeperServiceRegistryProvider#";
    // Handles the discovery.
    @Inject
    @ZookeeperServiceRegistryQualifier
    private ServiceDiscovery<InstanceMetaData> serviceDiscovery;

    /**
     * Creates a new bean of <code>ZookeeperServiceRegistryProvider</code>. This constructor is used by the cdi container to get this class proxied.
     */
    public ZookeeperServiceRegistryProvider() {
        // Used by CDI container.
    }

    /**
     * Register service in zookeeper with structure:
     * @param instanceMetaData metaData contains metaData of the registering service.
     * @return true the service is registered, otherwise false the instance is already registered.
     */
    public boolean registerService(@NotNull final InstanceMetaData instanceMetaData) {
        LOGGER.debug(LOG_PREFIX + "registerService [instanceMetaData=" + instanceMetaData + "]");

        boolean isRegistered = false;

        // Check if a ServiceInstance is already registered.
        this.checkIfServiceInstanceIsAlreadyRegistered(instanceMetaData);
        if (this.checkIfServiceInstanceIsAlreadyRegistered(instanceMetaData) > 0L) {
            LOGGER.warn(LOG_PREFIX + "registerService - a ServiceInstance with the given InstanceMetaData is already registered! [instanceMetaData=" + instanceMetaData + "]");

            return isRegistered;
        }

        try {
            final ServiceInstance<InstanceMetaData>
                serviceInstance =
                ServiceInstance.<InstanceMetaData>builder()
                               .uriSpec(new UriSpec("{scheme}://{address}:{port}/" + instanceMetaData.getContextRoot() + "/" + instanceMetaData.getApplicationPath() + "/"))
                               .address(instanceMetaData.getHostAdress())
                               .port(instanceMetaData.getPort())
                               .name(instanceMetaData.getServiceName())
                               .payload(instanceMetaData).serviceType(ServiceType.DYNAMIC) // Corresponds to CreateMode.EPHEMERAL
                    .build();

            this.serviceDiscovery.registerService(serviceInstance);

            isRegistered = true;
        } catch (final Exception ex) {
            final String errorMessage = LOG_PREFIX + "registerService - registering of service failed! ";
            LOGGER.error(errorMessage, ex);

            throw new AppZookeeperException(errorMessage, ex);
        }

        return isRegistered;
    }

    /**
     * Check if a <code>ServiceInstance</code> with the given instanceMetaData is already registered.
     * @param instanceMetaData the given instance to compare.
     * @return 0 the instance is not already registered, 1 the instance is already registered.
     */
    private long checkIfServiceInstanceIsAlreadyRegistered(final InstanceMetaData instanceMetaData) {
        LOGGER.debug(LOG_PREFIX + "checkIfServiceInstanceIsAlreadyRegistered [instanceMetaData=" + instanceMetaData + "]");

        long count;

        try {
            final Collection<ServiceInstance<InstanceMetaData>> serviceInstanceCollection = this.serviceDiscovery.queryForInstances(instanceMetaData.getServiceName());
            count =
                serviceInstanceCollection.stream()
                                         .filter(instanceMetaDataServiceInstance -> instanceMetaDataServiceInstance.getPayload()
                                                                                                                   .equals(instanceMetaData))
                                         .count();

            return count;
        } catch (final Exception ex) {
            final String errorMessage = LOG_PREFIX + "checkIfServiceInstanceIsAlreadyRegistered - failed! [instanceMetaData=" + instanceMetaData + "]";
            LOGGER.error(errorMessage, ex);

            throw new AppZookeeperException(errorMessage, ex);
        }
    }

    /**
     * Unregister all registered services by the given serviceInstanceName on the zNode of ServiceDiscovery instance in zookeeper.
     * @param serviceInstanceName name service instance to be unregistered.
     * @throws AppZookeeperException if the service could not removed from zookeeper.
     */
    public void unregisterService(@NotNull @Size(min = 3) final String serviceInstanceName) {
        LOGGER.debug(LOG_PREFIX + "unregisterService [serviceInstanceName=" + serviceInstanceName + "]");

        if (this.serviceDiscovery != null) {
            try {
                final Collection<ServiceInstance<InstanceMetaData>> serviceInstanceCollection = this.serviceDiscovery.queryForInstances(serviceInstanceName);
                serviceInstanceCollection.forEach(instanceMetaData -> {
                    try {
                        LOGGER.debug(LOG_PREFIX + "unregisterService [instanceMetaData=" + instanceMetaData + "]");
                        this.serviceDiscovery.unregisterService(instanceMetaData);
                    } catch (final Exception e) {
                        LOGGER.error(LOG_PREFIX + "unregisterService#lambda unregistering failed! [instanceMetaData=" + instanceMetaData + "]");
                    }
                });
            } catch (final Exception ex) {
                LOGGER.error(LOG_PREFIX + "unregisterService queryForInstances failed!");
            }
        }
    }

    /**
     * Return all registered instances by the discovery instance. To use a discovsery, a <code>ServiceProvider</code> instance has to be created.
     * @param serviceName the name of the searched service.
     * @return a <code>Collection</code> with all registered instances or a empty collection if no service is registered by the given serviceName.
     */
    public Collection<ServiceInstance<InstanceMetaData>> getService(@NotNull @Size(min = 3) final String serviceName) {
        LOGGER.debug("ZookeeperServiceRegistryProvider#getService [serviceName=" + serviceName + "]");

        final Collection<ServiceInstance<InstanceMetaData>> instances;

        try {
            instances = Collections.unmodifiableCollection(this.serviceDiscovery.queryForInstances(serviceName));

            return instances;
        } catch (final Exception e) {
            final String errorMessage = LOG_PREFIX + "getService - Error while getting registered instances [serviceName=" + serviceName + "]";
            LOGGER.error(errorMessage, e);

            throw new AppZookeeperException(errorMessage, e);
        }
    }
}
