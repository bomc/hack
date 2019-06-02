package de.bomc.poc.zk.test.arq;

import de.bomc.poc.zk.concurrent.producer.ManagedConcurrencyProducer;
import de.bomc.poc.zk.concurrent.qualifier.ManagedThreadFactoryQualifier;
import de.bomc.poc.zk.config.accessor.ZookeeperConfigAccessor;
import de.bomc.poc.zk.config.accessor.impl.ZookeeperConfigAccessorImpl;
import de.bomc.poc.zk.config.env.EnvConfigKeys;
import de.bomc.poc.zk.config.env.qualifier.EnvConfigQualifier;
import de.bomc.poc.zk.curator.producer.CuratorFrameworkProducer;
import de.bomc.poc.zk.curator.producer.SystemDefaultDnsResolver;
import de.bomc.poc.zk.curator.qualifier.CuratorFrameworkQualifier;
import de.bomc.poc.zk.exception.AppZookeeperException;
import de.bomc.poc.zk.services.InstanceMetaData;
import de.bomc.poc.zk.services.discovery.ZookeeperServiceDiscoveryProvider;
import de.bomc.poc.zk.services.lock.interceptor.LockInterceptor;
import de.bomc.poc.zk.services.lock.qualifier.LockQualifier;
import de.bomc.poc.zk.services.registry.ZookeeperServiceRegistryProvider;
import de.bomc.poc.zk.services.registry.producer.ZookeeperServiceRegistryProducer;
import de.bomc.poc.zk.services.registry.qualifier.ZookeeperServiceRegistryQualifier;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

