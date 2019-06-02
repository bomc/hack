/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.controller.application.basis.performance.interceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import javax.inject.Inject;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import de.bomc.poc.controller.ArquillianBase;
import de.bomc.poc.controller.CategoryBasisIntegrationTestIT;
import de.bomc.poc.controller.application.internal.AppErrorCodeEnum;
import de.bomc.poc.controller.application.basis.jmx.AbstractMBean;
import de.bomc.poc.controller.application.basis.jmx.JmxGaugeMonitor;
import de.bomc.poc.controller.application.basis.jmx.MBeanController;
import de.bomc.poc.controller.application.basis.jmx.MBeanInfo;
import de.bomc.poc.controller.application.basis.jmx.MonitorControllerSingletonEJB;
import de.bomc.poc.controller.application.basis.jmx.RootName;
import de.bomc.poc.controller.application.basis.jmx.RootNameMBean;
import de.bomc.poc.controller.application.basis.jmx.ThresholdNotificationListener;
import de.bomc.poc.controller.application.basis.jmx.metrics.JvmMetrics;
import de.bomc.poc.controller.application.basis.jmx.metrics.JvmMetricsMBean;
import de.bomc.poc.controller.application.basis.jmx.performance.PerformanceEntry;
import de.bomc.poc.controller.application.basis.jmx.performance.PerformanceEntryNotifyData;
import de.bomc.poc.controller.application.basis.jmx.performance.PerformanceTracking;
import de.bomc.poc.controller.application.basis.jmx.performance.PerformanceTrackingMXBean;
import de.bomc.poc.controller.application.basis.performance.qualifier.PerformanceTrackingQualifier;
import de.bomc.poc.controller.infrastructure.events.basis.ThresholdEvent;

/**
 * Tests the {@link PerformanceTrackingInterceptor} exception handling.
 * <p>
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=PerformanceTrackingInterceptorTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 23.11.2018
 */
@RunWith(Arquillian.class)
@Category(CategoryBasisIntegrationTestIT.class)
public class PerformanceTrackingInterceptorTestIT extends ArquillianBase {

    private static final String WEB_ARCHIVE_NAME = "bomc-performance-tracking-interceptor-war";
    private static final String LOG_PREFIX = "PerformanceTrackingInterceptorTestIT#";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Inject
    private SutCdiBean sutCdiBean;
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchiveWithPerfTracker(WEB_ARCHIVE_NAME);
        webArchive.addClasses(PerformanceTrackingInterceptorTestIT.class, CategoryBasisIntegrationTestIT.class);
        webArchive.addClasses(LoggerProducer.class, LoggerQualifier.class);
        webArchive.addClasses(MonitorControllerSingletonEJB.class, MBeanController.class, RootName.class, AbstractMBean.class, RootNameMBean.class, JmxGaugeMonitor.class, MBeanInfo.class,
            ThresholdNotificationListener.class, ThresholdEvent.class, JvmMetrics.class, JvmMetricsMBean.class, PerformanceTracking.class, PerformanceTrackingMXBean.class, PerformanceEntry.class,
            PerformanceEntryNotifyData.class);
        webArchive.addClasses(PerformanceTrackingQualifier.class, PerformanceTrackingInterceptor.class);
        webArchive.addClasses(AppErrorCodeEnum.class);
        webArchive.addClasses(SutCdiBean.class);

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

        System.out.println("PerformanceTrackingInterceptorTestIT#createDeployment: " + webArchive.toString(true));

        return webArchive;
    }

    @Before
    public void setup() {
        this.logger.debug(LOG_PREFIX + "setup");

        assertThat(this.logger, notNullValue());
        assertThat(this.sutCdiBean, notNullValue());
    }

    @After
    public void cleanup() {
        this.logger.debug(LOG_PREFIX + "cleanup");
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=PerformanceTrackingInterceptorTestIT#test010_performanceTrackingWithAppRuntimeException_fail
     *
     * <b><code>test010_performanceTrackingWithAppRuntimeException_fail</code>:</b><br>
     *  Tests the exception handling for the PerformanceTrackingInterceptor when throwing AppRuntimeException.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The method 'methodUnderTest' with argument and value '42' is invoked.
     *  - A AppRuntimeException is thrown.
     *
     * <b>Postconditions:</b><br>
     *  A AppRuntimeException is expected.
     * </pre>
     * @throws AppRuntimeException
     */
    @Test
    @InSequence(10)
    public void test010_performanceTrackingWithAppRuntimeException_fail() throws AppRuntimeException {
        this.logger.debug(LOG_PREFIX + "test010_performanceTrackingWithAppRuntimeException_fail");

        this.thrown.expect(AppRuntimeException.class);

        this.sutCdiBean.methodUnderTest(SutCdiBean.APP_RUNTIME_EX);
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=PerformanceTrackingInterceptorTestIT#test020_performanceTrackingWithIllegalArgumentException_fail
     *
     * <b><code>test020_performanceTrackingWithIllegalArgumentException_fail</code>:</b><br>
     *  Tests the exception handling for the PerformanceTrackingInterceptor when throwing IllegalArgumentException.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The method 'methodUnderTest' with argument and value '33' is invoked.
     *  - A IllegalArgumentException is thrown.
     *
     * <b>Postconditions:</b><br>
     *  A AppRuntimeException is expected.
     * </pre>
     * @throws AppRuntimeException
     */
    @Test
    @InSequence(20)
    public void test020_performanceTrackingWithIllegalArgumentException_fail() throws AppRuntimeException {
        this.logger.debug(LOG_PREFIX + "test020_performanceTrackingWithIllegalArgumentException_fail");

        this.thrown.expect(AppRuntimeException.class);

        this.sutCdiBean.methodUnderTest(SutCdiBean.ILLEGAL_ARGUMENT_EX);
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=PerformanceTrackingInterceptorTestIT#test030_performanceTrackingWithAppRuntimeExIsLogged_fail
     *
     * <b><code>test030_performanceTrackingWithAppRuntimeExIsLogged_fail</code>:</b><br>
     *  Tests the exception handling for the PerformanceTrackingInterceptor when throwing AppRuntimeException that is logged.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The method 'methodUnderTest' with argument and value '124' is invoked.
     *  - A AppRuntimeException is created and logged.
     *  - The AppRuntimeException is thrown.
     *
     * <b>Postconditions:</b><br>
     *  A AppRuntimeException is expected.
     * </pre>
     * @throws AppRuntimeException
     */
    @Test
    @InSequence(30)
    public void test030_performanceTrackingWithAppRuntimeExIsLogged_fail() throws AppRuntimeException {
        this.logger.debug(LOG_PREFIX + "test030_performanceTrackingWithAppRuntimeExIsLogged_fail");

        this.thrown.expect(AppRuntimeException.class);

        this.sutCdiBean.methodUnderTest(SutCdiBean.APP_RUNTIME_EX_IS_LOGGED);
    }
}
