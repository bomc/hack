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
package ${package}.application.basis.jmx;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import ${package}.infrastructure.events.basis.ThresholdEvent;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import ${package}.ArquillianBase;
import ${package}.CategoryBasisIntegrationTestIT;
import ${package}.application.internal.AppErrorCodeEnum;
import ${package}.application.basis.jmx.metrics.JvmMetrics;
import ${package}.application.basis.jmx.metrics.JvmMetricsMBean;
import ${package}.application.basis.jmx.performance.PerformanceEntry;
import ${package}.application.basis.jmx.performance.PerformanceEntryNotifyData;
import ${package}.application.basis.jmx.performance.PerformanceTracking;
import ${package}.application.basis.jmx.performance.PerformanceTrackingMXBean;
import ${package}.application.basis.performance.interceptor.PerformanceTrackingInterceptor;
import ${package}.application.basis.performance.qualifier.PerformanceTrackingQualifier;

/**
 * Tests the jmx monitoring singleton ejb.
 * <p>
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=MonitorControllerSingletonEJBTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@RunWith(Arquillian.class)
@Category(CategoryBasisIntegrationTestIT.class)
public class MonitorControllerSingletonEJBTestIT extends ArquillianBase {

    private static final String WEB_ARCHIVE_NAME = "bomc-monitoring-war";
    private static final String LOG_PREFIX = "MonitorControllerSingletonEJBTestIT${symbol_pound}";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @EJB
    private MonitorControllerSingletonEJB ejb;
    @Inject
    private SutCdiBean sutCdiBean;
    @Inject
    private SimpleThresholdEventReceiver simpleThresholdEventReceiver;

    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchiveWithPerfTracker(WEB_ARCHIVE_NAME);
        webArchive.addClasses(MonitorControllerSingletonEJBTestIT.class, CategoryBasisIntegrationTestIT.class);
        webArchive.addClasses(LoggerProducer.class, LoggerQualifier.class);
        webArchive.addClasses(MonitorControllerSingletonEJB.class, MBeanController.class, AppRuntimeException.class, AppErrorCodeEnum.class, RootName.class, AbstractMBean.class, RootNameMBean.class,
            JmxGaugeMonitor.class, MBeanInfo.class, ThresholdNotificationListener.class, JvmMetrics.class, JvmMetricsMBean.class, ThresholdEvent.class, PerformanceTracking.class, PerformanceTrackingMXBean.class,
            PerformanceEntry.class, PerformanceEntryNotifyData.class, PerformanceTrackingInterceptor.class, PerformanceTrackingQualifier.class, SutCdiBean.class, SimpleThresholdEventReceiver.class);

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

    @Before
    public void setup() {
        this.logger.debug(LOG_PREFIX + "setup");

        assertThat(this.logger, notNullValue());
        assertThat(this.ejb, notNullValue());
        assertThat(this.sutCdiBean, notNullValue());
    }

    @After
    public void cleanup() {
        this.logger.debug(LOG_PREFIX + "cleanup");
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=MonitorControllerSingletonEJBTestIT${symbol_pound}test010_registerMBeans_Pass
     *
     * <b><code>test010_registerMBeans_Pass</code>:</b><br>
     *  Tests the algorithm for determining the average time of a method.
     *  After 100 calls, the algorithm checks whether the method call is 25% above the average value.
     *  If this is the case, a notification is sent.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - 100 invocations on method 'methodUnderTest' are made for warmup and set the average value.
     *  - Next, 50 invocations are made, the 'methodUnderTest' runs with a duration between 100 and 150 ms.
     *  - Notifications will be emitted.
     *
     * <b>Postconditions:</b><br>
     *  One or more notification are emitted.
     * </pre>
     * @throws MalformedObjectNameException
     * @throws ReflectionException
     * @throws MBeanException
     * @throws InstanceNotFoundException
     * @throws AttributeNotFoundException
     */
    @Test
    @InSequence(10)
    public void test010_registerMBeans_Pass() throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
        this.logger.debug(LOG_PREFIX + "test010_registerMBeans_Pass");

        // Warmup, run 100 methods invocations to determine the average time.
        for (int i = 0; i < 100; i++) {
            this.sutCdiBean.methodUnderTest(0);
        }

        for (int i = 0; i < 50; i++) {
            this.sutCdiBean.methodUnderTest((int)getRandomNumberInRange(100, 150));
        }

        final Set<ObjectName>
            queryNameSet =
            ManagementFactory.getPlatformMBeanServer()
                             .queryNames(new ObjectName("*" + RootName.DOMAIN_NAME + ":*"), null);
        final List<ObjectName>
            collect =
            queryNameSet.stream()
                        .filter(queryName -> queryName.toString()
                                                      .contains("PerformanceTracking"))
                        .collect(Collectors.toList());

        final Object
            attributeObject =
            ManagementFactory.getPlatformMBeanServer()
                             .getAttribute(collect.get(0), "EmittedNotifications");
        final String[] notifications = (String[])attributeObject;

        assertThat(notifications.length, greaterThan(0));

        this.logger.debug(LOG_PREFIX + "test01_registerMBeans_pass [" + notifications[0] + "]");
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=MonitorControllerSingletonEJBTestIT${symbol_pound}test020_thresholdEvent_Pass
     *
     * <b><code>test020_thresholdEvent_Pass</code>:</b><br>
     *  Tests the receiving of threshold event from the PerformanceTrackingIntercptor.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - A CDI bean must be registered with a method that observes the 'ThresholdEvent'.
     *  - Run a warmup with 100 method invocations on the sut, to determine the average time.
     *  - Invoke randomly the method with delayed execution time, 20% larger than the average time.
     *  - The CDI bean will receive one or more ThresholdEvent's.
     *
     * <b>Postconditions:</b><br>
     *  - The CDI bean flag 'isEventReceived' is true.
     * </pre>
     * @throws MalformedObjectNameException
     * @throws ReflectionException
     * @throws MBeanException
     * @throws InstanceNotFoundException
     * @throws AttributeNotFoundException
     */
    @Test
    @InSequence(20)
    public void test020_thresholdEvent_Pass() throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
        this.logger.debug(LOG_PREFIX + "test010_registerMBeans_Pass");

        // Warmup, run 100 methods invocations to determine the average time.
        for (int i = 0; i < 100; i++) {
            this.sutCdiBean.methodUnderTest(0);
        }

        for (int i = 0; i < 20; i++) {
            this.sutCdiBean.methodUnderTest((int)getRandomNumberInRange(100, 150));
        }

        assertThat(simpleThresholdEventReceiver.isEventReceived(), equalTo(true));
    }

    /**
     * Defines a value between min and max.
     * @param min the minimum value.
     * @param max the maximum value.
     * @return a random value between min and max.
     */
    private long getRandomNumberInRange(long min, long max) {
        final Random r = new Random();

        return r.longs(min, (max + 1))
                .limit(1)
                .findFirst()
                .getAsLong();
    }
}
