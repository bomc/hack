/**
 * Project: Poc-prometheus
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-17 16:25:31 +0100 (Di, 17 Jan 2017) $
 *
 *  revision: $Revision: 9685 $
 *
 * </pre>
 */
package de.bomc.poc.prometheus.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 7 "no XML" approach to activating
 * JAX-RS.
 * 
 * <p>
 * Resources are served relative to the servlet path specified in the {@link ApplicationPath} annotation.
 * </p>
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 07.03.2016
 */
@ApplicationPath(JaxRsActivator.APPLICATION_PATH)
public class JaxRsActivator extends Application {
    public static final String APPLICATION_PATH = "rest";

    /* class body intentionally left blank */
}
