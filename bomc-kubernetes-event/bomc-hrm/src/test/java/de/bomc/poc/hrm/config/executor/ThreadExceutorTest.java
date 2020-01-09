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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * TODO ...
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 07.01.2020
 */
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest(classes = { ThreadPoolConfig.class })
public class ThreadExceutorTest {

	private static final String LOG_PREFIX = ThreadExceutorTest.class.getName() + "#";
	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadExceutorTest.class.getName());
	
	@Autowired
	private TaskExecutor taskExecutor;
	
	@Test
	public void test010_createExecutor_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_createExecutor_pass");
		
		assertThat(this.taskExecutor, notNullValue());
		
		final ThreadPoolTaskExecutor threadPoolTaskExecutor = ((ThreadPoolTaskExecutor)taskExecutor);
		assertThat(threadPoolTaskExecutor.getCorePoolSize(), equalTo(ThreadPoolConfig.CORES));
		assertThat(threadPoolTaskExecutor.getMaxPoolSize(), equalTo(ThreadPoolConfig.CORES * 2));
		assertThat(threadPoolTaskExecutor.getThreadNamePrefix(), equalTo(ThreadPoolConfig.THREAD_NAME_PREFIX));
	}

}
