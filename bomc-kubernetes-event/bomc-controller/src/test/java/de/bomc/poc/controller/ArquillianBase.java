/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.controller;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import de.bomc.poc.controller.application.util.JaxRsActivator;

import java.net.URI;
import java.net.URISyntaxException;
/**
 * A base class for arquillian tests.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
public class ArquillianBase {

    // The default scheme.
    private static final String DEFAULT_SCHEME = "http";
    // A systemProperty that is used, to get the host name of a running wildfly
    // instance, during arquillian tests at runtime.
    private static final String WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY = "jboss.bind.address";
    // A systemProperty that is used, to get the port-offset of a running
    // wildfly instance, during arquillian tests at runtime.
    private static final String WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY = "jboss.socket.binding.port-offset";
    // Configuration parameter for rest client.
    protected static final int DEFAULT_REST_CLIENT_CONNECTION_TTL = 2000;
    protected static final int DEFAULT_REST_CLIENT_ESTABLISHED_CONNECTION_TIMEOUT = 2000;
    protected static final int DEFAULT_REST_CLIENT_SOCKET_TIMEOUT = 2000;
    // Constants for running tests.
    private static final String HBM2DDL_DEFAULT_VALUE = "create-drop";

    /**
     * Creates a new web archive, with cdi and persistence ("create-drop")
     * enabled.
     *
     * @param webArchiveName
     *            the name of the archive to be created.
     * @return a simple archive, with cdi and persistence enabled.
     */
    protected static WebArchive createTestArchive(final String webArchiveName) {

        return createTestArchive(webArchiveName, HBM2DDL_DEFAULT_VALUE);
    }

    /**
     * Creates a new web archive, with cdi and persistence enabled.
     *
     * @param webArchiveName
     *            the name of the archive to be created.
     * @param hbml2ddlValue
     *            the value for using ddl.
     * @return a simple archive, with cdi and persistence enabled.
     */
    protected static WebArchive createTestArchive(final String webArchiveName, final String hbml2ddlValue) {

        return ShrinkWrap.create(WebArchive.class, webArchiveName + ".war")
                         .addClass(ArquillianBase.class)
                         .addAsWebInfResource(getBeansXml(), "beans.xml")
                         .addAsResource(getPersistenceXml(hbml2ddlValue), "META-INF/persistence.xml");
    }

    /**
     * Creates a empty new web archive.
     *
     * @param webArchiveName
     *            the name of the archive to be created.
     * @return a simple archive.
     */
    protected static WebArchive createEmptyTestArchive(final String webArchiveName) {

        return ShrinkWrap.create(WebArchive.class, webArchiveName + ".war")
                         .addClass(ArquillianBase.class)
                         .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    /**
     * Creates a new web archive, with cdi enabled.
     *
     * @param webArchiveName
     *            the name of the archive to be created.
     * @return a simple archive, with cdi enabled.
     */
    protected static WebArchive createTestArchiveWithEmptyAssets(final String webArchiveName) {

        return ShrinkWrap.create(WebArchive.class, webArchiveName + ".war")
                         .addClass(ArquillianBase.class)
                         .addAsWebInfResource(getEmptyBeansXml(), "beans.xml");
    }

    /**
     * Creates a new web archive, with cdi and the performance tracker as
     * interceptor enabled.
     *
     * @param webArchiveName
     *            the name of the archive to be created.
     * @return a new web archive, with cdi and the performance tracker as
     *         interceptor enabled.
     */
    protected static WebArchive createTestArchiveWithPerfTracker(final String webArchiveName) {

        return ShrinkWrap.create(WebArchive.class, webArchiveName + ".war")
                         .addClass(ArquillianBase.class)
                         .addAsWebInfResource(getBeansXmlWithPerformanceTrackingInterceptor(), "beans.xml");
    }

    protected static StringAsset getBeansXml() {
        return new StringAsset(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\" "
                        + "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                        + "     xsi:schemaLocation=\"\n" + "        http://xmlns.jcp.org/xml/ns/javaee\n"
                        + "        http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\" bean-discovery-mode=\"all\">\n"
                        + "    <!-- 'annotated' - loosely translated, means that only components with a class-level annotation are processed.\n"
                        + "         'all'       - all components are processed, just like they were in Java EE 6 with the explicit beans.xml.\n"
                        + "         'none'      - CDI is effectively disabled. -->\n" + "\n"
                        + "    <!--<alternatives>\n"
                        + "        <stereotype>\n"
                        + "            de.bom.poc.db.qualifier.H2DatabaseQualifier\n"
                        + "        </stereotype>\n"
                        + "    </alternatives> -->\n"
                        + "</beans>");
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

    protected static StringAsset getBeansXmlWithPerformanceTrackingInterceptor() {
        return new StringAsset(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\" "
                        + "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                        + "     xsi:schemaLocation=\"\n" + "        http://xmlns.jcp.org/xml/ns/javaee\n"
                        + "        http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\" bean-discovery-mode=\"all\">\n"
                        + "    <!-- 'annotated' - loosely translated, means that only components with a class-level annotation are processed.\n"
                        + "         'all'       - all components are processed, just like they were in Java EE 6 with the explicit beans.xml.\n"
                        + "         'none'      - CDI is effectively disabled. -->\n" + "    <interceptors>\n"
                        + "    <class>de.bomc.poc.controller.application.basis.performance.interceptor.PerformanceTrackingInterceptor</class>"
                        + " </interceptors>\n" + "</beans>");
    }

    /**
     * Create the persistence.xml.
     *
     * @param hbm2ddlValue
     *            the configurable hbm2ddl value in the persistence.xml .
     * @return a persistence.xml as string.
     */
    protected static StringAsset getPersistenceXml(final String hbm2ddlValue) {
        return new StringAsset("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<persistence version=\"2.1\"\n"
                + "   xmlns=\"http://xmlns.jcp.org/xml/ns/persistence\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "   xsi:schemaLocation=\"\n" + "        http://xmlns.jcp.org/xml/ns/persistence\n"
                + "        http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd\">\n"
                + "   <persistence-unit name=\"bomc-h2-pu\">\n"
                + "      <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>\n"
                + "      <jta-data-source>java:jboss/datasources/ExampleDS</jta-data-source>\n" + "\n"
                + "      <!-- Caching is enabled for all entities except those for which Cacheable(false) is specified. -->\n"
                + "      <!--<shared-cache-mode>DISABLE_SELECTIVE</shared-cache-mode>\n-->" + "\n"
                + "      <properties>\n"
                + "         <!-- Properties for Hibernate -->\n"
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
                + "      </properties>\n" + "   </persistence-unit>\n"
                + "</persistence>");
    }

    // _______________________________________________
    // Helper methods.
    // -----------------------------------------------

    /**
     * The arquillian war is already deployed to wildfly, read now the
     * connection properties from global system properties, to build the base
     * url.
     * @param webArchiveName part of the base url, 'http://localhost:8080/webArchiveName/'.
     * @return the base url, created from system properties.
     * @throws URISyntaxException is thrown to indicate that a string could not be parsed as a URI reference.
     */
    protected URI buildUri(final String webArchiveName) throws URISyntaxException {
        // The wildfly default port for http requests.
        final int wildflyDefaultHttpPort = 8080;
        // Get port of the running wildfly instance.
        final int port = wildflyDefaultHttpPort + Integer.parseInt(System.getProperty(this.WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY));

        // Build the base Url.
        return new URI(this.DEFAULT_SCHEME, null, System.getProperty(this.WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY), port, "/" + webArchiveName + JaxRsActivator.APPLICATION_PATH, null, null);
    }
}
