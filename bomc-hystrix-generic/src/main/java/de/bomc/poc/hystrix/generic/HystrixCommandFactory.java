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

import com.netflix.hystrix.HystrixInvokable;
import org.apache.log4j.Logger;

import javax.interceptor.InvocationContext;

/**
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public class HystrixCommandFactory {

	private static final String LOG_PREFIX = "HystrixCommandFactory#";
	private static final Logger LOGGER = Logger.getLogger(HystrixCommandFactory.class);
	//
	// Other member variables.
	private static final HystrixCommandFactory INSTANCE = new HystrixCommandFactory();

	/**
	 * Prevents instantiation.
	 */
	private HystrixCommandFactory() {
		//
		// Prevents instantiation.
	}

	public static HystrixCommandFactory getInstance() {
		return INSTANCE;
	}

	public HystrixInvokable<?> create(final InvocationContext invocationContext, final MetaHolder metaHolder) {
		LOGGER.debug(
				LOG_PREFIX + "create [invocationContext=" + invocationContext.getClass().getName() + ", metaHolder]");

		final HystrixInvokable<?> executable = new GenericCommand(invocationContext,
				HystrixCommandBuilderFactory.getInstance().create(metaHolder));

		return executable;
	}
}
