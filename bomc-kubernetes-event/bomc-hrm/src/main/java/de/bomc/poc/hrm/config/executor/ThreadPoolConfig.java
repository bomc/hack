/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: micha
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.config.executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configures a custom {@code TaskExecutor}.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 05.01.2020
 */
@Configuration
@EnableAsync // It detects -at Async annotation, see AsyncMessageProducer.
public class ThreadPoolConfig {

	protected static final int CORES = Runtime.getRuntime().availableProcessors();
	protected static final int QUEUE_CAPACITY = 25;
	protected static final String THREAD_NAME_PREFIX = "bomc-hrm-executor-";
	protected static final int AWAIT_TERMINATION_SECONDS = 30;
	protected static final boolean WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN = true;
	protected static final boolean ALLOW_CORE_THREAD_TIMEOUT = true;

	/**
	 * The TaskExecutor helps to customize the thread executor such as configuring
	 * number of threads for an application, queue limit size and so on. Spring will
	 * specifically look for this bean when the server is started. If this bean is
	 * not defined, Spring will create SimpleAsyncTaskExecutor by default.
	 * 
	 * @return a custom configured taskExecutor.
	 */
	@Bean
	public TaskExecutor taskExecutor() {

		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(CORES);
		threadPoolTaskExecutor.setMaxPoolSize(CORES * 2);
		threadPoolTaskExecutor.setQueueCapacity(QUEUE_CAPACITY);
		threadPoolTaskExecutor.setThreadNamePrefix(THREAD_NAME_PREFIX);
		threadPoolTaskExecutor.setAllowCoreThreadTimeOut(ALLOW_CORE_THREAD_TIMEOUT);
		
		// By default, wait for tasks to finish, and wait up to an hour.
		threadPoolTaskExecutor.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
		threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN);

		threadPoolTaskExecutor.afterPropertiesSet();

		return threadPoolTaskExecutor;
	}
}
