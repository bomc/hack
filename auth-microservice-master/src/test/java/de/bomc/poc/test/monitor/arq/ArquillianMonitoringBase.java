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
package de.bomc.poc.test.monitor.arq;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * A base class for arquillian tests.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class ArquillianMonitoringBase {

	static WebArchive createTestArchive(final String webArchiveName) {

		return ShrinkWrap.create(WebArchive.class, webArchiveName + ".war").addClass(ArquillianMonitoringBase.class)
				.addAsWebInfResource(getBeansXml(), "beans.xml");
	}

	private static StringAsset getBeansXml() {
		return new StringAsset(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" 
						+ "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\" "
						+ "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
						+ "     xsi:schemaLocation=\"\n" + "        http://xmlns.jcp.org/xml/ns/javaee\n"
						+ "        http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\" bean-discovery-mode=\"all\">\n"
						+ "    <!-- 'annotated' - loosely translated, means that only components with a class-level annotation are processed.\n"
						+ "         'all'       - all components are processed, just like they were in Java EE 6 with the explicit beans.xml.\n"
						+ "         'none'      - CDI is effectively disabled. -->\n"
						+ "	<interceptors>\n"
						+ "		<class>de.bomc.poc.monitor.interceptor.PerformanceTrackingInterceptor</class>"
						+ "	</interceptors>\n" 
						+ "</beans>");
	}
}
