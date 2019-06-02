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
package de.bomc.poc.order.application.basis.performance.interceptor;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.core.BasisErrorCodeEnum;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.order.application.basis.performance.qualifier.PerformanceTrackingQualifier;

/**
 * A cdi bean for testing the {@link de.bomc.poc.order.application.performance.interceptor.PerformanceTrackingInterceptor}.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@PerformanceTrackingQualifier
public class SutCdiBean {

    private static final String LOG_PREFIX = "SutCdiBean#";
    //
    // Member variables.
    public static final int APP_RUNTIME_EX = 42;
    public static final int APP_RUNTIME_EX_IS_LOGGED = 142;
    public static final int ILLEGAL_ARGUMENT_EX = 33;
    public static final int LONG_RUNNING = 100;
    @Inject
    @LoggerQualifier
    private Logger logger;

    public SutCdiBean() {

    }

    public void methodUnderTest(final int delay) {
        this.logger.debug(LOG_PREFIX + "methodUnderTest [delay=" + delay + "]");

        if (delay == APP_RUNTIME_EX) {
            final AppRuntimeException appRuntimeException = new AppRuntimeException(BasisErrorCodeEnum.UNEXPECTED_10000);

            throw appRuntimeException;
        } else if (delay == ILLEGAL_ARGUMENT_EX) {
            throw new IllegalArgumentException();
        } else if (delay == APP_RUNTIME_EX_IS_LOGGED) {
            final AppRuntimeException appRuntimeException = new AppRuntimeException(BasisErrorCodeEnum.UNEXPECTED_10000);
            appRuntimeException.setIsLogged(true);

            throw appRuntimeException;
        } else {
            this.logger.info(LOG_PREFIX + "methodUnderTest [delay=" + delay + ", nothing to do!]");
        }
    }
}
