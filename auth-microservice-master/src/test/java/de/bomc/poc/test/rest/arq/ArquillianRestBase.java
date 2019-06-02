/**
 * Project: MY_POC
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
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.rest.arq;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import de.bomc.poc.test.GlobalArqTestProperties;

/**
 * A parent class for arquillian tests.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
public class ArquillianRestBase {

    protected static WebArchive createTestArchive(final String webArchiveName) {

        // Add dependencies
//        final MavenResolverSystem resolver = Maven.resolver();

        // shared module library
//        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
//                                          .resolve("de.bomc:poc-app-shared-jar:jar:?")
//                                          .withoutTransitivity()
//                                          .asFile());
//
//        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
//                                          .resolve("org.mapstruct:mapstruct-jdk8:jar:?")
//                                          .withoutTransitivity()
//                                          .asFile());

        return ShrinkWrap.create(WebArchive.class, webArchiveName + ".war")
                                                .addClass(ArquillianRestBase.class);
    }

    protected static StringAsset getBeansXml() {
        return new StringAsset("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                               + "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"\n"
                               + "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                               + "     xsi:schemaLocation=\"\n"
                               + "        http://xmlns.jcp.org/xml/ns/javaee\n"
                               + "        http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\" bean-discovery-mode=\"all\">\n"
                               + "    <!-- 'annotated' - loosely translated, means that only components with a class-level annotation are processed.\n"
                               + "         'all'       - all components are processed, just like they were in Java EE 6 with the explicit beans.xml.\n"
                               + "         'none'      - CDI is effectively disabled. -->\n"
                               + "\n"
                               + "</beans>");
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

