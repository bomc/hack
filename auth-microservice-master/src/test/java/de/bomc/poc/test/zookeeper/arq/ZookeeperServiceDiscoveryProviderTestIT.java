/**
 * Project: MY_POC
 * <p/>
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
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.zookeeper.arq;

import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.concurrency.producer.ManagedConcurrencyProducer;
import de.bomc.poc.concurrency.qualifier.ManagedScheduledExecutorServiceQualifier;
import de.bomc.poc.concurrency.qualifier.ManagedThreadFactoryQualifier;
import de.bomc.poc.config.EnvConfigKeys;
import de.bomc.poc.config.EnvConfigSingletonEJB;
import de.bomc.poc.config.producer.EnvConfigProducer;
import de.bomc.poc.config.qualifier.EnvConfigQualifier;
import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.exception.app.AppInitializationException;
import de.bomc.poc.exception.app.AppZookeeperException;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.rest.logger.ResteasyClientLogger;
import de.bomc.poc.zookeeper.InstanceMetaData;
import de.bomc.poc.zookeeper.curator.producer.CuratorFrameworkProducer;
import de.bomc.poc.zookeeper.curator.qualifier.CuratorFrameworkQualifier;
import de.bomc.poc.zookeeper.discovery.ServiceDiscoveryProvider;
import de.bomc.poc.zookeeper.discovery.interceptor.LockInterceptor;
import de.bomc.poc.zookeeper.discovery.qualifier.LockQualifier;
import de.bomc.poc.zookeeper.registry.ServiceRegistry;
import de.bomc.poc.zookeeper.registry.services.ZookeeperServiceRegistry;
import de.bomc.poc.zookeeper.registry.services.qualifier.ZookeeperServicesQualifier;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test {@link ServiceDiscoveryProvider} for cocurrent usage.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@RunWith(Arquillian.class)
public class ZookeeperServiceDiscoveryProviderTestIT extends ArquillianZookeeperBase {

    private static final String LOG_PREFIX = "ZookeeperServiceDiscoveryProviderTestIT#";
    private static final String WEB_ARCHIVE_NAME = "auth-zookeeper-sdp-war";
    @Inject
    @LoggerQualifier(logPrefix = LOG_PREFIX)
    private Logger logger;

    @Deployment
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = ArquillianZookeeperBase.createTestArchive(WEB_ARCHIVE_NAME + ".war")
                                                             .addClasses(ZookeeperServiceDiscoveryProviderTestIT.class)
                                                             .addClasses(LoggerProducer.class, LoggerQualifier.class)
                                                             .addClasses(AppAuthRuntimeException.class, AppInitializationException.class, AppZookeeperException.class)
                                                             .addClasses(EnvConfigKeys.class, EnvConfigQualifier.class, EnvConfigSingletonEJB.class, EnvConfigProducer.class)
                                                             .addClasses(ZookeeperServicesQualifier.class, ServiceRegistry.class, ZookeeperServiceRegistry.class,
                                                                 CuratorFrameworkProducer.class, CuratorFrameworkQualifier.class, InstanceMetaData.class, ServiceDiscoveryProvider.class)
                                                             .addClasses(ManagedConcurrencyProducer.class, ManagedScheduledExecutorServiceQualifier.class,
                                                                 ManagedThreadFactoryQualifier.class)
                                                             .addClasses(LockQualifier.class, LockInterceptor.class)
                                                             .addClasses(JaxRsActivator.class)
                                                             .addClass(ResteasyClientLogger.class)
                                                             .addAsWebInfResource(getBeansXmlWithLockInterceptor(), "beans.xml")
                                                             .addAsResource("zookeeper.properties");

        // Add dependencies
        final MavenResolverSystem resolver = Maven.resolver();

        // shared module library

        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("org.apache.curator:curator-framework:jar:?")
//                                          .withMavenCentralRepo(false)
                                          .withTransitivity()
                                          .asFile());
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("org.apache.curator:curator-recipes:jar:?")
//                                          .withMavenCentralRepo(false)
                                          .withTransitivity()
                                          .asFile());
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("org.apache.curator:curator-x-discovery:jar:?")
//                                          .withMavenCentralRepo(false)
                                          .withTransitivity()
                                          .asFile());

        System.out.println("archiveContent: " + webArchive.toString(true));

        return webArchive;
    }

    @Inject
    ServiceDiscoveryProvider serviceDiscoveryProvider;
    @Inject
    @ZookeeperServicesQualifier
    ServiceRegistry serviceRegistry;
    @Inject
    EnvConfigSingletonEJB ejb;

    /**
     * NOTE: Zookeeper must be accessible on 'localhost:2181'.
     * <p/>
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperServiceDiscoveryProviderTestIT#test01_serviceDiscoveryProvider_Pass
     * </pre>
     */
    @Test
    @InSequence(1)
    public void test01_serviceDiscoveryProvider_Pass() throws Exception {
        this.logger.debug(LOG_PREFIX + "test01_serviceDiscoveryProvider_Pass");

        final int serviceCount = 20;

        // Wait for a while, so that the injected CuratorFramework is connected.
        // In the real application the CuratorFramework is injected in a Singleton EJB and is started at startup.
        TimeUnit.SECONDS.sleep(1l);

        assertThat(this.serviceRegistry, notNullValue());
        assertThat(this.serviceRegistry.isConnected(), is(equalTo(true)));
        assertThat(this.ejb, notNullValue());
        assertThat(this.serviceDiscoveryProvider, notNullValue());

        final String znodeBasePath = this.ejb.getZnodeBasePath();

        // Register some uri endpoints. The outcome are two services, which are deployed 10 times for every service. The service names are 'serviceName-1' and 'serviceName-2'.
        final List<InstanceMetaData> list = new ArrayList<>();

        for (int i = 0; i < serviceCount; i++) {
            int counter = 0;
            if ((i % 2) == 0) {
                counter++;
            }

            final String newServiceName = "serviceName-" + counter;

            final InstanceMetaData instanceMetaData = InstanceMetaData.hostAdress("127.0.0." + i)
                                                                      .port(8180)
                                                                      .serviceName(newServiceName)
                                                                      .contextRoot("contextRoot")
                                                                      .applicationPath("applicationPath")
                                                                      .build();

            list.add(instanceMetaData);

            TimeUnit.MILLISECONDS.sleep(15);

            // Invoke the serviceRegistry and register the created services at zookeeper.
            this.serviceRegistry.registerService(instanceMetaData, znodeBasePath);
        } // end for

        Collection<ServiceInstance<InstanceMetaData>> services = this.serviceRegistry.getService(list.iterator()
                                                                                                     .next()
                                                                                                     .getServiceName());
        assertThat(services.size(), is(equalTo(10)));

        //
        // Start parallel access to service discovery to check the concurrent access to the serviceDisveryProvider.
        //
        // Creates a thread pool using all available processors as its target parallelism level.
        final ExecutorService executor = Executors.newWorkStealingPool();
        //
        // First create some Callables, that will be performed parallel in next step.
        final List<Callable<ServiceInstance<InstanceMetaData>>> callables = new ArrayList<>();

        for (int i = 0; i < (serviceCount * 10); i++) {
            int counter = 0;
            if ((i % 2) == 0) {
                counter++;
            }

            final String newServiceName = "serviceName-" + counter;

            final Callable<ServiceInstance<InstanceMetaData>> task = () -> {
                try {
                    ServiceInstance<InstanceMetaData> serviceInstance = this.serviceDiscoveryProvider.getServiceInstance(newServiceName);
                    this.logger.debug(LOG_PREFIX + "test01_serviceDiscoveryProvider_Pass - Discovery [newServiceName=" + newServiceName
                                      + ", hostAddress=" + serviceInstance.getAddress() + "]");

                    return serviceInstance;
                } catch (InterruptedException e) {
                    throw new IllegalStateException("task interrupted", e);
                }
            };

            callables.add(task);
        } // end for

        //
        // Now run it in a loop, all created Callables will processed parallel.
        for (int i = 0; i < 10; i++) {
            //
            // Invoke all callables parallel.
            executor.invokeAll(callables)
                    .stream()
                    .map(future -> {
                        try {
                            final ServiceInstance<InstanceMetaData> serviceInstance = future.get();

                            return serviceInstance;
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    })
                    .forEach(serviceInstance -> this.logger.debug(
                        LOG_PREFIX + "test01_serviceDiscoveryProvider_Pass - Callable finished [newServiceName=" + serviceInstance.getName() + ", hostAddress=" + serviceInstance.getAddress() + "]"));

            TimeUnit.MILLISECONDS.sleep(500L);
        } // end for
    }

    /**
     * _____________________________________________________________________________________________________________________________
     * NOTE:
     * 'serviceDiscoveryProvider' and 'serviceRegistry' should be stopped at the end of the test.
     * Problem, the right way is using {@link org.junit.BeforeClass}, but the instances must be static in this cases.
     * Otherwise the tests stopps with two exceptions during the teardown process.
     * Using the {@link After} means, it is only used for one testcase. So for following testcases the instances are not initialized.
     */
    @After
    public void cleanup() {
        //
        // Prevents exceptions in app-server during teardown of this testclass.
        //
    	if (this.serviceRegistry != null) {
    		this.serviceRegistry.unregisterService();
    	}

    	if (this.serviceDiscoveryProvider != null) {
            this.serviceDiscoveryProvider.close();
        }
    }
}
