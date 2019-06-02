package de.bomc.poc.logging.producer;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * A factory for creating a logger instance.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @since 19.07.2016
 */
public class LoggerProducer {

    @Produces
    @LoggerQualifier
    public Logger getPrintLog(final InjectionPoint injectionPoint) {
        final LoggerQualifier
            loggerQualifier =
            injectionPoint.getAnnotated()
                          .getAnnotation(LoggerQualifier.class);

        if (loggerQualifier != null && !loggerQualifier.logPrefix()
                                                       .equals(LoggerQualifier.DEFAULT_PREFIX)) {
            return Logger.getLogger(injectionPoint.getMember()
                                                  .getDeclaringClass()
                                                  .getName() + LoggerQualifier.DEFAULT_PREFIX + loggerQualifier.logPrefix());
        } else {
            return Logger.getLogger(injectionPoint.getMember()
                                                  .getDeclaringClass()
                                                  .getName());
        }
    }
}
