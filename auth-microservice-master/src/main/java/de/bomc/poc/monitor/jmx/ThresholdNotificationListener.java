/**
 * Project: MY_POC_MICROSERVICE
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.monitor.jmx;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.monitor.MonitorNotification;

import org.apache.log4j.Logger;

import de.bomc.poc.monitor.jmx.performance.PerformanceTracking;

/**
 * A listener for exceeded thresholds.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class ThresholdNotificationListener implements NotificationListener {
	// The log prefix.
	private static final String LOG_PREFIX = "ThresholdNotificationListener#";
	private static final Logger LOGGER = Logger.getLogger(ThresholdNotificationListener.class.getName());

	/**
	 * Creates a new instance of <code>ThresholdNotificationListener</code>.
	 */
	public ThresholdNotificationListener() {
		//
	}

	/**
	 * Invoked when a JMX notification occurs. The implementation of this method
	 * should return as soon as possible, to avoid blocking its notification
	 * broadcaster.
	 * 
	 * @param notification
	 *            The notification.
	 * @param handback
	 *            An opaque object which helps the listener to associate
	 *            information regarding the MBean emitter. This object is passed
	 *            to the addNotificationListener call and resent, without
	 *            modification, to the listener.
	 */
	@Override
	public void handleNotification(final Notification notification, final Object handback) {
		if (notification instanceof Notification) {
			Notification mNotification = (Notification) notification;
			
			if (mNotification.getType().equals(PerformanceTracking.EXCEEDED_LIMIT_NOTIFICATION_TYPE)) {
				this.printOutToConsole(notification, handback, null);

				// Do here more meaningful stuff, e.g. send a email.
				// ...
				//final PerformanceTracking performanceTracking = (PerformanceTracking)mNotification.getSource();
			}
		}

		if (notification instanceof MonitorNotification) {
			final MonitorNotification mNotification = (MonitorNotification) notification;
			
			if (mNotification.getType().equals(MonitorNotification.THRESHOLD_ERROR)) {
				//
				// Notification type denoting that the type
				// of the thresholds, offset or modulus is
				// not correct.
				
			} else if (mNotification.getType()
					.equals(MonitorNotification.THRESHOLD_HIGH_VALUE_EXCEEDED)) {
				//
				// Notification type denoting that the
				// observed attribute has exceeded the
				// threshold high value.
				
			} else if (mNotification.getType()
					.equals(MonitorNotification.THRESHOLD_LOW_VALUE_EXCEEDED)) {
				//
				// Notification type denoting that the
				// observed attribute has exceeded the
				// threshold low value.
				
			}
			
			StringBuffer sb = new StringBuffer();
			sb.append(", observedAttribute=").append(mNotification.getObservedAttribute()).append(", trigger=")
					.append(mNotification.getTrigger());

			this.printOutToConsole(mNotification, handback, sb.toString());
		}
	}

	private void printOutToConsole(final Notification notification, final Object handback, final String addition) {
		String userData = null;

		if (notification.getUserData() != null) {
			userData = notification.getUserData().toString();
		}

		final StringBuffer sb = new StringBuffer();
		sb.append("Notification [type=").append(notification.getType()).append(", seqNumber=")
				.append(Long.toString(notification.getSequenceNumber())).append(", message=")
				.append(notification.getMessage()).append(", source=").append(notification.getSource().toString())
				.append(", userData=").append(userData).append(", handback=").append(handback);

		if (addition != null) {
			sb.append(addition);
		}

		sb.append("]");

		LOGGER.debug(LOG_PREFIX + "printOutToConsole - " + sb.toString());
	}
}
