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

import javax.interceptor.InvocationContext;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.hystrix.generic.exeception.RetryException;

/**
 * Implementation of AbstractHystrixCommand which returns an Object as result.
 *
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public class GenericCommand extends AbstractHystrixCommand<Object> {

	private static final String LOG_PREFIX = "GenericCommand#";
	private static final Logger LOGGER = Logger.getLogger(GenericCommand.class);

	private final InvocationContext invocationContext;

	/**
	 * Creates a new instance of <code>GenericCommand</code>
	 *
	 * @param invocationContext
	 *            the contextual information about a method invocation in CDI
	 *            context.
	 * @param builder
	 *            the a initialized histrixCommandBuilder.
	 */
	public GenericCommand(final InvocationContext invocationContext, final HystrixCommandBuilder builder) {
		super(builder);

		LOGGER.debug(LOG_PREFIX + "co [invocationContext, builder]");

		this.invocationContext = invocationContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object run() throws Exception {

		LOGGER.debug(LOG_PREFIX + "run - execute command: [commandKey.name=" + this.getCommandKey().name() + "]");

		return this.process(new Action() {
			@Override
			Object execute() throws AppRuntimeException, RetryException {
				final ExecutionTypeEnum executionTypeEnum = GenericCommand.this.getExecutionTypeEnum();
				final CommandAction commandAction = GenericCommand.this.getCommandAction();

				return commandAction.execute(GenericCommand.this.invocationContext, executionTypeEnum);
			}
		});
	}
}
