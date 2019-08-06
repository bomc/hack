/**
 * Project: bomc-invoice
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.invoice.application.idempotent.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.invoice.application.idempotent.IdempotentHandlerEJB;
import de.bomc.poc.invoice.application.idempotent.annotation.Idempotent;
import de.bomc.poc.invoice.application.idempotent.qualifier.IdempotentFilterQualifier;
import de.bomc.poc.invoice.application.internal.AppErrorCodeEnum;
import de.bomc.poc.invoice.application.internal.ApplicationUserEnum;
import de.bomc.poc.invoice.application.log.LoggerQualifier;
import de.bomc.poc.invoice.domain.model.idempotent.IdempotentMessage;

/**
 * <pre>
 * This interceptor enables the processing of idempotent method calls.
 * For a method to be treated as idempotent, the method must be annotated with the qualifier {@link Idempotent}.
 * The parameter idempotentId must exist in the method itself also a userId. If the userId is not set in UUID format, a empty string is stored in db.
 * A idempotentId must be available in format: iid_aab5a110-c5c2-4edb-ad5e-d5ad6e503e15.
 * - "iid"                                      the processorName
 * - "_"                                        the seperator
 * - "aab5a110-c5c2-4edb-ad5e-d5ad6e503e15"     a UUID
 * </pre>
 * 
 * See the regex {@link FilterParameter}
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Interceptor
@IdempotentFilterQualifier
@Priority(Interceptor.Priority.APPLICATION)
public class IdempotentFilterInterceptor {

	private static final String LOG_PREFIX = "IdempotentFilterInterceptor#";
	@Inject
	@LoggerQualifier
	private Logger logger;
	@EJB
	private IdempotentHandlerEJB idempotentHandlerEJB;

	/**
	 * Check if the method call should be handled idempotent.
	 * 
	 * @param invocationContext the given context.
	 * @return the context.
	 * @throws Exception
	 */
	@AroundInvoke
	public Object interceptorMethod(final InvocationContext invocationContext) throws Exception {
		this.logger.log(Level.FINE,
				LOG_PREFIX + "intercept [class=" + invocationContext.getTarget().getClass().getName() + ", method="
						+ invocationContext.getMethod().getName() + "]");
		final FilterParameter filterParameter = new FilterParameter();

		Object result = null;

		// Check if method has to be handled idempotent.
		final Idempotent idempotent = invocationContext.getMethod().getAnnotation(Idempotent.class);

		if (idempotent != null) {
			//
			// Method is annotated with Idempotent, so the method is handled idempotent.
			// this.logger.info(LOG_PREFIX + "interceptorMethod - " +
			// getMethodInfo(invocationContext.getMethod()));

			final Object[] parameters = invocationContext.getParameters();

			if (parameters.length > 0) {
				// Extract the idempotentId and the userId from method and header parameters.
				final List<String> idempotentIdList = Stream.of(parameters).map(filterParameter::findIdempotentId)
						.filter(p -> p.length() > 0).collect(Collectors.toList());

				final List<String> userIdList = Stream.of(parameters).map(filterParameter::findUserId)
						.filter(p -> p.length() > 0).collect(Collectors.toList());

				if (!idempotentIdList.isEmpty()) {
					//
					// List contains a idempotentId, so the invocation is handled idempotent.
					final Matcher matcherExtractedIdempotentId = FilterParameter.UUID_PATTERN
							.matcher(idempotentIdList.get(0).toString());
					matcherExtractedIdempotentId.find();
					final String extractedIdempotentId = matcherExtractedIdempotentId.group(0);

					final String extractedProcessorName = idempotentIdList.get(0).toString().substring(0,
							(idempotentIdList.get(0).toString().length() - extractedIdempotentId.length() - 1));
					//
					// Check if a task is already running with this idempotentId.
					final List<IdempotentMessage> idempotentMessageList = this.idempotentHandlerEJB
							.queryString(extractedIdempotentId, extractedProcessorName);

					if (idempotentMessageList.isEmpty()) {
						//
						// No task is already running by the given idempotentId. Start idempotent
						// handling.
						final IdempotentMessage idempotentMessage = new IdempotentMessage(extractedIdempotentId,
								extractedProcessorName);

						if (!userIdList.isEmpty()) {
							this.idempotentHandlerEJB.persist(idempotentMessage, userIdList.get(0));
						} else {
							this.idempotentHandlerEJB.persist(idempotentMessage, ApplicationUserEnum.SYSTEM_USER.name());
						}

						// ___________________________
						// bomc: Uncomment for idempotent tests.
						// ---------------------------
						//TimeUnit.MILLISECONDS.sleep(this.getRandomNumberInRange(0, 7500));
						
						try {
							// Invoke the method on the target component.
							result = invocationContext.proceed();
						} finally {
							// Remove idempotentId from db.
							if (!userIdList.isEmpty()) {
								this.idempotentHandlerEJB.remove(idempotentMessage, userIdList.get(0));
							} else {
								this.idempotentHandlerEJB.remove(idempotentMessage, ApplicationUserEnum.SYSTEM_USER.name());
							}
						}
					} else {
						//
						// There is already a invocation for this idempotentId running.
						final IdempotentMessage idempotentMessage = idempotentMessageList.get(0);
						this.logger.log(Level.WARNING, LOG_PREFIX
								+ "interceptorMethod - There is already a invocation for this idempotentId running "
								+ idempotentMessage);

						result = null;
					}
				} else {
					final String errMsg = LOG_PREFIX
							+ "interceptorMethod - Idempotent Annotation is set, but no or a not valid idempotentId is set. A idempotentId must be availbale in format: iid_aab5a110-c5c2-4edb-ad5e-d5ad6e503e15";
					this.logger.log(Level.SEVERE, errMsg);

					throw new AppRuntimeException(errMsg, AppErrorCodeEnum.APP_IDEMPOTENT_FAILURE_10606);
				}
			} else {
				// Should not be occur in production. This indicates a development issue.
				final String errMsg = LOG_PREFIX
						+ "interceptorMethod - Idempotent Annotation is set, but method has no parameter. A idempotentId must be available in format: iid_aab5a110-c5c2-4edb-ad5e-d5ad6e503e15";
				this.logger.log(Level.SEVERE, errMsg);

				throw new AppRuntimeException(errMsg, AppErrorCodeEnum.APP_IDEMPOTENT_FAILURE_10606);
			}
		} else {
			// Should not be occur in production. This indicates a development issue.
			this.logger.log(Level.FINE, LOG_PREFIX + "interceptMethod - annotation @Idempotent is not set. [method="
					+ invocationContext.getMethod().getName() + "]");
			result = invocationContext.proceed();
		}

		return result;
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
	
	/**
	 * Defines a value between min and max.
	 * 
	 * @param min the minimum value.
	 * @param max the maximum value.
	 * @return a random value between min and max.
	 */
	@SuppressWarnings("unused")
	private long getRandomNumberInRange(long min, long max) {
		final Random r = new Random();
		return r.longs(min, (max + 1)).limit(1).findFirst().getAsLong();

	}
}
