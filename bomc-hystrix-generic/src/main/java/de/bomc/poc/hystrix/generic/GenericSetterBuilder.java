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
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

import de.bomc.poc.hystrix.generic.conf.HystrixPropertiesManager;
import de.bomc.poc.hystrix.generic.exeception.HystrixPropertyException;
import de.bomc.poc.hystrix.generic.qualifier.HystrixProperty;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Builder for Hystrix Setters: {@link HystrixCommand.Setter}.
 *
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public class GenericSetterBuilder {

	private static final String LOG_PREFIX = "GenericSetterBuilder#";
	private static final Logger LOGGER = Logger.getLogger(GenericSetterBuilder.class);
	//
	// Other member variables.
	private final String groupKey;
	private final String commandKey;
	private List<HystrixProperty> commandProperties = Collections.emptyList();

	public GenericSetterBuilder(final Builder builder) {
		LOGGER.debug(LOG_PREFIX + "co [groupKey=" + builder.groupKey + ", commandKey=" + builder.commandKey + "]");

		this.groupKey = builder.groupKey;
		this.commandKey = builder.commandKey;
		this.commandProperties = builder.commandProperties;
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Creates instance of {@link HystrixCommand.Setter}.
	 *
	 * @return the instance of {@link HystrixCommand.Setter}
	 */
	public HystrixCommand.Setter build() throws HystrixPropertyException {
		LOGGER.debug(LOG_PREFIX + "build");

		final HystrixCommand.Setter setter = HystrixCommand.Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey(this.groupKey))
				.andCommandKey(HystrixCommandKey.Factory.asKey(this.commandKey));

		try {
			setter.andCommandPropertiesDefaults(
					HystrixPropertiesManager.initializeCommandProperties(this.commandProperties));
		} catch (final IllegalArgumentException e) {
			throw new HystrixPropertyException("Failed to set Command properties. " + this.getInfo(), e);
		}

		return setter;
	}

	private String getInfo() {
		LOGGER.debug(LOG_PREFIX + "getInfo [groupKey=" + this.groupKey + ", commandKey=" + this.commandKey + "]");

		return "groupKey: '" + this.groupKey + "', commandKey: '" + this.commandKey + "'";
	}

	public static class Builder {
		private String groupKey;
		private String commandKey;
		private List<HystrixProperty> commandProperties = Collections.emptyList();

		public Builder groupKey(final String groupKey) {
			LOGGER.debug(LOG_PREFIX + "Builder#groupKey [groupKey=" + groupKey + "]");

			this.groupKey = groupKey;

			return this;
		}

		public Builder commandKey(final String commandKey) {
			LOGGER.debug(LOG_PREFIX + "Builder#commandKey [commandKey=" + commandKey + "]");

			this.commandKey = commandKey;

			return this;
		}

		public Builder commandProperties(final List<HystrixProperty> properties) {
			LOGGER.debug(LOG_PREFIX + "Builder#commandProperties [commandProperties=" + this.commandProperties + "]");

			this.commandProperties = properties;

			return this;
		}

		public GenericSetterBuilder build() {
			return new GenericSetterBuilder(this);
		}
	}
}
