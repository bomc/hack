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
package de.bomc.poc.order.application.util;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * A class extending {@link Application} and annotated with @ApplicationPath is
 * the Java EE 7 "no XML" approach to activating JAX-RS.
 * <p>
 * <p>
 * Resources are served relative to the servlet path specified in the
 * {@link ApplicationPath} annotation.
 * </p>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@ApplicationPath(JaxRsActivator.APPLICATION_PATH)
public class JaxRsActivator extends Application {

    // Defines the base URI for all resource URIs
    public static final String APPLICATION_PATH = "/rest";
    // Defines the REST endpoint paths.
    public static final String OVERVIEW_ENDPOINT_PATH = "/overview";
    public static final String VERSION_ENDPOINT_PATH = "/version";
    public static final String EXCEPTION_ENDPOINT_PATH = "/exception";
    public static final String PERFORMANCE_ENDPOINT_PATH = "/performance";
    public static final String CUSTOMER_ENDPOINT_PATH = "/customer";
    public static final String ORDER_ENDPOINT_PATH = "/order";
}
