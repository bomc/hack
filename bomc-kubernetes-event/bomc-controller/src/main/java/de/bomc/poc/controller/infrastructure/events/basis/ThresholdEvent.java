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
package de.bomc.poc.controller.infrastructure.events.basis;

/**
 * A CDI event that is fired when a threshold is exceeded.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class ThresholdEvent {

    private String payload;

    public ThresholdEvent(final String payload) {
        this.payload = payload;
    }

    /**
     * @return the payload
     */
    public String getPayload() {
        return payload;
    }

    /**
     * @param payload the payload to set
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }
}
