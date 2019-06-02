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
package de.bomc.poc.test.auth.arq;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import de.bomc.poc.test.GlobalArqTestProperties;

/**
 * A base class for Arquillian tests.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public abstract class ArquillianAuthBase {
    protected static final String TEST_USERNAME = "SystemUser";
    protected static final String TEST_PASWWORD = "bomc-1234";

    /**
     * Creates a archive without db configuration.
     * @param archiveName the name of the created archive.
     * @return a war archive.
     */
    protected static WebArchive createTestArchive(final String archiveName) {
        return ShrinkWrap.create(WebArchive.class, archiveName + ".war")
                                                .addClass(ArquillianAuthBase.class)
                                                .addAsWebInfResource(getBeansXml(), "beans.xml");
    }

    /**
     * Creates a archive with mysql db configuration.
     * @param archiveName the name of the created archive.
     * @return a war archive.
     */
    protected static WebArchive createTestArchiveWithMysqlDb(final String archiveName) {
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, archiveName + ".war")
                                                .addClass(ArquillianAuthBase.class)
                                                .addAsWebInfResource(getBeansXml(), "beans.xml")
                                                .addAsResource(getMysqlPersistenceXml(), "META-INF/persistence.xml")
                                                // This adds the model and all affected classes to the archive.
                                                .addPackages(true, "de.bomc.poc.auth.model");
        return webArchive;
    }

    /**
     * Creates a archive with mysql db configuration.
     * @param archiveName the name of the created archive.
     * @return a war archive.
     */
    protected static WebArchive createTestArchiveWithH2Db(final String archiveName) {
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, archiveName + ".war")
                                                .addClass(ArquillianAuthBase.class)
                                                .addAsWebInfResource(getBeansXml(), "beans.xml")
                                                .addAsResource(getH2PersistenceXml(), "META-INF/persistence.xml")
                                                // This adds the model and all affected classes to the archive.
                                                .addPackages(true, "de.bomc.poc.auth.model");
        return webArchive;
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


   	public static StringAsset getH2PersistenceXml() {
           return new StringAsset("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                  + "<persistence version=\"2.1\"\n"
                                  + "   xmlns=\"http://xmlns.jcp.org/xml/ns/persistence\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                  + "   xsi:schemaLocation=\"\n"
                                  + "        http://xmlns.jcp.org/xml/ns/persistence\n"
                                  + "        http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd\">\n"
                                  + "   <persistence-unit name=\"poc-auth-h2-pu\">\n"
                                  + "      <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>\n"
                                  + "      <jta-data-source>java:jboss/datasources/poc-auth-h2-ds</jta-data-source>\n"
                                  + "\n"
                                  + "      <!-- Caching is enabled for all entities except those for which Cacheable(false) is specified. -->\n"
                                  + "      <!--<shared-cache-mode>DISABLE_SELECTIVE</shared-cache-mode>\n-->"
                                  + "\n"
                                  + "      <properties>\n"
                                  + "         <!-- Properties for Hibernate -->\n"
                                  + "         <property name=\"hibernate.dialect\" value=\"org.hibernate.dialect.H2Dialect\"/>\n"
                                  + "\n"
                                  + "         <!-- Um die Validierung auszuschalten muss der Parameter auf 'none' gesetzt werden.\n"
                                  + "              Sollte nur fuer Testfaelle eingesetzt werden. -->\n"
                                  + "         <property name=\"hibernate.hbm2ddl.auto\" value=\"create-drop\" />\n"
                                  + "         <!-- Im produktiv-Betrieb MUESSEN diese Properties auf jeden Fall auf 'false' stehen -> Ansonsten Performance issues. -->\n"
                                  + "         <property name=\"hibernate.show_sql\" value=\"false\" />\n"
                                  + "         <property name=\"hibernate.format_sql\" value=\"false\" />\n"
                                  + "         <property name=\"hibernate.use_sql_comments\" value=\"false\"/>\n"
                                  + "\n"
                                  + "         <!--<property name=\"hibernate.cache.use_second_level_cache\" value=\"false\"/>\n-->"
                                  + "         <!--<property name=\"hibernate.cache.use_query_cache\" value=\"false\" />\n-->"
                                  + "      </properties>\n"
                                  + "   </persistence-unit>\n"
                                  + "</persistence>");
       }

   	private static StringAsset getMysqlPersistenceXml() {
           return new StringAsset("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                  + "<persistence version=\"2.1\"\n"
                                  + "   xmlns=\"http://xmlns.jcp.org/xml/ns/persistence\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                  + "   xsi:schemaLocation=\"\n"
                                  + "        http://xmlns.jcp.org/xml/ns/persistence\n"
                                  + "        http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd\">\n"
                                  + "   <persistence-unit name=\"poc-auth-mysql-pu\">\n"
                                  + "      <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>\n"
                                  + "      <jta-data-source>java:jboss/datasources/poc-auth-mysql-ds</jta-data-source>\n"
                                  + "\n"
                                  + "      <!-- Caching is enabled for all entities except those for which Cacheable(false) is specified. -->\n"
                                  + "      <!--<shared-cache-mode>DISABLE_SELECTIVE</shared-cache-mode>\n-->"
                                  + "\n"
                                  + "      <properties>\n"
                                  + "         <!-- Properties for Hibernate -->\n"
                                  + "         <property name=\"hibernate.dialect\" value=\"org.hibernate.dialect.MySQL5Dialect\"/>\n"
                                  + "\n"
                                  + "         <!-- Um die Validierung auszuschalten muss der Parameter auf 'none' gesetzt werden.\n"
                                  + "              Sollte nur fuer Testfaelle eingesetzt werden. -->\n"
                                  + "         <property name=\"hibernate.hbm2ddl.auto\" value=\"create-drop\" />\n"
                                  + "         <!-- Im produktiv-Betrieb MUESSEN diese Properties auf jeden Fall auf 'false' stehen -> Ansonsten Performance issues. -->\n"
                                  + "         <property name=\"hibernate.show_sql\" value=\"true\" />\n"
                                  + "         <property name=\"hibernate.format_sql\" value=\"true\" />\n"
                                  + "         <property name=\"hibernate.use_sql_comments\" value=\"true\"/>\n"
                                  + "\n"
                                  + "         <!--<property name=\"hibernate.cache.use_second_level_cache\" value=\"false\"/>\n-->"
                                  + "         <!--<property name=\"hibernate.cache.use_query_cache\" value=\"false\" />\n-->"
                                  + "      </properties>\n"
                                  + "   </persistence-unit>\n"
                                  + "</persistence>");
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
