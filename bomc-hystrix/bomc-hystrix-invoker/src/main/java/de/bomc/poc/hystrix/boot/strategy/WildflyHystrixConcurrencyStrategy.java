package de.bomc.poc.hystrix.boot.strategy;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;

import de.bomc.poc.logging.qualifier.LoggerQualifier;

/**
 * Overwrites the hystrix strategy to define a threadpool.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
public class WildflyHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

	private static final String LOG_PREFIX = "WildflyHystrixConcurrencyStrategy#";

	// The number of threads to keep in the pool, even if thy are idle.
	private static final Integer CORE_POOL_SIZE = 5;
	// The maximum number of threads to allow in the pool.
	private static final Integer MAX_POOL_SIZE = 10;
	// When the number of threads is greater than the core,
	// this is the maximum time that excess idle threads will wait for new tasks
	// before terminating.
	private static final Long KEEP_ALIVE_TIME = 5L;
	// Size of the blocking queue.
	private static final Integer BLOCKING_QUEUE_SIZE = 10;

	@Inject
	@LoggerQualifier
	private Logger logger;
	private final ManagedThreadFactory threadFactory;

	/**
	 * Creates a new instance of <code>WildflyHystrixConcurrencyStrategy</code>.
	 * 
	 * @param threadFactory
	 *            the threadFactory resource configured in wildfly.
	 */
	public WildflyHystrixConcurrencyStrategy(final ManagedThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
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
	 *            BlockingQueue<Runnable> as provided by getBlockingQueue(int)
	 *            qparam workQueue BlockingQueue<Runnable> as provided by
	 *            getBlockingQueue(int)
	 */
	@Override
	public ThreadPoolExecutor getThreadPool(final HystrixThreadPoolKey threadPoolKey,
			final HystrixProperty<Integer> corePoolSize, final HystrixProperty<Integer> maximumPoolSize,
			final HystrixProperty<Integer> keepAliveTime, final TimeUnit unit,
			final BlockingQueue<Runnable> workQueue) {

		this.logger.debug(LOG_PREFIX + "getThreadPool [threadPoolKey=" + threadPoolKey.name() + ", corePoolSize="
				+ corePoolSize.toString() + ", maximumPoolSize=" + maximumPoolSize.toString() + ", keepAliveTime="
				+ keepAliveTime.toString() + ", unit=" + unit.name() + "]");

		// All threads will run as part of this application component.
		final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
				KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(BLOCKING_QUEUE_SIZE),
				threadFactory);

		this.logger.debug(LOG_PREFIX + "getThreadPool - initialized threadpool executor [threadPoolKey="
				+ threadPoolKey.name() + "]");

		return threadPoolExecutor;
	}
}
