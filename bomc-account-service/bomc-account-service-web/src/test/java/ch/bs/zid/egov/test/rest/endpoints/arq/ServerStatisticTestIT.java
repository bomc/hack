package ch.bs.zid.egov.test.rest.endpoints.arq;

import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.service.impl.ServerStatisticsSingletonEJB;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.performance.annotation.Performance;
import org.jboss.arquillian.performance.annotation.PerformanceTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link ServerStatisticsSingletonEJB}.
 * <pre>
 *     mvn clean install -Parq-wildfly-remote -Dtest=ServerStatisticTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@PerformanceTest(resultsThreshold = 10)
@RunWith(Arquillian.class)
public class ServerStatisticTestIT extends ArquillianBase {
    private static final String LOG_PREFIX = "ServerStatisticTestIT#";
    private static final String WEB_ARCHIVE_NAME = "egov-statistic";
    @Inject
    @LoggerQualifier
    private Logger logger;

    // 'testable = true', means all the tests are running inside of the container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClass(ServerStatisticTestIT.class);
        webArchive.addClasses(ServerStatisticsSingletonEJB.class, LoggerQualifier.class, LoggerProducer.class);
        webArchive.addAsWebInfResource("META-INF/beans.xml", "beans.xml");

        System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

        return webArchive;
    }

    @EJB
    ServerStatisticsSingletonEJB ejb;

    /**
     * Test reading start time from app server.
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ServerStatisticTestIT#test01_v1_readStartTime_Pass
     * </pre>
     */
    @Test
    @InSequence(1)
    @Performance(time = 1500)
    public void test01_v1_readStartTime_Pass() {
        this.logger.info(LOG_PREFIX + "test01_v1_readStartTime_Pass");

        final String startDateTime = this.ejb.getDateTimeAsString();

        this.logger.info(LOG_PREFIX + "test01_v1_readStartTime_Pass [startDateTime=" + startDateTime + "]");

        assertThat(startDateTime, is(notNullValue()));
    }
}
