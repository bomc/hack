package de.bomc.poc.zk.services.registry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.log4j.Logger;

import de.bomc.poc.zk.curator.qualifier.CuratorFrameworkQualifier;
import de.bomc.poc.zk.services.InstanceMetaData;
import de.bomc.poc.zk.services.discovery.ZookeeperServiceDiscoveryProvider;
import de.bomc.poc.zk.services.registry.qualifier.ZookeeperServiceRegistryQualifier;

@Startup
@Singleton
public class RegistryStarterSingletonEJB {

	private static final Logger LOGGER = Logger.getLogger(RegistryStarterSingletonEJB.class.getSimpleName());
	private static final String LOG_PREFIX = "SingletonEJBStarter#";
	@Inject
	@ZookeeperServiceRegistryQualifier
	private ServiceDiscovery<InstanceMetaData> serviceDiscovery;
	@Inject
	private ZookeeperServiceDiscoveryProvider zookeeperServiceDiscoveryProvider;

	@Inject
	@CuratorFrameworkQualifier
	private CuratorFramework client;
	
	@PostConstruct
	public void setup() {
		System.out.println(LOG_PREFIX + "setup");
		LOGGER.debug(LOG_PREFIX + "setup");
		
		try {
			serviceDiscovery.registerService(this.getServiceInstance());

			for (int i = 0; i < 25; i++) {
				ServiceInstance<InstanceMetaData> serviceInstance = zookeeperServiceDiscoveryProvider
						.getServiceInstance("serviceName" + i);
				LOGGER.debug(LOG_PREFIX + "setup - " + serviceInstance.toString());
				System.out.println(LOG_PREFIX + "setup - " + serviceInstance.toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@PreDestroy
	public void cleanup() {
		System.out.println(LOG_PREFIX + "cleanup");
		LOGGER.debug(LOG_PREFIX + "setup");

		try {
			serviceDiscovery.unregisterService(this.getServiceInstance());
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * @return a initialized ServiceInstance.
	 * @throws Exception
	 *             if creation of ServiceInstance failed.
	 */
	private ServiceInstance<InstanceMetaData> getServiceInstance() throws Exception {

		final InstanceMetaData instanceMetaData = this.getInstanceMetaData();

		final ServiceInstance<InstanceMetaData> serviceInstance = ServiceInstance.<InstanceMetaData>builder()
				.uriSpec(new UriSpec("{scheme}://{address}:{port}/" + instanceMetaData.getContextRoot() + "/"
						+ instanceMetaData.getApplicationPath() + "/"))
				.address(instanceMetaData.getHostAdress()).port(instanceMetaData.getPort())
				.name(instanceMetaData.getServiceName()).payload(instanceMetaData).serviceType(ServiceType.DYNAMIC) // Corresponds
																													// to
																													// CreateMode.EPHEMERAL
				.build();

		return serviceInstance;
	}

	/**
	 * Create an initialized {@link InstanceMetaData} object.
	 * 
	 * @return an initialized object.
	 */
	private InstanceMetaData getInstanceMetaData() {
		LOGGER.debug(LOG_PREFIX + "getInstanceMetaData");

		// Metadata for service registration.
		final InstanceMetaData instanceMetaData = InstanceMetaData.hostAdress("127.0.0.3").port(8180)
				.serviceName("serviceName").contextRoot("test-me").applicationPath("serviceName")
				.description("my_description").build();

		return instanceMetaData;
	}
}
