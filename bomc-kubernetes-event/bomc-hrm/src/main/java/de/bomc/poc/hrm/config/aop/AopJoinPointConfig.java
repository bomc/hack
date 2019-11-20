/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: micha
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.config.aop;

import org.aspectj.lang.annotation.Pointcut;

/**
 * A common class to store all the pointcuts. This helps in maintaining the pointcuts in one place.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 20.11.2019
 */
public class AopJoinPointConfig {

    @Pointcut(value = "@within(de.bomc.poc.hrm.application.metrics.aop.Metric) || @annotation(de.bomc.poc.hrm.application.metrics.aop.Metric)")
    public void metricExecution() {}
    
    @Pointcut(value = "@within(de.bomc.poc.hrm.application.log.method.Loggable) || @annotation(de.bomc.poc.hrm.application.log.method.Loggable)")
    public void loggableExecution() {}
}
