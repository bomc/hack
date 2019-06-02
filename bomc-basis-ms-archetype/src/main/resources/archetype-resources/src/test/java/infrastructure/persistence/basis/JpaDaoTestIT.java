#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.infrastructure.persistence.basis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import ${package}.ArquillianBase;
import ${package}.CategoryBasisIntegrationTestIT;
import ${package}.application.internal.AppErrorCodeEnum;
import ${package}.application.internal.ApplicationUserEnum;
import ${package}.domain.model.basis.log.ExceptionLogEntity;
import ${package}.domain.model.basis.log.JpaExceptionLogDao;
import ${package}.domain.model.basis.AbstractEntity;
import ${package}.domain.model.basis.AbstractMetadataEntity;
import ${package}.domain.model.basis.DomainObject;
import ${package}.infrastructure.persistence.basis.impl.AbstractJpaDao;
import ${package}.infrastructure.persistence.basis.producer.DatabaseProducer;
import ${package}.infrastructure.persistence.basis.qualifier.JpaDao;
import ${package}.infrastructure.persistence.basis.log.JpaExceptionLogDaoImpl;

/**
 * <pre>
 * Tests the dao shared components {@link JpaGenericDao, AbstractJpaDao,
 * AbstractMetadataEntityJpaDao, DatabaseProducer, JpaDao}.
 * It uses the {@link ExceptionLogEntity} to test the shared dao components.
 *
 *  mvn clean install -Parq-wildfly-remote -Dtest=JpaDaoTestIT
 * </pre>
 * @author <a href="mailto:ExceptionLogEntity">bomc</a>
 * @since 04.12.2018
 */
