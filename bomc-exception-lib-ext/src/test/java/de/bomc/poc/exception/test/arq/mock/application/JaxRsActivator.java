/**
 * Project: bomc-exception-lib-ext
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
package de.bomc.poc.exception.test.arq.mock.application;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * <pre>
 *  A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 7 "no XML" approach to activating JAX-RS.
 *  Resources are served relative to the servlet path specified in the {@link ApplicationPath} annotation.
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
@ApplicationPath(JaxRsActivator.APPLICATION_PATH)
public class JaxRsActivator extends Application {

	// Defines the base URI for all resource URIs.
	public static final String APPLICATION_PATH = "exception";
}