/**
 * Tests the service registry.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
@RunWith(Arquillian.class)
public class ZookeeperServiceDiscoveryProviderTestIT extends ArquillianBase {

	private static final Logger LOGGER = Logger.getLogger(ZookeeperServiceDiscoveryProviderTestIT.class);
	private static final String LOG_PREFIX = "ZookeeperServiceDiscoveryProviderTestIT#";
	private static final String WEB_ARCHIVE_NAME = "service-discovery-provider-war";
	private static final String SERVICE_NAME = "service-discovery-provider-test";
	private static final int DEFAULT_PORT = 8080;
	private static final int SERVICE_INSTANCE_COUNT = 10;
	private final String HOST_PART1 = "127.0.0.";
	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Deployment
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = ArquillianBase.createTestArchive(WEB_ARCHIVE_NAME + ".war");

		webArchive.addClasses(CuratorFrameworkProducerTestIT.class).addClass(AppZookeeperException.class)
				.addClasses(CuratorFrameworkProducer.class, CuratorFrameworkQualifier.class)
				.addClasses(ManagedConcurrencyProducer.class, ManagedThreadFactoryQualifier.class)
				.addClasses(EnvConfigKeys.class, EnvConfigQualifier.class).addClasses(EnvConfigProducer.class)
				.addClasses(ZookeeperServiceRegistryProducer.class, ZookeeperServiceRegistryQualifier.class,
						InstanceMetaData.class)
				.addClasses(ZookeeperServiceRegistryProvider.class)
				.addClasses(ZookeeperServiceDiscoveryProvider.class, LockInterceptor.class, LockQualifier.class)
				.addClasses(ZookeeperConfigAccessor.class, ZookeeperConfigAccessorImpl.class)
				.addClass(SystemDefaultDnsResolver.class).addAsResource("zookeeper.properties");

		LOGGER.info(LOG_PREFIX + "archiveContent: " + webArchive.toString(true));

		return webArchive;
	}

	@Inject
	private ZookeeperServiceDiscoveryProvider zookeeperServiceDiscoveryProvider;
	@Inject
	private ZookeeperServiceRegistryProvider zookeeperServiceRegistryProvider;
	@Inject
	private ZookeeperConfigAccessor zookeeperConfigAccessor;

	@Before
	public void setup() {
		LOGGER.debug(LOG_PREFIX + "setup");

		assertThat(this.zookeeperServiceDiscoveryProvider, notNullValue());
		assertThat(this.zookeeperServiceRegistryProvider, notNullValue());

		// Register some service.
		for (int i = 0; i < SERVICE_INSTANCE_COUNT; i++) {
			this.zookeeperServiceRegistryProvider
					.registerService(this.getInstanceMetaData(this.HOST_PART1 + i, SERVICE_NAME));
		}

		// Check if services are registered.
		final Collection<ServiceInstance<InstanceMetaData>> serviceInstanceCollection = this.zookeeperServiceRegistryProvider
				.getService(SERVICE_NAME);
		assertThat(serviceInstanceCollection.size(), is(equalTo(SERVICE_INSTANCE_COUNT)));
	}

	@After
	public void cleanup() {
		LOGGER.debug(LOG_PREFIX + "cleanup");

		this.zookeeperServiceRegistryProvider.unregisterService(SERVICE_NAME);

		this.zookeeperConfigAccessor.deleteNodeWithChildrenIfNeeded("/account-microservice");
	}

	/**
	 * Create an initialized {@link InstanceMetaData} object.
	 * 
	 * @param host
	 *            the registered name of the service.
	 * @return an initialized object.
	 */
	private InstanceMetaData getInstanceMetaData(final String host, final String serviceName) {
		LOGGER.debug(LOG_PREFIX + "getInstanceMetaData [host=" + host + "]");

		// Metadata for service registration.
		final InstanceMetaData instanceMetaData = InstanceMetaData.hostAdress(host).port(DEFAULT_PORT)
				.serviceName(serviceName).contextRoot(WEB_ARCHIVE_NAME).applicationPath(serviceName)
				.description("my_description").build();

		return instanceMetaData;
	}

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceDiscoveryProviderTestIT#test010_serviceDiscoveryProvider_Pass
	 * 
	 * <b><code>test010_serviceDiscoveryProvider_Pass</code>:</b><br>
	 * 	Tests the configured round-robin strategy.
	 *
	 * <b>Preconditions:</b><br>
	 *	- The artifact is successful deployed in wildfly and the server is running.
	 *	- Zookeeper is started.
	 *	- Count of SERVICE_INSTANCE_COUNT services are registered in zookeeper.
	 *	- The registered services are configured with the round-robin strategy.
	 *	- The zookeeperServiceDiscoveryProvider is successful injected.
	 *
	 * <b>Scenario:</b><br>
	 * 	The following steps are executed:
	 * 	- zookeeperServiceDiscoveryProvider is invoked for the next service instance.
	 *
	 * <b>Postconditions:</b><br>
	 *	The service instances are invoked in the right order 0 ... SERVICE_INSTANCE_COUNT.
	 * </pre>
	 */
	@Test
	@InSequence(10)
	public void test010_serviceDiscoveryProvider_Pass() {
		LOGGER.debug(LOG_PREFIX + "test010_serviceDiscoveryProvider_Pass");

		// Order is not ranked by the hostaddress, e.g. 127.0.0.0, 127.0.0.1,
		// 127.0.0.2, ... , but is sorted in itself.
		// So during every iteration a fix position is checked. The checked
		// position is 5.
		String checkedAddressPart = null;

		for (int k = 0; k < 10; k++) {

			for (int i = 0; i < 10; i++) {
				final ServiceInstance<InstanceMetaData> serviceInstance = this.zookeeperServiceDiscoveryProvider
						.getServiceInstance(SERVICE_NAME);

				if ((i - 1) == 5) {
					// The configured failover policy is 'RoundRobinStrategy'.
					final String address = serviceInstance.getAddress();
					final String[] splittedAddress = address.split("\\.");
					final String splittedAddressPart = splittedAddress[splittedAddress.length - 1];

					if (checkedAddressPart != null) {
						assertThat(Integer.parseInt(checkedAddressPart),
								is(equalTo(Integer.parseInt(splittedAddressPart))));
					}

					checkedAddressPart = splittedAddressPart;
				}
			} // end for i
		} // end for k
	}

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceDiscoveryProviderTestIT#test020_serviceDiscoveryProviderParallelServices_Pass
	 * 
	 * <b><code>test020_serviceDiscoveryProviderParallelServices_Pass</code>:</b><br>
	 * Tests the parallel access to the zookeeperServiceDiscoveryProvider (threadsafe), which is used for service discovery.
	 *
	 * <b>Preconditions:</b><br>
	 *	- The artifact is successful deployed in wildfly and the server is running.
	 *	- Zookeeper is started.
	 *	- Count of SERVICE_INSTANCE_COUNT services are registered in zookeeper.
	 *	- The registered services using the round-robin strategy.
	 *	- The zookeeperServiceDiscoveryProvider is successful injected.
	 *
	 * <b>Scenario:</b><br>
	 * 	The following steps are executed:
	 * 	- Create a thread pool using all available processors as its target parallelism level.
	 *	- Create a list with {@link Callable}s that holds the zookeeperServiceDiscoveryProvider that returns the requested ServiceInstance.
	 *	- Start the executer with the {@link Callable}s.
	 *	- Check every 10 return ServiceInstance.
	 *
	 * <b>Postconditions:</b><br>
	 *	The ServiceInstance must be always the same.
	 * </pre>
	 */
	@Test
	@InSequence(20)
	public void test020_serviceDiscoveryProviderParallelServices_Pass() throws InterruptedException {
		LOGGER.debug(LOG_PREFIX + "test020_serviceDiscoveryProviderParallelServices_Pass");

		// TODO: warum legst du nochmals welche an? SInd doch schon welche registriert?
//		for (int k = 0; k < 2; k++) {
//			final String newServiceName = SERVICE_NAME + "-" + k;
//
//			// Register some service.
//			for (int i = 0; i < SERVICE_INSTANCE_COUNT; i++) {
//				this.zookeeperServiceRegistryProvider
//						.registerService(this.getInstanceMetaData(this.HOST_PART1 + i, newServiceName));
//			}
//		}

		//
		// Start parallel access to service discovery to check the concurrent
		// access to the serviceDisveryProvider.
		//
		// Creates a thread pool using all available processors as its target
		// parallelism level.
		final ExecutorService executor = Executors.newWorkStealingPool();
		//
		// First create some Callables, that will be performed parallel in next
		// step.
		final List<Callable<ServiceInstance<InstanceMetaData>>> callables = new ArrayList<>();

		for (int i = 0; i < (SERVICE_INSTANCE_COUNT * 1); i++) {
			int counter = 0;
			if ((i % 2) == 0) {
				counter++;
			}

			final String newServiceName = SERVICE_NAME + "-" + counter;

			// final Callable<ServiceInstance<InstanceMetaData>> task = () ->
			// this.zookeeperServiceDiscoveryProvider
			// .getServiceInstance(newServiceName);
			final ServiceRegistryProviderCallable task = new ServiceRegistryProviderCallable(newServiceName,
					zookeeperServiceDiscoveryProvider);

			callables.add(task);
		} // end for

		final AtomicInteger j = new AtomicInteger();
		final Set<String> addressSet = new HashSet<>(1);
		//
		// Now run it in a loop, all created Callables will processed parallel.
		for (int i = 0; i < 10; i++) {
			//
			// Invoke all callables parallel.
			executor.invokeAll(callables).stream().map(future -> {
				try {
					return future.get();
				} catch (Exception e) {
					LOGGER.error(
							LOG_PREFIX + "test020_serviceDiscoveryProviderParallelServices_Pass#executor - failed!", e);
					throw new IllegalStateException(e);
				}
			}).forEach(serviceInstance -> {
				// Test here the order of the invocation, only one element
				// should be added to the set.
				LOGGER.debug(LOG_PREFIX
						+ "test020_serviceDiscoveryProviderParallelServices_Pass#executor - Callable finished [newServiceName="
						+ serviceInstance.getName() + ", hostAddress=" + serviceInstance.getAddress() + "]");

				if (serviceInstance.getName().equals(SERVICE_NAME + "-" + 0)) {
					if ((j.getAndIncrement() % 10) == 0) {
						addressSet.add(serviceInstance.getPayload().getHostAdress());
					}
				}
			});
		} // end for

		assertThat(addressSet.size(), lessThanOrEqualTo(3));
	}

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceDiscoveryProviderTestIT#test030_serviceDiscoveryProviderNullValue_Fail
	 * 
	 * <b><code>test030_serviceDiscoveryProviderNullValue_Fail</code>:</b><br>
	 * Tests the behavior if the ServiceName is null-String.
	 *
	 * <b>Preconditions:</b><br>
	 *	- The artifact is successful deployed in wildfly and the server is running.
	 *	- Zookeeper is started.
	 *	- Count of SERVICE_INSTANCE_COUNT services are registered in zookeeper.
	 *	- The zookeeperServiceDiscoveryProvider is successful injected.
	 *
	 * <b>Scenario:</b><br>
	 * 	The following steps are executed:
	 * 	- Invoke the zookeeperServiceDiscoveryProvider with a null-String.
	 *
	 * <b>Postconditions:</b><br>
	 *	A <code>ConstraintViolationException</code> is thrown.
	 * </pre>
	 */
	@Test
	@InSequence(30)
	public void test030_serviceDiscoveryProviderNullValue_Fail() {
		LOGGER.debug(LOG_PREFIX + "test030_serviceDiscoveryProviderNullValue_Fail");

		this.thrown.expect(ConstraintViolationException.class);

		this.zookeeperServiceDiscoveryProvider.getServiceInstance(null);
	}

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceDiscoveryProviderTestIT#test040_serviceDiscoveryProviderEmptyValue_Fail
	 * 
	 * <b><code>test040_serviceDiscoveryProviderEmptyValue_Fail</code>:</b><br>
	 * Tests the behavior if the ServiceName is empty-String.
	 *
	 * <b>Preconditions:</b><br>
	 *	- The artifact is successful deployed in wildfly and the server is running.
	 *	- Zookeeper is started.
	 *	- Count of SERVICE_INSTANCE_COUNT services are registered in zookeeper.
	 *	- The zookeeperServiceDiscoveryProvider is successful injected.
	 *
	 * <b>Scenario:</b><br>
	 * 	The following steps are executed:
	 * 	- Invoke the zookeeperServiceDiscoveryProvider with a empty-String.
	 *
	 * <b>Postconditions:</b><br>
	 *	A <code>ConstraintViolationException</code> is thrown.
	 * </pre>
	 */
	@Test
	@InSequence(40)
	public void test040_serviceDiscoveryProviderEmptyValue_Fail() {
		LOGGER.debug(LOG_PREFIX + "test040_serviceDiscoveryProviderEmptyValue_Fail");

		this.thrown.expect(ConstraintViolationException.class);

		this.zookeeperServiceDiscoveryProvider.getServiceInstance("");
	}

	// __________________________________________________
	// Inner class.
	// --------------------------------------------------

	/**
	 * This callable provides functionality to invoke the
	 * {@link ZookeeperServiceRegistryProvider} parallel.
	 */
	class ServiceRegistryProviderCallable implements Callable<ServiceInstance<InstanceMetaData>> {

		private final String serviceName;
		// @Inject
		private final ZookeeperServiceDiscoveryProvider zookeeperServiceDiscoveryProvider;

		/**
		 * Creates a new instance of
		 * <code>ServiceRegistryProviderCallable</code>
		 * 
		 * @param serviceName
		 *            the given service name to invoke.
		 */
		public ServiceRegistryProviderCallable(final String serviceName,
				final ZookeeperServiceDiscoveryProvider zookeeperServiceDiscoveryProvider) {
			this.serviceName = serviceName;
			this.zookeeperServiceDiscoveryProvider = zookeeperServiceDiscoveryProvider;
		}

		@Override
		public ServiceInstance<InstanceMetaData> call() throws Exception {
			return zookeeperServiceDiscoveryProvider.getServiceInstance(this.serviceName);
		}
	} // end inner class.
}
