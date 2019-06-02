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
package ${package}.application.config.producer;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import ${package}.application.config.ConfigKeys;
import ${package}.application.config.ConfigSingletonEJB;
import ${package}.application.config.qualifier.ConfigQualifier;

/**
 * A producer for injecting the service configuration parameter.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class ConfigProducer {

    private static final String LOG_PREFIX = "ConfigProducer${symbol_pound}";
    private final ConfigSingletonEJB configSingletonEJB;

    @Inject
    public ConfigProducer(final ConfigSingletonEJB configSingletonEJB) {
        this.configSingletonEJB = configSingletonEJB;
    }

    /**
     * Produces the environment parameter depends on the annotated key.
     * @param injectionPoint the given injection point.
     * @return the parameter depends on the key, or a IllegalStateException if a wrong or no key is defined.
     */
    @Produces
    @ConfigQualifier(key = ConfigKeys.PRODUCER)
    public String produceConfigurationValue(InjectionPoint injectionPoint) {

        final Annotated annotated = injectionPoint.getAnnotated();
        final ConfigQualifier annotation = annotated.getAnnotation(ConfigQualifier.class);

        if (annotation != null) {
            final ConfigKeys key = annotation.key();
            switch (key) {
                case EXCEPTION_LOG_SCHEDULER_DAY_OF_WEEK:
                    return configSingletonEJB.getExceptionSchedulerDayOfWeek();
                case EXCEPTION_LOG_SCHEDULER_MINUTE:
                    return configSingletonEJB.getExceptionSchedulerMinute();
                case EXCEPTION_LOG_SCHEDULER_HOUR:
                    return configSingletonEJB.getExceptionSchedulerHour();
                case EXCEPTION_LOG_SCHEDULER_PERSISTENT:
                    return configSingletonEJB.getExceptionSchedulerPersistent();
                case EXCEPTION_LOG_SCHEDULER_TEST_MODUS:
                    return configSingletonEJB.getExceptionSchedulerTestModus();
                case EXCEPTION_LOG_SCHEDULER_DAYS_TO_SUBSTRACT:
                    return configSingletonEJB.getExceptionSchedulerDaysToSubstract();
                case SERVICE_NAME:
                    return configSingletonEJB.getServiceName();
                default:
                    throw new IllegalStateException(LOG_PREFIX + "produceConfigurationValue - No key for injection point: " + injectionPoint);
            }
        }

        throw new IllegalStateException(LOG_PREFIX + "produceConfigurationValue - No key for injection point: " + injectionPoint);
    }
}
