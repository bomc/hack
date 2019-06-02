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

import java.util.LinkedList;
import java.util.Queue;
/**
 * A data object that is shown in the jmx-console and holds statistics data for
 * method invocations.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class PerformanceEntry {

    // Default max exceeded limit value.
    private static final float DEFAULT_LIMIT_IN_PERCENT = 10.0f;
    // The tracked buffer size.
    private static final int CALLS_BUFFER_LENGTH = 100;
    // The tracked service name.
    private final String service;
    // The tracked method name.
    private final String method;
    // The method invocation counter.
    private int invocationCounter;
    // The minimum value in ms for a method invocation.
    private long min = Long.MAX_VALUE;
    // The maximum value in ms for a method invocation.
    private long max = Long.MIN_VALUE;
    // The maximum duration of method tracking.
    private long sum;
    // Holds the method invocation, depends on CALLS_BUFFER_LENGTH.
    private final Queue<String> callsQueue = new LinkedList<>();
    // Holds the exceeded values that has to be notified, depends on
    // CALLS_BUFFER_LENGTH.
    private final Queue<PerformanceEntryNotifyData> notifyDataQueue = new LinkedList<>();
    // After 100 invocation the monitoring will be activated. This means if a
    // following method call exceeded 25 percent of the average time a
    // notification is emitted by the PerformanceTracking MBean.
    private boolean isMonitoringEnabled = false;
    // The max allowed high limit value in percent.
    private float limitInPercent = DEFAULT_LIMIT_IN_PERCENT;

    /**
     * Creates a new instance of <code>PerformanceEntry</code>.
     * @param service the endpoint class name.
     * @param method  the invoked method name.
     */
    public PerformanceEntry(final String service, final String method) {
        this.service = service;
        this.method = method;
    }

    /**
     * Add method invocation values to map.
     * @param time        the measured time for method invocation.
     * @param successType value for method is finished successful.
     */
    public void increment(final long time, final String successType) {
        this.invocationCounter++;
        this.sum += time;
        this.min = Math.min(this.min, time);
        this.max = Math.max(this.max, time);

        this.callsQueue.add(successType);

        if (this.callsQueue.size() > CALLS_BUFFER_LENGTH) {
            //
            // Remove the first element from queue.
            this.callsQueue.poll();
            //
            // Check if counter reached CALLS_BUFFER_LENGTH to allow monitoring.
            if (this.isMonitoringEnabled == false) {
                this.isMonitoringEnabled = true;
            }
        }

        if (this.isMonitoringEnabled == true) {
            this.checkForExceededLimit(time);
        }
    }

    /**
     * Check for exceeded limit. If the time is exceed the limit, the value is
     * added to a list for notification.
     * @param time the time to compare.
     */
    private void checkForExceededLimit(final long time) {
        if (this.isMonitoringEnabled()) {
            final float maxLimitValue = (((100.0f + this.limitInPercent) * this.getAverage()) / 100.0f);

            if (time >= maxLimitValue) {
                this.notifyDataQueue.add(new PerformanceEntryNotifyData(time));

                if (this.notifyDataQueue.size() > CALLS_BUFFER_LENGTH) {
                    //
                    // Remove the first element from queue, prevents overflow.
                    this.pollExceededInvocation();
                }
            }
        }
    }

    /**
     * @return the first data object, or null if no entry is available.
     */
    public PerformanceEntryNotifyData pollExceededInvocation() {
        return this.notifyDataQueue.poll();
    }

    /**
     * @return the size for notification. If size is larger than null, notifications has to be sent.
     */
    public int getNotifyDataListSize() {
        return this.notifyDataQueue.size();
    }

    /**
     * Determine the average value in ms.
     * @return the average time of method invocations.
     */
    public float getAverage() {
        if (this.invocationCounter > 0) {
            return (float)this.sum / (float)this.invocationCounter;
        } else {
            return 0;
        }
    }

    /**
     * @return the service name 'classname#methodename'.
     */
    public String getService() {
        return this.service;
    }

    /**
     * @return the methode name.
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * @return the maximum value of method invocation.
     */
    public long getMax() {
        return this.max;
    }

    /**
     * @return the minimum value of method invocation.
     */
    public long getMin() {
        return this.min;
    }

    /**
     * Builds a string that codes the success information of the past requests.
     * <p>
     * TZDBMM: Success: - Business exception: O Any other, usually technical
     * exception: X
     * @return String
     */
    public String getSuccessString() {
        StringBuilder sb = new StringBuilder();

        for (String curr : this.callsQueue) {
            sb.append(curr);
        }
        return sb.toString();
    }

    /**
     * @return the count of method invocations since start.
     */
    public int getInvocationCount() {
        return this.invocationCounter;
    }

    /**
     * @return Allows the <code>PerformanceTracking</code> MBean monitoring.
     */
    public boolean isMonitoringEnabled() {
        return this.isMonitoringEnabled;
    }

    /**
     * @return the limitInPercent
     */
    public float getLimitInPercent() {
        return limitInPercent;
    }

    /**
     * @param limitInPercent the limitInPercent to set
     */
    public void setLimitInPercent(float limitInPercent) {
        this.limitInPercent = limitInPercent;
    }

    public void cleanup() {
        this.notifyDataQueue.clear();
        this.callsQueue.clear();
    }

    @Override
    public String toString() {
        return "Entry [service="
               + this.service
               + ", method="
               + this.method
               + ", avg="
               + this.getAverage()
               + ", invocationCounter="
               + this.invocationCounter
               + ", min="
               + this.min
               + ", max="
               + this.max
               + " , sum="
               + this.sum
               + /*", callsQueue=" + this.callsQueue +*/ ", callsBufferLength="
               + CALLS_BUFFER_LENGTH
               + ", isMonitoringEnabled="
               + this.isMonitoringEnabled
               + ", limitInPercent="
               + this.limitInPercent
               + ", notifyDataQueue.size="
               + this.notifyDataQueue.size()
               + "]";
    }
}
