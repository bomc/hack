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

import java.util.concurrent.TimeUnit;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;

import com.netflix.hystrix.exception.HystrixBadRequestException;

import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.hystrix.generic.conf.HystrixPropertiesManager;
import de.bomc.poc.hystrix.generic.exeception.RetryException;
import de.bomc.poc.hystrix.generic.interceptor.HystrixInterceptor;
import de.bomc.poc.hystrix.generic.qualifier.HystrixCommand;
import de.bomc.poc.hystrix.generic.qualifier.HystrixProperty;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

/**
 * A mock EJB that handles a hystrix invocation.
 *   
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
@Stateless
@Interceptors(HystrixInterceptor.class)
public class GenericTestServiceEJB {

	private static final String LOG_PREFIX = "GenericTestServiceEJB#";

	@Inject
	@LoggerQualifier
	private Logger logger;

	@HystrixCommand(groupKey = "bomc-generic-test", commandKey = "invokeCommand", commandProperties = {
			@HystrixProperty(name = HystrixPropertiesManager.FALLBACK_ENABLED, value = "false"),
			@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value = "THREAD"),
			@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_TIMEOUT_ENABLED, value = "true"),
			@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "1000"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ENABLED, value = "true"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "100"),
			@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "1") })
	public HystrixGenericResponseDTO invokeCommand(final HystrixGenericParameterDTO hystrixGenericParameterDTO) {
		this.logger.debug(LOG_PREFIX + "invokeCommand [hystrixGenericParameterDTO="
				+ hystrixGenericParameterDTO.toString() + "]");

		HystrixGenericResponseDTO hystrixGenericResponseDTO = null;

		if (hystrixGenericParameterDTO.getB().equals("timeout")) {
			try {
				TimeUnit.MILLISECONDS.sleep(1500L);
			} catch (InterruptedException e) {
				// Ignore
			}
		} else if (hystrixGenericParameterDTO.getB().equals("half")) {
			try {
				TimeUnit.MILLISECONDS.sleep(750L);
			} catch (InterruptedException e) {
				// Ignore
			}
		} else if (hystrixGenericParameterDTO.getB().equals("exception")) {
			//
			throw new RuntimeException("test throwing a exception");
		} else if (hystrixGenericParameterDTO.getB().equals("pass")) {
			//
			hystrixGenericResponseDTO = new HystrixGenericResponseDTO(hystrixGenericParameterDTO.getA(),
					hystrixGenericParameterDTO.getB());
		} else if (hystrixGenericParameterDTO.getB().equals("bad")) {
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
		this.logger.debug(LOG_PREFIX + "invokeCommand [hystrixGenericParameterDTO="
				+ hystrixGenericParameterDTO.toString() + "]");

		throw new AppRuntimeException("CommandActionExceutionException", ErrorCode.RESILIENCE_10500);
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
				+ hystrixGenericParameterDTO.toString() + "]");

		throw new RetryException("run a retry...");
	}

	@HystrixCommand
	public HystrixGenericResponseDTO invokeCommandRetryExceptionWithDefaultConfig(
			final HystrixGenericParameterDTO hystrixGenericParameterDTO) {
		this.logger.debug(LOG_PREFIX + "invokeCommandRetryExceptionWithDefaultConfig [hystrixGenericParameterDTO="
				+ hystrixGenericParameterDTO.toString() + "]");

		throw new RetryException("start retry, with default configuration...");
	}
	
	@HystrixCommand(maxRetryCount = -1)
	public HystrixGenericResponseDTO invokeCommandWithoutRetry(
			final HystrixGenericParameterDTO hystrixGenericParameterDTO) {
		this.logger.debug(LOG_PREFIX + "invokeCommandWithoutRetry [hystrixGenericParameterDTO="
				+ hystrixGenericParameterDTO.toString() + "]");

		throw new RetryException("start retry, without retry...");
	}
	
	@HystrixCommand(groupKey = "bomc-generic-test", commandKey = "invokeCommandWithUnknownParameter", commandProperties = {
			@HystrixProperty(name = "unknown.parameter", value = "false") })
	public HystrixGenericResponseDTO invokeCommandWithUnknownParameter(
			final HystrixGenericParameterDTO hystrixGenericParameterDTO) {
		this.logger.debug(LOG_PREFIX + "invokeCommandWithUnknownParameter [hystrixGenericParameterDTO="
				+ hystrixGenericParameterDTO.toString() + "]");

		HystrixGenericResponseDTO hystrixGenericResponseDTO = null;

		hystrixGenericResponseDTO = new HystrixGenericResponseDTO(hystrixGenericParameterDTO.getA(),
				hystrixGenericParameterDTO.getB());

		return hystrixGenericResponseDTO;
	}

	public HystrixGenericResponseDTO withoutHystrixCommand(
			final HystrixGenericParameterDTO hystrixGenericParameterDTO) {
		this.logger.debug(LOG_PREFIX + "withoutHystrixCommand [hystrixGenericParameterDTO="
				+ hystrixGenericParameterDTO.toString() + "]");

		HystrixGenericResponseDTO hystrixGenericResponseDTO = null;

		hystrixGenericResponseDTO = new HystrixGenericResponseDTO(hystrixGenericParameterDTO.getA(),
				hystrixGenericParameterDTO.getB());

		return hystrixGenericResponseDTO;
	}

	@HystrixCommand(commandProperties = { @HystrixProperty(name = HystrixPropertiesManager.FALLBACK_ENABLED, value = "false") })
	public HystrixGenericResponseDTO withoutDefaultParameter(
			final HystrixGenericParameterDTO hystrixGenericParameterDTO) {
		this.logger.debug(LOG_PREFIX + "withoutDefaultParameter [hystrixGenericParameterDTO="
				+ hystrixGenericParameterDTO.toString() + "]");

		HystrixGenericResponseDTO hystrixGenericResponseDTO = null;

		hystrixGenericResponseDTO = new HystrixGenericResponseDTO(hystrixGenericParameterDTO.getA(),
				hystrixGenericParameterDTO.getB());

		return hystrixGenericResponseDTO;
	}
}
