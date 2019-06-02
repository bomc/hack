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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.order.infrastructure.events.basis.ThresholdEvent;
import org.apache.log4j.Logger;

/**
 * <pre>
 * A CDI bean for testing the receiving of ThresholdEvent's.
 *
 * NOTE: The bean must be ApplicationScoped annotated,
 * otherwise there are more than one bean of this type.
 * This means always a new cdi bean in case a new event is emitted.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@ApplicationScoped
public class SimpleThresholdEventReceiver {

    private static final String LOG_PREFIX = "SimpleThresholdEventReceiver#";
    @Inject
    @LoggerQualifier
    private Logger logger;
    private boolean isEventReceived = false;

    public SimpleThresholdEventReceiver() {
        // Used by CDI provider.
    }

    /**
     * @return the isEventReceived
     */
    public boolean isEventReceived() {
        return this.isEventReceived;
    }

    public void receiveEvent(@Observes ThresholdEvent thresholdEvent) {
        logger.debug(LOG_PREFIX + "#receiveEvent [thresholdEvent=" + thresholdEvent + "]");

        this.isEventReceived = true;
    }
}
