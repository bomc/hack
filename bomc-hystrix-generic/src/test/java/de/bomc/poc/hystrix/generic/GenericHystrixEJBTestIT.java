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
package de.bomc.poc.hystrix.generic;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.hystrix.boot.HystrixBootstrapSingletonEJB;
import de.bomc.poc.hystrix.boot.strategy.WildflyHystrixConcurrencyStrategy;
import de.bomc.poc.hystrix.generic.arq.ArquillianBase;
import de.bomc.poc.hystrix.generic.mock.GenericTestServiceEJB;
import de.bomc.poc.hystrix.generic.mock.HystrixGenericParameterDTO;
import de.bomc.poc.hystrix.generic.mock.HystrixGenericResponseDTO;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests the retry/timeout mechanism.
 * <p>
 * 
 * <pre>
 * mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
@RunWith(Arquillian.class)
public class GenericHystrixEJBTestIT extends ArquillianBase {

	private static final String LOG_PREFIX = "GenericHystrixEJBTestIT#";
	private static final String WEB_ARCHIVE_NAME = "bomc-generic-hystrix";
	private static final String TEST_TYPE_PASS = "pass";
	private static final String TEST_TYPE_EXCEPTION = "exception";
	private static final String TEST_TYPE_TIMEOUT = "timeout";
	private static final String TEST_TYPE_RETRY = "retry";
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	// 'testable = true', means all the tests are running inside of the
	// container.
	@Deployment(testable = true)
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
		webArchive.addClass(GenericHystrixEJBTestIT.class);
		webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
		webArchive.addPackages(true, "de.bomc.poc.hystrix.generic");
		webArchive.addClasses(GenericTestServiceEJB.class, HystrixGenericParameterDTO.class,
				HystrixGenericResponseDTO.class);
		webArchive.addClasses(HystrixBootstrapSingletonEJB.class, WildflyHystrixConcurrencyStrategy.class);
		webArchive.addAsWebInfResource("META-INF/beans.xml", "beans.xml");

