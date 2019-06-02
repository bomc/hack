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
package de.bomc.poc.hystrix.generic.interceptor;

import com.netflix.hystrix.HystrixInvokable;
import com.netflix.hystrix.exception.HystrixRuntimeException;

import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.hystrix.generic.CommandExecutor;
import de.bomc.poc.hystrix.generic.ExecutionTypeEnum;
import de.bomc.poc.hystrix.generic.HystrixCommandFactory;
import de.bomc.poc.hystrix.generic.MetaHolder;
import de.bomc.poc.hystrix.generic.backoff.BackOffExecution;
import de.bomc.poc.hystrix.generic.backoff.ExponentialBackOff;
import de.bomc.poc.hystrix.generic.exeception.ExceptionUtils;
import de.bomc.poc.hystrix.generic.exeception.RetryException;
import de.bomc.poc.hystrix.generic.qualifier.HystrixCommand;
import de.bomc.poc.hystrix.generic.timeout.TimeoutSingletonEJB;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A interceptor that handles the genric hystrix command invocation.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public class HystrixInterceptor {

	private static final String LOG_PREFIX = "HystrixInterceptor#";
	private static final Long JITTER = 1000L;
	@Inject
	@LoggerQualifier
	private Logger logger;
	@EJB
	private TimeoutSingletonEJB timeoutSingletonEJB;
	private static final Map<HystrixPointcutType, MetaHolderFactory> META_HOLDER_FACTORY_MAP;

	static {
		final Map<HystrixPointcutType, MetaHolderFactory> modifiableMap = new HashMap<HystrixPointcutType, MetaHolderFactory>();

		modifiableMap.put(HystrixPointcutType.COMMAND, new CommandMetaHolderFactory());
		META_HOLDER_FACTORY_MAP = Collections.unmodifiableMap(modifiableMap);
	}

	@AroundInvoke
	public Object intercept(final InvocationContext invocationContext) {
		this.logger.debug(LOG_PREFIX + "intercept [class=" + invocationContext.getTarget().getClass().getName()
				+ ", method=" + invocationContext.getMethod().getName() + "]");

		Object result = null;

		final Method method = getMethodFromTarget(invocationContext);

		if (method.isAnnotationPresent(HystrixCommand.class)) {
			// Create a metaholder factory and check 'HystrixPointcutType#of' is
			// annotated with the HystrixCommand annotation.
			final MetaHolderFactory metaHolderFactory = META_HOLDER_FACTORY_MAP.get(HystrixPointcutType.of(method));
			final MetaHolder metaHolder = metaHolderFactory.create(invocationContext);

			// Create backoff instance.
			final ExponentialBackOff backoff = new ExponentialBackOff(metaHolder.getInitialInterval(),
					metaHolder.getMultiplier(), metaHolder.getMaxRetryCount());
			final BackOffExecution backOffExecution = backoff.start();
			// meta data for retry handling.
			boolean failed;
			long delay = 0;

			do {
				try {
					if (backOffExecution.getRetryCount() > 0) {
						this.logger.debug(String.format(
								LOG_PREFIX + "intercept - method '%s' from class '%s' is being retried by thread '%s'",
								invocationContext.getMethod(), invocationContext.getMethod(),
								Thread.currentThread().getName()));
					}

					// Create a hystrix instance.
					final HystrixInvokable<?> invokable = HystrixCommandFactory.getInstance().create(invocationContext,
							metaHolder);
					final ExecutionTypeEnum executionTypeEnum = metaHolder.getExecutionTypeEnum();

					// Execute command on hystrix.
					result = CommandExecutor.execute(invokable, executionTypeEnum, metaHolder);

					// Stop running while loops.
					failed = false;
				} catch (final Throwable throwable) {

					this.logger.error(LOG_PREFIX + "intercept [throwable=" + throwable.getClass() + "]");

					if (throwable instanceof HystrixRuntimeException) {

						final Throwable unwrappedException = ExceptionUtils.unwrapCause(throwable);

						if (unwrappedException.getCause() != null
								&& unwrappedException.getCause() instanceof RetryException) {
							//
							// Indicates that a retry should be performed.
							this.logger.error(LOG_PREFIX + "intercept [error.class="
									+ unwrappedException.getCause().getClass().getSimpleName() + ", errMsg="
									+ unwrappedException.getCause().getMessage() + "]");

							delay = backOffExecution.nextBackOff();

							if (delay != -1) {
								// Unlock while loop.
								failed = true;
								// Sleep for a while...
								final CountDownLatch timeoutNotifyingLatch = this.timeoutSingletonEJB
										.createSingleActionTimer(delay);

								// isTimeoutInvoked is 'true' if the count
								// reached zero,
								// means the started timer is running off and
								// 'false' if
								// the waiting time elapsed before the count
								// reached
								// zero.
								boolean isTimeoutInvoked = true;

								try {
									isTimeoutInvoked = timeoutNotifyingLatch.await(delay + JITTER,
											TimeUnit.MILLISECONDS);
								} catch (final InterruptedException e) {
									// Ignore
								}

								this.logger.info(LOG_PREFIX + "intercept awake from sleep [delay=" + delay
										+ ", isTimeoutInvoked=" + isTimeoutInvoked + "]");
							} else {
								this.logger.info(LOG_PREFIX
										+ "intercept - retry was terminated unsuccessfully, remote service could not be connected!");

								throw new AppRuntimeException(
										LOG_PREFIX
												+ "intercept - retry was terminated unsuccessfully, remote service could not be connected!",
										ErrorCode.RESILIENCE_10500);
							}
						} else {
							// handles a Timeout,
							this.logger.error(LOG_PREFIX + "intercept [error.class.1="
									+ throwable.getCause().getClass().getSimpleName() + ", errMsg="
									+ throwable.getCause().getMessage() + "]");

							throw new AppRuntimeException(LOG_PREFIX + "intercept [error.class="
									+ throwable.getCause().getClass().getSimpleName() + ", error.msg="
									+ throwable.getMessage() + "]", ErrorCode.RESILIENCE_10500);
						}
					} else {
						this.logger.error(LOG_PREFIX + "intercept [error.class.2="
								+ throwable.getClass().getSimpleName() + ", errMsg=" + throwable.getMessage() + "]");

						throw new AppRuntimeException(
								LOG_PREFIX + "intercept [error.class=" + throwable.getCause().getClass().getSimpleName()
										+ ", error.msg=" + throwable.getMessage() + "]",
								throwable.getCause(), ErrorCode.UNEXPECTED_10000);
					}
				}
			} while (failed && (delay != -1));
		} else {
			this.logger.info(LOG_PREFIX
					+ "intercept - the method is invoking in context of a 'HystrixInterceptor', but no HystrixCOmmand annotation is available, so no hystrix command mechanism will be started! \n"
					+ getMethodInfo(method));
			try {
				result = invocationContext.proceed();
			} catch (final Exception ex) {
				this.logger.error(LOG_PREFIX + "intercept - error occurs during method invocation.", ex);
			}
		}

		return result;
	}

	// _______________________________________________
	// Inner class
	// -----------------------------------------------
	/**
	 * A factory to create MetaHolder depending on {@link HystrixPointcutType}.
	 */
	private abstract static class MetaHolderFactory {

		public MetaHolder create(final InvocationContext invocationContext) {
			final Method method = getMethodFromTarget(invocationContext);
			final Object obj = invocationContext.getTarget();
			final Object[] args = invocationContext.getParameters();

			return this.create(method, obj, args, invocationContext);
		}

		public abstract MetaHolder create(Method method, Object obj, Object[] args,
				final InvocationContext invocationContext);

		MetaHolder.Builder metaHolderBuilder(final Method method, final Object obj, final Object[] args,
				final InvocationContext invocationContext) {

			final MetaHolder.Builder builder = MetaHolder.builder().args(args).method(method).obj(obj);

			return builder;
		}
	}

	private static class CommandMetaHolderFactory extends MetaHolderFactory {

		@Override
		public MetaHolder create(final Method method, final Object obj, final Object[] args,
				final InvocationContext invocationContext) {

			final HystrixCommand hystrixCommand = method.getAnnotation(HystrixCommand.class);
			final ExecutionTypeEnum executionTypeEnum = ExecutionTypeEnum.getExecutionTypeEnum(method.getReturnType());
			final MetaHolder.Builder builder = this.metaHolderBuilder(method, obj, args, invocationContext);

			return builder.defaultCommandKey(method.getName()).defaultGroupKey(obj.getClass().getSimpleName())
					.hystrixCommand(hystrixCommand).maxRetryCount(hystrixCommand.maxRetryCount())
					.multiplier(hystrixCommand.multiplier()).initialInterval(hystrixCommand.initialInterval())
					.executionTypeEnum(executionTypeEnum).build();
		}
	}

	private enum HystrixPointcutType {
		COMMAND;

		static HystrixPointcutType of(final Method method) {
			// if (method.isAnnotationPresent(HystrixCommand.class)) {
			return COMMAND;
		} /*
			 * else { final String methodInfo = getMethodInfo(method); throw new
			 * IllegalStateException( LOG_PREFIX +
			 * "HystrixPointcutType - no valid annotation found for: \n" +
			 * methodInfo); }
			 */
		// }

	}

	// _______________________________________________
	// Auslagern ... AopUtil
	// -----------------------------------------------

	/**
	 * Gets a {@link Method} object from target object.
	 * 
	 * @param invocationContext
	 *            the {@link InvocationContext}
	 * @return a {@link Method} object or null if method doesn't exist.
	 */
	public static Method getMethodFromTarget(final InvocationContext invocationContext) {
		final Method method = getDeclaredMethod(invocationContext.getTarget().getClass(),
				invocationContext.getMethod().getName(), getParameterTypes(invocationContext));

		return method;
	}

	/**
	 * Gets parameter types of the join point.
	 * 
	 * @param invocationContext
	 *            the invocationContext
	 * @return the parameter types for the method this object represents
	 */
	@SuppressWarnings("rawtypes")
	public static Class[] getParameterTypes(final InvocationContext invocationContext) {
		final Method method = invocationContext.getMethod();

		return method.getParameterTypes();
	}

	/**
	 * Gets declared method from specified type by mame and parameters types.
	 * 
	 * @param type
	 *            the type
	 * @param methodName
	 *            the name of the method
	 * @param parameterTypes
	 *            the parameter array
	 * @return a {@link Method} object or null if method doesn't exist
	 */
	public static Method getDeclaredMethod(final Class<?> type, final String methodName,
			final Class<?>... parameterTypes) {
		Method method = null;

		try {
			method = type.getDeclaredMethod(methodName, parameterTypes);

			if (method.isBridge()) {
				System.out.println("HystrixInterceptor#getDeclaredMethod - TODO isBridge / unbride");
				// method = MethodProvider.getInstance().unbride(method, type);
			}
		} catch (final NoSuchMethodException e) {
			final Class<?> superclass = type.getSuperclass();
			if (superclass != null) {
				method = getDeclaredMethod(superclass, methodName, parameterTypes);
			}
		}

		return method;
	}

	public static <T extends Annotation> Optional<T> getAnnotation(final InvocationContext joinPoint,
			final Class<T> annotation) {
		return getAnnotation(joinPoint.getTarget().getClass(), annotation);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Annotation> Optional<T> getAnnotation(final Class<?> type, final Class<T> annotation) {
		notNull(annotation, LOG_PREFIX + "getAnnotation - annotation cannot be null");
		notNull(type, LOG_PREFIX + "getAnnotation - type cannot be null");

		for (final Annotation ann : type.getDeclaredAnnotations()) {
			if (ann.annotationType().equals(annotation)) {
				return Optional.of((T) ann);
			}
		}

		return Optional.empty();
	}

	private static <T> T notNull(final T object, final String message) {
		if (object == null) {
			throw new NullPointerException();
		}

		return object;
	}

	public static String getMethodInfo(final Method m) {
		final StringBuilder info = new StringBuilder();
		info.append("Method signature:").append("\n");
		info.append(m.toGenericString()).append("\n");

		info.append("Declaring class:\n");
		info.append(m.getDeclaringClass().getCanonicalName()).append("\n");

		info.append("\nFlags:").append("\n");
		info.append("Bridge=").append(m.isBridge()).append("\n");
		info.append("Synthetic=").append(m.isSynthetic()).append("\n");
		info.append("Final=").append(Modifier.isFinal(m.getModifiers())).append("\n");
		info.append("Native=").append(Modifier.isNative(m.getModifiers())).append("\n");
		info.append("Synchronized=").append(Modifier.isSynchronized(m.getModifiers())).append("\n");
		info.append("Abstract=").append(Modifier.isAbstract(m.getModifiers())).append("\n");
		info.append("AccessLevel=").append(getAccessLevel(m.getModifiers())).append("\n");

		info.append("\nReturn Type: \n");
		info.append("ReturnType=").append(m.getReturnType()).append("\n");
		info.append("GenericReturnType=").append(m.getGenericReturnType()).append("\n");

		info.append("\nParameters:");
		final Class<?>[] pType = m.getParameterTypes();
		final Type[] gpType = m.getGenericParameterTypes();
		if (pType.length != 0) {
			info.append("\n");
		} else {
			info.append("empty\n");
		}
		for (int i = 0; i < pType.length; i++) {
			info.append("parameter [").append(i).append("]:\n");
			info.append("ParameterType=").append(pType[i]).append("\n");
			info.append("GenericParameterType=").append(gpType[i]).append("\n");
		}

		info.append("\nExceptions:");
		final Class<?>[] xType = m.getExceptionTypes();
		final Type[] gxType = m.getGenericExceptionTypes();
		if (xType.length != 0) {
			info.append("\n");
		} else {
			info.append("empty\n");
		}
		for (int i = 0; i < xType.length; i++) {
			info.append("exception [").append(i).append("]:\n");
			info.append("ExceptionType=").append(xType[i]).append("\n");
			info.append("GenericExceptionType=").append(gxType[i]).append("\n");
		}

		info.append("\nAnnotations:");
		if (m.getAnnotations().length != 0) {
			info.append("\n");
		} else {
			info.append("empty\n");
		}

		for (int i = 0; i < m.getAnnotations().length; i++) {
			info.append("annotation[").append(i).append("]=").append(m.getAnnotations()[i]).append("\n");
		}

		return info.toString();
	}

	private static String getAccessLevel(final int modifiers) {
		if (Modifier.isPublic(modifiers)) {
			return "public";
		} else if (Modifier.isProtected(modifiers)) {
			return "protected";
		} else if (Modifier.isPrivate(modifiers)) {
			return "private";
		} else {
			return "default";
		}
	}
}
