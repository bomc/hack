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

/**
 * Determines execution types.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public enum ExecutionTypeEnum {

	/**
	 * Used for synchronous execution of command.
	 */
	SYNCHRONOUS;

	/**
	 * Gets execution type for specified class type.
	 * 
	 * @param type
	 *            the given type.
	 * @return the execution type {@link ExecutionTypeEnum}.
	 */
	public static ExecutionTypeEnum getExecutionTypeEnum(final Class<?> type) {
		return ExecutionTypeEnum.SYNCHRONOUS;
	}
}
