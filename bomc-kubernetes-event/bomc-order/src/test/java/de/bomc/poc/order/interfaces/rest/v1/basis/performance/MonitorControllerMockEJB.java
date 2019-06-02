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
package de.bomc.poc.order.interfaces.rest.v1.basis.performance;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

import de.bomc.poc.order.application.basis.jmx.MBeanController;
import de.bomc.poc.order.application.basis.jmx.performance.PerformanceTracking;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.management.JMException;

import org.apache.log4j.Logger;

/**
 * Simple mock for singleton controller to ensure availability of <code>PerformanceTracking</code>
 * within <code>MBeanController</code>.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@Startup
@Singleton
public class MonitorControllerMockEJB {

    private static final String LOG_PREFIX = "MonitorControllerMockEJB#";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Inject
    private MBeanController mBeanController;
    private PerformanceTracking performanceTracking = null;

    @PostConstruct
    public void init() {
        this.logger.debug(LOG_PREFIX + "init");

        performanceTracking = new PerformanceTracking();

        try {
            this.mBeanController.register(performanceTracking, null);
        } catch (JMException e) {
            throw AppRuntimeException.wrap(e);
        }
    }

    public PerformanceTracking getPerformanceTracking() {
        return performanceTracking;
    }
}
