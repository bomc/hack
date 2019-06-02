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
import de.bomc.poc.zk.services.registry.producer.ZookeeperServiceRegistryProducer;
import de.bomc.poc.zk.services.registry.qualifier.ZookeeperServiceRegistryQualifier;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This class tests creating the  {@link ZookeeperServiceRegistryProducer}.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
@RunWith(Arquillian.class)
public class ZookeeperServiceRegistryProducerTestIT extends ArquillianBase {

    private static final Logger LOGGER = Logger.getLogger(ZookeeperServiceRegistryProducerTestIT.class);
    private static final String LOG_PREFIX = "ZookeeperServiceRegistryProducerTestIT#";
    private static final String WEB_ARCHIVE_NAME = "service-registry-producer-war";
    // URI of the registered service.
    private URI uri;
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Deployment
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = ArquillianBase.createTestArchive(WEB_ARCHIVE_NAME + ".war");

        webArchive.addClasses(CuratorFrameworkProducerTestIT.class)
                  .addClass(AppZookeeperException.class)
                  .addClasses(CuratorFrameworkProducer.class, CuratorFrameworkQualifier.class)
                  .addClasses(ManagedConcurrencyProducer.class, ManagedThreadFactoryQualifier.class)
                  .addClasses(EnvConfigKeys.class, EnvConfigQualifier.class)
                  .addClasses(EnvConfigProducer.class)
                  .addClasses(ZookeeperServiceRegistryProducer.class, ZookeeperServiceRegistryQualifier.class, InstanceMetaData.class)
                  .addClasses(ZookeeperConfigAccessor.class, ZookeeperConfigAccessorImpl.class)
                  .addClass(SystemDefaultDnsResolver.class)
                  .addAsResource("zookeeper.properties");

        LOGGER.info(LOG_PREFIX + "archiveContent: " + webArchive.toString(true));

        return webArchive;
    }

    @Before
    public void setup() throws URISyntaxException {
        this.uri = this.buildUri(WEB_ARCHIVE_NAME);
    }

    @Inject
    @ZookeeperServiceRegistryQualifier
    private ServiceDiscovery<InstanceMetaData> serviceDiscovery;
    @Inject
    private ZookeeperConfigAccessor zookeeperConfigAccessor;

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceRegistryProducerTestIT#test010_useServiceDiscoveryProducer_Fail
	 * 
	 * <b><code>test010_useServiceDiscoveryProducer_Fail</code>:</b><br>
	 * 	Tests producing a zookeeper client which is wrapped by the CuratorFrameWork using the {@link CuratorFrameworkProducer}.
	 *
	 * <b>Preconditions:</b><br>
	 *
	 * <b>Scenario:</b><br>
	 * 	The following steps are executed:
	 * 	- The artifact is successful deployed in wildfly and the server is running.
	 * 	- The CuratorFramework client is successful injected.</li>
	 *
	 * <b>Postconditions:</b><br>
	 *	The CuratorFramework client is successful injected.
	 * </pre>
	 */
    @Test
    @InSequence(10)
    public void test010_useServiceDiscoveryProducer_Fail() throws Exception {
        LOGGER.debug(LOG_PREFIX + "test010_useServiceDiscoveryProducer_Fail");

        // To ensure zookeeper has no entries, cleanup zookeeper.
        this.zookeeperConfigAccessor.deleteNodeWithChildrenIfNeeded("/account-microservice");

        this.thrown.expect(KeeperException.NoNodeException.class);

        assertThat(this.serviceDiscovery, notNullValue());

        // Throws a Excecption, there is node registered.
        final Collection<String> collection = this.serviceDiscovery.queryForNames();

        assertThat(collection.size(), is(equalTo(0)));
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceRegistryProducerTestIT#test020_useServiceDiscoveryProducer_Pass
     * </pre>
     */
    @Test
    @InSequence(20)
    public void test020_useServiceDiscoveryProducer_Pass() throws Exception {
        LOGGER.debug(LOG_PREFIX + "test020_useServiceDiscoveryProducer_Pass");

        assertThat(this.serviceDiscovery, notNullValue());

        this.serviceDiscovery.registerService(this.getServiceInstance());

        // Check if service is registered.
        final Collection<ServiceInstance<InstanceMetaData>> collection = this.serviceDiscovery.queryForInstances(this.SERVICE_NAME_IN_SERVICE_REGISTRY);

        assertThat(collection, notNullValue());
        assertThat(collection.iterator()
                             .next()
                             .getName(), is(equalTo(this.SERVICE_NAME_IN_SERVICE_REGISTRY)));
    }

    /**
     * @return a initialized ServiceInstance.
     * @throws Exception if creation of ServiceInstance failed.
     */
    private ServiceInstance<InstanceMetaData> getServiceInstance() throws Exception {

        final InstanceMetaData instanceMetaData = this.getInstanceMetaData();

        final ServiceInstance<InstanceMetaData>
            serviceInstance =
            ServiceInstance.<InstanceMetaData>builder()
                           .uriSpec(new UriSpec("{scheme}://{address}:{port}/" + instanceMetaData.getContextRoot() + "/" + instanceMetaData.getApplicationPath() + "/"))
                           .address(instanceMetaData.getHostAdress())
                           .port(instanceMetaData.getPort())
                           .name(instanceMetaData.getServiceName())
                           .payload(instanceMetaData).serviceType(ServiceType.DYNAMIC) // Corresponds to CreateMode.EPHEMERAL
                .build();

        return serviceInstance;
    }

    // ___________________________________________________________________________
    // Helper methods
    // ---------------------------------------------------------------------------

    /**
     * @return a initialized {@link InstanceMetaData} instance.
     */
    private InstanceMetaData getInstanceMetaData() {
        LOGGER.debug(LOG_PREFIX + "getInstanceMetaData [bindAddress=" + this.uri.getHost() + ", port=" + this.uri.getPort() + "]");

        // Metadata for service registration.
        final InstanceMetaData instanceMetaData = InstanceMetaData.hostAdress(this.uri.getHost())
                                                                  .port(this.uri.getPort())
                                                                  .serviceName(this.SERVICE_NAME_IN_SERVICE_REGISTRY)
                                                                  .contextRoot(WEB_ARCHIVE_NAME)
                                                                  .applicationPath(this.SERVICE_NAME_IN_SERVICE_REGISTRY)
                                                                  .description("my_description")
                                                                  .build();

        return instanceMetaData;
    }
}
