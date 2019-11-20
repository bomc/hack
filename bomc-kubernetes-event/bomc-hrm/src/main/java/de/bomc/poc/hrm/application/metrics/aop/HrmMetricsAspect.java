/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: micha
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.application.metrics.aop;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * An aspect that enables collecting metrics on method or class for 'counter'
 * and 'timer' types.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 20.11.2019
 */
// LOMBOK
@Slf4j
// SPRING
@Aspect
@Component
public class HrmMetricsAspect {

	private static final String LOG_PREFIX = HrmMetricsAspect.class.getSimpleName() + "#";

	// _______________________________________________
	// Constants
	// -----------------------------------------------
	private static final String METRIC_SEPERATOR = "_";
	private static final String METRIC_PREFIX = "bomc_hrm" + METRIC_SEPERATOR;
	private static final String METRIC_COUNTER_PREFIX = "counter";
	private static final String METRIC_TIMER_PREFIX = "timer";

	// _______________________________________________
	// Member variables
	// -----------------------------------------------
	private final MeterRegistry registry;

	/**
	 * Autowired constructor.
	 *
	 * @param registry metrics registry
	 */
	public HrmMetricsAspect(final MeterRegistry registry) {
		this.registry = registry;
	}

	/**
	 * Create an aspect aspect with -at Around advice for collecting metrics.
	 * 
	 * @param proceedingJoinPoint exposes the proceed(..) method
	 * @return the method result.
	 * @throws Throwable
	 */
	@Around("de.bomc.poc.hrm.config.aop.AopJoinPointConfig.metricExecution()")
	public Object around(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

		final long start = System.currentTimeMillis();

		final MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
		final Method method = signature.getMethod();
		final Metric metricMethod = method.getAnnotation(Metric.class);

		final Metric metricClass = proceedingJoinPoint.getTarget().getClass().getAnnotation(Metric.class);

		// Get counter switch from annotation.
		final boolean isCollectCounterMetrics = metricMethod != null ? metricMethod.counter() : metricClass.counter();
		// Get timer switch from annotation.
		final boolean isCollectTimerMetrics = metricMethod != null ? metricMethod.timer() : metricClass.timer();

		final Object object;
		Throwable tmpThrowable = null;

		final String prometheusPrefixLabel = this.createPrometheusPrefixLabel(
		        proceedingJoinPoint.getTarget().getClass().getSimpleName(),
		        ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod().getName());

		try {
			if (isCollectCounterMetrics) {
				this.registry.counter(prometheusPrefixLabel.concat(METRIC_COUNTER_PREFIX)).increment();
			}

			// Delegate invocation to next aspect or method.
			object = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());

		} catch (final Throwable throwable) {
			log.error(LOG_PREFIX + "around - ", throwable);

			tmpThrowable = throwable;

			throw throwable;
		} finally {

			if (isCollectTimerMetrics) {
				final long turnaround = System.currentTimeMillis() - start;

				recordTimer(turnaround, proceedingJoinPoint, tmpThrowable, prometheusPrefixLabel);
			}
		}

		return object;
	}

	private void recordTimer(final long turnaround, final ProceedingJoinPoint joinPoint, final Throwable throwable,
	        final String prometheusPrefixLabel) {
		log.debug(LOG_PREFIX + "recordHealthIndicatorTurnaround - {} completed in {} ns (exception: {})",
		        joinPoint.getTarget().getClass().getSimpleName(), turnaround,
		        throwable != null ? throwable.getClass().getSimpleName() : "none");

		final Set<Tag> tags;

		if (throwable == null) {
			tags = MetricsUtils.newSuccessTagsSet();
		} else {
			tags = MetricsUtils.newFailureTagsSetForException(throwable);
		}

		this.registry.timer(prometheusPrefixLabel + METRIC_TIMER_PREFIX, tags).record(turnaround,
		        TimeUnit.MILLISECONDS);
	}

	/**
	 * Formats a given string in a prometheus format.
	 * 
	 * @param className  the class name of the joinpoint.
	 * @param methodName the method name of the joinpoint.
	 * @return a string in prometheus format.
	 */
	private String createPrometheusPrefixLabel(final String className, final String methodName) {

		return METRIC_PREFIX + this.convertToPrometheusFormatLabel(className) + METRIC_SEPERATOR
		        + this.convertToPrometheusFormatLabel(methodName) + METRIC_SEPERATOR;
	}

	/**
	 * A helper to convert a string in a prometheus compliant label.
	 * 
	 * @param stringToConvert the string to convert.
	 * @return a converted string.
	 */
	private String convertToPrometheusFormatLabel(final String stringToConvert) {

		final String convertedString = StringUtils
		        .join(StringUtils.splitByCharacterTypeCamelCase(stringToConvert), METRIC_SEPERATOR).toLowerCase();

		return convertedString;
	}

}
