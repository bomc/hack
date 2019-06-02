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

import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import de.bomc.poc.exception.core.BasisErrorCodeEnum;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;

import ${package}.application.basis.performance.qualifier.PerformanceTrackingQualifier;

/**
 * A cdi bean for testing the jmx monitoring.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@PerformanceTrackingQualifier
public class SutCdiBean {

    private static final String LOG_PREFIX = "SutCdiBean${symbol_pound}";
    private static final long MIN_VALUE = 90;
    private static final long MAX_VALUE = 110;
    @Inject
    @LoggerQualifier
    private Logger logger;

    public SutCdiBean() {

    }

    public void methodUnderTest(final int delay) {
        this.logger.debug(LOG_PREFIX + "methodUnderTest [delay=" + delay + "]");

        if (delay == 0) {
            // Only for testing.
            try {
                TimeUnit.MILLISECONDS.sleep(15L);
            } catch (final InterruptedException e) {
                // Ignore...
            }
        } else if (delay == 42) {
            final AppRuntimeException appRuntimeException = new AppRuntimeException(BasisErrorCodeEnum.UNEXPECTED_10000);

            throw appRuntimeException;
        } else {
            // Only for testing.
            final long duration = this.getRandomNumberInRange(MIN_VALUE, MAX_VALUE);

            try {
                TimeUnit.MILLISECONDS.sleep(duration);
            } catch (final InterruptedException e) {
                // Ignore...
            }
        }
    }

    /**
     * Defines a value between min and max.
     * @param min the minimum value.
     * @param max the maximum value.
     * @return a random value between min and max.
     */
    private long getRandomNumberInRange(long min, long max) {
        final Random r = new Random();

        return r.longs(min, (max + 1))
                .limit(1)
                .findFirst()
                .getAsLong();
    }
}
