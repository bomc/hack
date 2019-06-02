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
package de.bomc.poc.hystrix.boot.strategy;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;

import de.bomc.poc.logging.qualifier.LoggerQualifier;

import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutors;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.inject.Inject;
import java.lang.Runnable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Overwrites the hystrix strategy to define a threadpool.
 *
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public class WildflyHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

	private static final String LOG_PREFIX = "WildflyHystrixConcurrencyStrategy#";

	// The number of threads to keep in the pool, even if thy are idle.
	private static final Integer CORE_POOL_SIZE = 2;
	// The maximum number of threads to allow in the pool.
	private static final Integer MAX_POOL_SIZE = 5;
	// When the number of threads is greater than the core,
	// this is the maximum time that excess idle threads will wait for new tasks
	// before terminating.
	private static final Long KEEP_ALIVE_TIME = 2L;
	// Size of the blocking queue.
	private static final Integer BLOCKING_QUEUE_SIZE = 5;

	@Inject
	@LoggerQualifier
	private Logger logger;
	@Resource
	private ManagedThreadFactory threadFactory;

	/**
	 * Creates a new instance of <code>WildflyHystrixConcurrencyStrategy</code>
	 * default-co.
	 */
	public WildflyHystrixConcurrencyStrategy() {
		//
	}

	@Override
	public <T> Callable<T> wrapCallable(final Callable<T> callable) {
		return super.wrapCallable(ManagedExecutors.managedTask(callable, null));
	}

	/**
	 * Factory method to provide ThreadPoolExecutor instances as desired.
	 * 
	 * @param threadPoolKey
	 *            HystrixThreadPoolKey representing the HystrixThreadPool that
	 *            this ThreadPoolExecutor will be used for.
	 * @param corePoolSize
	 *            Core number of threads requested via properties (or system
	 *            default if no properties set).
	 * @param maximumPoolSize
	 *            Max number of threads requested via properties (or system
	 *            default if no properties set).
	 * @param keepAliveTime
	 *            Keep-alive time for threads requested via properties (or
	 *            system default if no properties set).
	 * @param unit
	 *            TimeUnit corresponding with keepAliveTimeworkQueue
	 *            BlockingQueue as provided by getBlockingQueue(int).
	 * @param workQueue
	 *            BlockingQueue as provided by getBlockingQueue(int).
	 */
	@Override
	public ThreadPoolExecutor getThreadPool(final HystrixThreadPoolKey threadPoolKey,
			final HystrixProperty<Integer> corePoolSize, final HystrixProperty<Integer> maximumPoolSize,
			final HystrixProperty<Integer> keepAliveTime, final TimeUnit unit,
			final BlockingQueue<Runnable> workQueue) {

		this.logger.debug(LOG_PREFIX + "getThreadPool - default [threadPoolKey=" + threadPoolKey.name()
				+ ", corePoolSize=" + corePoolSize.get() + ", maximumPoolSize=" + maximumPoolSize.get()
				+ ", keepAliveTime=" + keepAliveTime.get() + ", unit=" + unit.name()
				+ "], override with [threadPoolKey=" + threadPoolKey.name() + ", corePoolSize=" + CORE_POOL_SIZE
				+ ", maximumPoolSize=" + MAX_POOL_SIZE + ", keepAliveTime=" + KEEP_ALIVE_TIME + ", unit="
				+ TimeUnit.SECONDS.name() + "]");

		// All threads will run as part of this application component.
		final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
				KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(BLOCKING_QUEUE_SIZE),
				this.threadFactory);

		this.logger.debug(LOG_PREFIX + "getThreadPool - initialized threadpool executor [threadPoolKey="
				+ threadPoolKey.name() + "]");

		return threadPoolExecutor;
	}
}