		// Add dependencies
		final ConfigurableMavenResolverSystem resolver = Maven.configureResolver().withMavenCentralRepo(false);

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("com.netflix.hystrix:hystrix-core:jar:?")
				.withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
				.resolve("com.netflix.hystrix:hystrix-metrics-event-stream:jar:?").withTransitivity().asFile());

		webArchive.addAsLibraries(
				resolver.loadPomFromFile("pom.xml").resolve("io.reactivex:rxjava:jar:?").withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:exception-lib-ext:jar:?")
				.withTransitivity().asFile());

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
	private GenericTestServiceEJB genericTestServiceEJB;

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT#test010_checkStrategy_Pass
	 * </pre>
	 */
	@Test
	@InSequence(10)
	public void test010_checkStrategy_Pass() {
		this.logger.debug(LOG_PREFIX + "test010_checkStrategy_Pass");

		final HystrixConcurrencyStrategy concurrencyStrategy = HystrixPlugins.getInstance().getConcurrencyStrategy();

		assertThat(concurrencyStrategy, notNullValue());
		assertThat(concurrencyStrategy, instanceOf(WildflyHystrixConcurrencyStrategy.class));
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT#test020_generic_Pass
	 * </pre>
	 */
	@Test
	@InSequence(20)
	public void test020_generic_Pass() {
		this.logger.debug(LOG_PREFIX + "test020_generic_Pass");

		final HystrixGenericParameterDTO hystrixGenericParameterDTO = new HystrixGenericParameterDTO(
				System.currentTimeMillis(), TEST_TYPE_PASS);

		final HystrixGenericResponseDTO result = this.genericTestServiceEJB.invokeCommand(hystrixGenericParameterDTO);

		this.logger.debug(LOG_PREFIX + "test010_generic_Pass - [result=" + result + "]");

		assertThat(result.getB(), equalTo(TEST_TYPE_PASS));
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT#test030_unknownParameter_Fail
	 * </pre>
	 */
	@Test
	@InSequence(30)
	public void test030_unknownParameter_Fail() throws Exception {
		this.logger.debug(LOG_PREFIX + "test030_unknownParameter_Fail");

		this.thrown.expect(EJBException.class);

		final HystrixGenericParameterDTO hystrixGenericParameterDTO = new HystrixGenericParameterDTO(
				System.currentTimeMillis(), TEST_TYPE_PASS);

		try {
			this.genericTestServiceEJB.invokeCommandWithUnknownParameter(hystrixGenericParameterDTO);
		} catch (final Exception exception) {

		}

		this.genericTestServiceEJB.invokeCommandWithUnknownParameter(hystrixGenericParameterDTO);
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT#test040_withoutHystrixCommand_Pass
	 * </pre>
	 */
	@Test
	@InSequence(40)
	public void test040_withoutHystrixCommand_Pass() {
		this.logger.debug(LOG_PREFIX + "test040_withoutHystrixCommand_Pass");

		final HystrixGenericParameterDTO hystrixGenericParameterDTO = new HystrixGenericParameterDTO(
				System.currentTimeMillis(), TEST_TYPE_PASS);

		final HystrixGenericResponseDTO hystrixGenericResponseDTO = this.genericTestServiceEJB
				.withoutHystrixCommand(hystrixGenericParameterDTO);

		assertThat(hystrixGenericResponseDTO.getB(), equalTo(TEST_TYPE_PASS));
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT#test050_withoutDefaultParameter_Pass
	 * </pre>
	 */
	@Test
	@InSequence(50)
	public void test050_withoutDefaultParameter_Pass() throws Exception {
		this.logger.debug(LOG_PREFIX + "test050_withoutDefaultParameter_Pass");

		final HystrixGenericParameterDTO hystrixGenericParameterDTO = new HystrixGenericParameterDTO(
				System.currentTimeMillis(), TEST_TYPE_PASS);

		this.genericTestServiceEJB.withoutDefaultParameter(hystrixGenericParameterDTO);
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT#test060_parallel_Pass
	 * </pre>
	 */
	@Test
	@InSequence(60)
	public void test060_parallel_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test060_parallel_Pass");

		final int threadCount = 5;
		final int iteration = 3;
		final List<Future<HystrixGenericResponseDTO>> resultList = new ArrayList<Future<HystrixGenericResponseDTO>>(
				threadCount * iteration);

		for (int i = 0; i < 3; i++) {
			final Runner runner = new Runner(TEST_TYPE_PASS, this.genericTestServiceEJB);
			final List<Callable<HystrixGenericResponseDTO>> tasks = Collections.nCopies(threadCount, runner);

			final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
			// Invoke all...
			final List<Future<HystrixGenericResponseDTO>> futures = executorService.invokeAll(tasks);

			resultList.addAll(futures);

			assertThat(threadCount, equalTo(futures.size()));
		}

		assertThat(resultList.size(), equalTo(threadCount * iteration));

		for (final Future<HystrixGenericResponseDTO> future : resultList) {
			assertThat(future.get(), notNullValue());
			this.logger.debug(LOG_PREFIX + "test060_parallel_Pass - Future result is - " + " - " + future.get()
					+ "; And Task done is " + future.isDone());
		}
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT#test070_parallel_rejected_Pass
	 * </pre>
	 */
	@Test
	@InSequence(70)
	public void test070_parallel_rejected_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test070_parallel_rejected_Pass");

		final int threadCount = 15;
		final int iteration = 3;
		final List<Future<HystrixGenericResponseDTO>> resultList = new ArrayList<Future<HystrixGenericResponseDTO>>(
				threadCount * iteration);

		for (int i = 0; i < iteration; i++) {
			final Runner runner = new Runner(TEST_TYPE_PASS, this.genericTestServiceEJB);
			final List<Callable<HystrixGenericResponseDTO>> tasks = Collections.nCopies(threadCount, runner);

			final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
			// Invoke all...
			final List<Future<HystrixGenericResponseDTO>> futures = executorService.invokeAll(tasks);

			resultList.addAll(futures);

			assertThat(threadCount, equalTo(futures.size()));
		}

		boolean flag = true;
		for (final Future<HystrixGenericResponseDTO> aResultList : resultList) {

			try {
				final Future<HystrixGenericResponseDTO> future = aResultList;

				future.get(); // Will throw a exception.
			} catch (final ExecutionException excEx) {
				flag = false;
				break;
			}
		}

		assertThat(flag, equalTo(false));
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT#test080_timeout_Fail
	 * </pre>
	 */
	@Test
	@InSequence(80)
	public void test080_timeout_Pass() {
		this.logger.debug(LOG_PREFIX + "test080_timeout_Fail");

		for (int i = 0; i < 20; i++) {
			this.thrown.expect(EJBException.class);

			final HystrixGenericParameterDTO hystrixGenericParameterDTO = new HystrixGenericParameterDTO(
					System.currentTimeMillis(), TEST_TYPE_TIMEOUT);

			final HystrixGenericResponseDTO result = this.genericTestServiceEJB
					.invokeCommand(hystrixGenericParameterDTO);

			assertThat(result, nullValue());
		}
	}

	/**
	 * Tests short-circuited.
	 * <p>
	 * 
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT#test100_parallel_Pass
	 * </pre>
	 */
	@Test
	@InSequence(100)
	public void test100_parallel_Pass() throws Exception {
		this.logger.info(LOG_PREFIX + "test100_parallel_Pass");

		final int threadCount = 25;
		final int iteration = 3;
		final List<Future<HystrixGenericResponseDTO>> resultList = new ArrayList<Future<HystrixGenericResponseDTO>>(
				threadCount * iteration);

		for (int i = 0; i < iteration; i++) {
			final Runner runner = new Runner(TEST_TYPE_EXCEPTION, this.genericTestServiceEJB);
			final List<Callable<HystrixGenericResponseDTO>> tasks = Collections.nCopies(threadCount, runner);

			final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
			// Invoke all...
			final List<Future<HystrixGenericResponseDTO>> futures = executorService.invokeAll(tasks);

			resultList.addAll(futures);

			TimeUnit.MILLISECONDS.sleep(15);
			assertThat(threadCount, equalTo(futures.size()));
		}
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT#test110_commandActionExceutionException_Fail
	 * </pre>
	 */
	@Test
	@InSequence(110)
	public void test110_commandActionExceutionException_Fail() {
		this.logger.debug(LOG_PREFIX + "test110_commandActionExceutionException_Fail");

		this.thrown.expect(EJBException.class);

		final HystrixGenericParameterDTO hystrixGenericParameterDTO = new HystrixGenericParameterDTO(
				System.currentTimeMillis(), TEST_TYPE_PASS);

		this.genericTestServiceEJB.invokeCommandActionExceutionException(hystrixGenericParameterDTO);
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT#test120_retry_Fail
	 * </pre>
	 */
	@Test
	@InSequence(120)
	public void test120_retry_Pass() {
		this.logger.debug(LOG_PREFIX + "test120_retry_Fail");

		this.thrown.expect(EJBException.class);

		final HystrixGenericParameterDTO hystrixGenericParameterDTO = new HystrixGenericParameterDTO(
				System.currentTimeMillis(), TEST_TYPE_RETRY);

		this.genericTestServiceEJB.invokeCommandRetryException(hystrixGenericParameterDTO);
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT#test130_retry_withDefaultConfiguration_Fail
	 * </pre>
	 */
	@Test
	@InSequence(130)
	public void test130_retry_withDefaultConfiguration_Fail() {
		this.logger.debug(LOG_PREFIX + "test110_retry_withDefaultConfiguration_Fail");

		this.thrown.expect(EJBException.class);

		final HystrixGenericParameterDTO hystrixGenericParameterDTO = new HystrixGenericParameterDTO(
				System.currentTimeMillis(), TEST_TYPE_RETRY);

		this.genericTestServiceEJB.invokeCommandRetryExceptionWithDefaultConfig(hystrixGenericParameterDTO);
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT#test140_handle_RetryException_without_retry_configured_Fail
	 * </pre>
	 */
	@Test
	@InSequence(140)
	public void test140_handle_RetryException_without_retry_configured_Fail() {
		this.logger.debug(LOG_PREFIX + "test140_handle_RetryException_without_retry_configured_Fail");

		this.thrown.expect(EJBException.class);

		final HystrixGenericParameterDTO hystrixGenericParameterDTO = new HystrixGenericParameterDTO(
				System.currentTimeMillis(), TEST_TYPE_RETRY);

		this.genericTestServiceEJB.invokeCommandWithoutRetry(hystrixGenericParameterDTO);
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=GenericHystrixEJBTestIT#test150_unwrap_ejbException_Pass
	 * </pre>
	 */
	@Test
	@InSequence(150)
	public void test150_unwrap_ejbException_Pass() {
		this.logger.debug(LOG_PREFIX + "test150_unwrap_ejbException_Pass");

		final HystrixGenericParameterDTO hystrixGenericParameterDTO = new HystrixGenericParameterDTO(
				System.currentTimeMillis(), TEST_TYPE_EXCEPTION);

		try {
			this.genericTestServiceEJB.invokeCommandWithoutRetry(hystrixGenericParameterDTO);
		} catch (final EJBException eEx) {
			this.logger.error(
					LOG_PREFIX + "test150_unwrap_ejbException_Pass [exception.message=" + eEx.getMessage() + "]");

			final AppRuntimeException cEx = (AppRuntimeException) eEx.getCause();

			assertThat(cEx, instanceOf(AppRuntimeException.class));
		}
	}

	// _______________________________________________
	// Inner classes
	// -----------------------------------------------
	public class Runner implements Callable<HystrixGenericResponseDTO> {

		private final String type;
		private final GenericTestServiceEJB genericTestServiceEJB;

		public Runner(final String type, final GenericTestServiceEJB genericTestServiceEJB) {
			this.type = type;
			this.genericTestServiceEJB = genericTestServiceEJB;
		}

		@Override
		public HystrixGenericResponseDTO call() throws Exception {
			GenericHystrixEJBTestIT.this.logger.debug(LOG_PREFIX + "Runner#call");

			final HystrixGenericParameterDTO hystrixGenericParameterDTO = new HystrixGenericParameterDTO(
					System.currentTimeMillis(), this.type);
			final HystrixGenericResponseDTO result = this.genericTestServiceEJB
					.invokeCommand(hystrixGenericParameterDTO);

			GenericHystrixEJBTestIT.this.logger.debug(LOG_PREFIX + "Runner#call - [result=" + result + "]");

			return result;
		}
	}
}
