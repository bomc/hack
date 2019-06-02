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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;
import javax.management.ObjectName;
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
import de.bomc.poc.monitor.jmx.MBeanInfo;
import de.bomc.poc.monitor.jmx.performance.PerformanceEntryNotifyData;
import de.bomc.poc.monitor.jmx.performance.PerformanceTracking;
import de.bomc.poc.monitor.jmx.performance.PerformanceTrackingMXBean;

/**
 * Tests the PerformanceTracking MXBean.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PerformanceTrackingMBeanTest {
	private static final String LOG_PREFIX = "PerformanceTrackingMBeanTest#";
	private JMXConnectorServer jMXConnectorServer;
	private JMXConnector jMXConnector;

	@Test
	public void test01_makeFullPath_pass() {
		System.out.println(LOG_PREFIX + "test01_makeFullPath_pass");

		final MBeanController mBeanController = new MBeanController();
		final MBeanInfo mBean = new AuthRootServer();

		String fullPath = mBeanController.makeFullPath("bomc", mBean);
		assertThat(fullPath, is(equalTo("bomc/AuthMicroservice")));

		fullPath = mBeanController.makeFullPath("/", mBean);
		assertThat(fullPath, is(equalTo("/AuthMicroservice")));

		fullPath = mBeanController.makeFullPath(null, mBean);
		assertThat(fullPath, is(equalTo("/AuthMicroservice")));
	}

	@Test
	public void test20_registerMBean_pass() throws Exception {
		System.out.println(LOG_PREFIX + "test20_registerMBean_pass");

		try {
			final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			final MBeanInfo mBean = new AuthRootServer();
			final MBeanController mBeanController = new MBeanController(mBeanServer);

			mBeanController.register(mBean, null);

			// Check for registering in MBeanServer.
			this.setUpJMXConnector(mBeanServer);

			this.dump();

			mBeanController.unregister(mBean);
		} finally {
			this.tearDown();
		}
	}

	@Test
	public void test30_registerMBeanToParentMBean_pass() throws Exception {
		System.out.println(LOG_PREFIX + "test30_registerMBeanToParentMBean_pass");

		try {
			final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			// The parent mbean.
			final MBeanInfo mBeanParent = new AuthRootServer();
			//
			final PerformanceTracking mBeanLeaf = new PerformanceTracking();

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
					new ObjectName("de.bomc.auth.microservice:name0=AuthMicroservice,name1=PerformanceTracking")));

			mBeanController.unregisterAll();
		} finally {
			this.tearDown();
		}
	}

	@Test
	public void test40_perfomanceTrackingMBean_pass() throws Exception {
		System.out.println(LOG_PREFIX + "test40_perfomanceTrackingMBean_pass");

		try {
			// Get the MBeanServer.
			final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

			// The parent mbean.
			final MBeanInfo mBeanParent = new AuthRootServer();
			//
			final PerformanceTracking mBeanLeaf = new PerformanceTracking();
			final AtomicBoolean notificationSet = new AtomicBoolean(false);

			// Add a notification listener to the tracking bean.
			//
			// listener: The listener object which will handle the notifications
			// emitted by the broadcaster.
			// ------------------------------------------------------------------
			// filter: If filter is null, no filtering will be performed before
			// handling notifications.
			// ------------------------------------------------------------------
			// handback: An opaque object to be sent back to the listener when a
			// notification is emitted. This object cannot be used by the
			// Notification broadcaster object. It should be resent unchanged
			// with the notification to the listener.
			//
			// ((NotificationBroadcaster) mBeanLeaf).addNotificationListener(new
			// ThresholdNotificationListener(), null, null);
			((NotificationBroadcaster) mBeanLeaf).addNotificationListener(new NotificationListener() {
				public void handleNotification(Notification notification, Object handback) {

					assertThat(notification.getType(),
							is(equalTo(PerformanceTracking.EXCEEDED_LIMIT_NOTIFICATION_TYPE)));

					final PerformanceEntryNotifyData performanceEntryNotifyData = (PerformanceEntryNotifyData) notification
							.getUserData();

					assertThat(performanceEntryNotifyData.getTime(), is(equalTo(126l)));
					
					synchronized (notificationSet) {
						notificationSet.set(true);
						notificationSet.notify();
					}
				}
			}, null, null);

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
					new ObjectName("de.bomc.auth.microservice:name0=AuthMicroservice,name1=PerformanceTracking")));

			final String beanPath = mBeanController.getBeanPathByMBean(mBeanLeaf);
			assertThat(beanPath, is(equalTo("/AuthMicroservice")));

			final ObjectName objectName = mBeanController.makeObjectName(beanPath, mBeanLeaf);
			final PerformanceTrackingMXBean performanceTracking = MBeanServerInvocationHandler
					.newProxyInstance(mBeanServer, objectName, PerformanceTrackingMXBean.class, false);

			performanceTracking.start();

			// Setup some data
			for (int i = 0; i < 100; i++) {
				performanceTracking.track("myService1", "testMethod1", 100, "true");
				TimeUnit.MILLISECONDS.sleep(10);
			}

			performanceTracking.track("myService1", "testMethod1", 99, "false");
			performanceTracking.track("myService1", "testMethod1", 124, "false");
			performanceTracking.track("myService1", "testMethod1", 126, "false");

			final int count = (int) mBeanServer.getAttribute(objectName, "Count");
			assertThat("Must be 1, because one service and one method is used.", count, is(equalTo(1)));

			synchronized (notificationSet) {
				if (!notificationSet.get()) {
					notificationSet.wait(5000);
				}
			}

			assertThat(notificationSet.get(), equalTo(true));

			performanceTracking.dump();

			performanceTracking.stop();

			mBeanController.unregisterAll();
		} finally {
			this.tearDown();
		}
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
	@SuppressWarnings("unused")
	private long getRandomNumberInRange(long min, long max) {
		Random r = new Random();
		return r.longs(min, (max + 1)).limit(1).findFirst().getAsLong();

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

	/**
	 * Create a connection to the JMX agent and setup the M[X]Bean proxies.
	 * 
	 * @throws IOException
	 *             on connection failures
	 */
	// private void connect() throws IOException {
	// JMXServiceURL jmxUrl = new JMXServiceURL(String.format(fmtUrl, host,
	// port));
	// Map<String, Object> env = new HashMap<String, Object>();
	// env.put(JMXConnector.CREDENTIALS, new String[] { username, password });
	// JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl, env);
	// mbeanServerConn = jmxc.getMBeanServerConnection();
	//
	// try {
	// ObjectName name = new ObjectName(ssObjName);
	// ssProxy = JMX.newMBeanProxy(mbeanServerConn, name,
	// StorageServiceMBean.class);
	// name = new ObjectName(CompactionManager.MBEAN_OBJECT_NAME);
	// mcmProxy = JMX.newMBeanProxy(mbeanServerConn, name,
	// CompactionManagerMBean.class);
	// name = new ObjectName(StreamingService.MBEAN_OBJECT_NAME);
	// streamProxy = JMX.newMBeanProxy(mbeanServerConn, name,
	// StreamingServiceMBean.class);
	// } catch (MalformedObjectNameException e) {
	// throw new RuntimeException("Invalid ObjectName? Please report this as a
	// bug.", e);
	// }
	//
	// memProxy = ManagementFactory.newPlatformMXBeanProxy(mbeanServerConn,
	// ManagementFactory.MEMORY_MXBEAN_NAME,
	// MemoryMXBean.class);
	// runtimeProxy = ManagementFactory.newPlatformMXBeanProxy(mbeanServerConn,
	// ManagementFactory.RUNTIME_MXBEAN_NAME,
	// RuntimeMXBean.class);
	// }
}
