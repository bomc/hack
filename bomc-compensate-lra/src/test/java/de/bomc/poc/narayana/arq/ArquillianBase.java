package de.bomc.poc.narayana.arq;

import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import de.bomc.poc.narayana.jaxrs.JaxRsActivator;

/**
 * A base class for arquillian tests.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
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

	public static WebArchive createTestArchiveWithEmptyAssets(final String webArchiveName) {

		return ShrinkWrap.create(WebArchive.class, webArchiveName + ".war").addClass(ArquillianBase.class)
				.addAsWebInfResource(getEmptyBeansXml(), "beans.xml");
	}

	protected static StringAsset getEmptyBeansXml() {
		return new StringAsset(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\" "
						+ "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
						+ "     xsi:schemaLocation=\"\n" + "        http://xmlns.jcp.org/xml/ns/javaee\n"
						+ "        http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\" bean-discovery-mode=\"all\">\n"
						+ "    <!-- 'annotated' - loosely translated, means that only components with a class-level annotation are processed.\n"
						+ "         'all'       - all components are processed, just like they were in Java EE 6 with the explicit beans.xml.\n"
						+ "         'none'      - CDI is effectively disabled. -->\n" + "\n" + "</beans>");
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
	protected URI buildUri(final String webArchiveName) throws URISyntaxException {
		// The wildfly default port for http requests.
		final int WILDFLY_DEFAULT_HTTP_PORT = 8080;
		// Get port of the running wildfly instance.
		final int port = WILDFLY_DEFAULT_HTTP_PORT
				+ Integer.parseInt(System.getProperty(this.WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY));

		// Build the base Url.
		return new URI(this.DEFAULT_SCHEME, null, System.getProperty(this.WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY),
				port, "/" + webArchiveName + "/" + JaxRsActivator.APPLICATION_PATH, null, null);
	}
}
