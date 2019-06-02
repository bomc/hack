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
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public class HystrixCommandBuilderFactory {

	private static final String LOG_PREFIX = "HystrixCommandBuilderFactory#";
	private static final Logger LOGGER = Logger.getLogger(HystrixCommandBuilderFactory.class);
	private static final HystrixCommandBuilderFactory INSTANCE = new HystrixCommandBuilderFactory();

	public static HystrixCommandBuilderFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Prevents instantiation.
	 */
	private HystrixCommandBuilderFactory() {
		//
		// Prevents instantiation.
	}

	public <ResponseType> HystrixCommandBuilder create(final MetaHolder metaHolder) {
		LOGGER.debug(LOG_PREFIX + "create [metaHolder]");

		if (metaHolder == null || !metaHolder.isCommandAnnotationPresent()) {
			throw new IllegalArgumentException(LOG_PREFIX + "create - metaholder is null [metaHolder=" + metaHolder
					+ "] or annotation is not present.");
		}

		return HystrixCommandBuilder.builder().setterBuilder(this.createGenericSetterBuilder(metaHolder))
				.commandActions(this.createCommandActions(metaHolder))
				.executionTypeEnum(metaHolder.getExecutionTypeEnum()).build();
	}

	private GenericSetterBuilder createGenericSetterBuilder(final MetaHolder metaHolder) {
		LOGGER.debug(LOG_PREFIX + "createGenericSetterBuilder [metaHolder]");

		final GenericSetterBuilder.Builder setterBuilder = GenericSetterBuilder.builder()
				.groupKey(metaHolder.getCommandGroupKey()).commandKey(metaHolder.getCommandKey())
				.commandProperties(metaHolder.getCommandProperties());

		return setterBuilder.build();
	}

	private CommandActions createCommandActions(final MetaHolder metaHolder) {
		LOGGER.debug(LOG_PREFIX + "createCommandActions [metaHolder]");

		final CommandAction commandAction = this.createCommandAction(metaHolder);

		return CommandActions.builder().commandAction(commandAction).build();
	}

	private CommandAction createCommandAction(final MetaHolder metaHolder) {
		LOGGER.debug(LOG_PREFIX + "createCommandAction [metaHolder]");

		return new MethodExecutionAction(metaHolder.getObj(), metaHolder.getMethod(), metaHolder.getArgs(), metaHolder);
	}
}
