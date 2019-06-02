package de.bomc.poc.zk.test.arq;

import de.bomc.poc.zk.concurrent.producer.ManagedConcurrencyProducer;
import de.bomc.poc.zk.concurrent.qualifier.ManagedThreadFactoryQualifier;
import de.bomc.poc.zk.config.env.EnvConfigKeys;
import de.bomc.poc.zk.config.env.qualifier.EnvConfigQualifier;
import de.bomc.poc.zk.curator.producer.CuratorFrameworkProducer;
import de.bomc.poc.zk.curator.producer.SystemDefaultDnsResolver;
import de.bomc.poc.zk.curator.qualifier.CuratorFrameworkQualifier;
import de.bomc.poc.zk.exception.AppZookeeperException;
import de.bomc.poc.zk.services.InstanceMetaData;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the service registry.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
@RunWith(Arquillian.class)
public class ZookeeperServiceRegistryTestIT extends ArquillianBase {

    private static final Logger LOGGER = Logger.getLogger(ZookeeperServiceRegistryTestIT.class);
    private static final String LOG_PREFIX = "ZookeeperServiceRegistryTestIT#";
    private static final String WEB_ARCHIVE_NAME = "service-registry-war";
    private static final String SERVICE_NAME_IN_SERVICE_REGISTRY = "test/my_service_name_in_registry";
    private static final String SERVICE_NAME_IN_SERVICE_REGISTRY_OTHER = "test/my_service_name_in_registry_other";
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
                  .addClasses(ZookeeperServiceRegistryProvider.class)
                  .addClass(SystemDefaultDnsResolver.class)
                  .addAsResource("zookeeper.properties");

        LOGGER.info(LOG_PREFIX + "archiveContent: " + webArchive.toString(true));

