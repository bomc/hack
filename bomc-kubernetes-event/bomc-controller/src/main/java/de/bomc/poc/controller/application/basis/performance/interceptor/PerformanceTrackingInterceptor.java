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
package de.bomc.poc.controller.application.basis.performance.interceptor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;

import de.bomc.poc.controller.application.internal.AppErrorCodeEnum;
import de.bomc.poc.controller.application.basis.jmx.MBeanController;
import de.bomc.poc.controller.application.basis.jmx.performance.PerformanceTracking;
import de.bomc.poc.controller.application.basis.jmx.performance.PerformanceTrackingMXBean;
import de.bomc.poc.controller.application.basis.performance.qualifier.PerformanceTrackingQualifier;

/**
 * <pre>
 * This interceptor that tracks the performance of a TYPE or a METHOD.
 * &#64;PerformanceTrackingQualifier
 * public class MyClass
 *  ...
 *
 * or
 *
 * &#64;PerformanceTrackingQualifier
 * public void myMethod
 *  ...
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Interceptor
@PerformanceTrackingQualifier
public class PerformanceTrackingInterceptor implements Serializable {

    /**
     * The serial UID.
     */
    private static final long serialVersionUID = -2826181834330789307L;
    private static final String LOG_PREFIX = "PerformanceTrackingInterceptor#";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Inject
    private MBeanController mBeanController;

    /**
     * Logs invocation duration and success of a method/service
     * @param invocationContext
     * @return
     * @throws Exception
     */
    @AroundInvoke
    public Object logMethodEntry(InvocationContext invocationContext) throws Exception {
        this.logger.debug(LOG_PREFIX + "logMethodEntry");

        final PerformanceTrackingMXBean performanceTracking = (PerformanceTrackingMXBean)this.mBeanController.getMBeanBySimpleName(PerformanceTracking.class.getSimpleName());

        // Time in milliseconds.
        long timeInMs = System.currentTimeMillis();
        try {
            final Object response = invocationContext.proceed();

            final long duration = System.currentTimeMillis() - timeInMs;
            performanceTracking.track(invocationContext.getMethod()
                                                       .getDeclaringClass()
                                                       .getCanonicalName(), invocationContext.getMethod()
                                                                                             .getName(), duration, "success_" + this.getCurrentTime());

            return response;
        } catch (final Exception ex) {
            final long duration = System.currentTimeMillis() - timeInMs;

            AppRuntimeException appRuntimeException;

            final String errorMessage = LOG_PREFIX + "logMethodEntry - failed! ";

            if (ex instanceof AppRuntimeException) {

                appRuntimeException = (AppRuntimeException) ex;
                if (appRuntimeException.isLogged()) {
                    this.logger.error(errorMessage + ex.getMessage());
                } else {
                    this.logger.error(appRuntimeException.stackTraceToString());
                }

                appRuntimeException = new AppRuntimeException(errorMessage, AppErrorCodeEnum.APP_CREATE_LOG_ENTRY_FAILED_10601);
                performanceTracking.track(invocationContext.getMethod()
                                                           .getDeclaringClass()
                                                           .getCanonicalName(), invocationContext.getMethod()
                                                                                                 .getName(), duration, appRuntimeException.getUuid() + "_" + this.getCurrentTime());
            } else {
                this.logger.error(errorMessage, ex);

                appRuntimeException = new AppRuntimeException(errorMessage, AppErrorCodeEnum.APP_CREATE_LOG_ENTRY_FAILED_10601);
                performanceTracking.track(invocationContext.getMethod()
                                                           .getDeclaringClass()
                                                           .getCanonicalName(), invocationContext.getMethod()
                                                                                                 .getName(), duration, appRuntimeException.getUuid() + "_" + this.getCurrentTime());
            }

            throw appRuntimeException;
        } // end catch
    }

    private String getCurrentTime() {
        this.logger.debug(LOG_PREFIX + "getCurrentTime");

        final LocalDateTime now = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        final String formattedDate = now.format(formatter);

        return formattedDate;
    }
}
