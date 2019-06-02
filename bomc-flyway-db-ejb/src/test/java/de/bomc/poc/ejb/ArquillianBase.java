/**
 * Project: bomc-flyway-db-ejb
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2017
 */
package de.bomc.poc.ejb;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * A base class for arquillian tests.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class ArquillianBase {
	// The default scheme.
	final String DEFAULT_SCHEME = "http";
	// A systemProperty that is used, to get the host name of a running wildfly
	// instance, during arquillian tests at runtime.
	final String WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY = "jboss.bind.address";
	// A systemProperty that is used, to get the port-offset of a running
	// wildfly instance, during arquillian tests at runtime.
	final String WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY = "jboss.socket.binding.port-offset";

	public static WebArchive createTestArchive(final String webArchiveName) {

		return ShrinkWrap.create(WebArchive.class, webArchiveName + ".war").addClass(ArquillianBase.class);
	}

	// _______________________________________________
	// Helper methods.
	// -----------------------------------------------

	/**
	 * The arquillian war is already deployed to wildfly, read now the
	 * connection properties from global system properties, to build the base
	 * url.
	 * 
	 * @param webArchiveName
	 *            part of the base url, 'http://localhost:8080/webArchiveName/'.
	 * @return the base url, created from system properties.
	 * @throws URISyntaxException
	 *             is thrown to indicate that a string could not be parsed as a
	 *             URI reference.
	 */
//	protected URI buildUri(final String webArchiveName) throws URISyntaxException {
//		// The wildfly default port for http requests.
//		final int WILDFLY_DEFAULT_HTTP_PORT = 8080;
//		// Get port of the running wildfly instance.
//		final int port = WILDFLY_DEFAULT_HTTP_PORT
//				+ Integer.parseInt(System.getProperty(this.WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY));
//
//		// Build the base Url.
//		return new URI(this.DEFAULT_SCHEME, null, System.getProperty(this.WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY),
//				port, "/" + webArchiveName + "/" + JaxRsActivator.APPLICATION_PATH, null, null);
//	}
}
