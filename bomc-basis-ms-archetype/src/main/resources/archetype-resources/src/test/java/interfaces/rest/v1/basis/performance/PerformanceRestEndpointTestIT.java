#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.interfaces.rest.v1.basis.performance;

import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;
import ${package}.ArquillianBase;
import ${package}.CategoryBasisIntegrationTestIT;
import ${package}.application.basis.jmx.AbstractMBean;
import ${package}.application.basis.jmx.MBeanController;
import ${package}.application.basis.jmx.MBeanInfo;
import ${package}.application.basis.jmx.performance.PerformanceEntry;
import ${package}.application.basis.jmx.performance.PerformanceEntryNotifyData;
import ${package}.application.basis.jmx.performance.PerformanceTracking;
import ${package}.application.basis.jmx.performance.PerformanceTrackingMXBean;
import ${package}.application.util.JaxRsActivator;
import ${package}.interfaces.rest.v1.basis.PerformanceRestEndpoint;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.management.NotificationBroadcaster;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link PerformanceRestEndpointImpl}.
 *
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=PerformanceRestEndpointTestIT
 * </pre>
 */
@RunWith(Arquillian.class)
@Category(CategoryBasisIntegrationTestIT.class)
public class PerformanceRestEndpointTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "PerformanceRestEndpointTestIT${symbol_pound}";
    private static final String WEB_ARCHIVE_NAME = "basis-performance-war";

    @Inject
    @LoggerQualifier
    private Logger logger;
    @Inject
    private MonitorControllerMockEJB monitorControllerMockEJB;
    private ResteasyClient resteasyClient;

    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchiveWithEmptyAssets(WEB_ARCHIVE_NAME);
        // test classes and endpoints
        webArchive.addClasses(PerformanceRestEndpointTestIT.class, CategoryBasisIntegrationTestIT.class);
        webArchive.addClasses(PerformanceRestEndpoint.class, PerformanceRestEndpointImpl.class);
        // MBean basics
        webArchive.addClasses(MBeanController.class, AbstractMBean.class, MBeanInfo.class, NotificationBroadcaster.class);
        // simple singleton mock to ensure availability of performance mbean
        webArchive.addClasses(MonitorControllerMockEJB.class);
        // performance tracking
        webArchive.addClasses(PerformanceTrackingMXBean.class, PerformanceEntryNotifyData.class, PerformanceTracking.class, PerformanceEntry.class);
        // others
        webArchive.addClasses(JaxRsActivator.class, LoggerQualifier.class, LoggerProducer.class, ResteasyClientLogger.class);

        // Add dependencies
        final ConfigurableMavenResolverSystem
                resolver =
                Maven.configureResolver()
                        .withMavenCentralRepo(false);

        // NOTE@MVN:will be changed during mvn project generating.
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                .resolve("de.bomc.poc:exception-lib-ext:jar:?")
                .withTransitivity()
                .asFile());

        System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

        return webArchive;
    }

    /**
     * Setup.
     */
    @Before
    @SuppressWarnings("deprecation")
    public void setupClass() {
        // Create rest client with Resteasy Client Framework.
        resteasyClient = new ResteasyClientBuilder().connectionTTL(DEFAULT_REST_CLIENT_CONNECTION_TTL, TimeUnit.MILLISECONDS)
                .establishConnectionTimeout(DEFAULT_REST_CLIENT_ESTABLISHED_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .socketTimeout(DEFAULT_REST_CLIENT_SOCKET_TIMEOUT, TimeUnit.MILLISECONDS)
                .register(new ResteasyClientLogger(logger, true))
                .build();
        // Make sure that no performance tracking data is recorded before each test.
        monitorControllerMockEJB.getPerformanceTracking().reset();
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=PerformanceRestEndpointTestIT${symbol_pound}test010_v1_readEmpty_Pass
     *
     * <b><code>test010_v1_readEmpty_Pass</code>:</b><br>
     *  This test reads the current performance data and expects an empty response
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successfully deployed in Wildfly.
     *  - No performance data has been tracked.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  Returns an empty response, because nothing has been tracked yet.
     * </pre>
     *
     * @throws URISyntaxException is thrown during URI creation, is not expected.
     */
    @Test
    @InSequence(10)
    public void test010_v1_readEmpty_Pass() throws URISyntaxException {
        String methodPrefix = LOG_PREFIX + "test010_v1_readEmpty_Pass ";
        this.logger.debug(methodPrefix + "[uri=" + this.buildUri(WEB_ARCHIVE_NAME) + "]");

        final PerformanceRestEndpoint proxy = resteasyClient.target(this.buildUri(WEB_ARCHIVE_NAME))
                .proxy(PerformanceRestEndpoint.class);

        try (Response response = proxy.getWorstByAverage()) {
            this.logger.debug(methodPrefix + "[response=" + response + "]");
            assertThat("Response status matches expected", response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
            
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final JsonObject jsonObject = response.readEntity(JsonObject.class);
                this.logger.debug(methodPrefix + "[version=" + jsonObject + "]");
                assertThat("Empty JSON object expected", jsonObject.size(), equalTo(0));
            }
        }
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=PerformanceRestEndpointTestIT${symbol_pound}test020_v1_readWorstByAverage_pass
     *
     * <b><code>test020_v1_readWorstByAverage_pass</code>:</b><br>
     *     This test ready the current performance data and expects a response
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successfully deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  Returns performance data as recorded
     * </pre>
     *
     * @throws URISyntaxException is thrown during URI creation, is not expected.
     */
    @Test
    @InSequence(20)
    public void test020_v1_readWorstByAverageSimple_pass() throws URISyntaxException {
        String methodPrefix = LOG_PREFIX + "test030_v1_readWorstByAverage_pass ";
        this.logger.debug(methodPrefix + "[uri=" + this.buildUri(WEB_ARCHIVE_NAME) + "]");

        // Perform requests so that performance tracking has data - simulate 100 calls per service.
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int nextInt = random.nextInt(10);
            monitorControllerMockEJB.getPerformanceTracking().track("DummyService", "fastMethod", nextInt, "true");
            monitorControllerMockEJB.getPerformanceTracking().track("DummyService", "slowMethod", nextInt * 10, "true");
        }

        final PerformanceRestEndpoint proxy = resteasyClient.target(this.buildUri(WEB_ARCHIVE_NAME))
                .proxy(PerformanceRestEndpoint.class);

        try (Response response = proxy.getWorstByAverage()) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final JsonObject jsonObject = response.readEntity(JsonObject.class);
                this.logger.debug(methodPrefix + "[json=" + jsonObject + "]");

                assertThat("Expected Method", jsonObject.getString("method"), equalTo("slowMethod"));
            }
        }
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=PerformanceRestEndpointTestIT${symbol_pound}test030_v1_readWorstByCount_pass
     *
     * <b><code>test030_v1_readWorstByCount_pass</code>:</b><br>
     *     This test reads the current performance data and expects a response
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successfully deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  Returns performance data as recorded
     * </pre>
     *
     * @throws URISyntaxException is thrown during URI creation, is not expected.
     */
    @Test
    @InSequence(30)
    public void test030_v1_readWorstByCount_pass() throws URISyntaxException {
        String methodPrefix = LOG_PREFIX + "test030_v1_readWorstByCount_pass ";
        this.logger.debug(methodPrefix + "[uri=" + this.buildUri(WEB_ARCHIVE_NAME) + "]");

        // Perform requests so that performance tracking has data - simulate 100 calls per service.
        final Random random = new Random();

        for (int i = 0; i < 100; i++) {
            int nextInt = random.nextInt(10);
            monitorControllerMockEJB.getPerformanceTracking().track("DummyService", "oftenCalledMethod", nextInt, "true");
        
            if (i % 5 == 0) {
                monitorControllerMockEJB.getPerformanceTracking().track("DummyService", "lesserCalledMethod", nextInt * 10, "true");
            }
        } // end for

        final PerformanceRestEndpoint proxy = resteasyClient.target(this.buildUri(WEB_ARCHIVE_NAME))
                .proxy(PerformanceRestEndpoint.class);

        try (Response response = proxy.getWorstByCount()) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final JsonObject jsonObject = response.readEntity(JsonObject.class);
                this.logger.debug(methodPrefix + "[json=" + jsonObject + "]");
                assertThat("Expected Method", jsonObject.getString("method"), equalTo("oftenCalledMethod"));
            }
        }
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=PerformanceRestEndpointTestIT${symbol_pound}test040_v1_readWorstByTime_pass
     *
     * <b><code>test040_v1_readWorstByTime_pass</code>:</b><br>
     *     This test reads the current performance data and expects a response
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successfully deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  Returns performance data as recorded
     * </pre>
     *
     * @throws URISyntaxException is thrown during URI creation, is not expected.
     */
    @Test
    @InSequence(40)
    public void test040_v1_readWorstByTime_pass() throws URISyntaxException {
        String methodPrefix = LOG_PREFIX + "test040_v1_readWorstByTime_pass ";
        this.logger.debug(methodPrefix + "[uri=" + this.buildUri(WEB_ARCHIVE_NAME) + "]");

        // Perform requests so that performance tracking has data - simulate 100 calls per service.
        for (int i = 0; i < 100; i++) {
            monitorControllerMockEJB.getPerformanceTracking().track("DummyService", "oftenCalledFastMethod", i, "true");
           
            if (i % 5 == 0) {
                monitorControllerMockEJB.getPerformanceTracking().track("DummyService", "lesserCalledSlowMethod", i * 10, "true");
            }
        }

        final PerformanceRestEndpoint proxy = resteasyClient.target(this.buildUri(WEB_ARCHIVE_NAME))
                .proxy(PerformanceRestEndpoint.class);

        try (Response response = proxy.getWorstByTime()) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final JsonObject jsonObject = response.readEntity(JsonObject.class);
                this.logger.debug(methodPrefix + "[json=" + jsonObject + "]");

                assertThat("Expected Method", jsonObject.getString("method"), equalTo("lesserCalledSlowMethod"));
            } 
        }
    }
}
