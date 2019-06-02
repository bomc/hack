/**
 * Project: bomc-invoice
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
package de.bomc.poc.invoice.application.util;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

/**
 * A class extending {@link Application} and annotated with @ApplicationPath is
 * the Java EE 7 "no XML" approach to activating JAX-RS.
 * <p>
 * Resources are served relative to the servlet path specified in the
 * {@link ApplicationPath} annotation.
 * </p>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 05.04.2018
 */
@ApplicationPath(JaxRsActivator.APPLICATION_PATH)
@OpenAPIDefinition(
		info = @Info(title = "Invoice service, health", version = "1.00.00-SNAPSHOT", 
		contact = @Contact(name = "bomc", email = "bomc@bomc.org", url = "http://www.bomc.wordpress.com")), 
		servers = {
				@Server(url = "/rest", description = "localhost") 
		}
)
public class JaxRsActivator extends Application {

	// Defines the base URI for all resource URIs
	public static final String APPLICATION_PATH = "/rest";
	// Defines the REST endpoint paths.
	public static final String HEALTH_ENDPOINT_PATH = "/health";
}
