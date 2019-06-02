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
package ${package}.application.config;

/**
 * The defined configuration keys match the configuration properties from the
 * configuration.properties file.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public enum ConfigKeys {
    SERVICE_NAME(""),
    EXCEPTION_LOG_SCHEDULER_DAY_OF_WEEK("exception.log.scheduler.day.of.week"),
    EXCEPTION_LOG_SCHEDULER_MINUTE("exception.log.scheduler.minute"),
    EXCEPTION_LOG_SCHEDULER_HOUR("exception.log.scheduler.hour"),
    EXCEPTION_LOG_SCHEDULER_PERSISTENT("exception.log.scheduler.persistent"),
    EXCEPTION_LOG_SCHEDULER_TEST_MODUS("exception.log.scheduler.test.modus"),
    EXCEPTION_LOG_SCHEDULER_DAYS_TO_SUBSTRACT("exception.log.scheduler.days.to.substract"),
    PRODUCER("producer");

    private final String propertyValue;

    ConfigKeys(final String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyValue() {
        return this.propertyValue;
    }
}
