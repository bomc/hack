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

import javax.management.monitor.GaugeMonitor;

import org.apache.log4j.Logger;

/**
 * A wrapper for {@link GaugeMonitor}.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class JmxGaugeMonitor extends GaugeMonitor implements MBeanInfo {

    private static final String LOG_PREFIX = "JmxGaugeMonitor${symbol_pound}";
    private static final Logger LOGGER = Logger.getLogger(JmxGaugeMonitor.class);
    /**
     * Formatter for date time.
     */
    protected static final String DATE_TIME_FORMATTER = "dd.MM.yyyy HH:mm:ss";
    /**
     * Holds the start time of this MBean.
     */
    protected final LocalDateTime startTime;
    /**
     * The name of this MBean.
     */
    protected final String name;

    /**
     * Creates a instance of <code>JmxGaugeMonitor</code>.
     * @param name the given name of the service.
     */
    public JmxGaugeMonitor(final String name) {
        LOGGER.debug(LOG_PREFIX + "co");

        this.startTime = LocalDateTime.now();
        this.name = name;
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
        final String strStartTime = startTime.format(formatter)
                                             .toString();

        return strStartTime;
    }
}
