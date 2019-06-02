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
package ${package}.application.basis.jmx.metrics;

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
