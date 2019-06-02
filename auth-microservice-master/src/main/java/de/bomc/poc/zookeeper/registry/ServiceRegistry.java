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
package de.bomc.poc.zookeeper.registry;

import de.bomc.poc.zookeeper.InstanceMetaData;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.Collection;

/**
 * Interface for using the curator (zookeeper) curator.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
public interface ServiceRegistry {

    /**
     * Check if the curator (zookeeper) curator is connected
     * @return true is connected otherwise false.
     */
    boolean isConnected();

    /**
     * This is root path in zookeeper. The service is added relative to this path.
     * @return the root path in zookeeper.
     */
    String getRootZNode();

    /**
     * Register the service by given service name and URI.
     * @param instanceMetaData metadata for service registration.
     * @param baseRootZnode    the root path in zookeeper.
     */
    void registerService(InstanceMetaData instanceMetaData, String baseRootZnode);

    /**
     * Unregister all registered services.
     */
    void unregisterService();

    /**
     * Return all registered instances by the discovery instance.
     * @param serviceName the name of the searched service.
     * @return a <code>Collection</code> with all registered instances.
     */
    Collection<ServiceInstance<InstanceMetaData>> getService(String serviceName);
}
