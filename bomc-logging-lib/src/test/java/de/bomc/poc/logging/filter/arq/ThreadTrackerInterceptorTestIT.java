package de.bomc.poc.logging.filter.arq;

import de.bomc.poc.logging.filter.mock.MockCdiBean;
import de.bomc.poc.logging.interceptor.ThreadTrackerInterceptor;
import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.logging.qualifier.ThreadTrackerQualifier;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * Kommentar.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 7096 $ $Author: tzdbmm $ $Date: 2016-08-03 14:59:36 +0200 (Mi, 03 Aug 2016) $
 * @since 19.07.2016
 */
@RunWith(Arquillian.class)
public class ThreadTrackerInterceptorTestIT {

    private static final String WEB_ARCHIVE_NAME = "logging-thread-tracker-test";
    /** Logger */
    @Inject
    @LoggerQualifier
    private Logger logger;
    private static final Logger LOGGER = Logger.getLogger(ThreadTrackerInterceptorTestIT.class.getName());
    private static final String LOG_PREFIX = "ThreadTrackerInterceptorTestIT#";

    // NOTE:
    // __________________________________________________________________
    // 'testable = false', means all the tests are running outside of the
    // container.
    @Deployment(testable = true, order = 1)
    public static WebArchive createTestArchive() {
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, WEB_ARCHIVE_NAME + ".war");
        webArchive.addClass(ThreadTrackerInterceptorTestIT.class)
                  .addClasses(LoggerProducer.class, LoggerQualifier.class);
        webArchive.addClasses(ThreadTrackerInterceptor.class, ThreadTrackerQualifier.class);
        webArchive.addClasses(MockCdiBean.class);
        webArchive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        System.out.println("archiveContent: " + webArchive.toString(true));

        return webArchive;
    }

    @Inject
    MockCdiBean mockCdiBean;

    /**
     * Test the {@link ThreadTrackerInterceptor}, to validate the problem, see the JConsole or VisualVM for running threads.
     * <pre>
     * 	mvn clean install -Parq-wildfly-remote -Dtest=ThreadTrackerInterceptorTestIT#test010_ThreadTracking_Pass
     * </pre>
     */
    @Test
    @Ignore("Muss manuell ausgeführt werden, da Testergebnis in VisualVM oder in der JConsole validiert werden muss.")
    @InSequence(10)
    public void test010_ThreadTracking_Pass() {
        LOGGER.debug(LOG_PREFIX + "test010_ThreadTracking_Pass");

        this.mockCdiBean.sayHello();
    }
}
