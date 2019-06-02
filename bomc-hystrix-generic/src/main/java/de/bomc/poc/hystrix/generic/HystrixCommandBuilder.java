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

/**
 * Builder contains all necessary information required to create specific
 * hystrix command.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public class HystrixCommandBuilder {

	private static final String LOG_PREFIX = "HystrixCommandBuilder#";
	private static final Logger LOGGER = Logger.getLogger(HystrixCommandBuilder.class);
	//
	// Other member variables.
	private final GenericSetterBuilder setterBuilder;
	private final CommandActions commandActions;
	private final ExecutionTypeEnum executionTypeEnum;

	/**
	 * Creates a new instance of <code>HystrixCommandBuilder</code>.
	 * 
	 * @param builder
	 *            the given builder of HystrixCommandBuilder.
	 */
	public HystrixCommandBuilder(final Builder builder) {
		LOGGER.debug(LOG_PREFIX + "co [executionType.name=" + builder.executionTypeEnum.name() + "]");

		this.setterBuilder = builder.setterBuilder;
		this.commandActions = builder.commandActions;
		this.executionTypeEnum = builder.executionTypeEnum;
	}

	public static Builder builder() {
		return new Builder();
	}

	public GenericSetterBuilder getSetterBuilder() {
		return this.setterBuilder;
	}

	public CommandActions getCommandActions() {
		LOGGER.debug(LOG_PREFIX + "getCommandActions");

		return this.commandActions;
	}

	public ExecutionTypeEnum getExecutionTypeEnum() {
		return this.executionTypeEnum;
	}

	public static class Builder {

		private GenericSetterBuilder setterBuilder;
		private CommandActions commandActions;
		private ExecutionTypeEnum executionTypeEnum = ExecutionTypeEnum.SYNCHRONOUS;

		/**
		 * Sets the builder to create specific Hystrix setter, for instance
		 * HystrixCommand.Setter
		 * 
		 * @param setterBuilder
		 *            the builder to create specific Hystrix setter
		 * @return this {@link HystrixCommandBuilder.Builder}
		 */
		public Builder setterBuilder(final GenericSetterBuilder setterBuilder) {
			this.setterBuilder = setterBuilder;

			return this;
		}

		/**
		 * Sets command actions {@link CommandActions}.
		 * 
		 * @param commandActions
		 *            the command actions
		 * @return this {@link HystrixCommandBuilder.Builder}
		 */
		public Builder commandActions(final CommandActions commandActions) {
			LOGGER.debug(LOG_PREFIX + "commandActions [commandActions=" + commandActions + "]");

			this.commandActions = commandActions;

			return this;
		}

		/**
		 * Sets execution type, see {@link ExecutionTypeEnum}.
		 * 
		 * @param executionTypeEnum
		 *            the execution type enum
		 * @return this {@link HystrixCommandBuilder.Builder}
		 */
		public Builder executionTypeEnum(final ExecutionTypeEnum executionTypeEnum) {
			LOGGER.debug(LOG_PREFIX + "executionTypeEnum [executionTypeEnum.name=" + executionTypeEnum.name() + "]");

			this.executionTypeEnum = executionTypeEnum;

			return this;
		}

		/**
		 * Creates new {@link HystrixCommandBuilder} instance.
		 * 
		 * @return new {@link HystrixCommandBuilder} instance
		 */
		public HystrixCommandBuilder build() {

			return new HystrixCommandBuilder(this);
		}
	}
}
