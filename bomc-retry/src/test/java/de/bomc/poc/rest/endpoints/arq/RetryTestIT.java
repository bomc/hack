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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import de.bomc.poc.rest.api.FallbackDTO;
import de.bomc.poc.rest.api.HystrixDTO;
import de.bomc.poc.retry.interceptor.BackOff;
import de.bomc.poc.retry.interceptor.BackOffExecution;
import de.bomc.poc.retry.interceptor.ExponentialBackOff;
import de.bomc.poc.retry.interceptor.RetryHandlerInterceptor;
import de.bomc.poc.retry.qualifier.RetryPolicy;
import de.bomc.poc.retry.timeout.TimeoutSingletonEJB;

/**
 * Tests the retry/timeout mechanism.
 * 
 * <pre>
 *     mvn clean install -Parq-wildfly-remote -Dtest=RetryWithMockSleepInterceptorTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@RunWith(Arquillian.class)
public class RetryTestIT extends ArquillianBase {
	private static final String LOG_PREFIX = "RetryTestIT#";
	private static final String WEB_ARCHIVE_NAME = "bomc-retry";
	@Inject
	@LoggerQualifier
	private Logger logger;

	// 'testable = true', means all the tests are running inside of the
	// container.
	@Deployment(testable = true)
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
		webArchive.addClass(RetryTestIT.class);
		webArchive.addClasses(BackOff.class, BackOffExecution.class, ExponentialBackOff.class,
				RetryHandlerInterceptor.class, RetryPolicy.class, TimeoutSingletonEJB.class);
		webArchive.addClasses(MockEJB.class, HystrixDTO.class, FallbackDTO.class);
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
	private MockEJB mockEJB;

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=RetryTestIT#test010_retry_seq_Pass
	 * </pre>
	 */
	@Test
	@InSequence(10)
	public void test010_retry_seq_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test01_retry_Pass");

		this.mockEJB.invokeMethod();
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=RetryTestIT#test020_retry_parallel_Pass
	 * </pre>
	 */
	@Test
	@InSequence(20)
	public void test020_retry_parallel_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test020_retry_parallel_Pass");

		final int threadCount = 10;
		
		final Runner runner = new Runner(this.mockEJB);
		final List<Callable<Void>> tasks = Collections.nCopies(threadCount, runner);
		final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		final List<Future<Void>> futures = executorService.invokeAll(tasks);
		final List<Void> resultList = new ArrayList<Void>(futures.size());
		// Check for exceptions.
		for (final Iterator<Future<Void>> iterator = futures.iterator(); iterator.hasNext();) {
			Future<Void> future = (Future<Void>) iterator.next();
			resultList.add(future.get());
		}

		assertThat(threadCount, equalTo(futures.size()));
	}

	// _______________________________________________
	// Inner classes
	// -----------------------------------------------

	public class Runner implements Callable<Void> {

		private final MockEJB runnerMockEJB;

		public Runner(final MockEJB runnerMockEJB) {
			this.runnerMockEJB = runnerMockEJB;
		}

		@Override
		public Void call() throws Exception {
			logger.debug(LOG_PREFIX + "Runner#call");

			if (runnerMockEJB == null) {
				logger.debug(LOG_PREFIX + "Runner#call - injection of EJB is null.");

			}

			runnerMockEJB.invokeMethod();

			return null;
		}

	}
}
