package de.bomc.poc.logging.filter.mock;

import de.bomc.poc.logging.interceptor.ThreadTrackerInterceptor;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.logging.qualifier.ThreadTrackerQualifier;

import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * A mock cdi bean.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: 7096 $ $Author: tzdbmm $ $Date: 2016-08-03 14:59:36 +0200 (Mi, 03 Aug 2016) $
 * @since 19.07.2016
 */
@ThreadTrackerQualifier
@Interceptors(ThreadTrackerInterceptor.class)
public class MockCdiBean {

    private static final String LOG_PREFIX = "MockCdiBean#";
    private static final String SAY_HELLO = "sayHello";
    @Inject
    @LoggerQualifier
    private Logger logger;

    public MockCdiBean() {
        //
    }

    public void sayHello() {
        this.logger.debug(LOG_PREFIX + SAY_HELLO);

        try {
            Thread.sleep(120000);
        } catch (final InterruptedException ex) {
            this.logger.debug(LOG_PREFIX + "sayHello - interrupted.");
        }
    }
}
