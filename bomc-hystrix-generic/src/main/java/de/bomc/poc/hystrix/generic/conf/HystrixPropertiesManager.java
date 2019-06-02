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
package de.bomc.poc.hystrix.generic.conf;

import com.netflix.hystrix.HystrixCommandProperties;

import de.bomc.poc.hystrix.generic.qualifier.HystrixProperty;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides methods to set hystrix properties.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public final class HystrixPropertiesManager {

    private static final String LOG_PREFIX = "HystrixPropertiesManager#";
    private static final Logger LOGGER = Logger.getLogger(HystrixPropertiesManager.class);

    /**
     * Private constructor prevents instantiation.
     */
    private HystrixPropertiesManager() {
        //
        // Prevents instantiation.
    }

    /**
     * Command execution properties.
     */
    // The default, and the recommended setting, is to run HystrixCommands using thread isolation (THREAD) and HystrixObservableCommands using semaphore isolation (SEMAPHORE).
    public static final String EXECUTION_ISOLATION_STRATEGY = "execution.isolation.strategy";
    // This property sets the time in milliseconds after which the caller will observe a timeout and walk away from the command execution. (1000)
    public static final String EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS = "execution.isolation.thread.timeoutInMilliseconds";
    // This property indicates whether the HystrixCommand.run() execution should have a timeout. (true)
    public static final String EXECUTION_TIMEOUT_ENABLED = "execution.timeout.enabled";
    // This property indicates whether the HystrixCommand.run() execution should be interrupted when a timeout occurs. (true)
    public static final String EXECUTION_ISOLATION_THREAD_INTERRUPT_ON_TIMEOUT = "execution.isolation.thread.interruptOnTimeout";
    // This property sets the maximum number of requests allowed to a HystrixCommand.run() method when you are using ExecutionIsolationStrategy.SEMAPHORE. (10)
    public static final String EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS = "execution.isolation.semaphore.maxConcurrentRequests";
    /**
     * Command fallback properties.
     */
    // The following properties control how HystrixCommand.getFallback() executes. These properties apply to both ExecutionIsolationStrategy.THREAD and ExecutionIsolationStrategy.SEMAPHORE. (10)
    public static final String FALLBACK_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS = "fallback.isolation.semaphore.maxConcurrentRequests";
    // This property determines whether a call to HystrixCommand.getFallback() will be attempted when failure or rejection occurs. (true)
    // The fallback case is not implemented here in generic way, so the default value is always 'false'.
    public static final String FALLBACK_ENABLED = "fallback.enabled";
    /**
     * Command circuit breaker properties.
     */
    // The circuit breaker properties control behavior of the HystrixCircuitBreaker.(true)
    public static final String CIRCUIT_BREAKER_ENABLED = "circuitBreaker.enabled";
    // This property sets the minimum number of requests in a rolling window that will trip the circuit. (20)
    // For example, if the value is 20, then if only 19 requests are received in the rolling window (say a window of 10 seconds) the circuit will not trip open even if all 19 failed.
    public static final String CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD = "circuitBreaker.requestVolumeThreshold";
    // This property sets the amount of time, after tripping the circuit, to reject requests before allowing attempts again to determine if the circuit should again be closed. (500)
    public static final String CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS = "circuitBreaker.sleepWindowInMilliseconds";
    // This property sets the error percentage at or above which the circuit should trip open and start short-circuiting requests to fallback logic. (50)
    public static final String CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE = "circuitBreaker.errorThresholdPercentage";
    // This property, if true, forces the circuit breaker into an open (tripped) state in which it will reject all requests. (false)
    public static final String CIRCUIT_BREAKER_FORCE_OPEN = "circuitBreaker.forceOpen";
    // This property, if true, forces the circuit breaker into a closed state in which it will allow requests regardless of the error percentage. (false)
    public static final String CIRCUIT_BREAKER_FORCE_CLOSED = "circuitBreaker.forceClosed";
    /**
     * Command metrics properties.
     */
    // This property indicates whether execution latencies should be tracked and calculated as percentiles. If they are disabled, all summary statistics (mean, percentiles) are returned as -1. (true)
    public static final String METRICS_ROLLING_PERCENTILE_ENABLED = "metrics.rollingPercentile.enabled";
    // This property sets the duration of the statistical rolling window, in milliseconds. This is how long Hystrix keeps metrics for the circuit breaker to use and for publishing. (10000)
    public static final String METRICS_ROLLING_PERCENTILE_TIME_IN_MILLISECONDS = "metrics.rollingPercentile.timeInMilliseconds";
    // This property sets the number of buckets the rolling statistical window is divided into. (10)
    public static final String METRICS_ROLLING_PERCENTILE_NUM_BUCKETS = "metrics.rollingPercentile.numBuckets";
    // This property sets the maximum number of execution times that are kept per bucket. If more executions occur during the time they will wrap around and start over-writing at the beginning of the bucket. (100)
    public static final String METRICS_ROLLING_PERCENTILE_BUCKET_SIZE = "metrics.rollingPercentile.bucketSize";
    //  This property sets the time to wait, in milliseconds, between allowing health snapshots to be taken that calculate success and error percentages and affect circuit breaker status. (500)
    public static final String METRICS_HEALTH_SNAPSHOT_INTERVAL_IN_MILLISECONDS = "metrics.healthSnapshot.intervalInMilliseconds";
    /**
     * Command CommandRequest Context properties.
     */
    // These properties concern HystrixRequestContext functionality used by HystrixCommand. (true)
    public static final String REQUEST_CACHE_ENABLED = "requestCache.enabled";
    // This property indicates whether HystrixCommand execution and events should be logged to HystrixRequestLog. (true)
    public static final String REQUEST_LOG_ENABLED = "requestLog.enabled";
    /**
     * Thread pool properties.
     */
    // public static final String MAX_QUEUE_SIZE = "maxQueueSize";
    // public static final String CORE_SIZE = "coreSize";
    // public static final String MAXIMUM_SIZE = "maximumSize";
    // public static final String ALLOW_MAXIMUM_SIZE_TO_DIVERGE_FROM_CORE_SIZE = "allowMaximumSizeToDivergeFromCoreSize";
    // public static final String KEEP_ALIVE_TIME_MINUTES = "keepAliveTimeMinutes";
    // public static final String QUEUE_SIZE_REJECTION_THRESHOLD = "queueSizeRejectionThreshold";
    // public static final String METRICS_ROLLING_STATS_NUM_BUCKETS = "metrics.rollingStats.numBuckets";
    // public static final String METRICS_ROLLING_STATS_TIME_IN_MILLISECONDS = "metrics.rollingStats.timeInMilliseconds";
    /**
     * Collapser properties.
     */
    // public static final String MAX_REQUESTS_IN_BATCH = "maxRequestsInBatch";
    // public static final String TIMER_DELAY_IN_MILLISECONDS = "timerDelayInMilliseconds";
    //
    // Other member variables.
    private static final Map<String, PropSetter<HystrixCommandProperties.Setter, String>> CMD_PROP_MAP = getUnmodifiablePropertiesMap();

	/**
	 * Creates and sets Hystrix command properties.
	 * 
	 * @param propertyList
	 *            the collapser properties as list.
	 * @return a initialized HystrixCommandProperties.Setter.
	 */
	public static HystrixCommandProperties.Setter initializeCommandProperties(final List<HystrixProperty> propertyList)
			throws IllegalArgumentException {

		LOGGER.debug(LOG_PREFIX + "initializeCommandProperties [properties=" + propertyList + "]");

		return initializeProperties(HystrixCommandProperties.Setter(), propertyList, CMD_PROP_MAP, "command");
	}

	private static <S> S initializeProperties(final S setter, final List<HystrixProperty> propertyList,
			final Map<String, PropSetter<S, String>> propMap, final String type) {
		LOGGER.debug(LOG_PREFIX + "initializeProperties [setter, propertyList, propMap, type=" + type + "]");

		if (propertyList != null && !propertyList.isEmpty()) {

			for (final HystrixProperty property : propertyList) {
				validate(property);

				if (!propMap.containsKey(property.name())) {
					throw new IllegalArgumentException(
							LOG_PREFIX + "initializeProperties - unknown " + type + " property: " + property.name());
				}

				LOGGER.debug(LOG_PREFIX + "initializeProperties - add to propMap [property.name=" + property.name()
						+ ", property.value=" + property.value() + "]");

				propMap.get(property.name()).set(setter, property.value());
			}
		}

		return setter;
	}

	private static void validate(final HystrixProperty hystrixProperty) throws IllegalArgumentException {
		if (hystrixProperty.name() == null || hystrixProperty.name().isEmpty()) {

			throw new IllegalArgumentException(LOG_PREFIX + "validate - hystrix property name cannot be null or blank");
		}
	}

	private static Map<String, PropSetter<HystrixCommandProperties.Setter, String>> getUnmodifiablePropertiesMap() {
		final Map<String, PropSetter<HystrixCommandProperties.Setter, String>> modifiablePropertiesMap = new HashMap<String, PropSetter<HystrixCommandProperties.Setter, String>>();

		modifiablePropertiesMap.put(EXECUTION_ISOLATION_STRATEGY,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withExecutionIsolationStrategy(toEnum(EXECUTION_ISOLATION_STRATEGY, value,
								HystrixCommandProperties.ExecutionIsolationStrategy.class,
								HystrixCommandProperties.ExecutionIsolationStrategy.values()));
					}
				});
		modifiablePropertiesMap.put(EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withExecutionTimeoutInMilliseconds(
								toInt(EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value));
					}
				});
		modifiablePropertiesMap.put(EXECUTION_TIMEOUT_ENABLED,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withExecutionTimeoutEnabled(toBoolean(value));
					}
				});
		modifiablePropertiesMap.put(EXECUTION_ISOLATION_THREAD_INTERRUPT_ON_TIMEOUT,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withExecutionIsolationThreadInterruptOnTimeout(toBoolean(value));
					}
				});
		modifiablePropertiesMap.put(EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withExecutionIsolationSemaphoreMaxConcurrentRequests(
								toInt(EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value));
					}
				});
		modifiablePropertiesMap.put(FALLBACK_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withFallbackIsolationSemaphoreMaxConcurrentRequests(
								toInt(FALLBACK_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value));
					}
				});
		modifiablePropertiesMap.put(FALLBACK_ENABLED, new PropSetter<HystrixCommandProperties.Setter, String>() {
			@Override
			public void set(final HystrixCommandProperties.Setter setter, final String value)
					throws IllegalArgumentException {
				setter.withFallbackEnabled(false);
			}
		});
		modifiablePropertiesMap.put(CIRCUIT_BREAKER_ENABLED, new PropSetter<HystrixCommandProperties.Setter, String>() {
			@Override
			public void set(final HystrixCommandProperties.Setter setter, final String value)
					throws IllegalArgumentException {
				// Fallback is not implemented in a generic way, so it must be
				// always 'false'.
				setter.withCircuitBreakerEnabled(toBoolean(value));
			}
		});
		modifiablePropertiesMap.put(CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withCircuitBreakerRequestVolumeThreshold(
								toInt(CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value));
					}
				});
		modifiablePropertiesMap.put(CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withCircuitBreakerSleepWindowInMilliseconds(
								toInt(CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value));
					}
				});
		modifiablePropertiesMap.put(CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withCircuitBreakerErrorThresholdPercentage(
								toInt(CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value));
					}
				});
		modifiablePropertiesMap.put(CIRCUIT_BREAKER_FORCE_OPEN,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withCircuitBreakerForceOpen(toBoolean(value));
					}
				});
		modifiablePropertiesMap.put(CIRCUIT_BREAKER_FORCE_CLOSED,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withCircuitBreakerForceClosed(toBoolean(value));
					}
				});
		modifiablePropertiesMap.put(METRICS_ROLLING_PERCENTILE_ENABLED,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withMetricsRollingPercentileEnabled(toBoolean(value));
					}
				});
		modifiablePropertiesMap.put(METRICS_ROLLING_PERCENTILE_TIME_IN_MILLISECONDS,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withMetricsRollingPercentileWindowInMilliseconds(
								toInt(METRICS_ROLLING_PERCENTILE_TIME_IN_MILLISECONDS, value));
					}
				});
		modifiablePropertiesMap.put(METRICS_ROLLING_PERCENTILE_NUM_BUCKETS,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withMetricsRollingPercentileWindowBuckets(
								toInt(METRICS_ROLLING_PERCENTILE_NUM_BUCKETS, value));
					}
				});
		modifiablePropertiesMap.put(METRICS_ROLLING_PERCENTILE_BUCKET_SIZE,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withMetricsRollingPercentileBucketSize(
								toInt(METRICS_ROLLING_PERCENTILE_BUCKET_SIZE, value));
					}
				});
		modifiablePropertiesMap.put(METRICS_HEALTH_SNAPSHOT_INTERVAL_IN_MILLISECONDS,
				new PropSetter<HystrixCommandProperties.Setter, String>() {
					@Override
					public void set(final HystrixCommandProperties.Setter setter, final String value)
							throws IllegalArgumentException {
						setter.withMetricsHealthSnapshotIntervalInMilliseconds(
								toInt(METRICS_HEALTH_SNAPSHOT_INTERVAL_IN_MILLISECONDS, value));
					}
				});
		modifiablePropertiesMap.put(REQUEST_CACHE_ENABLED, new PropSetter<HystrixCommandProperties.Setter, String>() {
			@Override
			public void set(final HystrixCommandProperties.Setter setter, final String value)
					throws IllegalArgumentException {
				setter.withRequestCacheEnabled(toBoolean(value));
			}
		});
		modifiablePropertiesMap.put(REQUEST_LOG_ENABLED, new PropSetter<HystrixCommandProperties.Setter, String>() {
			@Override
			public void set(final HystrixCommandProperties.Setter setter, final String value)
					throws IllegalArgumentException {
				setter.withRequestLogEnabled(toBoolean(value));
			}
		});

		return Collections.unmodifiableMap(modifiablePropertiesMap);
	}

	private interface PropSetter<S, V> {

		void set(S setter, V value) throws IllegalArgumentException;
	}

	@SuppressWarnings("unchecked")
	private static <E extends Enum<E>> E toEnum(final String propName, final String propValue, final Class<E> enumType,
			final E... values) throws IllegalArgumentException {
		LOGGER.debug(LOG_PREFIX + "toEnum [propName=" + propName + ", propValue=" + propValue + ", enumType="
				+ enumType.getName() + "]");

		try {

			return Enum.valueOf(enumType, propValue);
		} catch (final NullPointerException npe) {
			LOGGER.error(LOG_PREFIX + "toEnum", npe);

			throw createBadEnumError(propName, propValue, values);
		} catch (final IllegalArgumentException e) {
			LOGGER.error(LOG_PREFIX + "toEnum", e);

			throw createBadEnumError(propName, propValue, values);
		}
	}

	private static int toInt(final String propName, final String propValue) throws IllegalArgumentException {
		LOGGER.debug(LOG_PREFIX + "toInt [propName=" + propName + ", propValue=" + propValue + "]");

		try {
			return Integer.parseInt(propValue);
		} catch (final NumberFormatException e) {

			throw new IllegalArgumentException(LOG_PREFIX + "toInt - bad property value. property name '" + propName
					+ "'. Expected int value, actual = " + propValue);
		}
	}

	private static boolean toBoolean(final String propValue) {
		LOGGER.debug(LOG_PREFIX + "toBoolean [propValue=" + propValue + "]");

		return Boolean.valueOf(propValue);
	}

	@SuppressWarnings("rawtypes")
	private static IllegalArgumentException createBadEnumError(final String propName, final String propValue,
			final Enum... values) {
		throw new IllegalArgumentException(LOG_PREFIX + "createBadEnumError - bad property value. property name '"
				+ propName + "'. Expected correct enum value, one of the [" + Arrays.toString(values) + "] , actual = "
				+ propValue);
	}
}
