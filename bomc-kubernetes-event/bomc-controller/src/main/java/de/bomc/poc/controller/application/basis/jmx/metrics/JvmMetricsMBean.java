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
package de.bomc.poc.controller.application.basis.jmx.metrics;

/**
 * Reporting JVM CPU usage statistics.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public interface JvmMetricsMBean {

    /**
     * @return cpu load of own process or -1 for a invalid value.
     */
    double getProcessCpuLoad();

    /**
     * @return cpu load of the whole system or -1 for a invalid value.
     */
    double getSystemCpuLoad();

    /**
     * @return the current heap size.
     */
    double getHeapMemoryUsage();

    /**
     * @return the current non-heap size.
     */
    double getNonHeapMemoryUsage();
}
