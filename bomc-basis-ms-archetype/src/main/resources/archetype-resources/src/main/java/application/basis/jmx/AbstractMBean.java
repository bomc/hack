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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;
/**
 * A common class that is inherited by all MBean's.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public abstract class AbstractMBean implements MBeanInfo {

    private static final String LOG_PREFIX = "AuthMBean${symbol_pound}";
    protected static final Logger LOGGER = Logger.getLogger(AbstractMBean.class);
    // Set application specific attributes
    // NOTE@MVN: The domain name will be set during project creation by maven and
    // will be the same as the entered package name.
    public static final String DOMAIN_NAME = "${package}.${artifactId}";
    /**
     * Formatter for date time.
     */
    protected static final String DATE_TIME_FORMATTER = "dd.MM.yyyy HH:mm:ss.SSS";
    /**
     * Holds the start time of this MBean.
     */
    protected final LocalDateTime startTime;
    /**
     * The name of this MBean.
     */
    protected final String name;

    /**
     * Creates a new instance of <code>AuthMBean</code>.
     * @param name the registered name of the MBean.
     */
    public AbstractMBean(final String name) {
        LOGGER.debug(LOG_PREFIX + "co");

        this.name = name;
        this.startTime = LocalDateTime.now();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        LOGGER.debug(LOG_PREFIX + "getName [name=" + this.name + "]");

        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartTime() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
        final String strStartTime = startTime.format(formatter);

        return strStartTime;
    }

    /**
     * Initialize the mbean.
     */
    public abstract void start();

    /**
     * Cleanup resources.
     */
    public abstract void stop();
}
