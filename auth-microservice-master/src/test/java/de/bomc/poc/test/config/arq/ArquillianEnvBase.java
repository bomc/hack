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
package de.bomc.poc.test.config.arq;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * A parent class for environment configuration arquillian tests.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class ArquillianEnvBase {
    protected static WebArchive createTestArchive(final String webArchiveName) {
        return ShrinkWrap.create(WebArchive.class, webArchiveName + ".war")
                                                .addClass(ArquillianEnvBase.class)
                                                .addAsWebInfResource(getBeansXml(), "beans.xml");
    }
	
	private static StringAsset getBeansXml() {
        return new StringAsset("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                               + "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\" "
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
}
