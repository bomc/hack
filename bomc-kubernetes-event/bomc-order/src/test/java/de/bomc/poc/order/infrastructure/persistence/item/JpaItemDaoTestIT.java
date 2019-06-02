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
package de.bomc.poc.order.infrastructure.persistence.item;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.UUID;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.order.ArquillianBase;
import de.bomc.poc.order.CategoryFastIntegrationTestIT;
import de.bomc.poc.order.application.internal.ApplicationUserEnum;
import de.bomc.poc.order.domain.model.basis.AbstractEntity;
import de.bomc.poc.order.domain.model.basis.AbstractMetadataEntity;
import de.bomc.poc.order.domain.model.basis.DomainObject;
import de.bomc.poc.order.domain.model.item.ItemEntity;
import de.bomc.poc.order.domain.model.item.JpaItemDao;
import de.bomc.poc.order.infrastructure.persistence.basis.JpaGenericDao;
import de.bomc.poc.order.infrastructure.persistence.basis.impl.AbstractJpaDao;
import de.bomc.poc.order.infrastructure.persistence.basis.producer.DatabaseProducer;
import de.bomc.poc.order.infrastructure.persistence.basis.qualifier.JpaDao;

/**
 * Tests the dao layer for {@link ItemEntity} and
 * {@link JpaItemDaoImplMock}.
 * <p>
 * 
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=JpaItemDaoTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 23.11.2018
 */
@RunWith(Arquillian.class)
@Category(CategoryFastIntegrationTestIT.class)
public class JpaItemDaoTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "JpaItemDaoTestIT#";
    private static final String WEB_ARCHIVE_NAME = "bomc-item-dao-war";
    // _______________________________________________
    // Constants
    // -----------------------------------------------
    // See name 'item_import.sql'.
    private static final String NAME = "Porsche";
    private static final String USER_ID = UUID.randomUUID().toString();

    // _______________________________________________
    // Membervariables
    // -----------------------------------------------
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Inject
    @JpaDao
    private JpaItemDao jpaItemDao;

    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClasses(JpaItemDaoTestIT.class, CategoryFastIntegrationTestIT.class);
        webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
        webArchive.addClasses(JpaItemDao.class, JpaItemDaoImpl.class);
        webArchive.addClasses(ItemEntity.class, AbstractEntity.class, AbstractMetadataEntity.class,
                DomainObject.class);
        webArchive.addClasses(AbstractJpaDao.class, JpaGenericDao.class, DatabaseProducer.class, JpaDao.class);
        webArchive.addClasses(ApplicationUserEnum.class);

        // Add initial data.
        webArchive.addAsResource("test.scripts/item_import.sql", "import.sql");
        // 
        // Add dependencies
        final ConfigurableMavenResolverSystem resolver = Maven.configureResolver().withMavenCentralRepo(false);

        // NOTE@MVN:will be changed during mvn project generating.
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:exception-lib-ext:jar:?")
                .withTransitivity().asFile());

        System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

        return webArchive;
    }

    /**
     * Setup.
     */
    @Before
    public void setupClass() {
        //
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaItemDaoTestIT#test010_findItemEntityByName_pass
     *
     * <b><code>test010_findItemEntityByName_pass</code>:</b><br>
     *  Tests NamedQuery 'NQ_FIND_BY_ITEM_NAME' in ItemEntity.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *  - item_import.sql must be successful imported by wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Find item with 'Porsche' from db.
     *
     * <b>Postconditions:</b><br>
     *  - Entity is found.
     * </pre>
     */
    @Test
    @InSequence(10)
    public void test010_findItemEntityByName_pass() {
        this.logger.debug(LOG_PREFIX + "test010_findItemEntityByName_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        final ItemEntity itemEntity = this.jpaItemDao.findByName(NAME, USER_ID);

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(itemEntity, notNullValue());
        assertThat(itemEntity.getName(), equalTo(NAME));
    }
}