@RunWith(Arquillian.class)
@Category(CategoryBasisIntegrationTestIT.class)
public class JpaDaoTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "JpaDaoTestIT${symbol_pound}";
    private static final String WEB_ARCHIVE_NAME = "bomc-jpa-dao-shared-war";
    // _______________________________________________
    // Constants
    // -----------------------------------------------
    private static final int COUNT_FORM_IMPORT_SQL = 30;
    // _______________________________________________
    // Membervariables
    // -----------------------------------------------
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Inject
    private UserTransaction utx;
    @Inject
    @JpaDao
    private JpaExceptionLogDao jpaExceptionLogDao;
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClasses(JpaDaoTestIT.class, CategoryBasisIntegrationTestIT.class);
        webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
        webArchive.addClasses(JpaExceptionLogDao.class, JpaExceptionLogDaoImpl.class);
        webArchive.addClasses(ExceptionLogEntity.class, AbstractEntity.class, AbstractMetadataEntity.class, DomainObject.class);
        webArchive.addClasses(AbstractJpaDao.class, JpaGenericDao.class, DatabaseProducer.class, JpaDao.class);
        webArchive.addClasses(ApplicationUserEnum.class, AppErrorCodeEnum.class);
        // Add initial data.
        webArchive.addAsResource("exception_log_import.sql", "import.sql");

        // Add dependencies
        final ConfigurableMavenResolverSystem
            resolver =
            Maven.configureResolver()
                 .withMavenCentralRepo(false);

        // NOTE@MVN:will be changed during mvn project generating.
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("de.bomc.poc:exception-lib-ext:jar:?")
                                          .withTransitivity()
                                          .asFile());

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
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaDaoTestIT${symbol_pound}test010_findRange_pass
     *
     * <b><code>test010_findRange_pass</code>:</b><br>
     *
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *  - The 'import.sql' must be loaded in db.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Read the range 4 - 28 from db.
     *
     * <b>Postconditions:</b><br>
     *  - The method returns 24 entities.
     * </pre>
     * @throws Exception not expected.
     */
    @Test
    @InSequence(10)
    public void test010_findRange_pass() throws Exception {
        this.logger.info(LOG_PREFIX + "test010_findRange_pass");

        final int startRange = 4;
        final int endRange = 28;
        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final int count = this.jpaExceptionLogDao.count();
        assertThat(count, equalTo(COUNT_FORM_IMPORT_SQL));

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        final List<ExceptionLogEntity> findRangeList = this.jpaExceptionLogDao.findRange(new int[]{startRange, endRange});

        this.logger.debug(LOG_PREFIX + "test010_findRange_pass [findRangeList.size=" + findRangeList.size() + "]");

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(findRangeList.size(), equalTo(endRange - startRange));
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaDaoTestIT${symbol_pound}test020_findAll_pass
     *
     * <b><code>test020_findAll_pass</code>:</b><br>
     *
     *
     * <b>Preconditions:</b><br>
     * - Artifact must be successful deployed in Wildfly.
     * - The 'import.sql' must be loaded in db.
     *
     * <b>Scenario:</b><br>
     * The following steps are executed:
     * - Use 'findAll' method to read all entities from db.
     *
     * <b>Postconditions:</b><br>
     * - The method returns 30 entities.
     * </pre>
     * @throws Exception not expected.
     */
    @Test
    @InSequence(20)
    public void test020_findAll_pass() throws Exception {
        this.logger.info(LOG_PREFIX + "test020_findAll_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final int count = this.jpaExceptionLogDao.count();
        assertThat(count, equalTo(COUNT_FORM_IMPORT_SQL));

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        final List<ExceptionLogEntity> findAllList = this.jpaExceptionLogDao.findAll();

        this.logger.debug(LOG_PREFIX + "test020_findAll_pass [findAllList.size=" + findAllList.size() + "]");

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(findAllList.size(), equalTo(COUNT_FORM_IMPORT_SQL));
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaDaoTestIT${symbol_pound}test030_merge_pass
     *
     * <b><code>test030_merge_pass</code>:</b><br>
     *
     *
     * <b>Preconditions:</b><br>
     * - Artifact must be successful deployed in Wildfly.
     * - The 'import.sql' must be loaded in db.
     *
     * <b>Scenario:</b><br>
     * The following steps are executed:
     * - Read a entity from db and update it.
     * - Change the exceptionUuid of the ExceptionLogEntity.
     * - Use 'merge' method to write it down to db.
     *
     * <b>Postconditions:</b><br>
     * - The entity is updated and has the new exceptionUuid.
     * </pre>
     * @throws Exception not expected.
     */
    @Test
    @InSequence(30)
    public void test030_merge_pass() throws Exception {
        this.logger.info(LOG_PREFIX + "test030_merge_pass");

        final String updatedUUID = "updatedUUID";

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final int count = this.jpaExceptionLogDao.count();
        assertThat(count, equalTo(COUNT_FORM_IMPORT_SQL));

        final List<ExceptionLogEntity> findAllList = this.jpaExceptionLogDao.findAll();
        final ExceptionLogEntity exceptionLogEntity = findAllList.get(0);
        assertThat(exceptionLogEntity, notNullValue());

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        this.utx.begin();

        exceptionLogEntity.setExceptionUuid(updatedUUID);
        final ExceptionLogEntity retExceptionLogEntity = this.jpaExceptionLogDao.merge(exceptionLogEntity, ApplicationUserEnum.TEST_USER.name());

        this.utx.commit();

        this.logger.debug(LOG_PREFIX + "test030_merge_pass [retExceptionLogEntity=" + retExceptionLogEntity + "]");

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(updatedUUID, equalTo(retExceptionLogEntity.getExceptionUuid()));
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaDaoTestIT${symbol_pound}test040_findByPositionalParameters_pass
     *
     * <b><code>test040_findByPositionalParameters_pass</code>:</b><br>
     *
     *
     * <b>Preconditions:</b><br>
     * - Artifact must be successful deployed in Wildfly.
     * - The 'import.sql' must be loaded in db.
     *
     * <b>Scenario:</b><br>
     * The following steps are executed:
     * - Run findByPositionalParameters-method with NQ_FIND_BY_EXCEPTION_UUID NamedQuery.
     *
     * <b>Postconditions:</b><br>
     * - One entity is returned with the given exceptionUuid.
     * </pre>
     * @throws Exception not expected.
     */
    @Test
    @InSequence(40)
    public void test040_findByPositionalParameters_pass() throws Exception {
        this.logger.info(LOG_PREFIX + "test040_findByPositionalParameters_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final int count = this.jpaExceptionLogDao.count();
        assertThat(count, equalTo(COUNT_FORM_IMPORT_SQL));

        final List<ExceptionLogEntity> findAllList = this.jpaExceptionLogDao.findAll();
        final ExceptionLogEntity exceptionLogEntity = findAllList.get(0);
        assertThat(exceptionLogEntity, notNullValue());

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        // See 'import.sql' for valid date.
        final Object[] values = new Object[]{exceptionLogEntity.getExceptionUuid()};
        final List<ExceptionLogEntity> retExceptionLogEntityList = this.jpaExceptionLogDao.findByPositionalParameters(ExceptionLogEntity.NQ_FIND_BY_EXCEPTION_UUID, values);

        this.logger.debug(LOG_PREFIX + "test040_findByPositionalParameters_pass [retExceptionLogEntityList.size=" + retExceptionLogEntityList.size() + "]");

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(retExceptionLogEntityList.size(), equalTo(1));
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaDaoTestIT${symbol_pound}test040_findByPositionalParameters_failed
     *
     * <b><code>test040_findByPositionalParameters_failed</code>:</b><br>
     *  Tests the 'test040_findByPositionalParameters_failed'-method when the parameter map is null.
     *
     * <b>Preconditions:</b><br>
     * - Artifact must be successful deployed in Wildfly.
     * - The 'import.sql' must be loaded in db.
     *
     * <b>Scenario:</b><br>
     * The following steps are executed:
     * - Run findByPositionalParameters-method with NQ_FIND_BY_EXCEPTION_UUID NamedQuery and parameter map equals 'null'.
     *
     * <b>Postconditions:</b><br>
     * A IllegalArgumentException is expected.
     * </pre>
     * @throws IllegalArgumentException not expected.
     */
    @Test
    @InSequence(50)
    public void test050_findByPositionalParameters_fail() throws Exception {
        this.logger.info(LOG_PREFIX + "test050_findByPositionalParameters_fail");

        this.thrown.expect(IllegalArgumentException.class);

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final int count = this.jpaExceptionLogDao.count();
        assertThat(count, equalTo(COUNT_FORM_IMPORT_SQL));

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        final Object[] params = null;
        this.jpaExceptionLogDao.findByPositionalParameters(ExceptionLogEntity.NQ_FIND_BY_EXCEPTION_UUID, params);

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaDaoTestIT${symbol_pound}test060_findByNamedParameters_pass
     *
     * <b><code>test060_findByNamedParameters_pass</code>:</b><br>
     *
     *
     * <b>Preconditions:</b><br>
     * - Artifact must be successful deployed in Wildfly.
     * - The 'import.sql' must be loaded in db.
     *
     * <b>Scenario:</b><br>
     * The following steps are executed:
     * - Run findByNamedParameters-method with NQ_FIND_BY_RESPONSE_STATUS NamedQuery.
     *
     * <b>Postconditions:</b><br>
     * - All entities will be returned (See 'import.sql' for more information).
     * </pre>
     * @throws Exception not expected.
     */
    @Test
    @InSequence(60)
    public void test060_findByNamedParameters_pass() throws Exception {
        this.logger.info(LOG_PREFIX + "test060_findByNamedParameters_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final int count = this.jpaExceptionLogDao.count();
        assertThat(count, equalTo(COUNT_FORM_IMPORT_SQL));

        final List<ExceptionLogEntity> findAllList = this.jpaExceptionLogDao.findAll();
        final ExceptionLogEntity exceptionLogEntity = findAllList.get(0);
        assertThat(exceptionLogEntity, notNullValue());

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        // See 'import.sql' for valid date.
        final Map<String, String> params = new HashMap<String, String>(1);
        params.put("responseStatus", "responseStatus");
        final List<ExceptionLogEntity> retExceptionLogEntityList = this.jpaExceptionLogDao.findByNamedParameters(ExceptionLogEntity.NQ_FIND_BY_RESPONSE_STATUS, params);

        this.logger.debug(LOG_PREFIX + "test060_findByPositionalParameters_pass [retExceptionLogEntityList.size=" + retExceptionLogEntityList.size() + "]");

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(retExceptionLogEntityList.size(), equalTo(30));
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaDaoTestIT${symbol_pound}test070_remove_pass
     *
     * <b><code>test070_remove_pass</code>:</b><br>
     *
     *
     * <b>Preconditions:</b><br>
     * - Artifact must be successful deployed in Wildfly.
     * - The 'import.sql' must be loaded in db.
     *
     * <b>Scenario:</b><br>
     * The following steps are executed:
     * - Run remove-method.
     * - Clean up jpa/entityManager cache.
     *
     * <b>Postconditions:</b><br>
     * - One entity has to be removed from db.
     * </pre>
     * @throws Exception not expected.
     */
    @Test
    @InSequence(70)
    public void test070_remove_pass() throws Exception {
        this.logger.info(LOG_PREFIX + "test070_remove_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        this.utx.begin();

        final int count = this.jpaExceptionLogDao.count();
        assertThat(count, equalTo(COUNT_FORM_IMPORT_SQL));

        final List<ExceptionLogEntity> findAllList = this.jpaExceptionLogDao.findAll();
        final ExceptionLogEntity exceptionLogEntity = findAllList.get(0);
        assertThat(exceptionLogEntity, notNullValue());

        final int listSize = findAllList.size();

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------

        this.jpaExceptionLogDao.remove(exceptionLogEntity);

        this.utx.commit();

        // Clean up jpa cache.
        this.utx.begin();

        this.jpaExceptionLogDao.clearEntityManagerCache();

        this.utx.commit();

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(this.jpaExceptionLogDao.findAll()
                                          .size(), equalTo(listSize - 1));
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaDaoTestIT${symbol_pound}test080_clearCache_fail
     *
     * <b><code>test080_clearCache_fail</code>:</b><br>
     *  Tests the 'clearCache'-method without and a transaction context.
     *
     * <b>Preconditions:</b><br>
     * - Artifact must be successful deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     * The following steps are executed:
     * - Invoke the 'clearCache'-method.
     *
     * <b>Postconditions:</b><br>
     * - A AppRuntimeException must be thrown.
     * </pre>
     * @throws AppRuntimeException
     */
    @Test
    @InSequence(80)
    public void test080_clearCache_fail() throws AppRuntimeException {
        this.logger.info(LOG_PREFIX + "test080_clearCache_fail");

        this.thrown.expect(AppRuntimeException.class);

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        this.jpaExceptionLogDao.clearEntityManagerCache();

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaDaoTestIT${symbol_pound}test999_againstEmptyDb_fail
     *
     * <b><code>test999_againstEmptyDb_fail</code>:</b><br>
     *  ______________________________________________
     *  NOTE: This test must be always the last test, ALL data will be deleted from db.
     *
     * <b>Preconditions:</b><br>
     * - Artifact must be successful deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     * The following steps are executed:
     * -
     *
     * <b>Postconditions:</b><br>
     * -
     * </pre>
     * @throws Exception is not expected.
     */
    @Test
    @InSequence(999)
    public void test999_againstEmptyDb_fail() throws Exception {
        this.logger.info(LOG_PREFIX + "test999_againstEmptyDb_fail");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        this.utx.begin();

        final LocalDate localDate = LocalDate.of(2018, 11, 29);
        this.jpaExceptionLogDao.deleteEntriesFromSpecifiedDate(localDate);

        this.utx.commit();

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        //
        // Check if all entities are deleted, by 'count'-method.
        final int count = this.jpaExceptionLogDao.count();
        // Check if all entities are deleted, by 'findAll'-method.
        final List<ExceptionLogEntity> emptyList = this.jpaExceptionLogDao.findAll();

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(count, equalTo(0));

        assertThat(emptyList, notNullValue());
        assertThat(emptyList.size(), equalTo(0));
    }
}
