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

import de.bomc.poc.exception.core.app.AppRuntimeException;

/**
 * A action to encapsulate some logic to process it in a Hystrix command.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public interface CommandAction {

	MetaHolder getMetaHolder();

	/**
	 * Executes action in accordance with the given execution type.
	 * 
	 * @param invovationContext
	 *            contextual information about a method invocation in CDI
	 *            context.
	 * @param executionTypeEnum
	 *            the execution type enum.
	 * @return result of execution.
	 * @throws AppRuntimeException
	 *             is thrown during the command invocation if something failed.
	 */
	Object execute(InvocationContext invovationContext, ExecutionTypeEnum executionTypeEnum) throws AppRuntimeException;

	/**
	 * Executes action with parameters in accordance with the given execution
	 * type.
	 * 
	 * @param invovationContext
	 *            contextual information about a method invocation in CDI
	 *            context.
	 * @param executionTypeEnum
	 *            the execution type
	 * @param args
	 *            the parameters of the action
	 * @return result of execution
	 * @throws AppRuntimeException
	 *             is thrown during the command invocation if something failed.
	 */
	Object executeWithArgs(InvocationContext invovationContext, ExecutionTypeEnum executionTypeEnum, Object[] args)
			throws AppRuntimeException;

	/**
	 * Gets action name. Useful for debugging.
	 * 
	 * @return the action name
	 */
	String getActionName();
}
