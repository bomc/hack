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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.log4j.Logger;

import de.bomc.poc.hystrix.generic.qualifier.HystrixCommand;
import de.bomc.poc.hystrix.generic.qualifier.HystrixProperty;

/**
 * A immutable container to keep all necessary information about current method
 * to build Hystrix command.
 *
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public final class MetaHolder {

	private static final String LOG_PREFIX = "MetaHolder#";
	private static final Logger LOGGER = Logger.getLogger(MetaHolder.class);
	//
	// Other member variables.
	private final HystrixCommand hystrixCommand;
	private final Method method;
	private final Object obj;
	private final Object[] args;
	private final String defaultGroupKey;
	private final String defaultCommandKey;
	private final ExecutionTypeEnum executionTypeEnum;
	// Attributes for the retry mechanism.
	private final long initialInterval;
	private final double multiplier;
	private final int maxRetryCount;

	private static final Function<?, ?> identityFun = new Function<Object, Object>() {
		@Override
		public Object apply(final Object input) {
			return input;
		}
	};

	/**
	 * Create a new instance of <code>MetaHolder</code>.
	 *
	 * @param builder
	 *            the given builder instance.
	 */
	private MetaHolder(final Builder builder) {
		LOGGER.debug(LOG_PREFIX + "co [defaultGroupKey=" + builder.defaultGroupKey + ", defaultCommandKey="
				+ builder.defaultCommandKey + "]");

		this.hystrixCommand = builder.hystrixCommand;
		this.method = builder.method;
		this.obj = builder.obj;
		this.args = builder.args;
		this.defaultGroupKey = builder.defaultGroupKey;
		this.defaultCommandKey = builder.defaultCommandKey;
		this.executionTypeEnum = builder.executionTypeEnum;
		this.initialInterval = builder.initialInterval;
		this.multiplier = builder.multiplier;
		this.maxRetryCount = builder.maxRetryCount;
	}

	public static Builder builder() {
		return new Builder();
	}

	public HystrixCommand getHystrixCommand() {
		return this.hystrixCommand;
	}

	public Method getMethod() {
		return this.method;
	}

	public Object getObj() {
		return this.obj;
	}

	public ExecutionTypeEnum getExecutionTypeEnum() {
		return this.executionTypeEnum;
	}

	public long getInitialInterval() {
		return this.initialInterval;
	}

	public double getMultiplier() {
		return this.multiplier;
	}

	public int getMaxRetryCount() {
		return this.maxRetryCount;
	}

	public Object[] getArgs() {
		if (this.args != null) {
			return Arrays.copyOf(this.args, this.args.length);
		} else {
			return new Object[] {};
		}
	}

	public String getCommandGroupKey() {
		if (this.isCommandAnnotationPresent()) {
			return this.get(this.hystrixCommand.groupKey(), this.defaultGroupKey);
		} else {
			return "";
		}
	}

	public String getCommandKey() {
		if (this.isCommandAnnotationPresent()) {
			return this.get(this.hystrixCommand.commandKey(), this.defaultCommandKey);
		} else {
			return "";
		}
	}

	public boolean isCommandAnnotationPresent() {
		return this.hystrixCommand != null;
	}

	private String get(final String key, final String defaultKey) {
		if (!key.isEmpty()) {
			return key;
		} else {
			return defaultKey;
		}
	}

	public List<HystrixProperty> getCommandProperties() {
		LOGGER.debug(LOG_PREFIX + "getCommandProperties");

		// if (!isCommandAnnotationPresent()) {
		// return Collections.emptyList();
		// }

		return this.getOrDefault(new Supplier<List<HystrixProperty>>() {
			@Override
			public List<HystrixProperty> get() {
				LOGGER.debug(LOG_PREFIX + "getCommandProperties - ImmutableList");

				return Collections.unmodifiableList(Arrays.asList(MetaHolder.this.hystrixCommand.commandProperties()));
			}
		}, new Supplier<List<HystrixProperty>>() {
			@Override
			public List<HystrixProperty> get() {
				LOGGER.debug(LOG_PREFIX + "getCommandProperties - Collections");

				return Collections.emptyList();
			}
		}, this.nonEmptyList());

	}

	private <T> Predicate<List<T>> nonEmptyList() {

		return new Predicate<List<T>>() {
			@Override
			public boolean test(final List<T> input) {
				return input != null && !input.isEmpty();
			}
		};
	}

	@SuppressWarnings("unchecked")
	private <T> T getOrDefault(final Supplier<T> source, final Supplier<T> defaultChoice,
			final Predicate<T> isDefined) {

		return this.getOrDefault(source, defaultChoice, isDefined, (Function<T, T>) identityFun);
	}

	private <T> T getOrDefault(final Supplier<T> source, final Supplier<T> defaultChoice, final Predicate<T> isDefined,
			final Function<T, T> map) {
		T res = source.get();

		if (!isDefined.test(res)) {
			res = defaultChoice.get();
		}

		return map.apply(res);
	}

	public static final class Builder {

		private HystrixCommand hystrixCommand;
		private Method method;
		private Object obj;
		private Object[] args;
		private String defaultGroupKey;
		private String defaultCommandKey;
		private ExecutionTypeEnum executionTypeEnum;
		// Attributes for the retry mechanism.
		private long initialInterval;
		private double multiplier;
		private int maxRetryCount;

		public Builder hystrixCommand(final HystrixCommand hystrixCommand) {
			this.hystrixCommand = hystrixCommand;
			return this;
		}

		public Builder method(final Method method) {
			this.method = method;
			return this;
		}

		public Builder initialInterval(final long initialInterval) {
			this.initialInterval = initialInterval;
			return this;
		}

		public Builder multiplier(final double multiplier) {
			this.multiplier = multiplier;
			return this;
		}

		public Builder maxRetryCount(final int maxRetryCount) {
			this.maxRetryCount = maxRetryCount;
			return this;
		}

		public Builder obj(final Object obj) {
			this.obj = obj;
			return this;
		}

		public Builder args(final Object[] args) {
			this.args = args;
			return this;
		}

		public Builder executionTypeEnum(final ExecutionTypeEnum executionTypeEnum) {
			this.executionTypeEnum = executionTypeEnum;
			return this;
		}

		public Builder defaultGroupKey(final String defGroupKey) {
			this.defaultGroupKey = defGroupKey;
			return this;
		}

		public Builder defaultCommandKey(final String defCommandKey) {
			this.defaultCommandKey = defCommandKey;
			return this;
		}

		public MetaHolder build() {
			return new MetaHolder(this);
		}
	}
}
