/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.order.application.basis.jmx.metrics;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.monitor.MonitorNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;

import de.bomc.poc.order.CategoryBasisUnitTest;
import de.bomc.poc.order.application.basis.jmx.JmxGaugeMonitor;
import de.bomc.poc.order.application.basis.jmx.MBeanController;
import de.bomc.poc.order.application.basis.jmx.MBeanInfo;
import de.bomc.poc.order.application.basis.jmx.RootName;

/**
 * Tests the JvmMetrics MBean.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Category(CategoryBasisUnitTest.class)
public class JvmMetricsSlowTest {

    private static final String LOG_PREFIX = "JvmMetricsSlowTest#";
    private static final Logger LOGGER = Logger.getLogger(JvmMetricsSlowTest.class);
    private JMXConnectorServer jMXConnectorServer;
    private JMXConnector jMXConnector;

    @Test
    public void test010_gaugeMonitor_OS_pass() throws Exception {
        LOGGER.debug(LOG_PREFIX + "test010_gaugeMonitor_OS_pass");

        double thresholdProcessHigh = 7.0d;
        double thresholdProcessLow = 6.9d;
        double thresholdSystemHigh = 5.0d;
        double thresholdSystemLow = 4.9d;

        try {
            final AtomicBoolean notificationSet = new AtomicBoolean(false);

            // Get the MBeanServer.
            final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

            // The parent mbean.
            final MBeanInfo mBeanParent = new RootName();
            final JvmMetrics jvmMetrics = new JvmMetrics();
            final MBeanController mBeanController = new MBeanController(mBeanServer);
            // Register first parent...
            mBeanController.register(mBeanParent, null);
            // ...now register leaf with reference to the parent.
            mBeanController.register((MBeanInfo)jvmMetrics, mBeanParent);

            final String beanPath = mBeanController.getBeanPathByMBean(jvmMetrics);
            final ObjectName jvmMetricsObjectName = mBeanController.makeObjectName(beanPath, jvmMetrics);

            LOGGER.debug(LOG_PREFIX + "test010_gaugeMonitor_OS_pass [objectName=" + jvmMetricsObjectName + "]");

            //
            // Register a monitor, check peaks in activity, use JMX's
            // GaugeMonitor.
            final JmxGaugeMonitor jmxProcessGaugeMonitor = this.getGaugeMonitor(thresholdProcessHigh, thresholdProcessLow, jvmMetricsObjectName, "JvmProcessCpuLoadMetricsGaugeMonitor", "ProcessCpuLoad");
            mBeanController.register(jmxProcessGaugeMonitor, (MBeanInfo)jvmMetrics);

            final JmxGaugeMonitor jmxSystemGaugeMonitor = this.getGaugeMonitor(thresholdSystemHigh, thresholdSystemLow, jvmMetricsObjectName, "JvmSystemCpuLoadMetricsGaugeMonitor", "SystemCpuLoad");
            mBeanController.register(jmxSystemGaugeMonitor, (MBeanInfo)jvmMetrics);

            // Setup the monitor: register a listener
            this.setUpJMXConnector(mBeanServer);
            final MBeanServerConnection connection = this.conn();

            // Add a up notification listener to the connection
            connection.addNotificationListener(this.getObjectName(mBeanController, jmxProcessGaugeMonitor), new NotificationListener() {
                public void handleNotification(Notification notification, Object handback) {

                    if (notification instanceof MonitorNotification) {
                        final MonitorNotification mNotification = (MonitorNotification)notification;

                        if (mNotification.getType()
                                         .equals(MonitorNotification.THRESHOLD_ERROR)) {
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

                        LOGGER.debug(LOG_PREFIX + "test010_gaugeMonitor_OS_pass - [observedAttribute=" + mNotification.getObservedAttribute() + ", trigger=" + mNotification.getTrigger() + "]" + mNotification.toString());

                        // synchronized (notificationSet) {
                        // notificationSet.set(true);
                        // notificationSet.notify();
                        // }
                    }
                }
            }, null, null);

            // Add a up notification listener to the connection
            connection.addNotificationListener(this.getObjectName(mBeanController, jmxSystemGaugeMonitor), new NotificationListener() {
                public void handleNotification(final Notification notification, final Object handback) {

                    if (notification instanceof MonitorNotification) {
                        final MonitorNotification mNotification = (MonitorNotification)notification;

                        if (mNotification.getType()
                                         .equals(MonitorNotification.THRESHOLD_ERROR)) {
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

                        LOGGER.debug(LOG_PREFIX + "test010_gaugeMonitor_OS_pass - [observedAttribute=" + mNotification.getObservedAttribute() + ", trigger=" + mNotification.getTrigger() + "]" + mNotification.toString());

                        // synchronized (notificationSet) {
                        // notificationSet.set(true);
                        // notificationSet.notify();
                        // }
                    }
                }
            }, null, null);

            jmxProcessGaugeMonitor.start();
            jmxSystemGaugeMonitor.start();

            synchronized (notificationSet) {
                if (!notificationSet.get()) {
                    notificationSet.wait(5000);
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
    public void test020_gaugeMonitor_Memory_pass() throws Exception {
        LOGGER.debug(LOG_PREFIX + "test020_gaugeMonitor_Memory_pass");

        double thresholdHeapHigh = 0.55d;
        double thresholdHeapLow = 0.54d;
        double thresholdNonHeapHigh = 0.2d;
        double thresholdNonHeapLow = 0.1d;

        try {
            final AtomicBoolean notificationSet = new AtomicBoolean(false);

            // Get the MBeanServer.
            final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

            // The parent mbean.
            final MBeanInfo mBeanParent = new RootName();
            final JvmMetrics jvmMetrics = new JvmMetrics();
            final MBeanController mBeanController = new MBeanController(mBeanServer);
            // Register first parent...
            mBeanController.register(mBeanParent, null);
            // ...now register leaf with reference to the parent.
            mBeanController.register((MBeanInfo)jvmMetrics, mBeanParent);

            final String beanPath = mBeanController.getBeanPathByMBean(jvmMetrics);
            final ObjectName jvmMetricsObjectName = mBeanController.makeObjectName(beanPath, jvmMetrics);

            LOGGER.debug(LOG_PREFIX + "test020_gaugeMonitor_Memory_pass [objectName=" + jvmMetricsObjectName + "]");

            //
            // Register a monitor, check peaks in activity, use JMX's
            // GaugeMonitor.
            final JmxGaugeMonitor jmxHeapGaugeMonitor = this.getGaugeMonitor(thresholdHeapHigh, thresholdHeapLow, jvmMetricsObjectName, "JvmHeapMetricsGaugeMonitor", "HeapMemoryUsage");
            mBeanController.register(jmxHeapGaugeMonitor, (MBeanInfo)jvmMetrics);

            final JmxGaugeMonitor jmxNonHeapGaugeMonitor = this.getGaugeMonitor(thresholdNonHeapHigh, thresholdNonHeapLow, jvmMetricsObjectName, "JvmNonHeapMetricsGaugeMonitor", "NonHeapMemoryUsage");
            mBeanController.register(jmxNonHeapGaugeMonitor, (MBeanInfo)jvmMetrics);

            // Setup the monitor: register a listener
            this.setUpJMXConnector(mBeanServer);
            final MBeanServerConnection connection = this.conn();

            // Add a up notification listener to the connection
            connection.addNotificationListener(this.getObjectName(mBeanController, jmxHeapGaugeMonitor), new NotificationListener() {
                public void handleNotification(Notification notification, Object handback) {

                    if (notification instanceof MonitorNotification) {
                        final MonitorNotification mNotification = (MonitorNotification)notification;

                        if (mNotification.getType()
                                         .equals(MonitorNotification.THRESHOLD_ERROR)) {
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

                        LOGGER.debug(
                            LOG_PREFIX + "test020_gaugeMonitor_Memory_pass - [observedAttribute=" + mNotification.getObservedAttribute() + ", trigger=" + mNotification.getTrigger() + "]" + mNotification.toString());

                        // synchronized (notificationSet) {
                        // notificationSet.set(true);
                        // notificationSet.notify();
                        // }
                    }
                }
            }, null, null);

            // Add a up notification listener to the connection
            connection.addNotificationListener(this.getObjectName(mBeanController, jmxNonHeapGaugeMonitor), new NotificationListener() {
                public void handleNotification(Notification notification, Object handback) {

                    if (notification instanceof MonitorNotification) {
                        final MonitorNotification mNotification = (MonitorNotification)notification;

                        if (mNotification.getType()
                                         .equals(MonitorNotification.THRESHOLD_ERROR)) {
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

                        LOGGER.debug(
                            LOG_PREFIX + "test020_gaugeMonitor_Memory_pass - [observedAttribute=" + mNotification.getObservedAttribute() + ", trigger=" + mNotification.getTrigger() + "]" + mNotification.toString());

                        // synchronized (notificationSet) {
                        // notificationSet.set(true);
                        // notificationSet.notify();
                        // }
                    }
                }
            }, null, null);

            jmxHeapGaugeMonitor.start();
            jmxNonHeapGaugeMonitor.start();

            synchronized (notificationSet) {
                if (!notificationSet.get()) {
                    notificationSet.wait(5000);
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

    private JmxGaugeMonitor getGaugeMonitor(final double highValue, final double lowValue, final ObjectName observedObjectName, final String mBeanName, final String observedAttribute) {
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
    private ObjectName getObjectName(final MBeanController mBeanController, final JmxGaugeMonitor jmxGaugeMonitor) throws Exception {
        final String beanPath = mBeanController.getBeanPathByMBean(jmxGaugeMonitor);
        final ObjectName jmxGaugeMonitorObjectName = mBeanController.makeObjectName(beanPath, jmxGaugeMonitor);

        LOGGER.debug(LOG_PREFIX + "getObjectName [objectName=" + jmxGaugeMonitorObjectName + "]");

        return jmxGaugeMonitorObjectName;
    }

    private void setUpJMXConnector(final MBeanServer mBeanServer) throws IOException {
        LOGGER.debug(LOG_PREFIX + "setUpJMXConnector");

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
        LOGGER.debug(LOG_PREFIX + "tearDown");

        try {
            if (this.jMXConnector != null) {
                this.jMXConnector.close();
            }
        } catch (final IOException e) {
            LOGGER.debug(LOG_PREFIX + "#tearDown - Unexpected, ignoring!" + e);
        }
        // Help GC.
        this.jMXConnector = null;

        try {
            if (this.jMXConnectorServer != null) {
                this.jMXConnectorServer.stop();
            }
        } catch (final IOException e) {
            LOGGER.debug(LOG_PREFIX + "tearDown - Unexpected, ignoring" + e);
        }
        // Help GC.
        this.jMXConnectorServer = null;
    }
}
