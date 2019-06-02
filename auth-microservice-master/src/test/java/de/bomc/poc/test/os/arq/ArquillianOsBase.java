/**
 * Project: MY_POC
 * <p/>
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
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.os.arq;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import de.bomc.poc.test.GlobalArqTestProperties;

/**
 * A base class for arquillian tests.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class ArquillianOsBase {

	protected static WebArchive createTestArchive(final String webArchiveName) {

		return ShrinkWrap.create(WebArchive.class, webArchiveName + ".war").addClass(ArquillianOsBase.class)
				.addAsWebInfResource(getBeansXml(), "beans.xml").addPackages(true, "de.bomc.poc.auth.model");
	}

	private static StringAsset getBeansXml() {
		return new StringAsset(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\" "
						+ "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
						+ "     xsi:schemaLocation=\"\n" + "        http://xmlns.jcp.org/xml/ns/javaee\n"
						+ "        http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\" bean-discovery-mode=\"all\">\n"
						+ "    <!-- 'annotated' - loosely translated, means that only components with a class-level annotation are processed.\n"
						+ "         'all'       - all components are processed, just like they were in Java EE 6 with the explicit beans.xml.\n"
						+ "         'none'      - CDI is effectively disabled. -->\n" + "\n" + "</beans>");
	}

	/**
	 * The arquillian war is already deployed to wildfly, read now the
	 * connection properties from global system properties, to build the base
	 * url.
	 * 
	 * @param webArchiveName
	 *            part of the base url, 'http://localhost:8080/webArchiveName/'.
	 * @return the base url, created from system properties.
	 */
	protected String buildBaseUrl(final String webArchiveName) {
		final String bindAddressProperty = System.getProperty(GlobalArqTestProperties.WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY);
		final int port = GlobalArqTestProperties.WILDFLY_DEFAULT_HTTP_PORT
				+ Integer.parseInt(System.getProperty(GlobalArqTestProperties.WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY).toString());

		final String baseUrl = "http://" + bindAddressProperty + ":" + port + "/" + webArchiveName;

		return baseUrl;
	}
}
