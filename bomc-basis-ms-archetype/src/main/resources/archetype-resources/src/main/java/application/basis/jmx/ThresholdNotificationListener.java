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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.monitor.MonitorNotification;

import ${package}.infrastructure.events.basis.ThresholdEvent;
import org.apache.log4j.Logger;

import ${package}.application.basis.jmx.performance.PerformanceTracking;

/**
 * A listener for exceeded thresholds.
 * NOTE: This listener is ApplicationScoped.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@ApplicationScoped
public class ThresholdNotificationListener implements NotificationListener {

    // The log prefix.
    private static final String LOG_PREFIX = "ThresholdNotificationListener${symbol_pound}";
    private static final Logger LOGGER = Logger.getLogger(ThresholdNotificationListener.class);
    @Inject
    private Event<ThresholdEvent> thresHoldEvent;

    /**
     * Creates a new instance of <code>ThresholdNotificationListener</code>.
     */
    public ThresholdNotificationListener() {
        // Used by CDI provider.
    }

    /**
     * Invoked when a JMX notification occurs. The implementation of this method
     * should return as soon as possible, to avoid blocking its notification
     * broadcaster.
     * @param notification The notification.
     * @param handback     An opaque object which helps the listener to associate information regarding the MBean emitter. This object is passed to the addNotificationListener call and resent, without modification, to
     *                     the listener.
     */
    @Override
    public void handleNotification(final Notification notification, final Object handback) {
        LOGGER.debug(LOG_PREFIX + "handleNotification [notification=" + notification + ", handback=" + handback + "]");

        if (notification instanceof Notification) {
            Notification mNotification = (Notification)notification;

            if (mNotification.getType()
                             .equals(PerformanceTracking.EXCEEDED_LIMIT_NOTIFICATION_TYPE)) {
                this.printOutToConsole(notification, handback, null);

                // Do here more meaningful stuff, e.g. send a email.
                // ...
                // final PerformanceTracking performanceTracking =
                // (PerformanceTracking)mNotification.getSource();
                this.thresHoldEvent.fire(new ThresholdEvent(notification.getUserData()
                                                                        .toString()));
            }
        }

        if (notification instanceof MonitorNotification) {
            final MonitorNotification mNotification = (MonitorNotification)notification;

            if (mNotification.getType()
                             .equals(MonitorNotification.THRESHOLD_ERROR)) {
                //
                // Notification type denoting that the type
                // of the thresholds, offset or modulus is
                // not correct.
                this.thresHoldEvent.fire(new ThresholdEvent(notification.getUserData()
                                                                        .toString()));
            } else if (mNotification.getType()
                                    .equals(MonitorNotification.THRESHOLD_HIGH_VALUE_EXCEEDED)) {
                //
                // Notification type denoting that the
                // observed attribute has exceeded the
                // threshold high value.
                this.thresHoldEvent.fire(new ThresholdEvent(notification.getUserData()
                                                                        .toString()));
            } else if (mNotification.getType()
                                    .equals(MonitorNotification.THRESHOLD_LOW_VALUE_EXCEEDED)) {
                //
                // Notification type denoting that the
                // observed attribute has exceeded the
                // threshold low value.
                this.thresHoldEvent.fire(new ThresholdEvent(notification.getUserData()
                                                                        .toString()));
            }

            final StringBuffer sb = new StringBuffer();
            sb.append(", observedAttribute=")
              .append(mNotification.getObservedAttribute())
              .append(", trigger=")
              .append(mNotification.getTrigger());

            this.printOutToConsole(mNotification, handback, sb.toString());
        }
    }

    private void printOutToConsole(final Notification notification, final Object handback, final String addition) {
        String userData = null;

        if (notification.getUserData() != null) {
            userData =
                notification.getUserData()
                            .toString();
        }

        final StringBuffer sb = new StringBuffer();
        sb.append("Notification [type=")
          .append(notification.getType())
          .append(", seqNumber=")
          .append(Long.toString(notification.getSequenceNumber()))
          .append(", message=")
          .append(notification.getMessage())
          .append(", source=")
          .append(notification.getSource()
                              .toString())
          .append(", userData=")
          .append(userData)
          .append(", handback=")
          .append(handback);

        if (addition != null) {
            sb.append(addition);
        }

        sb.append("]");

        LOGGER.debug(LOG_PREFIX + "printOutToConsole - " + sb.toString());
    }
}