        return webArchive;
    }

    @Inject
    private ZookeeperServiceRegistryProvider zookeeperServiceRegistryProvider;

    @Before
    public void setup() throws URISyntaxException {
        LOGGER.debug(LOG_PREFIX + "setup");

        this.uri = this.buildUri(WEB_ARCHIVE_NAME);

        assertThat(this.zookeeperServiceRegistryProvider, notNullValue());
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceRegistryTestIT#test010_simpleServiceRegistry_Pass
     * </pre>
     */
    @Test
    @InSequence(10)
    public void test010_simpleServiceRegistry_Pass() {
        LOGGER.debug(LOG_PREFIX + "test010_simpleServiceRegistry_Pass");
        //
        // The application is connected to zookeeper, so register services.
        final InstanceMetaData instanceMetaData = this.getInstanceMetaData(SERVICE_NAME_IN_SERVICE_REGISTRY);
        this.zookeeperServiceRegistryProvider.registerService(instanceMetaData);

        // Read service back for assertion.
        final Collection<ServiceInstance<InstanceMetaData>> collection = this.zookeeperServiceRegistryProvider.getService(SERVICE_NAME_IN_SERVICE_REGISTRY);

        try {
            TimeUnit.SECONDS.sleep(100);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        assertThat(collection, notNullValue());
        assertThat(collection.size(), is(equalTo(1)));

        collection.forEach(serviceInstance -> {
            assertThat(serviceInstance.getPort(), is(equalTo(this.uri.getPort())));
            assertThat(serviceInstance.getId(), notNullValue());
            assertThat(serviceInstance.getName(), is(equalTo(SERVICE_NAME_IN_SERVICE_REGISTRY)));
            assertThat(serviceInstance.getPayload(), is(equalTo(instanceMetaData)));
        });

        // Cleanup resources: Unregister the registered service.
        this.unregisterServiceWithAssertion(SERVICE_NAME_IN_SERVICE_REGISTRY);
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceRegistryTestIT#test020_multipleInstanceMetaDataWithSameServiceName_Pass
     * </pre>
     */
    @Test
    @InSequence(20)
    public void test020_multipleInstanceMetaDataWithSameServiceName_Pass() {
        LOGGER.debug(LOG_PREFIX + "test020_multipleInstanceMetaDataWithSameServiceName_Pass");

        //
        // Check that no service is registered.
        final Collection<ServiceInstance<InstanceMetaData>> collection = this.zookeeperServiceRegistryProvider.getService(SERVICE_NAME_IN_SERVICE_REGISTRY);

        assertThat(collection, notNullValue());
        assertThat(collection.size(), is(equalTo(0)));

        //
        // The application is connected to zookeeper, so register services.
        // Try to registering two same objects with same serviceName, this is not allowed.
        this.zookeeperServiceRegistryProvider.registerService(this.getInstanceMetaData(SERVICE_NAME_IN_SERVICE_REGISTRY));
        this.zookeeperServiceRegistryProvider.registerService(this.getInstanceMetaData(SERVICE_NAME_IN_SERVICE_REGISTRY));

        // Read service back for assertion.
        final Collection<ServiceInstance<InstanceMetaData>> instanceMetaDataCollection = this.zookeeperServiceRegistryProvider.getService(SERVICE_NAME_IN_SERVICE_REGISTRY);

        assertThat(instanceMetaDataCollection, notNullValue());
        assertThat(instanceMetaDataCollection.size(), is(equalTo(1)));

        // Cleanup resources: Unregister the registered service.
        this.unregisterServiceWithAssertion(SERVICE_NAME_IN_SERVICE_REGISTRY);
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceRegistryTestIT#test030_multipleInstanceMetaDataWithDifferentServiceName_Pass
     * </pre>
     */
    @Test
    @InSequence(30)
    public void test030_multipleInstanceMetaDataWithDifferentServiceName_Pass() {
        LOGGER.debug(LOG_PREFIX + "test030_multipleInstanceMetaDataWithDifferentServiceName_Pass");

        //
        // Check that no service is registered.
        final Collection<ServiceInstance<InstanceMetaData>> collection = this.zookeeperServiceRegistryProvider.getService(SERVICE_NAME_IN_SERVICE_REGISTRY);

        assertThat(collection, notNullValue());
        assertThat(collection.size(), is(equalTo(0)));

        //
        // The application is connected to zookeeper, so register services.
        this.zookeeperServiceRegistryProvider.registerService(this.getInstanceMetaData(SERVICE_NAME_IN_SERVICE_REGISTRY));
        this.zookeeperServiceRegistryProvider.registerService(this.getInstanceMetaData(SERVICE_NAME_IN_SERVICE_REGISTRY_OTHER));

        // Read service SERVICE_NAME_IN_SERVICE_REGISTRY back for assertion.
        Collection<ServiceInstance<InstanceMetaData>> instanceMetaDataCollection = this.zookeeperServiceRegistryProvider.getService(SERVICE_NAME_IN_SERVICE_REGISTRY);
        assertThat(instanceMetaDataCollection, notNullValue());
        assertThat(instanceMetaDataCollection.size(), is(equalTo(1)));
        // Read service SERVICE_NAME_IN_SERVICE_REGISTRY_OTHER back for assertion.
        instanceMetaDataCollection = this.zookeeperServiceRegistryProvider.getService(SERVICE_NAME_IN_SERVICE_REGISTRY_OTHER);
        assertThat(instanceMetaDataCollection, notNullValue());
        assertThat(instanceMetaDataCollection.size(), is(equalTo(1)));
        //
        // Cleanup resources: Unregister the registered service.
        this.unregisterServiceWithAssertion(SERVICE_NAME_IN_SERVICE_REGISTRY);
        // Cleanup resources: Unregister the registered service.
        this.unregisterServiceWithAssertion(SERVICE_NAME_IN_SERVICE_REGISTRY_OTHER);
    }

    /*
    * <pre>
    *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceRegistryTestIT#test040_multipleInstanceMetaDataWithSameServiceNameDifferentURI_Pass
    * </pre>
    */
    @Test
    @InSequence(40)
    public void test040_multipleInstanceMetaDataWithSameServiceNameAndDifferentURI_Pass() {
        LOGGER.debug(LOG_PREFIX + "test040_multipleInstanceMetaDataWithSameServiceNameDifferentURI_Pass");

        //
        // Check that no service is registered with name SERVICE_NAME_IN_SERVICE_REGISTRY.
        Collection<ServiceInstance<InstanceMetaData>> collection = this.zookeeperServiceRegistryProvider.getService(SERVICE_NAME_IN_SERVICE_REGISTRY);
        assertThat(collection, notNullValue());
        assertThat(collection.size(), is(equalTo(0)));
        //
        // Check that no service is registered with name SERVICE_NAME_IN_SERVICE_REGISTRY_OTHER.
        collection = this.zookeeperServiceRegistryProvider.getService(SERVICE_NAME_IN_SERVICE_REGISTRY_OTHER);
        assertThat(collection, notNullValue());
        assertThat(collection.size(), is(equalTo(0)));

        //
        // The application is connected to zookeeper, so register services.
        this.zookeeperServiceRegistryProvider.registerService(this.getInstanceMetaData(SERVICE_NAME_IN_SERVICE_REGISTRY));

        final InstanceMetaData instanceMetaData = InstanceMetaData.hostAdress("127.0.0.2")
                                                                  .port(8280)
                                                                  .serviceName(SERVICE_NAME_IN_SERVICE_REGISTRY)
                                                                  .contextRoot(WEB_ARCHIVE_NAME)
                                                                  .applicationPath(SERVICE_NAME_IN_SERVICE_REGISTRY)
                                                                  .description("my_description")
                                                                  .build();
        this.zookeeperServiceRegistryProvider.registerService(instanceMetaData);

        // Read service back for assertion.
        final Collection<ServiceInstance<InstanceMetaData>> instanceMetaDataCollection = this.zookeeperServiceRegistryProvider.getService(SERVICE_NAME_IN_SERVICE_REGISTRY);
        assertThat(instanceMetaDataCollection, notNullValue());
        assertThat(instanceMetaDataCollection.size(), is(equalTo(2)));

        // Cleanup resources: Unregister the registered service, both registered ServiceInstances will be unregistered and removed.
        this.unregisterServiceWithAssertion(SERVICE_NAME_IN_SERVICE_REGISTRY);
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceRegistryTestIT#test050_readServiceFromRegistryWithoutARegisteringBefore_Pass
     * </pre>
     */
    @Test
    @InSequence(50)
    public void test050_readServiceFromRegistryWithoutARegisteringBefore_Pass() {
        LOGGER.debug(LOG_PREFIX + "test050_readServiceFromRegistryWithoutARegisteringBefore_Pass");

        // Read service back for assertion.
        final Collection<ServiceInstance<InstanceMetaData>> collection = this.zookeeperServiceRegistryProvider.getService(SERVICE_NAME_IN_SERVICE_REGISTRY);

        assertThat(collection, notNullValue());
        assertThat(collection.size(), is(equalTo(0)));
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceRegistryTestIT#test060_registerNullValue_Fail
     * </pre>
     */
    @Test
    @InSequence(60)
    public void test060_registerNullValue_Fail() {
        LOGGER.debug(LOG_PREFIX + "test060_registerNullValue_Fail");

        this.thrown.expect(ConstraintViolationException.class);

        this.zookeeperServiceRegistryProvider.registerService(null);
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceRegistryTestIT#test070_registerUnknownServiceNameValue_Fail
     * </pre>
     */
    @Test
    @InSequence(70)
    public void test070_registerUnknownServiceNameValue_Fail() {
        LOGGER.debug(LOG_PREFIX + "test070_registerUnknownServiceNameValue_Fail");

        this.zookeeperServiceRegistryProvider.getService("unknown");
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceRegistryTestIT#test070_unregisterNullValue_Fail
     * </pre>
     */
    @Test
    @InSequence(70)
    public void test070_unregisterNullValue_Fail() {
        LOGGER.debug(LOG_PREFIX + "test070_unregisterNullValue_Fail");

        this.thrown.expect(ConstraintViolationException.class);

        this.zookeeperServiceRegistryProvider.unregisterService(null);
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceRegistryTestIT#test080_unregisterEmptyServiceName_Fail
     * </pre>
     */
    @Test
    @InSequence(80)
    public void test080_unregisterEmptyServiceName_Fail() {
        LOGGER.debug(LOG_PREFIX + "test080_unregisterEmptyServiceName_Fail");

        this.thrown.expect(ConstraintViolationException.class);

        this.zookeeperServiceRegistryProvider.unregisterService("");
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceRegistryTestIT#test090_getServiceNullValue_Fail
     * </pre>
     */
    @Test
    @InSequence(90)
    public void test090_getServiceNullValue_Fail() {
        LOGGER.debug(LOG_PREFIX + "test090_getServiceNullValue_Fail");

        this.thrown.expect(ConstraintViolationException.class);

        this.zookeeperServiceRegistryProvider.getService(null);
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceRegistryTestIT#test100_getServiceEmptyName_Fail
     * </pre>
     */
    @Test
    @InSequence(100)
    public void test100_getServiceEmptyName_Fail() {
        LOGGER.debug(LOG_PREFIX + "test100_getServiceEmptyName_Fail");

        this.thrown.expect(ConstraintViolationException.class);

        this.zookeeperServiceRegistryProvider.getService("");
    }

    // ___________________________________________________________________________
    // Helper methods
    // ---------------------------------------------------------------------------

    /**
     * Create an initialized {@link InstanceMetaData} object.
     * @param serviceName the registered name of the service.
     * @return an initialized object.
     */
    private InstanceMetaData getInstanceMetaData(final String serviceName) {
        LOGGER.debug(LOG_PREFIX + "getInstanceMetaData [bindAddress=" + this.uri.getHost() + ", port=" + this.uri.getPort() + ", serviceName" + serviceName + "]");

        // Metadata for service registration.
        final InstanceMetaData instanceMetaData = InstanceMetaData.hostAdress(this.uri.getHost())
                                                                  .port(this.uri.getPort())
                                                                  .serviceName(serviceName)
                                                                  .contextRoot(WEB_ARCHIVE_NAME)
                                                                  .applicationPath(serviceName)
                                                                  .description("my_description")
                                                                  .build();

        return instanceMetaData;
    }

    /**
     * Unregister the service from zookeeper.
     * @param serviceName the given serviceName to unregister.
     */
    private void unregisterServiceWithAssertion(final String serviceName) {
        LOGGER.debug(LOG_PREFIX + "unregisterServiceWithAssertion [serviceName=" + serviceName + "]");

        this.zookeeperServiceRegistryProvider.unregisterService(serviceName);

        // Do the assertion.
        final Collection<ServiceInstance<InstanceMetaData>> serviceInstanceCollection = this.zookeeperServiceRegistryProvider.getService(serviceName);
        assertThat(serviceInstanceCollection.size(), is(equalTo(0)));
    }
}
