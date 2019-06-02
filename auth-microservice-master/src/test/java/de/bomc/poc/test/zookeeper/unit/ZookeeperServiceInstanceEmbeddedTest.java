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
package de.bomc.poc.test.zookeeper.unit;

import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.test.GlobalArqTestProperties;
import de.bomc.poc.zookeeper.InstanceMetaData;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the registration and discovery of services with a running zookeeper
 * instance.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ZookeeperServiceInstanceEmbeddedTest extends UnitZookeeperBase {

	private static final Logger LOGGER = Logger.getLogger(ZookeeperServiceInstanceEmbeddedTest.class);
	private static final String LOG_PREFIX = "ZookeeperServiceInstanceEmbeddedTest#";
	private static final String ROOT_ZNODE = "/zookeeperServiceInstanceEmbeddedTest" + GlobalArqTestProperties.RELATIVE_ROOT_Z_NODE;
	private final String SERVICE_NAME = JaxRsActivator.APPLICATION_PATH;
	private final String HOST_ADDRESS_1 = "127.0.0.1";
	private final String HOST_ADDRESS_2 = "127.0.0.2";
	private final int PORT = 8180;
	private final List<ServiceInstance<InstanceMetaData>> serviceInstanceList = new ArrayList<ServiceInstance<InstanceMetaData>>();
	private ServiceDiscovery<InstanceMetaData> serviceDiscovery;
	/**
	 * Tests positive registration and discovery of service instances.
	 * 
	 * <pre>
	 * mvn clean install -Dtest=ZookeeperServiceInstanceEmbeddedTest#test01_serviceInstanceDiscovery_pass
	 * </pre>
	 */
	@Test
	public void test01_serviceInstanceDiscovery_pass() {
		LOGGER.debug(LOG_PREFIX + "test01_serviceInstanceDiscovery_pass");

		// Create curator.
		final ZookeeperClient zookeeperClient = new ZookeeperClient(server.getConnectString(),
				GlobalArqTestProperties.CONNECTION_TIMEOUT_MS, GlobalArqTestProperties.SESSION_TIMEOUT_MS,
			ROOT_ZNODE);

		try {
			final ServiceInstance<InstanceMetaData> instance1 = this.createServiceInstance(
					InstanceMetaData.hostAdress(HOST_ADDRESS_1).port(PORT).serviceName(SERVICE_NAME).contextRoot(null)
							.applicationPath(null).description("additional service description 1").build());
			final ServiceInstance<InstanceMetaData> instance2 = this.createServiceInstance(
					InstanceMetaData.hostAdress(HOST_ADDRESS_2).port(PORT).serviceName(SERVICE_NAME).contextRoot(null)
							.applicationPath(null).description(null).build());
			this.serviceInstanceList.add(instance1);
			this.serviceInstanceList.add(instance2);

			this.serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceMetaData.class)
					.client(zookeeperClient.getCuratorFramework())
					.serializer(new JsonInstanceSerializer<InstanceMetaData>(InstanceMetaData.class))
					.basePath(ROOT_ZNODE).build();
			try {
				this.serviceDiscovery.start();
			} catch (Exception ex) {
				LOGGER.error(
						LOG_PREFIX + "test01_serviceInstanceDiscovery_pass - starting 'serviceDiscovery' failed! ");
				ex.printStackTrace();
			}

			// Register the ServiceInstance for discovery.
			this.registerServiceInstancesForDiscovery();

			// Check discovery collection.
			final Collection<ServiceInstance<InstanceMetaData>> discoveryCollection = this.getService(this.SERVICE_NAME,
					this.serviceDiscovery);
			assertThat(discoveryCollection.size(), is(equalTo(2)));

			// Create a ServiceProvider, this is a facade and encapsulates the
			// discovery service for a particular named service along with a
			// provider strategy. A provider strategy is a scheme for selecting
			// one instance
			// from a set of instances for a given service. There are three
			// bundled strategies: Round Robin, Random and Sticky (always
			// selects the same one)..
			final ServiceProvider<InstanceMetaData> serviceProvider = this.serviceDiscovery.serviceProviderBuilder()
					.serviceName(this.SERVICE_NAME).providerStrategy(new RoundRobinStrategy<>()).build();
			// The provider must be started before use.
			try {
				serviceProvider.start();
			} catch (Exception ex) {
				LOGGER.error(LOG_PREFIX + "test01_serviceInstanceDiscovery_pass - starting 'serviceProvider' failed! ");
				ex.printStackTrace();
			}

			// Check the provider strategy.
			for (int i = 0; i < 1000; i++) {
				String address = "";
				try {
					final ServiceInstance<InstanceMetaData> retServiceInstance = serviceProvider.getInstance();
					LOGGER.debug(LOG_PREFIX + "test01_serviceInstanceDiscovery_pass - check strategy [instance="
							+ retServiceInstance.toString() + ", uri=" + retServiceInstance.buildUriSpec() + "]");

					assertThat(address, not(equalTo(retServiceInstance.getAddress())));
					address = retServiceInstance.getAddress();
				} catch (Exception ex) {
					LOGGER.error(LOG_PREFIX + "test01_serviceInstanceDiscovery_pass - strategy check failed! ", ex);
				}
			}
		} finally {
			// Cleanup resources.
			this.cleanup(zookeeperClient);
		}
	}

	/**
	 * This test checks after <code>ServiceInstance</code> discovery
	 * registration and a following unregistration, if the the
	 * <code>ServiceInstance</code> was removed immediately by zookeeper.
	 * 
	 * <pre>
	 * mvn clean install -Dtest=ZookeeperServiceInstanceEmbeddedTest#test02_serviceInstanceDiscovery_fail
	 * </pre>
	 */
	@Test
	public void test02_serviceInstanceDiscovery_fail() throws Exception {
		LOGGER.debug(LOG_PREFIX + "test02_serviceInstanceDiscovery_fail");

		// Create two service instances, which will be registered in zookeeper.  
		final ServiceInstance<InstanceMetaData> instance1 = this.createServiceInstance(
				InstanceMetaData.hostAdress(HOST_ADDRESS_1).port(PORT).serviceName(SERVICE_NAME).contextRoot(null)
						.applicationPath(null).description("additional service description 1").build());
		final ServiceInstance<InstanceMetaData> instance2 = this
				.createServiceInstance(InstanceMetaData.hostAdress(HOST_ADDRESS_2).port(PORT).serviceName(SERVICE_NAME)
						.contextRoot(null).applicationPath(null).description(null).build());

		// Write the instances in a list, which works as a cache.
		this.serviceInstanceList.add(instance1);
		this.serviceInstanceList.add(instance2);

		// Create curator.
		final ZookeeperClient zookeeperClient = new ZookeeperClient(server.getConnectString(),
				GlobalArqTestProperties.CONNECTION_TIMEOUT_MS, GlobalArqTestProperties.SESSION_TIMEOUT_MS,
			ROOT_ZNODE);
		// Create a serviceDiscovery instance, with the zookepperClient instance before.
		this.serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceMetaData.class)
				.client(zookeeperClient.getCuratorFramework())
				.serializer(new JsonInstanceSerializer<InstanceMetaData>(InstanceMetaData.class))
				.basePath(ROOT_ZNODE).build();
		try {
			// Start the serviceDiscovery.
			this.serviceDiscovery.start();
		} catch (Exception ex) {
			LOGGER.error(LOG_PREFIX + "test02_serviceInstanceDiscovery_fail - starting 'serviceDiscovery' failed! ");
			ex.printStackTrace();
		}

		// Register the ServiceInstance for discovery.
		this.registerServiceInstancesForDiscovery();

		// Check discovery collection.
		final Collection<ServiceInstance<InstanceMetaData>> discoveryCollection = this.getService(this.SERVICE_NAME,
				this.serviceDiscovery);
		assertThat(discoveryCollection.size(), is(equalTo(2)));

		// Create a ServiceProvider, this is a facade and encapsulates the
		// discovery service for a particular named service along with a
		// provider strategy. A provider strategy is a scheme for selecting one
		// instance
		// from a set of instances for a given service. There are three bundled
		// strategies: Round Robin, Random and Sticky (always selects the same
		// one)..
		final ServiceProvider<InstanceMetaData> serviceProvider = this.serviceDiscovery.serviceProviderBuilder()
				.serviceName(this.SERVICE_NAME).providerStrategy(new RoundRobinStrategy<>()).build();
		// The provider must be started before use.
		try {
			serviceProvider.start();
			//
			// Now the service provider is ready for discovery.
			//
			// ...
		} catch (Exception ex) {
			LOGGER.error(LOG_PREFIX + "test02_serviceInstanceDiscovery_fail - starting 'serviceProvider' failed! ", ex);
		}

		// Unregister all instances, after this no serviceInstance is registered
		// in zookeeper.
		this.cleanup(zookeeperClient);

		// Create new curator and check if the unregistered ServiceInstances are
		// removed.
		final ZookeeperClient checkZookeeperClient = new ZookeeperClient(server.getConnectString(),
				GlobalArqTestProperties.CONNECTION_TIMEOUT_MS, GlobalArqTestProperties.SESSION_TIMEOUT_MS,
			ROOT_ZNODE);

		final List<String> uris = checkZookeeperClient.getCuratorFramework().getChildren()
				.forPath(ROOT_ZNODE + "/" + this.SERVICE_NAME);
		assertThat(uris.size(), is(0));

		checkZookeeperClient.close();
	}

	/**
	 * Get the URI of the registered endpoint from zookeeper.
	 * 
	 * @return the URI of the registered endpoint service.
	 */
	@SuppressWarnings("unused")
	private String discoverServiceURI(final CuratorFramework curatorFrameworkInstance) {
		LOGGER.debug(LOG_PREFIX + "discoverServiceURI");

		try {
			// auth/test.node/service/auth-api
			final String znode = ROOT_ZNODE + this.SERVICE_NAME;

			final List<String> uris = curatorFrameworkInstance.getChildren().forPath(znode);

			return new String(curatorFrameworkInstance.getData().forPath(ZKPaths.makePath(znode, uris.get(0))));
		} catch (Exception ex) {
			LOGGER.debug(LOG_PREFIX + "discoverServiceURI - failed! ", ex);
			throw new RuntimeException("Discover ServiceURI failed!");
		}
	}

	/**
	 * Cleanup resources.
	 * 
	 * @param zookeeperClient
	 */
	private void cleanup(final ZookeeperClient zookeeperClient) {
		// Cleanup resources.
		this.unregisterServiceInstancesFromDiscovery();

		try {
			this.serviceDiscovery.close();
		} catch (IllegalStateException | IOException ex) {
			LOGGER.error(LOG_PREFIX
					+ "test01_serviceInstanceDiscovery#cleanup - serviceDiscovery.close failed, can be ignored! " + ex);
		}

		zookeeperClient.close();
	}

	/**
	 * Unregister <code>ServiceInstance</code>s from discovery.
	 */
	private void unregisterServiceInstancesFromDiscovery() {
		LOGGER.debug(LOG_PREFIX + "test01_serviceInstanceDiscovery_pass#unregisterServiceInstancesFromDiscovery");

		this.serviceInstanceList.forEach(serviceInstance -> {
			try {
				this.serviceDiscovery.unregisterService(serviceInstance);
			} catch (Exception ex) {
				LOGGER.debug(LOG_PREFIX
						+ "test01_serviceInstanceDiscovery_pass#unregisterServiceInstancesForDiscovery - failed! [serviceInstance="
						+ serviceInstance.getPayload().toString() + "]", ex);
				throw new RuntimeException("Registration failed! " + serviceInstance.getPayload().toString());
			}
		});
	}

	/**
	 * Register <code>ServiceInstance</code>s for discovery.
	 */
	private void registerServiceInstancesForDiscovery() {
		LOGGER.debug(LOG_PREFIX + "test01_serviceInstanceDiscovery_pass#registerServiceInstancesForDiscovery");

		this.serviceInstanceList.forEach(serviceInstance -> {
			try {
				this.serviceDiscovery.registerService(serviceInstance);
			} catch (Exception ex) {
				LOGGER.debug(LOG_PREFIX
						+ "test01_serviceInstanceDiscovery_pass#registerServiceInstancesForDiscovery - failed! [serviceInstance="
						+ serviceInstance.getPayload().toString() + "]", ex);
				throw new RuntimeException("Registration failed! " + serviceInstance.getPayload().toString());
			}
		});
	}

	/**
	 * Creates a new {@link ServiceInstance} for discovery.
	 * 
	 * @param instanceMetaData
	 *            contains the registration metaData.
	 * @return a initialized <code>ServiceInstance</code>.
	 */
	private ServiceInstance<InstanceMetaData> createServiceInstance(final InstanceMetaData instanceMetaData) {
		LOGGER.debug(LOG_PREFIX + "test01_serviceInstanceDiscovery_pass#createServiceInstance - [instanceMetaData="
				+ instanceMetaData.toString() + "]");

		try {
			ServiceInstance<InstanceMetaData> instance = ServiceInstance.<InstanceMetaData> builder()
					.uriSpec(new UriSpec("{scheme}://{address}:{port}/context-root/application-path"))
					.address(instanceMetaData.getHostAdress()).port(instanceMetaData.getPort())
					.name(instanceMetaData.getServiceName()).payload(instanceMetaData).build();
			LOGGER.debug(LOG_PREFIX + "test01_serviceInstanceDiscovery_pass#createServiceInstance - [instance="
					+ instance.toString() + "]");

			return instance;
		} catch (Exception ex) {
			LOGGER.error(LOG_PREFIX + "test01_serviceInstanceDiscovery_pass#createServiceInstance - failed! ", ex);

			throw new RuntimeException("Creating ServiceInstance failed!");
		}
	}

	/**
	 * Return all registered instances by the discovery instance.
	 * 
	 * @param serviceName
	 *            the name of the searched service.
	 * @return a <code>Collection</code> with all registered instances.
	 */
	private Collection<ServiceInstance<InstanceMetaData>> getService(final String serviceName,
			final ServiceDiscovery<InstanceMetaData> discovery) {
		LOGGER.debug(LOG_PREFIX + "test01_serviceInstanceDiscovery#getService [serviceName=" + serviceName + "]");

		Collection<ServiceInstance<InstanceMetaData>> instances = Collections.emptyList();

		try {
			instances = discovery.queryForInstances(serviceName);

			return instances;
		} catch (Exception e) {
			LOGGER.error(
					LOG_PREFIX
							+ "test01_serviceInstanceDiscovery#getService - Error while getting registered instances.",
					e);

			throw new RuntimeException("Creating ServiceInstance failed!");
		}
	}
}
