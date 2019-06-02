package de.bomc.poc.rest.mock;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * <pre>
 *  A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 7 "no XML" approach to activating JAX-RS.
 *  Resources are served relative to the servlet path specified in the {@link ApplicationPath} annotation.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: 6791 $ $Author: tzdbmm $ $Date: 2016-07-19 09:07:13 +0200 (Di, 19 Jul 2016) $
 * @since 12.07.2016
 */
@ApplicationPath(JaxRsActivator.APPLICATION_PATH)
public class JaxRsActivator extends Application {

    // Defines the base URI for all resource URIs.
    public static final String APPLICATION_PATH = "logging";
}
