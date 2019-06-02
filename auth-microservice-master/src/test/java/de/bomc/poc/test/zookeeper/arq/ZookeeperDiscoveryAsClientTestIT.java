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
import de.bomc.poc.test.GlobalArqTestProperties;
import de.bomc.poc.validation.ConstraintViolationMapper;
import de.bomc.poc.zookeeper.ZookeeperStarterSingletonEJB;
import de.bomc.poc.zookeeper.curator.producer.CuratorFrameworkProducer;
import de.bomc.poc.zookeeper.curator.qualifier.CuratorFrameworkQualifier;
import de.bomc.poc.zookeeper.registry.services.ZookeeperServiceRegistry;
import de.bomc.poc.zookeeper.registry.services.qualifier.ZookeeperServicesQualifier;
import de.bomc.poc.zookeeper.InstanceMetaData;
import de.bomc.poc.zookeeper.registry.ServiceRegistry;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * This test the discovery of the microservice.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@RunWith(Arquillian.class)
public class ZookeeperDiscoveryAsClientTestIT extends ArquillianZookeeperBase {

    private static final String LOG_PREFIX = "ZookeeperDiscoveryAsClientTestIT#";
    private static final String WEB_ARCHIVE_NAME = "auth-zookeeper-as-curator-discovery-war";
    // Could not be injected here, because test client is running outside of the container.
    //@Inject
    //@LoggerQualifier(logPrefix = LOG_PREFIX)
    private final static Logger LOGGER = Logger.getLogger(ZookeeperDiscoveryAsClientTestIT.class);
    @Context
    private UriInfo uriInfo;

    @Deployment
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = ArquillianZookeeperBase.createTestArchive(WEB_ARCHIVE_NAME + ".war")
                                                             .addClasses(ZookeeperRegistryTestIT.class, ServiceDiscoveryAgent.class)
                                                             .addClasses(LoggerProducer.class, LoggerQualifier.class)
                                                             .addClasses(AppAuthRuntimeException.class, AppInitializationException.class, AppZookeeperException.class)
                                                             .addClasses(EnvConfigKeys.class, EnvConfigQualifier.class, EnvConfigSingletonEJB.class, EnvConfigProducer.class)
                                                             .addClasses(ZookeeperStarterSingletonEJB.class, ZookeeperServicesQualifier.class, ServiceRegistry.class, ZookeeperServiceRegistry.class,
                                                                 CuratorFrameworkProducer.class, CuratorFrameworkQualifier.class, InstanceMetaData.class)
                                                             .addClasses(ManagedConcurrencyProducer.class, ManagedScheduledExecutorServiceQualifier.class, ManagedThreadFactoryQualifier.class)
                                                             .addClasses(JaxRsActivator.class, VersionRestEndpoint.class, VersionRestEndpointImpl.class, VersionSingletonEJB.class)

                                                             .addPackages(true, ApiError.class.getPackage().getName())
                                                             .addPackages(true, ApiExceptionInterceptor.class.getPackage().getName())
                                                             .addPackages(true, ApiExceptionQualifier.class.getPackage().getName())
                                                             .addPackages(true, WebAuthRuntimeException.class.getPackage().getName())
                                                             .addPackages(true, ConstraintViolationMapper.class.getPackage().getName())

                                                             .addClass(ResteasyClientLogger.class)
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

