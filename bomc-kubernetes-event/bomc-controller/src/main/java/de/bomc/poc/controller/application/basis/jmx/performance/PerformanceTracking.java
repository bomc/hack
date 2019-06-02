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
package de.bomc.poc.controller.application.basis.jmx.performance;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

import de.bomc.poc.controller.application.basis.jmx.AbstractMBean;

/**
 * MBean that holds the performance data.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class PerformanceTracking extends AbstractMBean implements PerformanceTrackingMXBean, NotificationBroadcaster {

    private static final String LOG_PREFIX = "PerformanceTracking#";
    /**
     * The type of notification that is sent when a statistics value is outside
     * of the acceptable range.
     * NOTE@MVN: will be set during creation time with maven.
     */
    public static final String EXCEEDED_LIMIT_NOTIFICATION_TYPE = "de.bomc.poc.controller.monitor.threshold.exceeded";
    /**
     * The exceeded limit for a method invocation, depending on the average
     * value.
     */
    private static final float MAX_EXCEEDED_LIMIT_IN_PERCENT = 25;
    private float exceededLimitInPercent = MAX_EXCEEDED_LIMIT_IN_PERCENT;
    /**
     * The tracked notification buffer size.
     */
    private static final int NOTIFICATION_BUFFER_LENGTH = 50;
    /**
     * Holds the emitted notifications, depends on NOTIFICATION_BUFFER_LENGTH.
     */
    private Queue<Notification> notificationQueue = new LinkedList<>();
    /**
     * Holds interested listener on threshold changes.
     */
    private NotificationBroadcasterSupport notificationBroadcasterSupport = new NotificationBroadcasterSupport();
    /**
     * Monitoring state. The default value is set to <CODE>false</CODE>.
     */
    private AtomicBoolean isMonitoringActive = new AtomicBoolean(false);
    /**
     * Scheduler Service.
     */
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    /**
     * A map that holds the recorded entries.
     */
    private final SortedMap<String, PerformanceEntry> entries = new TreeMap<>();
    /**
     * This sequence number is required by the JMX Notification API.
     */
    private AtomicLong sequenceNumber = new AtomicLong(0);

    /**
     * Creates a instance of <code>PerformanceTracking</code>.
     */
    public PerformanceTracking() {
        super(PerformanceTracking.class.getSimpleName());
    }

    /**
     * Starts the performance tracking bean.
     */
    public synchronized void start() {
        LOGGER.debug(LOG_PREFIX + "start");

        if (this.isMonitoringActive.get() == false) {
            this.isMonitoringActive.getAndSet(true);

            /**
             * This class implements the Runnable interface.
             *
             * The SchedulerTask is executed periodically with a given fixed
             * delay by the Scheduled Executor Service.
             */
            Runnable task = () -> {
                final Map<String, PerformanceEntry> unmodifiablePerfEntriesMap = Collections.unmodifiableMap(entries);

                // Collect all monitored performance entries.
                final Map<String, PerformanceEntry>
                    monitoredPerfEntriesMap =
                    unmodifiablePerfEntriesMap.entrySet()
                                              .stream()
                                              .filter(performanceEntry -> (performanceEntry.getValue()
                                                                                           .isMonitoringEnabled()
                                                                           && performanceEntry.getValue()
                                                                                              .getNotifyDataListSize() > 0))
                                              .collect(Collectors.toMap(performanceEntry -> performanceEntry.getKey(), performanceEntry -> performanceEntry.getValue()));

                monitoredPerfEntriesMap.entrySet()
                                       .stream()
                                       .parallel()
                                       .forEach(performanceEntry -> {
                                           while (performanceEntry.getValue()
                                                                  .getNotifyDataListSize() > 0) {
                                               this.sendNotification(performanceEntry.getValue(), performanceEntry.getValue()
                                                                                                                  .pollExceededInvocation());
                                           } // end while
                                       });
            };
            //
            // The executor checks every second for entries to notify.
            this.executor.scheduleWithFixedDelay(task, 0, 1, TimeUnit.SECONDS);
        }
    }

    /**
     * Stops the performance tracking bean and cleanup all resources.
     */
    public synchronized void stop() {
        LOGGER.debug(LOG_PREFIX + "stop");

        if (this.isMonitoringActive.get() == true && executor != null) {
            try {
                executor.shutdown();
                executor.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.error(LOG_PREFIX + "stop - Tasks interrupted. ", e);
            } finally {
                if (!executor.isTerminated()) {
                    LOGGER.info(LOG_PREFIX + "stop - Cancel non-finished tasks.");
                }

                executor.shutdownNow();

                LOGGER.debug(LOG_PREFIX + "stop - Shutdown finished");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        LOGGER.debug(LOG_PREFIX + "reset");

        this.entries.entrySet()
                    .stream()
                    .parallel()
                    .forEach(performanceEntry -> {
                        try {
                            performanceEntry.getValue()
                                            .cleanup();
                        } catch (Exception ex) {
                            LOGGER.error(LOG_PREFIX + "reset - failed.", ex);
                        }
                    });

        this.entries.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void track(final String service, final String method, final long time, final String success) {
        LOGGER.debug(LOG_PREFIX + "track - [service=" + service + ", method=" + method + ", time=" + time + ", sucess=" + success + "]");

        final String key = buildKey(service, method);
        PerformanceEntry performanceEntry = this.entries.get(key);

        if (performanceEntry == null) {
            performanceEntry = new PerformanceEntry(service, method);
            performanceEntry.setLimitInPercent(MAX_EXCEEDED_LIMIT_IN_PERCENT);

            this.entries.put(key, performanceEntry);
        }

        performanceEntry.increment(time, success);
    }

    private String buildKey(final String service, final String method) {
        LOGGER.debug(LOG_PREFIX + "buildKey - [service=" + service + ", method=" + method + "]");

        return service + "#" + method;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PerformanceEntry> getAll() {
        LOGGER.debug(LOG_PREFIX + "getAll [entries.size=" + this.entries.size() + "]");

        return new ArrayList<PerformanceEntry>(entries.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        LOGGER.debug(LOG_PREFIX + "getCount [entries.size=" + this.entries.size() + "]");

        return this.entries.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PerformanceEntry getWorstByTime() {
        LOGGER.debug(LOG_PREFIX + "getWorstByTime");

        float max = 0L;
        PerformanceEntry maxEntry = null;

        for (java.util.Map.Entry<String, PerformanceEntry> curr : this.entries.entrySet()) {
            float
                time =
                curr.getValue()
                    .getAverage() * curr.getValue()
                                        .getInvocationCount();
            if (time > max) {
                max = time;
                maxEntry = curr.getValue();
            }
        }

        return maxEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PerformanceEntry getWorstByAverage() {
        LOGGER.debug(LOG_PREFIX + "getWorstByAverage");

        float max = 0L;
        PerformanceEntry maxEntry = null;

        for (java.util.Map.Entry<String, PerformanceEntry> curr : this.entries.entrySet()) {
            if (curr.getValue()
                    .getAverage() > max) {
                max =
                    curr.getValue()
                        .getAverage();
                maxEntry = curr.getValue();
            }
        }

        return maxEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PerformanceEntry getWorstByCount() {
        LOGGER.debug(LOG_PREFIX + "getWorstByCount");

        int max = 0;
        PerformanceEntry maxEntry = null;

        for (java.util.Map.Entry<String, PerformanceEntry> curr : this.entries.entrySet()) {
            if (curr.getValue()
                    .getInvocationCount() > max) {
                max =
                    curr.getValue()
                        .getInvocationCount();
                maxEntry = curr.getValue();
            }
        }

        return maxEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dump() {
        for (PerformanceEntry curr : entries.values()) {
            LOGGER.info(LOG_PREFIX + "dumpSorted - [" + curr.toString() + "]");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getExceededLimitInPercent() {
        LOGGER.debug(LOG_PREFIX + "getExceededLimitInPercent");

        return this.exceededLimitInPercent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExceededLimitInPercent(final float exceededLimitInPercent) {
        LOGGER.debug(LOG_PREFIX + "setExceededLimitInPercent [exceededLimitInPercent=" + exceededLimitInPercent + "]");

        this.exceededLimitInPercent = exceededLimitInPercent;
    }

    /**
     * Add a notification to the list.
     * @param notification the notification to add.
     */
    public void addNotification(final Notification notification) {
        LOGGER.debug(LOG_PREFIX + "addNotification [" + notification.toString() + "]");

        this.notificationQueue.add(notification);

        if (this.notificationQueue.size() > NOTIFICATION_BUFFER_LENGTH) {
            //
            // Remove the first element from queue.
            this.notificationQueue.poll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getEmittedNotifications() {
        LOGGER.debug(LOG_PREFIX + "getEmittedNotifications [notificationQueue.size=" + this.notificationQueue.size() + "]");
        // Map the List<Notification> first to a List<String> and than to a string array.
        final List<String>
            strNotificationList =
            this.notificationQueue.stream()
                                  .map(n -> n.toString())
                                  .collect(Collectors.toList());
        final Stream<String> stream = strNotificationList.stream();
        final String[] notifications = stream.toArray(size -> new String[size]);

        return notifications;
    }

    /**
     * Used to send the JMX notification because a measured value is exceeded
     * and fall within the acceptable range.
     * @param performanceEntry           the given performanceEntry.
     * @param performanceEntryNotifyData the given performanceEntryNotifyData.
     */
    private void sendNotification(final PerformanceEntry performanceEntry, final PerformanceEntryNotifyData performanceEntryNotifyData) {
        LOGGER.debug(LOG_PREFIX + "sendNotification [entry=" + performanceEntry + "]");

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
        final String
            timestamp =
            performanceEntryNotifyData.getTimestamp()
                                      .format(formatter);

        final StringBuffer sb = new StringBuffer();
        sb.append("NotificationData - [service=")
          .append(performanceEntry.getService())
          .append(", exceededLimit=")
          .append(performanceEntryNotifyData.getTime())
          .append(", avg=")
          .append(performanceEntry.getAverage())
          .append(", timestamp=")
          .append(timestamp)
          .append("]");

        final Notification notification = new Notification(EXCEEDED_LIMIT_NOTIFICATION_TYPE, this.getName(), sequenceNumber.getAndIncrement(), System.currentTimeMillis(), sb.toString());
        // Allows adding additional information to the notification.
        notification.setUserData(performanceEntryNotifyData);

        this.addNotification(notification);
        this.notificationBroadcasterSupport.sendNotification(notification);
    }

    // _____________________________________________
    // Implements NotificationBroadCaster
    // ---------------------------------------------

    @Override
    public void addNotificationListener(final NotificationListener listener, final NotificationFilter filter, final Object handback) throws IllegalArgumentException {
        LOGGER.info(LOG_PREFIX + "addNotificationListener");

        this.notificationBroadcasterSupport.addNotificationListener(listener, filter, handback);
    }

    @Override
    public void removeNotificationListener(final NotificationListener listener) throws ListenerNotFoundException {
        LOGGER.info(LOG_PREFIX + "removeNotificationListener");

        this.notificationBroadcasterSupport.removeNotificationListener(listener);
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        LOGGER.info(LOG_PREFIX + "getNotificationInfo");

        MBeanNotificationInfo[] mBeanNotificationInfoArray = new MBeanNotificationInfo[1];
        String[] notifTypes = new String[]{EXCEEDED_LIMIT_NOTIFICATION_TYPE};
        String name = this.getName();
        String description = "The limit for a method threshold value is exceeded, it depends on the avg time.";
        MBeanNotificationInfo mBeanNotificationInfo = new MBeanNotificationInfo(notifTypes, name, description);
        mBeanNotificationInfoArray[0] = mBeanNotificationInfo;

        return mBeanNotificationInfoArray;
    }
}
