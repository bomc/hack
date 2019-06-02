package de.bomc.poc.config;

import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import de.bomc.poc.rest.JaxRsActivator;

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
	// Constants for tests:
	private static final String HBM2DDL_DEFAULT_VALUE = "create-drop";

	protected static WebArchive createTestArchive(final String webArchiveName) {

		return createTestArchive(webArchiveName, HBM2DDL_DEFAULT_VALUE);
	}

	protected static WebArchive createTestArchive(final String webArchiveName, final String hbml2ddlValue) {

		return ShrinkWrap.create(WebArchive.class, webArchiveName + ".war").addClass(ArquillianBase.class)
				.addAsWebInfResource(getBeansXml(), "beans.xml");
	}

	protected static WebArchive createTestArchiveWithEmptyAssets(final String webArchiveName) {

		return ShrinkWrap.create(WebArchive.class, webArchiveName + ".war").addClass(ArquillianBase.class)
				.addAsWebInfResource(getEmptyBeansXml(), "beans.xml");
	}

	protected static StringAsset getBeansXml() {
		return new StringAsset(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\" "
						+ "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
						+ "     xsi:schemaLocation=\"\n" + "        http://xmlns.jcp.org/xml/ns/javaee\n"
						+ "        http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\" bean-discovery-mode=\"all\">\n"
						+ "    <!-- 'annotated' - loosely translated, means that only components with a class-level annotation are processed.\n"
						+ "         'all'       - all components are processed, just like they were in Java EE 6 with the explicit beans.xml.\n"
						+ "         'none'      - CDI is effectively disabled. -->\n" + "\n" + "    <alternatives>\n"
						+ "        <stereotype>\n" + "            ch.bs.zid.egov.dao.db.qualifier.H2DatabaseQualifier\n"
						+ "        </stereotype>\n" + "    </alternatives>\n" + "</beans>");
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

	/**
	 * <pre>
	 * Create the persistence.xml.
	 * There are two different database entitymanager defined.
	 * This means one for integrationtest and one for productiv stage.
	 * To configure the persistence.xml and the wildlfy for this two cases, the two specified pu'ss must also be configured in the server.
	 * Because no Oracle database is available during the integration test phase, the same in-memory database is used.
	 * </pre>
	 * 
	 * @param hbm2ddlValue
	 *            the configurable hbm2ddl value in the persistence.xml .
	 * @return a persistence.xml as string.
	 */
	protected static StringAsset getPersistenceXml(String hbm2ddlValue) {
		return new StringAsset("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<persistence version=\"2.1\"\n"
				+ "   xmlns=\"http://xmlns.jcp.org/xml/ns/persistence\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
				+ "   xsi:schemaLocation=\"\n" + "        http://xmlns.jcp.org/xml/ns/persistence\n"
				+ "        http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd\">\n"
				+ "   <persistence-unit name=\"egov-h2-pu\">\n"
				+ "      <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>\n"
				// + "
				// <jta-data-source>java:jboss/datasources/ExampleDS</jta-data-source>\n"
				+ "      <jta-data-source>java:jboss/datasources/BomcH2DS</jta-data-source>\n" + "\n"
				+ "      <!-- Caching is enabled for all entities except those for which Cacheable(false) is specified. -->\n"
				+ "      <!--<shared-cache-mode>DISABLE_SELECTIVE</shared-cache-mode>\n-->" + "\n"
				+ "      <properties>\n" + "         <!-- Properties for Hibernate -->\n"
				+ "         <property name=\"hibernate.dialect\" value=\"org.hibernate.dialect.H2Dialect\"/>\n" + "\n"
				+ "         <!-- Um die Validierung auszuschalten muss der Parameter auf 'none' gesetzt werden.\n"
				+ "              Sollte nur fuer Testfaelle eingesetzt werden. -->\n"
				+ "         <property name=\"hibernate.hbm2ddl.auto\" value=\"" + hbm2ddlValue + "\" />\n"
				+ "         <!-- Im produktiv-Betrieb MUESSEN diese Properties auf jeden Fall auf 'false' stehen -> Ansonsten Performance issues. -->\n"
				+ "         <property name=\"hibernate.show_sql\" value=\"true\" />\n"
				+ "         <property name=\"hibernate.format_sql\" value=\"true\" />\n"
				+ "         <property name=\"hibernate.use_sql_comments\" value=\"true\"/>\n" + "\n"
				+ "         <!--<property name=\"hibernate.cache.use_second_level_cache\" value=\"false\"/>\n-->"
				+ "         <!--<property name=\"hibernate.cache.use_query_cache\" value=\"false\" />\n-->"
				+ "      </properties>\n" + "   </persistence-unit>\n" + "</persistence>");
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
