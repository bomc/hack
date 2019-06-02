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
package de.bomc.poc.monitor.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.management.JMException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.app.AppMonitorException;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.monitor.jmx.AuthRootServer;
import de.bomc.poc.monitor.jmx.JmxGaugeMonitor;
import de.bomc.poc.monitor.jmx.MBeanInfo;
import de.bomc.poc.monitor.jmx.ThresholdNotificationListener;
import de.bomc.poc.monitor.jmx.jvm.JvmMetrics;
import de.bomc.poc.monitor.jmx.performance.PerformanceTracking;

/**
 * A controller for performing the registration of mbeans.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Startup
@Singleton
public class MonitorControllerSingletonEJB {
	private static final String LOG_PREFIX = "MonitorControllerSingletonEJB#";
	// Defines the thresholds for the process cpu in percent 0-100.
	private static final double THRESHOLD_PROCESS_HIGH = 70.0d;
	private static final double THRESHOLD_PROCESS_LOW = 65.d;
	// Defines the thresholds for the system cpu in percent 0-100.
	private static final double THRESHOLD_SYSTEM_HIGH = 55.0d;
	private static final double THRESHOLD_SYSTEM_LOW = 50.0d;
	// Defines the thresholds for the heap- and non-heap memory in percent
	// 0-100.
	private static final double THRESHOLD_HEAP_HIGH = 90.0d;
	private static final double THRESHOLD_HEAP_LOW = 80.0d;
	private static final double THRESHOLD_NON_HEAP_HIGH = 90.0d;
	private static final double THRESHOLD_NON_HEAP_LOW = 80.0d;
	/**
	 * The logger.
	 */
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	private MBeanController mBeanController;
	private AuthRootServer authRootServer = null;
	// The performance tracking mbean.
	private PerformanceTracking performanceTracking = null;
	// The jvm metrics mbean.
	private JvmMetrics jvmMetrics = null;
	// A list that contais all gauge monitor mbeans.
	private List<JmxGaugeMonitor> gaugeMonitorList = new ArrayList<>();

	@PostConstruct
	public void init() {
		this.logger.debug(LOG_PREFIX + "init");

		authRootServer = new AuthRootServer();
		performanceTracking = new PerformanceTracking();
		jvmMetrics = new JvmMetrics();

		try {
			// Register first parent...
			this.mBeanController.register(authRootServer, null);

			// ...now register leafs with reference to the parent and add
			// notification listener.
			this.mBeanController.register((MBeanInfo) performanceTracking, authRootServer);
			// ...add NotificationListener.
			performanceTracking.addNotificationListener(new ThresholdNotificationListener(), null, null);

			// ...now register leafs with reference to the parent and add
			// notification listener.
			this.mBeanController.register((MBeanInfo) jvmMetrics, authRootServer);

			final String beanPath = this.mBeanController.getBeanPathByMBean(jvmMetrics);
			final ObjectName jvmMetricsObjectName = this.mBeanController.makeObjectName(beanPath, jvmMetrics);

			//
			// Register a monitor, check memory thresholds, use JMX's
			// GaugeMonitor.
			this.gaugeMonitorList.add(this.getGaugeMonitor(THRESHOLD_HEAP_HIGH, THRESHOLD_HEAP_LOW, jvmMetricsObjectName,
					"JvmHeapMetrics", "HeapMemoryUsage"));
			this.gaugeMonitorList.add(this.getGaugeMonitor(THRESHOLD_NON_HEAP_HIGH, THRESHOLD_NON_HEAP_LOW,
					jvmMetricsObjectName, "JvmNonHeapMetrics", "NonHeapMemoryUsage"));

			//
			// Register a monitor, check peaks in activity, use JMX's
			// GaugeMonitor.
			this.gaugeMonitorList.add(this.getGaugeMonitor(THRESHOLD_PROCESS_HIGH, THRESHOLD_PROCESS_LOW,
					jvmMetricsObjectName, "JvmProcessCpuLoadMetrics", "ProcessCpuLoad"));
			this.gaugeMonitorList.add(this.getGaugeMonitor(THRESHOLD_SYSTEM_HIGH, THRESHOLD_SYSTEM_LOW,
					jvmMetricsObjectName, "JvmSystemCpuLoadMetrics", "SystemCpuLoad"));

			this.setupJmxGaugeMonitors();
			
			this.performanceTracking.start();
		} catch (JMException ex) {
			this.logger.error(LOG_PREFIX + "init - failed! ", ex);

			throw new AppMonitorException(LOG_PREFIX + "init - failed! ", true);
		}
	}

	/**
	 * Add to all {@link JmxGaugeMonitor}s a <code>NotificationListener</code>, register them to the MBeanServer and start them.
	 */
	private void setupJmxGaugeMonitors() {
		this.logger.debug(LOG_PREFIX + "setupJmxGaugeMonitors");

		this.gaugeMonitorList.forEach(jmxGaugeMonitor -> {
			try {
				// Order is mandatory.
				// 1. register...
				this.mBeanController.register(jmxGaugeMonitor, (MBeanInfo) jvmMetrics);
				// 2. ... add notification listener...
				this.mBeanController.addNotificationListener(this.getObjectName(jmxGaugeMonitor), new ThresholdNotificationListener(), null, null);
				// 3. ...start.
				jmxGaugeMonitor.start();
			} catch (Exception ex) {
				final String errorMessage = LOG_PREFIX +  "addNotificationListenerAndRegisterToMBeanServer - Could not setup JmxGaugeMonitor! ";
				this.logger.error(errorMessage, ex);
				
				throw new AppMonitorException(errorMessage + ex.getMessage());
			}
		});
	}
	
	private void stopJmxGaugeMonitors() {
		this.logger.debug(LOG_PREFIX + "stopJmxGaugeMonitors");
		
		this.gaugeMonitorList.forEach(jmxGaugeMonitor -> {
				jmxGaugeMonitor.stop();
			
		});
	}

	@PreDestroy
	public void cleanup() {
		this.logger.debug(LOG_PREFIX + "cleanup");

		this.stopJmxGaugeMonitors();
		this.performanceTracking.stop();
		// Unregister MBeans.
		mBeanController.unregisterAll();
	}

	private ObjectName getObjectName(final JmxGaugeMonitor jmxGaugeMonitor) throws MalformedObjectNameException {
		final String beanPath = this.mBeanController.getBeanPathByMBean(jmxGaugeMonitor);
		final ObjectName jmxGaugeMonitorObjectName = this.mBeanController.makeObjectName(beanPath, jmxGaugeMonitor);

		return jmxGaugeMonitorObjectName;
	}

	private JmxGaugeMonitor getGaugeMonitor(final double highValue, final double lowValue,
			final ObjectName observedObjectName, final String mBeanName, final String observedAttribute) {
		//
		// Register a monitor, check peaks in activity, use JMX's
		// GaugeMonitor
		final JmxGaugeMonitor jmxGaugeMonitor = new JmxGaugeMonitor(mBeanName);
		// Setup the monitor: we want to be notified if we have too many
		// clients
		// or too less
		jmxGaugeMonitor.setThresholds(new Double(highValue), new Double(lowValue));
		// Setup the monitor: we want to know if a threshold is exceeded
		jmxGaugeMonitor.setNotifyHigh(true);
		jmxGaugeMonitor.setNotifyLow(true);
		jmxGaugeMonitor.setDifferenceMode(false);
		// Setup the monitor: link to the service MBean
		jmxGaugeMonitor.addObservedObject(observedObjectName);
		jmxGaugeMonitor.setObservedAttribute(observedAttribute);
		// Setup the monitor: a short granularity period
		jmxGaugeMonitor.setGranularityPeriod(50L);

		return jmxGaugeMonitor;
	}
}