    /**
     * <pre>
     * _________________________________________________________________________________________________
     * NOTE: Zookeeper must be accessible on 'localhost:2181'.
     *       Further this test runs as 'RunAsClient', this means the tests run outside the container.
     *       So the log messages are not part of the server.log.
     *       Log messages of this test are seen on the console output.
     * <p/>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperDiscoveryAsClientTestIT#test01_zookeeperDiscovery_Pass
     * </pre>
     */
    @Test
    @InSequence(1)
    @RunAsClient
    public void test01_zookeeperDiscovery_Pass() throws Exception {
        LOGGER.debug(LOG_PREFIX + "test01_zookeeperDiscovery_Pass");

        final String rootZnode = "/" + WEB_ARCHIVE_NAME + "/local/node0";
        final CuratorFramework curatorFramework = this.createClient(GlobalArqTestProperties.CONNECTION_STRING, GlobalArqTestProperties.CONNECTION_TIMEOUT_MS, GlobalArqTestProperties.SESSION_TIMEOUT_MS, rootZnode);

        ServiceDiscoveryAgent serviceDiscoveryAgent = null;

        try {
            serviceDiscoveryAgent = new ServiceDiscoveryAgent(curatorFramework, rootZnode);
            final ServiceInstance<InstanceMetaData> instance = serviceDiscoveryAgent.getServiceInstance(JaxRsActivator.APPLICATION_PATH);
            final String uri = instance.buildUriSpec();

            LOGGER.debug(LOG_PREFIX + "test01_zookeeperDiscovery_Pass [uri=" + uri + "]");

            assertThat(instance.getPayload()
                               .getServiceName(), is(equalTo(JaxRsActivator.APPLICATION_PATH)));

            for (int i = 0; i < 3; i++) {
                // Create a rest client and invoke the version endpoint to check the discovery.
                final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(250, TimeUnit.MILLISECONDS)
                                                                         .socketTimeout(250, TimeUnit.MILLISECONDS)
                                                                         .build();
                client.register(new ResteasyClientLogger(LOGGER, true));
                final ResteasyWebTarget webTarget = client.target(uri);
                final VersionRestEndpoint proxy = webTarget.proxy(VersionRestEndpoint.class);
                final Response response = proxy.getVersion();

                if (response != null && response.getStatus() == Response.Status.OK.getStatusCode()) {
                    final String version = response.readEntity(String.class);

                    LOGGER.debug(LOG_PREFIX + "test01_zookeeperDiscovery_Pass [version=" + version + "]");
                } else {
                    fail("Should not be reached in test cases!");
                }
            }
        } finally {
            serviceDiscoveryAgent.close();
            CloseableUtils.closeQuietly(curatorFramework);
        }
    }

//    /**
//     * Create a new instance of <code>CuratorFramework</code>.
//     * @param connectString       <pre>
//     *                                                                  Running ZooKeeper in standalone mode is convenient for
//     *                                                                  development, and testing. In production ZooKeeper should run
//     *                                                                  in replicated mode. A replicated group of servers in the same
//     *                                                                  application is called a quorum, and in replicated mode, all
//     *                                                                  servers in the quorum have copies of the same configuration
//     *                                                                  file.
//     *                                                                  server.<positive id> = <address1>:<port1>:<port2>[:role];[<curator port address>:]<curator port>
//     *
//     *                                                                  Examples of legal server statements:
//     *                                                                  server.5 = 125.23.63.23:1234:1235;1236
//     *                                                                  server.5 = 125.23.63.23:1234:1235:participant;1236
//     *                                                                  server.5 = 125.23.63.23:1234:1235:observer;1236
//     *                                                                  server.5 = 125.23.63.23:1234:1235;125.23.63.24:1236
//     *                                                                  server.5 = 125.23.63.23:1234:1235:participant;125.23.63.23:1236
//     *                                                                  </pre>
//     * @param connectionTimeoutMs connection timeout
//     * @param sessionTimeoutMs    session timeout
//     * @param rootZnode           Every node in a ZooKeeper tree is referred to as a znode. Znodes maintain a stat structure that includes version numbers for data changes.
//     * @return a initialized and connected zookepper curator.
//     * @throws AppZookeeperException if zookeeper curator initialization failed.
//     */
//    private CuratorFramework createClient(final String connectString, final int connectionTimeoutMs, final int sessionTimeoutMs, final String rootZnode) {
//        LOGGER.debug(
//            "ZookeeperDiscoveryAsClientTestIT#createClient [connectString=" + connectString + ", connectionTimeoutMs=" + connectionTimeoutMs + ", sessionTimeoutMs=" + sessionTimeoutMs + ", rootZnode=" + rootZnode + "]");
//        try {
//            final CuratorFramework
//                client =
//                CuratorFrameworkFactory.builder()
//                                       .connectString(connectString)
//                                       .retryPolicy(new ExponentialBackoffRetry(1000, 1))
//                                       .connectionTimeoutMs(connectionTimeoutMs)
//                                       .sessionTimeoutMs(sessionTimeoutMs)
//                                       .maxCloseWaitMs(3000)
//                                       .build();
//
//            // Start the curator. Most mutator methods will not work until the curator is started.
//            client.start();
//            // Make sure you're connected to zookeeper.
//            client.getZookeeperClient()
//                  .blockUntilConnectedOrTimedOut();
//
//            return client;
//        } catch (InterruptedException ex) {
//            LOGGER.error("ZookeeperDiscoveryAsClientTestIT#createClient - blockUntilConnectedOrTimedOut failed. ", ex);
//
//            throw new AppZookeeperException(ex.getMessage());
//        } catch (Exception ex) {
//            LOGGER.error("ZookeeperDiscoveryAsClientTestIT#createClient - failed. ", ex);
//
//            throw new AppZookeeperException("ZookeeperDiscoveryAsClientTestIT#createClient - failed!");
//        }
//    }
}
