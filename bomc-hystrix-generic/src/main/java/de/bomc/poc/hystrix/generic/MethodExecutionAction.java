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

import org.apache.log4j.Logger;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.hystrix.generic.exeception.ExceptionUtils;

import javax.interceptor.InvocationContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * This implementation invokes methods using the {@link InvocationContext}. If
 * {@link InvocationContext#proceed()} throws exception, then this exception is
 * wrapped to {@link AppRuntimeException} for further unwrapping and processing.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public class MethodExecutionAction implements CommandAction {

	private static final String LOG_PREFIX = "MethodExecutionAction#";
	private static final Logger LOGGER = Logger.getLogger(MethodExecutionAction.class);
	//
	// Other member variables.
	private static final Object[] EMPTY_ARGS = {};
	private final Object object;
	private final Method method;
	private final Object[] args;
	private final MetaHolder metaHolder;

	/**
	 * Creates a new instance of <code>MethodExecutionAction</code>.
	 * 
	 * @param object
	 *            the target object that is wrapped for a hystrix-command.
	 * @param method
	 *            the target method that is wrapped for a hsytrix-command.
	 * @param metaHolder
	 *            the given meta data for this invocation.
	 */
	public MethodExecutionAction(final Object object, final Method method, final MetaHolder metaHolder) {
		this.object = object;
		this.method = method;
		this.args = EMPTY_ARGS;
		this.metaHolder = metaHolder;
	}

	/**
	 * Creates a new instance of <code>MethodExecutionAction</code>.
	 * 
	 * @param object
	 *            the target object that is wrapped for a hystrix-command.
	 * @param method
	 *            the target method that is wrapped for a hsytrix-command.
	 * @param args
	 *            the method parameter arguments.
	 * @param metaHolder
	 *            the given meta data for this invocation.
	 */
	public MethodExecutionAction(final Object object, final Method method, final Object[] args,
			final MetaHolder metaHolder) {
		this.object = object;
		this.method = method;
		this.args = args;
		this.metaHolder = metaHolder;
	}

	public Object getObject() {

		return this.object;
	}

	public Method getMethod() {

		return this.method;
	}

	public Object[] getArgs() {

		return this.args;
	}

	@Override
	public MetaHolder getMetaHolder() {

		return this.metaHolder;
	}

	@Override
	public Object execute(final InvocationContext invocationContext, final ExecutionTypeEnum executionTypeEnum)
			throws AppRuntimeException {
		LOGGER.debug(LOG_PREFIX + "execute [invocationContext, executionTypeEnum=" + executionTypeEnum.name() + "]");

		return this.executeWithArgs(invocationContext, executionTypeEnum, this.args);
	}

	/**
	 * Invokes the method. Also private method also can be invoked.
	 * 
	 * @return result of execution
	 */
	@Override
	public Object executeWithArgs(final InvocationContext invocationContext, final ExecutionTypeEnum executionTypeEnum,
			final Object[] args) throws AppRuntimeException {
		LOGGER.debug(LOG_PREFIX + "executeWithArgs [executionType=" + executionTypeEnum.name() + ", args="
				+ Arrays.toString(args) + "]");

		return this.execute(invocationContext, this.object, this.method, args);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getActionName() {
		LOGGER.debug(LOG_PREFIX + "getActionName [action.name=" + this.method.getName() + "]");

		return this.method.getName();
	}

	/**
	 * Invokes the method.
	 * 
	 * @return result of execution
	 */
	private Object execute(final InvocationContext invocationContext, final Object object, final Method method,
			final Object... args) throws AppRuntimeException {
		LOGGER.debug(LOG_PREFIX + "execute [invocationContext=" + invocationContext.getTarget().getClass().getName()
				+ ", object=" + object.getClass().getName() + ", method=" + method.getName() + ", args="
				+ Arrays.toString(args) + "]");

		Object result = null;

		try {
			result = invocationContext.proceed();
		} catch (final IllegalAccessException iEx) {
			LOGGER.error(LOG_PREFIX + "execute (IllegalAccessException)", iEx);

			this.propagateCause(iEx);
		} catch (final InvocationTargetException iTEx) {
			LOGGER.error(LOG_PREFIX + "execute (InvocationTargetException)", iTEx);

			this.propagateCause(iTEx);
		} catch (final Exception ex) {
			LOGGER.error(LOG_PREFIX + "execute (Exception)", ex);

			this.propagateCause(ex);
		}

		return result;
	}

	/**
	 * Retrieves cause exception and wraps to {@link AppRuntimeException}.
	 * 
	 * @param throwable
	 *            the throwable
	 */
	private void propagateCause(final Throwable throwable) throws AppRuntimeException {
		LOGGER.debug(LOG_PREFIX + "propagateCause - " + throwable.getClass().getSimpleName());

		ExceptionUtils.propagateCause(throwable);
	}
}
