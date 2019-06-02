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
 * Wrapper for command actions combines different actions together.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public class CommandActions {

	private static final String LOG_PREFIX = "CommandActions#";
	private static final Logger LOGGER = Logger.getLogger(CommandActions.class);
	//
	// Other member variables.
	private final CommandAction commandAction;

	/**
	 * Creates a new instance of <code>CommandActions</code>.
	 * 
	 * @param builder
	 *            the builder for the CommandActions.
	 */
	public CommandActions(final Builder builder) {
		LOGGER.debug(LOG_PREFIX + "co [builder=" + builder.commandAction.getActionName() + "]");

		this.commandAction = builder.commandAction;
	}

	public static Builder builder() {
		return new Builder();
	}

	public CommandAction getCommandAction() {
		return this.commandAction;
	}

	public static class Builder {

		private CommandAction commandAction;

		public Builder commandAction(final CommandAction commandAction) {
			this.commandAction = commandAction;
			return this;
		}

		public CommandActions build() {
			return new CommandActions(this);
		}
	}
}
