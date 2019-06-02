#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.application.basis.jmx.performance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

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
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;

import ${package}.CategoryBasisUnitTest;
import ${package}.application.basis.jmx.AbstractMBean;
import ${package}.application.basis.jmx.MBeanController;
import ${package}.application.basis.jmx.MBeanInfo;
import ${package}.application.basis.jmx.RootName;

/**
 * Tests the Mbean.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Category(CategoryBasisUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PerformanceTrackingMBeanTest {
	private static final String LOG_PREFIX = "PerformanceTrackingMBeanTest${symbol_pound}";
	private JMXConnectorServer jMXConnectorServer;
	private JMXConnector jMXConnector;
	AtomicBoolean notificationSet;

	@Test
	public void test010_makeFullPath_pass() {
		System.out.println(LOG_PREFIX + "test010_makeFullPath_pass");

		final MBeanController mBeanController = new MBeanController();
		final MBeanInfo mBean = new RootName();

		String fullPath = mBeanController.makeFullPath("bomc", mBean);
		assertThat(fullPath, is(equalTo("bomc/Microservice")));

		fullPath = mBeanController.makeFullPath("/", mBean);
		assertThat(fullPath, is(equalTo("/Microservice")));

		fullPath = mBeanController.makeFullPath(null, mBean);
		assertThat(fullPath, is(equalTo("/Microservice")));
	}

	@Test
	public void test020_registerMBean_pass() throws Exception {
		System.out.println(LOG_PREFIX + "test020_registerMBean_pass");

		try {
			final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			final MBeanInfo mBean = new RootName();
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
	public void test030_registerMBeanToParentMBean_pass() throws Exception {
		System.out.println(LOG_PREFIX + "test030_registerMBeanToParentMBean_pass");

		try {
			final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			// The parent mbean.
			final MBeanInfo mBeanParent = new RootName();
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
			assertThat(setObjNames.contains(new ObjectName("${package}.${artifactId}:name0=Microservice")), equalTo(true));
			assertThat(setObjNames.contains(new ObjectName("${package}.${artifactId}:name0=Microservice,name1=PerformanceTracking")),
					equalTo(true));

			mBeanController.unregisterAll();
		} finally {
			this.tearDown();
		}
	}

	@Test
	public void test040_perfomanceTrackingMBean_pass() throws Exception {
		System.out.println(LOG_PREFIX + "test040_perfomanceTrackingMBean_pass");

		try {
			// Get the MBeanServer.
			final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

			// The parent mbean.
			final MBeanInfo mBeanParent = new RootName();
			//
			final PerformanceTracking mBeanLeaf = new PerformanceTracking();
			notificationSet = new AtomicBoolean(false);

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
			final TestNotificationListener testNotificationListener = new TestNotificationListener();
			((NotificationBroadcaster) mBeanLeaf).addNotificationListener(testNotificationListener, null, null);

			final MBeanController mBeanController = new MBeanController(mBeanServer);

			// Register first parent...
			mBeanController.register(mBeanParent, null);
			// ...now register leaf with reference to the parent.
			mBeanController.register((MBeanInfo) mBeanLeaf, mBeanParent);

			// Check for registering in MBeanServer.
			this.setUpJMXConnector(mBeanServer);

			final Set<ObjectName> setObjNames = this.dump();

			// Check registered objectNames.
			assertThat(setObjNames.contains(new ObjectName("${package}.${artifactId}:name0=Microservice")), equalTo(true));
			assertThat(setObjNames.contains(new ObjectName("${package}.${artifactId}:name0=Microservice,name1=PerformanceTracking")),
					equalTo(true));

			final String beanPath = mBeanController.getBeanPathByMBean(mBeanLeaf);
			assertThat(beanPath, is(equalTo("/Microservice")));

			final ObjectName objectName = mBeanController.makeObjectName(beanPath, mBeanLeaf);
			final PerformanceTrackingMXBean performanceTracking = MBeanServerInvocationHandler
					.newProxyInstance(mBeanServer, objectName, PerformanceTrackingMXBean.class, false);

			performanceTracking.start();

			// Setup some data
			for (int i = 0; i < 100; i++) {
				performanceTracking.track("myService1", "testMethod1", 100, "true");
				TimeUnit.MILLISECONDS.sleep(5);
			}

			performanceTracking.track("myService1", "testMethod1", 99, "false");
			performanceTracking.track("myService1", "testMethod1", 124, "false");
			performanceTracking.track("myService1", "testMethod1", 126, "false");

			// Read all attributes from performanceTracking
			final int count = (int) mBeanServer.getAttribute(objectName, "Count");
			assertThat("Must be 1, because one service and one method is used.", count, is(equalTo(1)));

			final float exceededLimitInPercent = (float) mBeanServer.getAttribute(objectName, "ExceededLimitInPercent");
			assertThat(exceededLimitInPercent, notNullValue());

			final CompositeDataSupport compositeDataSupportCount = (CompositeDataSupport) mBeanServer
					.getAttribute(objectName, "WorstByCount");
			assertThat(compositeDataSupportCount, notNullValue());
			this.checkAttributes(compositeDataSupportCount);

			final CompositeDataSupport compositeDataSupportTime = (CompositeDataSupport) mBeanServer
					.getAttribute(objectName, "WorstByTime");
			assertThat(compositeDataSupportTime, notNullValue());
			this.checkAttributes(compositeDataSupportTime);

			final CompositeData[] compositeDataSupportAll = (CompositeData[]) mBeanServer.getAttribute(objectName,
					"All");
			assertThat(compositeDataSupportAll, notNullValue());

			final CompositeDataSupport compositeDataSupportAverage = (CompositeDataSupport) mBeanServer
					.getAttribute(objectName, "WorstByAverage");
			assertThat(compositeDataSupportAverage, notNullValue());
			this.checkAttributes(compositeDataSupportAverage);

			synchronized (notificationSet) {
				if (!notificationSet.get()) {
					notificationSet.wait(2000);
				}
			}

			assertThat(notificationSet.get(), equalTo(true));

			performanceTracking.dump();
			performanceTracking.reset();
			performanceTracking.dump();

			performanceTracking.stop();

			// Remove listener.
			((NotificationBroadcaster) mBeanLeaf).removeNotificationListener(testNotificationListener);

			mBeanController.unregisterAll();
		} finally {
			this.tearDown();
		}
	}

	/**
	 * Check attributes by the given CompositeDataSupport of the
	 * PerformanceTrackingMXBean.
	 * 
	 * @param compositeDataSupport
	 *            the given data to check.
	 */
	private void checkAttributes(final CompositeDataSupport compositeDataSupport) {
		final float average = (float) compositeDataSupport.get("average");
		assertThat(Float.valueOf(average), greaterThan(0F));

		final int invocationCount = (int) compositeDataSupport.get("invocationCount");
		assertThat(Integer.valueOf(invocationCount), greaterThan(0));

		final float limitInPercent = (float) compositeDataSupport.get("limitInPercent");
		assertThat(Float.valueOf(limitInPercent), greaterThan(0F));

		final long max = (long) compositeDataSupport.get("max");
		assertThat(Long.valueOf(max), greaterThan(0L));

		final String method = (String) compositeDataSupport.get("method");
		assertThat(method, notNullValue());

		final long min = (long) compositeDataSupport.get("min");
		assertThat(Long.valueOf(min), greaterThan(0L));

		final boolean monitoringEnabled = (boolean) compositeDataSupport.get("monitoringEnabled");
		assertThat(Boolean.valueOf(monitoringEnabled), equalTo(true));

		final int notifyDataListSize = (int) compositeDataSupport.get("notifyDataListSize");
		assertThat(Integer.valueOf(notifyDataListSize), greaterThan(0));

		final String service = (String) compositeDataSupport.get("service");
		assertThat(service, notNullValue());

		final String successString = (String) compositeDataSupport.get("successString");
		assertThat(successString, notNullValue());
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
	private long getRandomNumberInRange(final long min, final long max) {
		final Random r = new Random();

		return r.longs(min, (max + 1)).limit(1).findFirst().getAsLong();

	}

	// __________________________________________________________
	// Jmx helper methods.
	// ----------------------------------------------------------
	private Set<ObjectName> dump() throws IOException {
		System.out.println(LOG_PREFIX + "dump");

		Set<ObjectName> beans;

		try {
			beans = this.conn().queryNames(new ObjectName(AbstractMBean.DOMAIN_NAME + ":*"), null);
		} catch (final MalformedObjectNameException e) {
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
		} catch (final IOException e) {
			System.out.println(LOG_PREFIX + "${symbol_pound}tearDown - Unexpected, ignoring!" + e);

		}
		// Help GC.
		this.jMXConnector = null;

		try {
			if (this.jMXConnectorServer != null) {
				this.jMXConnectorServer.stop();
			}
		} catch (final IOException e) {
			System.out.println(LOG_PREFIX + "tearDown - Unexpected, ignoring" + e);

		}
		// Help GC.
		this.jMXConnectorServer = null;
	}

	// _______________________________________________
	// Inner class
	// -----------------------------------------------
	public class TestNotificationListener implements NotificationListener {

		@Override
		public void handleNotification(Notification notification, Object handback) {

			assertThat(notification.getType(), is(equalTo(PerformanceTracking.EXCEEDED_LIMIT_NOTIFICATION_TYPE)));

			final PerformanceEntryNotifyData performanceEntryNotifyData = (PerformanceEntryNotifyData) notification
					.getUserData();

			System.out.println(LOG_PREFIX
					+ "test040_perfomanceTrackingMBean_pass${symbol_pound}handleNotification - [performanceEntryNotifyData="
					+ performanceEntryNotifyData + "]");

			assertThat(performanceEntryNotifyData.getTime(), is(equalTo(126l)));

			synchronized (notificationSet) {
				notificationSet.set(true);
				notificationSet.notify();
			}
		}
	} // end inner class
}
