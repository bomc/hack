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

import com.netflix.hystrix.HystrixCommand;

import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.app.AppRuntimeException;

import org.apache.log4j.Logger;

/**
 * Base class for hystrix commands.
 *
 * @param <T>
 *            the return type
 *
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public abstract class AbstractHystrixCommand<T> extends HystrixCommand<T> {

	private static final String LOG_PREFIX = "AbstractHystrixCommand#";
	private static final Logger LOGGER = Logger.getLogger(AbstractHystrixCommand.class);
	//
	// Other member variables.
	private final CommandActions commandActions;
	private final ExecutionTypeEnum executionTypeEnum;

	/**
	 * Creates a new instance of <code>AbstractHystrixCommand</code>.
	 *
	 * @param hystrixCommandBuilder
	 *            the hystrixCommandBuilder for HystrixCommandBuilder.
	 */
	protected AbstractHystrixCommand(final HystrixCommandBuilder hystrixCommandBuilder) {
		super(hystrixCommandBuilder.getSetterBuilder().build());

		this.commandActions = hystrixCommandBuilder.getCommandActions();
		this.executionTypeEnum = hystrixCommandBuilder.getExecutionTypeEnum();
	}

	/**
	 * Gets command action.
	 *
	 * @return command action
	 */
	protected CommandAction getCommandAction() {
		return this.commandActions.getCommandAction();
	}

	protected ExecutionTypeEnum getExecutionTypeEnum() {
		LOGGER.debug(LOG_PREFIX + "getExecutionTypeEnum [executionTypeEnum=" + this.executionTypeEnum.name() + "]");

		return this.executionTypeEnum;
	}

	/**
	 * Executes an action. If an action has failed and an exception is ignorable
	 * then propagate it as HystrixBadRequestException otherwise propagate
	 * original exception to trigger fallback method. Note: If an exception
	 * occurred in a command directly extends {@link Throwable} then this
	 * exception cannot be re-thrown as original exception because
	 * HystrixCommand.run() allows throw subclasses of {@link Exception}. Thus
	 * we need to wrap cause in RuntimeException, anyway in this case the
	 * fallback logic will be triggered.
	 *
	 * @param action
	 *            the action
	 * @return result of command action execution
	 */
	Object process(final Action action) throws Exception {
		LOGGER.debug(LOG_PREFIX + "process [action=" + action + "]");

		final Object result;

		try {
			result = action.execute();
			// flushCache();
		} catch (final AppRuntimeException throwable) {
			LOGGER.error(LOG_PREFIX + "process [throwable=" + throwable.getClass().getSimpleName() + "]");

			final Throwable cause = throwable.getCause();

			if (cause instanceof RuntimeException) {

				throw (RuntimeException) cause;
			} else if (cause instanceof Exception) {

				throw (Exception) cause;
			} else {
				// Instance of Throwable.
				throw new AppRuntimeException(AppRuntimeException.wrap(cause), ErrorCode.RESILIENCE_10500);
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	protected abstract T run() throws Exception;

	// /**
	// * Clears cache for the specified hystrix command.
	// */
	// protected void flushCache() {
	// if (cacheRemoveInvocationContext != null) {
	// HystrixRequestCacheManager.getInstance().clearCache(cacheRemoveInvocationContext);
	// }
	// }

	/**
	 * Common action.
	 */
	abstract class Action {
		/**
		 * Each implementation of this method should wrap any exceptions in
		 * CommandActionExecutionException.
		 *
		 * @return execution result
		 * @throws AppRuntimeException
		 */
		abstract Object execute() throws AppRuntimeException;
	}
}
