#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
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

import java.util.List;
/**
 * MBean that holds the performance data.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public interface PerformanceTrackingMXBean {

    /**
     * Clears all data
     */
    void reset();

    /**
     * Enters a new report of a service invocation
     * @param service Name of the service
     * @param method  Name of the method
     * @param time    Duration of the invocation in ms.
     * @param success Success indicator string
     */
    void track(String service, String method, long time, String success);

    /**
     * @return Retrieves all entries
     */
    List<PerformanceEntry> getAll();

    /**
     * @return the number of entries
     */
    int getCount();

    /**
     * @return the entry that took up the most time in total
     */
    PerformanceEntry getWorstByTime();

    /**
     * @return the entry that has the longest average invocation time
     */
    PerformanceEntry getWorstByAverage();

    /**
     * @return the entry that has the highest call count
     */
    PerformanceEntry getWorstByCount();

    /**
     * Dumps all the data into the log file
     */
    void dump();

    /**
     * @return the exceededLimitInPercent
     */
    float getExceededLimitInPercent();

    /**
     * Set the exceeded limit for a method invocation value in percent.
     * @param exceededLimitInPercent the exceededLimitInPercent to set.
     */
    void setExceededLimitInPercent(float exceededLimitInPercent);

    /**
     * Starts the performance tracking bean.
     */
    void start();

    /**
     * Stops the performance tracking bean and cleanup all resources.
     */
    void stop();

    /**
     * @return the last emitted notifications.
     */
    String[] getEmittedNotifications();
}
