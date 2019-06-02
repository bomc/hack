/**
 * Project: MY_POC_MICROSERVICE
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.monitor.arq;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.performance.annotation.PerformanceTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.exception.app.AppMonitorException;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.monitor.controller.MBeanController;
import de.bomc.poc.monitor.controller.MonitorControllerSingletonEJB;
import de.bomc.poc.monitor.interceptor.PerformanceTrackingInterceptor;
import de.bomc.poc.monitor.jmx.AuthMBean;
import de.bomc.poc.monitor.jmx.AuthRootServer;
import de.bomc.poc.monitor.jmx.AuthRootServerMBean;
import de.bomc.poc.monitor.jmx.JmxGaugeMonitor;
import de.bomc.poc.monitor.jmx.MBeanInfo;
import de.bomc.poc.monitor.jmx.ThresholdNotificationListener;
import de.bomc.poc.monitor.jmx.jvm.JvmMetrics;
import de.bomc.poc.monitor.jmx.jvm.JvmMetricsMBean;
import de.bomc.poc.monitor.jmx.performance.PerformanceEntry;
import de.bomc.poc.monitor.jmx.performance.PerformanceEntryNotifyData;
import de.bomc.poc.monitor.jmx.performance.PerformanceTracking;
import de.bomc.poc.monitor.jmx.performance.PerformanceTrackingMXBean;
import de.bomc.poc.monitor.qualifier.PerformanceTrackingQualifier;

/**
 * Tests the jmx monitoring.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@PerformanceTest(resultsThreshold = 10)
@RunWith(Arquillian.class)
public class MonitorControllerTestIT extends ArquillianMonitoringBase {
	private static final String WEB_ARCHIVE_NAME = "auth-monitoring";
	private static final String LOG_PREFIX = "MonitorControllerTestIT#";

	@Inject
	@LoggerQualifier
	private Logger logger;
	@EJB
	private MonitorControllerSingletonEJB ejb;
	@Inject
	private SutCdiBean sutCdiBean;
	
	// 'testable = true', means all the tests are running inside of the
	// container.
	@Deployment(testable = true)
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
		webArchive.addClass(MonitorControllerTestIT.class);
		webArchive.addClasses(LoggerProducer.class, LoggerQualifier.class);
		webArchive.addClasses(MonitorControllerSingletonEJB.class, MBeanController.class, AppMonitorException.class,
				AppAuthRuntimeException.class, AuthRootServer.class, AuthMBean.class, AuthRootServerMBean.class,
				JmxGaugeMonitor.class, MBeanInfo.class, ThresholdNotificationListener.class, JvmMetrics.class,
				JvmMetricsMBean.class, PerformanceTracking.class, PerformanceTrackingMXBean.class,
				PerformanceEntry.class, PerformanceEntryNotifyData.class, PerformanceTrackingInterceptor.class,
				PerformanceTrackingQualifier.class, SutCdiBean.class);
		
		System.out.println("MonitorControllerTestIT#createDeployment: " + webArchive.toString(true));

		return webArchive;
	}

	@Before
	public void setup() {
		this.logger.debug(LOG_PREFIX + "setup");

		assertThat(this.logger, notNullValue());
		assertThat(this.ejb, notNullValue());
		assertThat(this.sutCdiBean, notNullValue());
	}

	@After
	public void cleanup() {
		this.logger.debug(LOG_PREFIX + "cleanup");

	}

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=MonitorControllerTestIT#test01_registerMBeans_Pass
	 * </pre>
	 * @throws MalformedObjectNameException
	 * @throws ReflectionException 
	 * @throws MBeanException 
	 * @throws InstanceNotFoundException 
	 * @throws AttributeNotFoundException 
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test01_registerMBeans_Pass() throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
		this.logger.debug(LOG_PREFIX + "test01_registerMBeans_pass");

		// Warmup, run 100 methods invocations to determine the average time. 
		for (int i = 0; i < 100; i++) {
			this.sutCdiBean.methodUnderTest(0);
		}
		
		for (int i = 0; i < 100; i++) {
			this.sutCdiBean.methodUnderTest((int)getRandomNumberInRange(100, 130));
		}
		
		final Set<ObjectName> queryNameSet = ManagementFactory.getPlatformMBeanServer().queryNames(new ObjectName("*" + AuthMBean.DOMAIN_NAME + ":*"), null);
		final List<ObjectName> collect = queryNameSet.stream().filter(queryName -> queryName.toString().contains("PerformanceTracking")).collect(Collectors.toList());

		final Object attributeObject = ManagementFactory.getPlatformMBeanServer().getAttribute(collect.get(0), "EmittedNotifications");
		String[] notifications = (String[]) attributeObject;
		
		assertThat(notifications.length, greaterThan(0));
		
		this.logger.debug(LOG_PREFIX + "test01_registerMBeans_pass [" + notifications[0] + "]");

//		try {
//			java.util.concurrent.TimeUnit.MINUTES.sleep(3);
//		} catch (InterruptedException e) {
//			// Ignore
//		}
	}
	
	/**
	 * Defines a value between min and max.
	 * 
	 * @param min
	 *            the minimum value.
	 * @param max
	 *            the maximum value.
	 * @return a random value between min and max.
	 */
	private long getRandomNumberInRange(long min, long max) {
		Random r = new Random();
		return r.longs(min, (max + 1)).limit(1).findFirst().getAsLong();

	}
}
