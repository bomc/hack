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

/**
 * MBean info interface. MBeanController uses the interface to generate a JMX
 * object name.
 * 
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
