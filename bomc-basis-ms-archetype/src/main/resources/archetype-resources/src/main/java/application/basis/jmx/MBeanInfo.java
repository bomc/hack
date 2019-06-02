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
package ${package}.application.basis.jmx;

/**
 * MBean info interface. MBeanController uses the interface to generate a JMX
 * object name.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public interface MBeanInfo {

    /**
     * @return a string identifying the MBean
     */
    String getName();

    /**
     * @return the start time of this mbean.
     */
    String getStartTime();
}
