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
package de.bomc.poc.test.zookeeper.arq;

import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.auth.rest.endpoint.v1.version.VersionRestEndpoint;
import de.bomc.poc.auth.rest.endpoint.v1.version.impl.VersionRestEndpointImpl;
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
import de.bomc.poc.exception.handling.ApiError;
import de.bomc.poc.exception.interceptor.ApiExceptionInterceptor;
import de.bomc.poc.exception.qualifier.ApiExceptionQualifier;
import de.bomc.poc.exception.web.WebAuthRuntimeException;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.os.version.VersionSingletonEJB;
import de.bomc.poc.rest.logger.ResteasyClientLogger;
import de.bomc.poc.validation.ConstraintViolationMapper;
import de.bomc.poc.zookeeper.ZookeeperStarterSingletonEJB;
import de.bomc.poc.zookeeper.curator.producer.CuratorFrameworkProducer;
import de.bomc.poc.zookeeper.curator.qualifier.CuratorFrameworkQualifier;
import de.bomc.poc.zookeeper.discovery.ServiceDiscoveryProvider;
import de.bomc.poc.zookeeper.registry.services.ZookeeperServiceRegistry;
import de.bomc.poc.zookeeper.registry.services.qualifier.ZookeeperServicesQualifier;
import de.bomc.poc.zookeeper.InstanceMetaData;
import de.bomc.poc.zookeeper.registry.ServiceRegistry;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This test the discovery of the microservice.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@RunWith(Arquillian.class)
public class ZookeeperDiscoveryInContainerTestIT extends ArquillianZookeeperBase {

    private static final String LOG_PREFIX = "ZookeeperDiscoveryInContainerTestIT#";
    private static final String WEB_ARCHIVE_NAME = "auth-zookeeper-in-container-discovery-war";
    @Inject
    @LoggerQualifier(logPrefix = LOG_PREFIX)
    private Logger logger;
    @Context
    private UriInfo uriInfo;
    @Inject
    private ServiceDiscoveryProvider serviceDiscoveryProvider;

    @Deployment
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = ArquillianZookeeperBase.createTestArchive(WEB_ARCHIVE_NAME + ".war")
                                                             .addClasses(ZookeeperRegistryTestIT.class, ServiceDiscoveryAgent.class)
                                                             .addClasses(LoggerProducer.class, LoggerQualifier.class)
                                                             .addClasses(AppAuthRuntimeException.class, AppInitializationException.class, AppZookeeperException.class)
                                                             .addClasses(EnvConfigKeys.class, EnvConfigQualifier.class, EnvConfigSingletonEJB.class, EnvConfigProducer.class)
                                                             .addClasses(ZookeeperStarterSingletonEJB.class, ZookeeperServicesQualifier.class, ServiceRegistry.class, ZookeeperServiceRegistry.class,
                                                                 CuratorFrameworkProducer.class, CuratorFrameworkQualifier.class, InstanceMetaData.class, ServiceDiscoveryProvider.class)
                                                             .addClasses(ManagedConcurrencyProducer.class, ManagedScheduledExecutorServiceQualifier.class, ManagedThreadFactoryQualifier.class)
                                                             .addClasses(JaxRsActivator.class, VersionRestEndpoint.class, VersionRestEndpointImpl.class, VersionSingletonEJB.class)
                                                             .addClass(ResteasyClientLogger.class)

                                                             .addPackages(true, ApiError.class.getPackage().getName())
                                                             .addPackages(true, ApiExceptionInterceptor.class.getPackage().getName())
                                                             .addPackages(true, ApiExceptionQualifier.class.getPackage().getName())
                                                             .addPackages(true, WebAuthRuntimeException.class.getPackage().getName())
                                                             .addPackages(true, ConstraintViolationMapper.class.getPackage().getName())

                                                             .addAsWebInfResource(getBeansXml(), "beans.xml")
                                                             .addAsResource("zookeeper.properties")
                                                             .addAsResource(VersionSingletonEJB.VERSION_PROPERTIES_FILE);

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

    @Before
    public void init() {
        assertThat(serviceDiscoveryProvider, notNullValue());
    }
    /**
     * <pre>
     * _________________________________________________________________________________________________
     * NOTE: Zookeeper must be accessible on 'localhost:2181'.
     * <p/>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperDiscoveryInContainerTestIT#test01_zookeeperDiscovery_Pass
     * </pre>
     */
    @Test
    @InSequence(1)
    public void test01_zookeeperDiscovery_Pass() throws Exception {
        logger.debug(LOG_PREFIX + "test01_zookeeperDiscovery_Pass");

        assertThat(serviceDiscoveryProvider, notNullValue());

        final ServiceInstance<InstanceMetaData> serviceInstance = serviceDiscoveryProvider.getServiceInstance(JaxRsActivator.APPLICATION_PATH);
        assertThat(serviceInstance, notNullValue());

        final String uri = serviceInstance.buildUriSpec();
        logger.debug(LOG_PREFIX + "test01_zookeeperDiscovery_Pass [uri=" + uri + "]");

        assertThat(serviceInstance.getName(), is(equalTo(JaxRsActivator.APPLICATION_PATH)));
        
        serviceDiscoveryProvider.cleanup();
    }
}
