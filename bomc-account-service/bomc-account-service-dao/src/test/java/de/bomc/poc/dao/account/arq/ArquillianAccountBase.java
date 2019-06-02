package de.bomc.poc.dao.account.arq;

import java.time.LocalDate;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import de.bomc.poc.model.account.Account;
import de.bomc.poc.model.account.Address;
import de.bomc.poc.model.account.LegalUser;
import de.bomc.poc.model.account.NaturalUser;
import de.bomc.poc.model.account.User;

/**
 * A base class for Arquillian tests.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
public abstract class ArquillianAccountBase {

	// ________________________________________________
	// Account
	protected static final String ACCOUNT_DISPLAY_NANE = "ACCOUNT_DISPLAY_NANE"; 
	// ________________________________________________
	// AccountUser
	public static final Boolean ACCOUNT_USER_OWNER_FLAG_TRUE = true;
	public static final Boolean ACCOUNT_USER_OWNER_FLAG_FALSE = false;
	// ________________________________________________
	// Person
	protected static final String USER_USERNAME = "USER_USERNAME";
	protected static final String USER_CHANGED_USERNAME = "USER_CHANGED_USERNAME";
	protected static final String USER_USERNAME_UNKNOWN = "USER_NAME_UNKNOWN";
	protected static final String LEGAL_USER_COMPANY_ID = "LEGAL_USER_COMPANY_ID";
	protected static final LocalDate NATURAL_USER_BIRTHDATE = LocalDate.of(2011, 11, 13);
	// ________________________________________________
	// Address
	protected static final String ADDRESS_CITY = "ADDRESS_CITY";
	protected static final String ADDRESS_COUNTRY = "ADDRESS_COUNTRY";
	protected static final String ADDRESS_STREET = "ADDRESS_STREET";
	protected static final String ADDRESS_ZIP= "4000";
	
	/**
	 * Creates a archive without db configuration.
	 * 
	 * @param archiveName
	 *            the name of the created archive.
	 * @return a war archive.
	 */
	protected static WebArchive createTestArchive(final String archiveName) {
		return ShrinkWrap.create(WebArchive.class, archiveName + ".war").addClass(ArquillianAccountBase.class)
				.addAsWebInfResource(getBeansXml(), "beans.xml");
	}

	/**
	 * Creates a archive with h2 db configuration.
	 * 
	 * @param archiveName
	 *            the name of the created archive.
	 * @return a war archive.
	 */
	public static WebArchive createTestArchiveWithH2Db(final String archiveName) {
		final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, archiveName + ".war")
				.addClass(ArquillianAccountBase.class).addAsWebInfResource(getBeansXml(), "beans.xml")
				.addAsResource(getPersistenceXml(), "META-INF/persistence.xml")
				// This adds the model and all affected classes to the archive.
				.addPackages(true, "de.bomc.poc.model");
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
                                  + "	<alternatives>\n"
                                  + "		<stereotype>de.bomc.poc.dao.db.qualifier.LocalDatabase</stereotype>\n"
                                  + "	</alternatives>\n"
                                  + "</beans>");
   	}
    
   	public static StringAsset getPersistenceXml() {
        return new StringAsset("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                               + "<persistence version=\"2.1\"\n"
                               + "   xmlns=\"http://xmlns.jcp.org/xml/ns/persistence\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                               + "   xsi:schemaLocation=\"\n"
                               + "        http://xmlns.jcp.org/xml/ns/persistence\n"
                               + "        http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd\">\n"
                               + "   <persistence-unit name=\"bomc-local-PU\">\n"
                               + "      <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>\n"
                               + "      <jta-data-source>java:jboss/datasources/BomcH2DS</jta-data-source>\n"
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
                               + "         <property name=\"hibernate.show_sql\" value=\"true\" />\n"
                               + "         <property name=\"hibernate.format_sql\" value=\"true\" />\n"
                               + "         <property name=\"hibernate.use_sql_comments\" value=\"true\"/>\n"
                               + "\n"
                               + "         <!--<property name=\"hibernate.cache.use_second_level_cache\" value=\"false\"/>\n-->"
                               + "         <!--<property name=\"hibernate.cache.use_query_cache\" value=\"false\" />\n-->"
                               + "      </properties>\n"
                               + "   </persistence-unit>\n"
                               + "   <persistence-unit name=\"bomc-prod-PU\">\n"
                               + "      <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>\n"
                               + "      <jta-data-source>java:jboss/datasources/BomcH2DS</jta-data-source>\n"
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
   	
   	// _______________________________________________
   	// Helper methods.
   	// -----------------------------------------------
   	
   	protected Address getNotPersistedAddress(final String zip) {
   		final Address address = new Address();
   		
   		address.setCity(ADDRESS_CITY);
   		address.setCountry(ADDRESS_COUNTRY);
   		address.setStreet(ADDRESS_STREET);
   		address.setZipCode(zip);
   		
   		return address;
   	}
   	
   	protected User getNotPersistedUser(final String username) {
   		final User user = new User(username);
   		
   		return user;
   	}

   	protected LegalUser getNotPersistedLegalUser(final String username) {
   		final LegalUser legalUser = new LegalUser(username, LEGAL_USER_COMPANY_ID);
   		
   		return legalUser;
   	}
   	
   	protected NaturalUser getNotPersistedNaturalUser(final String username) {
   		final NaturalUser naturalUser = new NaturalUser(username);
   		naturalUser.setBirthDate(NATURAL_USER_BIRTHDATE);
   		
   		return naturalUser;
   	}
   	
   	protected Account getNotPersistedAccount(final String displayName) {
   		final Account account = new Account(displayName);
   		
   		return account;
   	}
}