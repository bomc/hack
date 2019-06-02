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
package de.bomc.poc.order.application.basis.jmx;

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

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;

import de.bomc.poc.order.application.internal.AppErrorCodeEnum;
import de.bomc.poc.order.application.basis.jmx.metrics.JvmMetrics;
import de.bomc.poc.order.application.basis.jmx.performance.PerformanceTracking;

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
    private RootName rootName = null;
    // The performance tracking mbean.
    private PerformanceTracking performanceTracking = null;
    // The jvm metrics mbean.
    private JvmMetrics jvmMetrics = null;
    // A list that contais all gauge monitor mbeans.
    private List<JmxGaugeMonitor> gaugeMonitorList = new ArrayList<>();
    // A notification listener for performance tracking.
    @Inject
    private ThresholdNotificationListener perfThresholdNotificationlistener;

    @PostConstruct
    public void init() {
        this.logger.debug(LOG_PREFIX + "init");

        rootName = new RootName();
        performanceTracking = new PerformanceTracking();
        jvmMetrics = new JvmMetrics();

        try {
            // Register first parent...
            this.mBeanController.register(rootName, null);

            // ...now register leafs with reference to the parent and add
            // notification listener.
            this.mBeanController.register((MBeanInfo) performanceTracking, rootName);
            // ...add NotificationListener.
            performanceTracking.addNotificationListener(perfThresholdNotificationlistener, null, null);

            // ...now register leafs with reference to the parent and add
            // notification listener.
            this.mBeanController.register((MBeanInfo) jvmMetrics, rootName);

            final String beanPath = this.mBeanController.getBeanPathByMBean(jvmMetrics);
            final ObjectName jvmMetricsObjectName = this.mBeanController.makeObjectName(beanPath, jvmMetrics);

            //
            // Register a monitor, check memory thresholds, use JMX's
            // GaugeMonitor.
            this.gaugeMonitorList.add(this.getGaugeMonitor(THRESHOLD_HEAP_HIGH, THRESHOLD_HEAP_LOW,
                    jvmMetricsObjectName, "JvmHeapMetrics", "HeapMemoryUsage"));
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
        } catch (final JMException ex) {
            this.logger.error(LOG_PREFIX + "init - failed! ", ex);

            throw new AppRuntimeException(LOG_PREFIX + "init - failed! ",
                    AppErrorCodeEnum.APP_INITILIZATION_START_COMPONENT_FAILURE_10600);
        }
    }

    /**
     * Add to all {@link JmxGaugeMonitor}s a <code>NotificationListener</code>,
     * register them to the MBeanServer and start them.
     */
    private void setupJmxGaugeMonitors() {
        this.logger.debug(LOG_PREFIX + "setupJmxGaugeMonitors");

        this.gaugeMonitorList.forEach(jmxGaugeMonitor -> {
            try {
                // Order is mandatory.
                // 1. register...
                this.mBeanController.register(jmxGaugeMonitor, (MBeanInfo) jvmMetrics);
                // 2. ... add notification listener...
                this.mBeanController.addNotificationListener(this.getObjectName(jmxGaugeMonitor),
                        new ThresholdNotificationListener(), null, null);
                // 3. ...start.
                jmxGaugeMonitor.start();
            } catch (Exception ex) {
                final String errorMessage = LOG_PREFIX
                        + "addNotificationListenerAndRegisterToMBeanServer - Could not setup JmxGaugeMonitor! ";
                this.logger.error(errorMessage, ex);

                throw new AppRuntimeException(errorMessage, AppErrorCodeEnum.MBEAN_SETUP_GAUGE_MONITOR_FAILED_10705);
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
