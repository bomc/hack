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
package de.bomc.poc.hystrix.generic.mock;

import com.netflix.hystrix.exception.HystrixBadRequestException;

import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.hystrix.generic.conf.HystrixPropertiesManager;
import de.bomc.poc.hystrix.generic.exeception.RetryException;
import de.bomc.poc.hystrix.generic.interceptor.HystrixInterceptor;
import de.bomc.poc.hystrix.generic.qualifier.HystrixCommand;
import de.bomc.poc.hystrix.generic.qualifier.HystrixProperty;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.concurrent.TimeUnit;

/**
 * A mock service.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
@Interceptors(HystrixInterceptor.class)
public class GenericTestService {

	private static final String LOG_PREFIX = "GenericTestService#";
	@Inject
	@LoggerQualifier
	private Logger logger;

	@HystrixCommand(commandProperties = {
			@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value = "THREAD"),
			@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "1000"),
			@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_TIMEOUT_ENABLED, value = "true"),
			@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_INTERRUPT_ON_TIMEOUT, value = "true"),
			@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value = "10"),
			@HystrixProperty(name = HystrixPropertiesManager.FALLBACK_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value = "10"),
			@HystrixProperty(name = HystrixPropertiesManager.FALLBACK_ENABLED, value = "false"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ENABLED, value = "true"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "20"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "5000"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "50"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_FORCE_OPEN, value = "false"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_FORCE_CLOSED, value = "false"),
			@HystrixProperty(name = HystrixPropertiesManager.METRICS_ROLLING_PERCENTILE_ENABLED, value = "true"),
			@HystrixProperty(name = HystrixPropertiesManager.METRICS_ROLLING_PERCENTILE_TIME_IN_MILLISECONDS, value = "60000"),
			@HystrixProperty(name = HystrixPropertiesManager.METRICS_ROLLING_PERCENTILE_NUM_BUCKETS, value = "6"),
			@HystrixProperty(name = HystrixPropertiesManager.METRICS_ROLLING_PERCENTILE_BUCKET_SIZE, value = "100"),
			@HystrixProperty(name = HystrixPropertiesManager.METRICS_HEALTH_SNAPSHOT_INTERVAL_IN_MILLISECONDS, value = "500"),
			@HystrixProperty(name = HystrixPropertiesManager.REQUEST_CACHE_ENABLED, value = "true"),
			@HystrixProperty(name = HystrixPropertiesManager.REQUEST_LOG_ENABLED, value = "true") })
	public HystrixGenericResponseDTO invokeCommand(final HystrixGenericParameterDTO hystrixGenericParameterDTO) {
		this.logger.debug(LOG_PREFIX + "invokeCommand [hystrixGenericParameterDTO=" + hystrixGenericParameterDTO + "]");

		HystrixGenericResponseDTO hystrixGenericResponseDTO = null;

		switch (hystrixGenericParameterDTO.getB()) {
		case "timeout":
			try {
				TimeUnit.MILLISECONDS.sleep(1500L);
			} catch (final InterruptedException e) {
				// Ignore
			}
			break;
		case "exception":
			//
			throw new RuntimeException("test throwing a exception");
		case "pass":
			//
			hystrixGenericResponseDTO = new HystrixGenericResponseDTO(hystrixGenericParameterDTO.getA(),
					hystrixGenericParameterDTO.getB());
			break;
		case "bad":
			//
			throw new HystrixBadRequestException("bad argument exception!");
		}

		return hystrixGenericResponseDTO;
	}

	@HystrixCommand(groupKey = "bomc-generic-test", commandKey = "invokeCommand", commandProperties = {
			@HystrixProperty(name = HystrixPropertiesManager.FALLBACK_ENABLED, value = "false"),
			@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value = "THREAD"),
			@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_TIMEOUT_ENABLED, value = "true"),
			@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "1000"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ENABLED, value = "true"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "100"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "1") })
	public HystrixGenericResponseDTO invokeCommandActionExceutionException(
			final HystrixGenericParameterDTO hystrixGenericParameterDTO) {
		this.logger.debug(LOG_PREFIX + "invokeCommand [hystrixGenericParameterDTO=" + hystrixGenericParameterDTO + "]");

		throw new AppRuntimeException(LOG_PREFIX + "invokeCommandActionExceutionException",
				ErrorCode.RESILIENCE_10500);
	}

	@HystrixCommand(multiplier = 1.75, maxRetryCount = 3, initialInterval = 100, groupKey = "bomc-generic-test", commandKey = "invokeCommand", commandProperties = {
			@HystrixProperty(name = HystrixPropertiesManager.FALLBACK_ENABLED, value = "false"),
			@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value = "THREAD"),
			@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_TIMEOUT_ENABLED, value = "true"),
			@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "5000"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ENABLED, value = "true"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "1000"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "10") })
	public HystrixGenericResponseDTO invokeCommandRetryException(
			final HystrixGenericParameterDTO hystrixGenericParameterDTO) {
		this.logger.debug(LOG_PREFIX + "invokeCommandRetryException [hystrixGenericParameterDTO="
				+ hystrixGenericParameterDTO + "]");

		throw new RetryException(LOG_PREFIX + "invokeCommandRetryException");
	}

	@HystrixCommand
	public HystrixGenericResponseDTO invokeCommandRetryExceptionWithDefaultConfig(
			final HystrixGenericParameterDTO hystrixGenericParameterDTO) {
		this.logger.debug(LOG_PREFIX + "invokeCommandRetryExceptionWithDefaultConfig [hystrixGenericParameterDTO="
				+ hystrixGenericParameterDTO + "]");

		throw new RetryException(LOG_PREFIX + "invokeCommandRetryExceptionWithDefaultConfig");
	}

	@HystrixCommand(maxRetryCount = -1)
	public HystrixGenericResponseDTO invokeCommandWithoutRetry(
			final HystrixGenericParameterDTO hystrixGenericParameterDTO) {
		this.logger.debug(LOG_PREFIX + "invokeCommandWithoutRetry [hystrixGenericParameterDTO="
				+ hystrixGenericParameterDTO + "]");

		throw new RetryException(LOG_PREFIX + "invokeCommandWithoutRetry");
	}

	@HystrixCommand(groupKey = "bomc-generic-test", commandKey = "invokeCommandWithUnknownParameter", commandProperties = {
			@HystrixProperty(name = "unknown.parameter", value = "false") })
	public HystrixGenericResponseDTO invokeCommandWithUnknownParameter(
			final HystrixGenericParameterDTO hystrixGenericParameterDTO) {
		this.logger.debug(LOG_PREFIX + "invokeCommandWithUnknownParameter [hystrixGenericParameterDTO="
				+ hystrixGenericParameterDTO + "]");

		HystrixGenericResponseDTO hystrixGenericResponseDTO;

		hystrixGenericResponseDTO = new HystrixGenericResponseDTO(hystrixGenericParameterDTO.getA(),
				hystrixGenericParameterDTO.getB());

		return hystrixGenericResponseDTO;
	}

	public HystrixGenericResponseDTO withoutHystrixCommand(
			final HystrixGenericParameterDTO hystrixGenericParameterDTO) {
		this.logger.debug(
				LOG_PREFIX + "withoutHystrixCommand [hystrixGenericParameterDTO=" + hystrixGenericParameterDTO + "]");

		HystrixGenericResponseDTO hystrixGenericResponseDTO;

		hystrixGenericResponseDTO = new HystrixGenericResponseDTO(hystrixGenericParameterDTO.getA(),
				hystrixGenericParameterDTO.getB());

		return hystrixGenericResponseDTO;
	}

	@HystrixCommand(commandProperties = {
			@HystrixProperty(name = HystrixPropertiesManager.FALLBACK_ENABLED, value = "false") })
	public HystrixGenericResponseDTO withoutDefaultParameter(
			final HystrixGenericParameterDTO hystrixGenericParameterDTO) {
		this.logger.debug(
				LOG_PREFIX + "withoutDefaultParameter [hystrixGenericParameterDTO=" + hystrixGenericParameterDTO + "]");

		HystrixGenericResponseDTO hystrixGenericResponseDTO;

		hystrixGenericResponseDTO = new HystrixGenericResponseDTO(hystrixGenericParameterDTO.getA(),
				hystrixGenericParameterDTO.getB());

		return hystrixGenericResponseDTO;
	}
}
