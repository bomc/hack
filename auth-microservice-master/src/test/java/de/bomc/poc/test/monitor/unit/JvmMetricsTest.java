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
package de.bomc.poc.test.monitor.unit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.monitor.MonitorNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.bomc.poc.monitor.controller.MBeanController;
import de.bomc.poc.monitor.jmx.AuthMBean;
import de.bomc.poc.monitor.jmx.AuthRootServer;
import de.bomc.poc.monitor.jmx.JmxGaugeMonitor;
import de.bomc.poc.monitor.jmx.MBeanInfo;
import de.bomc.poc.monitor.jmx.jvm.JvmMetrics;
import de.bomc.poc.monitor.jmx.jvm.JvmMetricsMBean;

/**
 * Tests the JvmMetrics MBean.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JvmMetricsTest {
	private static final String LOG_PREFIX = "JvmMetricsTest#";
	private JMXConnectorServer jMXConnectorServer;
	private JMXConnector jMXConnector;

	@Test
	public void test01_readJmvSystemData_pass() {
		System.out.println(LOG_PREFIX + "test01_readJmvSystemData_pass");

		JvmMetricsMBean mbean = new JvmMetrics();

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// Ignore
		}

		System.out.println(Double.toString(mbean.getProcessCpuLoad()));
		System.out.println(Double.toString(mbean.getSystemCpuLoad()));
	}

	@Test
	public void test02_readMemoryData_pass() {
		System.out.println(LOG_PREFIX + "test02_readMemoryData_pass");

		JvmMetricsMBean mbean = new JvmMetrics();

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// Ignore
		}

		System.out.println(Double.toString(mbean.getHeapMemoryUsage()));
		System.out.println(Double.toString(mbean.getNonHeapMemoryUsage()));		
	}
	
	@Test
	public void test10_registerMBeanToParentMBean_pass() throws Exception {
		System.out.println(LOG_PREFIX + "test10_registerMBeanToParentMBean_pass");

		try {
			final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			// The parent mbean.
			final MBeanInfo mBeanParent = new AuthRootServer();
			//
			final JvmMetrics mBeanLeaf = new JvmMetrics();

			final MBeanController mBeanController = new MBeanController(mBeanServer);

			// Register first parent...
			mBeanController.register(mBeanParent, null);
			// ...now register leaf with reference to the parent.
			mBeanController.register((MBeanInfo) mBeanLeaf, mBeanParent);

			// Check for registering in MBeanServer.
			this.setUpJMXConnector(mBeanServer);

			final Set<ObjectName> setObjNames = this.dump();

			// Check registered objectNames.
			assertThat(setObjNames, hasItems(new ObjectName("de.bomc.auth.microservice:name0=AuthMicroservice"),
					new ObjectName("de.bomc.auth.microservice:name0=AuthMicroservice,name1=JvmMetrics")));

			mBeanController.unregisterAll();
		} finally {
			this.tearDown();
		}
	}

	@Test
	public void test20_gaugeMonitor_OS_pass() throws Exception {
		System.out.println(LOG_PREFIX + "test20_gaugeMonitor_OS_pass");

		double thresholdProcessHigh = 7.0d;
		double thresholdProcessLow = 6.9d;
		double thresholdSystemHigh = 5.0d;
		double thresholdSystemLow = 4.9d;
		
		try {
			final AtomicBoolean notificationSet = new AtomicBoolean(false);

			// Get the MBeanServer.
			final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			
			// The parent mbean.
			final MBeanInfo mBeanParent = new AuthRootServer();
			final JvmMetrics jvmMetrics = new JvmMetrics();
			final MBeanController mBeanController = new MBeanController(mBeanServer);
			// Register first parent...
			mBeanController.register(mBeanParent, null);
			// ...now register leaf with reference to the parent.
			mBeanController.register((MBeanInfo) jvmMetrics, mBeanParent);

			final String beanPath = mBeanController.getBeanPathByMBean(jvmMetrics);
			final ObjectName jvmMetricsObjectName = mBeanController.makeObjectName(beanPath, jvmMetrics);

			System.out.println(LOG_PREFIX + "test20_gaugeMonitor_OS_pass [objectName=" + jvmMetricsObjectName + "]");

			//
			// Register a monitor, check peaks in activity, use JMX's
			// GaugeMonitor.
			final JmxGaugeMonitor jmxProcessGaugeMonitor = this.getGaugeMonitor(thresholdProcessHigh, thresholdProcessLow, jvmMetricsObjectName,
					"JvmProcessCpuLoadMetricsGaugeMonitor", "ProcessCpuLoad");
			mBeanController.register(jmxProcessGaugeMonitor, (MBeanInfo) jvmMetrics);
			
			final JmxGaugeMonitor jmxSystemGaugeMonitor = this.getGaugeMonitor(thresholdSystemHigh, thresholdSystemLow, jvmMetricsObjectName,
					"JvmSystemCpuLoadMetricsGaugeMonitor", "SystemCpuLoad");
			mBeanController.register(jmxSystemGaugeMonitor, (MBeanInfo) jvmMetrics);
			
			// Setup the monitor: register a listener
			this.setUpJMXConnector(mBeanServer);
			final MBeanServerConnection connection = this.conn();

			// Add a up notification listener to the connection
			connection.addNotificationListener(this.getObjectName(mBeanController, jmxProcessGaugeMonitor),
					new NotificationListener() {
						public void handleNotification(Notification notification, Object handback) {

							if (notification instanceof MonitorNotification) {
								final MonitorNotification mNotification = (MonitorNotification) notification;

								if (mNotification.getType().equals(MonitorNotification.THRESHOLD_ERROR)) {
									//
									// Notification type denoting that the type
									// of the thresholds, offset or modulus is
									// not correct.
									
								} else if (mNotification.getType()
										.equals(MonitorNotification.THRESHOLD_HIGH_VALUE_EXCEEDED)) {
									//
									// Notification type denoting that the
									// observed attribute has exceeded the
									// threshold high value.
									assertThat((double)mNotification.getTrigger(), is(greaterThanOrEqualTo(thresholdProcessHigh)));
								} else if (mNotification.getType()
										.equals(MonitorNotification.THRESHOLD_LOW_VALUE_EXCEEDED)) {
									//
									// Notification type denoting that the
									// observed attribute has exceeded the
									// threshold low value.
									assertThat((double)mNotification.getTrigger(), is(lessThanOrEqualTo(thresholdProcessLow)));
								}

								System.out.println(LOG_PREFIX + "test20_gaugeMonitor_OS_pass - [observedAttribute="
										+ mNotification.getObservedAttribute() + ", trigger="
										+ mNotification.getTrigger() + "]" + mNotification.toString());

//								synchronized (notificationSet) {
//									notificationSet.set(true);
//									notificationSet.notify();
//								}
							}
						}
					}, null, null);

			// Add a up notification listener to the connection
			connection.addNotificationListener(this.getObjectName(mBeanController, jmxSystemGaugeMonitor),
					new NotificationListener() {
						public void handleNotification(Notification notification, Object handback) {

							if (notification instanceof MonitorNotification) {
								MonitorNotification mNotification = (MonitorNotification) notification;

								if (mNotification.getType().equals(MonitorNotification.THRESHOLD_ERROR)) {
									//
									// Notification type denoting that the type
									// of the thresholds, offset or modulus is
									// not correct.
									
								} else if (mNotification.getType()
										.equals(MonitorNotification.THRESHOLD_HIGH_VALUE_EXCEEDED)) {
									//
									// Notification type denoting that the
									// observed attribute has exceeded the
									// threshold high value.
									assertThat((double)mNotification.getTrigger(), is(greaterThanOrEqualTo(thresholdSystemHigh)));
								} else if (mNotification.getType()
										.equals(MonitorNotification.THRESHOLD_LOW_VALUE_EXCEEDED)) {
									//
									// Notification type denoting that the
									// observed attribute has exceeded the
									// threshold low value.
									assertThat((double)mNotification.getTrigger(), is(lessThanOrEqualTo(thresholdSystemLow)));
								}

								System.out.println(LOG_PREFIX + "test20_gaugeMonitor_OS_pass - [observedAttribute="
										+ mNotification.getObservedAttribute() + ", trigger="
										+ mNotification.getTrigger() + "]" + mNotification.toString());

//								synchronized (notificationSet) {
//									notificationSet.set(true);
//									notificationSet.notify();
//								}
							}
						}
					}, null, null);
			
			jmxProcessGaugeMonitor.start();
			jmxSystemGaugeMonitor.start();
			
			synchronized (notificationSet) {
				if (!notificationSet.get()) {
					notificationSet.wait(55000);
				}
			}

			jmxProcessGaugeMonitor.stop();
			jmxSystemGaugeMonitor.stop();
			
			// Unregister MBeans.
			mBeanController.unregisterAll();
		} finally {
			//
			// Cleanup resources.
			this.tearDown();
		}
	}

	@Test
	public void test30_gaugeMonitor_Memory_pass() throws Exception {
		System.out.println(LOG_PREFIX + "test30_gaugeMonitor_Memory_pass");

		double thresholdHeapHigh = 0.55d;
		double thresholdHeapLow = 0.54d;
		double thresholdNonHeapHigh = 0.2d;
		double thresholdNonHeapLow = 0.1d;
		
		try {
			final AtomicBoolean notificationSet = new AtomicBoolean(false);

			// Get the MBeanServer.
			final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			
			// The parent mbean.
			final MBeanInfo mBeanParent = new AuthRootServer();
			final JvmMetrics jvmMetrics = new JvmMetrics();
			final MBeanController mBeanController = new MBeanController(mBeanServer);
			// Register first parent...
			mBeanController.register(mBeanParent, null);
			// ...now register leaf with reference to the parent.
			mBeanController.register((MBeanInfo) jvmMetrics, mBeanParent);

			final String beanPath = mBeanController.getBeanPathByMBean(jvmMetrics);
			final ObjectName jvmMetricsObjectName = mBeanController.makeObjectName(beanPath, jvmMetrics);

			System.out.println(LOG_PREFIX + "test30_gaugeMonitor_Memory_pass [objectName=" + jvmMetricsObjectName + "]");

			//
			// Register a monitor, check peaks in activity, use JMX's
			// GaugeMonitor.
			final JmxGaugeMonitor jmxHeapGaugeMonitor = this.getGaugeMonitor(thresholdHeapHigh, thresholdHeapLow, jvmMetricsObjectName,
					"JvmHeapMetricsGaugeMonitor", "HeapMemoryUsage");
			mBeanController.register(jmxHeapGaugeMonitor, (MBeanInfo) jvmMetrics);
			
			final JmxGaugeMonitor jmxNonHeapGaugeMonitor = this.getGaugeMonitor(thresholdNonHeapHigh, thresholdNonHeapLow, jvmMetricsObjectName,
					"JvmNonHeapMetricsGaugeMonitor", "NonHeapMemoryUsage");
			mBeanController.register(jmxNonHeapGaugeMonitor, (MBeanInfo) jvmMetrics);
			
			// Setup the monitor: register a listener
			this.setUpJMXConnector(mBeanServer);
			final MBeanServerConnection connection = this.conn();

			// Add a up notification listener to the connection
			connection.addNotificationListener(this.getObjectName(mBeanController, jmxHeapGaugeMonitor),
					new NotificationListener() {
						public void handleNotification(Notification notification, Object handback) {

							if (notification instanceof MonitorNotification) {
								final MonitorNotification mNotification = (MonitorNotification) notification;

								if (mNotification.getType().equals(MonitorNotification.THRESHOLD_ERROR)) {
									//
									// Notification type denoting that the type
									// of the thresholds, offset or modulus is
									// not correct.
									
								} else if (mNotification.getType()
										.equals(MonitorNotification.THRESHOLD_HIGH_VALUE_EXCEEDED)) {
									//
									// Notification type denoting that the
									// observed attribute has exceeded the
									// threshold high value.
									assertThat((double)mNotification.getTrigger(), is(greaterThanOrEqualTo(thresholdHeapHigh)));
								} else if (mNotification.getType()
										.equals(MonitorNotification.THRESHOLD_LOW_VALUE_EXCEEDED)) {
									//
									// Notification type denoting that the
									// observed attribute has exceeded the
									// threshold low value.
									assertThat((double)mNotification.getTrigger(), is(lessThanOrEqualTo(thresholdHeapLow)));
								}

								System.out.println(LOG_PREFIX + "test30_gaugeMonitor_Memory_pass - [observedAttribute="
										+ mNotification.getObservedAttribute() + ", trigger="
										+ mNotification.getTrigger() + "]" + mNotification.toString());

//								synchronized (notificationSet) {
//									notificationSet.set(true);
//									notificationSet.notify();
//								}
							}
						}
					}, null, null);

			// Add a up notification listener to the connection
			connection.addNotificationListener(this.getObjectName(mBeanController, jmxNonHeapGaugeMonitor),
					new NotificationListener() {
						public void handleNotification(Notification notification, Object handback) {

							if (notification instanceof MonitorNotification) {
								MonitorNotification mNotification = (MonitorNotification) notification;

								if (mNotification.getType().equals(MonitorNotification.THRESHOLD_ERROR)) {
									//
									// Notification type denoting that the type
									// of the thresholds, offset or modulus is
									// not correct.
									
								} else if (mNotification.getType()
										.equals(MonitorNotification.THRESHOLD_HIGH_VALUE_EXCEEDED)) {
									//
									// Notification type denoting that the
									// observed attribute has exceeded the
									// threshold high value.
									assertThat((double)mNotification.getTrigger(), is(greaterThanOrEqualTo(thresholdNonHeapHigh)));
								} else if (mNotification.getType()
										.equals(MonitorNotification.THRESHOLD_LOW_VALUE_EXCEEDED)) {
									//
									// Notification type denoting that the
									// observed attribute has exceeded the
									// threshold low value.
									assertThat((double)mNotification.getTrigger(), is(lessThanOrEqualTo(thresholdNonHeapLow)));
								}

								System.out.println(LOG_PREFIX + "test30_gaugeMonitor_Memory_pass - [observedAttribute="
										+ mNotification.getObservedAttribute() + ", trigger="
										+ mNotification.getTrigger() + "]" + mNotification.toString());

//								synchronized (notificationSet) {
//									notificationSet.set(true);
//									notificationSet.notify();
//								}
							}
						}
					}, null, null);
			
			jmxHeapGaugeMonitor.start();
			jmxNonHeapGaugeMonitor.start();
			
			synchronized (notificationSet) {
				if (!notificationSet.get()) {
					notificationSet.wait(55000);
				}
			}

			jmxHeapGaugeMonitor.stop();
			jmxNonHeapGaugeMonitor.stop();
			
			// Unregister MBeans.
			mBeanController.unregisterAll();
		} finally {
			//
			// Cleanup resources.
			this.tearDown();
		}
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
	
	// __________________________________________________________
	// Jmx helper methods.
	// ----------------------------------------------------------
	private Set<ObjectName> dump() throws IOException {
		System.out.println(LOG_PREFIX + "dump");

		Set<ObjectName> beans;

		try {
			beans = this.conn().queryNames(new ObjectName(AuthMBean.DOMAIN_NAME + ":*"), null);
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}

		for (ObjectName bean : beans) {
			System.out.println("dump - [bean = " + bean.toString() + "]");
		}

		return beans;
	}
	
	private ObjectName getObjectName(final MBeanController mBeanController, final JmxGaugeMonitor jmxGaugeMonitor)
			throws Exception {
		final String beanPath = mBeanController.getBeanPathByMBean(jmxGaugeMonitor);
		final ObjectName jmxGaugeMonitorObjectName = mBeanController.makeObjectName(beanPath, jmxGaugeMonitor);

		System.out.println(LOG_PREFIX + "getObjectName [objectName=" + jmxGaugeMonitorObjectName + "]");

		return jmxGaugeMonitorObjectName;
	}

	private void setUpJMXConnector(final MBeanServer mBeanServer) throws IOException {
		System.out.println(LOG_PREFIX + "setUpJMXConnector");

		final JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://");
		this.jMXConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer);
		this.jMXConnectorServer.start();

		final JMXServiceURL addr = jMXConnectorServer.getAddress();

		this.jMXConnector = JMXConnectorFactory.connect(addr);
	}

	private MBeanServerConnection conn() throws IOException {
		return jMXConnector.getMBeanServerConnection();
	}

	private void tearDown() {
		System.out.println(LOG_PREFIX + "tearDown");

		try {
			if (this.jMXConnector != null) {
				this.jMXConnector.close();
			}
		} catch (IOException e) {
			System.out.println(LOG_PREFIX + "#tearDown - Unexpected, ignoring!" + e);

		}
		// Help GC.
		this.jMXConnector = null;

		try {
			if (this.jMXConnectorServer != null) {
				this.jMXConnectorServer.stop();
			}
		} catch (IOException e) {
			System.out.println(LOG_PREFIX + "tearDown - Unexpected, ignoring" + e);

		}
		// Help GC.
		this.jMXConnectorServer = null;
	}
}
