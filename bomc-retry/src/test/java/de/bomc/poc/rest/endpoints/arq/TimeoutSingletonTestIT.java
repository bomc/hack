/**
 * <pre>
 *
 * Last change:
 *
 *  by: $Author$
 *
 *  date: $Date$
 *
 *  revision: $Revision$
 *
 *    © Bomc 2018
 *
 * </pre>
 */
package de.bomc.poc.rest.endpoints.arq;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.retry.timeout.TimeoutSingletonEJB;

/**
 * Tests the {@link TimeoutSingletonTestEJB}.
 * 
 * <pre>
 *	mvn clean install -Parq-wildfly-remote -Dtest=TimeoutSingletonTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@RunWith(Arquillian.class)
public class TimeoutSingletonTestIT extends ArquillianBase {
	private static final String LOG_PREFIX = "TimeoutSingletonTestIT#";
	private static final String WEB_ARCHIVE_NAME = "bomc-timeout";
	@Inject
	@LoggerQualifier
	private Logger logger;

	// 'testable = true', means all the tests are running inside of the
	// container.
	@Deployment(testable = true)
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
		webArchive.addClass(TimeoutSingletonTestIT.class);
		webArchive.addClasses(TimeoutSingletonEJB.class);
		webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
		webArchive.addAsWebInfResource("META-INF/beans.xml", "beans.xml");

		System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

		return webArchive;
	}

	/**
	 * Setup.
	 */
	@Before
	public void setupClass() {
		//
	}

	@EJB
	private TimeoutSingletonEJB timeoutSingletonEJB;

	/**
	 * Test reading available heap from app server.
	 * 
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=TimeoutSingletonTestIT#test01_timeout_Pass
	 * </pre>
	 */
	@Test
	@InSequence(1)
	public void test01_timeout_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test01_timeout_Pass");

		final CountDownLatch timeoutNotifyingLatch1 = this.timeoutSingletonEJB.createSingleActionTimer(500);
		final boolean timeoutInvoked1 = timeoutNotifyingLatch1.await(1000L, TimeUnit.MILLISECONDS);
		this.logger.info(LOG_PREFIX + "test01_timeout_Pass [timeoutInvoked=" + timeoutInvoked1 + "]");
		assertThat("timeoutInvoked1 must be true and not false.", timeoutInvoked1, equalTo(true));
		
		final CountDownLatch timeoutNotifyingLatch2 = this.timeoutSingletonEJB.createSingleActionTimer(1500);
		final boolean timeoutInvoked2 = timeoutNotifyingLatch2.await(1000L, TimeUnit.MILLISECONDS);
		this.logger.info(LOG_PREFIX + "test01_timeout_Pass [timeoutInvoked2=" + timeoutInvoked2 + "]");
		assertThat("timeoutInvoked1 must be false and not true.", timeoutInvoked2, equalTo(false));
	}
}
