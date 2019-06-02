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
package de.bomc.poc.order.application.config;

/**
 * The defined configuration keys match the configuration properties from the
 * configuration.properties file.
 * 
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
    CUSTOMER_EXPIRY_SCHEDULER_DAY_OF_WEEK("customer.expiry.scheduler.day.of.week"), 
    CUSTOMER_EXPIRY_SCHEDULER_MINUTE("customer.expiry.scheduler.minute"), 
    CUSTOMER_EXPIRY_SCHEDULER_HOUR("customer.expiry.scheduler.hour"), 
    CUSTOMER_EXPIRY_SCHEDULER_PERSISTENT("customer.expiry.scheduler.persistent"), 
    CUSTOMER_EXPIRY_SCHEDULER_TEST_MODUS("customer.expiry.scheduler.test.modus"), 
    PRODUCER("producer");

    private final String propertyValue;

    ConfigKeys(final String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyValue() {
        return this.propertyValue;
    }
}
