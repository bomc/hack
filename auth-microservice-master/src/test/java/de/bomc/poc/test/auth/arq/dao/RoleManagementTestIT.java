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
package de.bomc.poc.test.auth.arq.dao;

import de.bomc.poc.auth.dao.jpa.generic.JpaGenericDao;
import de.bomc.poc.auth.dao.jpa.generic.impl.AbstractJpaDao;
import de.bomc.poc.auth.dao.jpa.producer.DatabaseMySqlProducer;
import de.bomc.poc.auth.dao.jpa.qualifier.JpaDao;
import de.bomc.poc.auth.dao.jpa.qualifier.MySQLDatabase;
import de.bomc.poc.auth.dao.jpa.role.JpaRoleManagementDao;
import de.bomc.poc.auth.dao.jpa.role.impl.JpaRoleManagementDaoImpl;
import de.bomc.poc.auth.dao.jpa.security.JpaGrantManagementDao;
import de.bomc.poc.auth.dao.jpa.security.impl.JpaGrantManagementDaoImpl;
import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.exception.app.AppInvalidArgumentException;
import de.bomc.poc.exception.app.AppInvalidPasswordException;
import de.bomc.poc.auth.model.usermanagement.Grant;
import de.bomc.poc.auth.model.usermanagement.Role;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.test.auth.arq.ArquillianAuthBase;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.performance.annotation.Performance;
import org.jboss.arquillian.performance.annotation.PerformanceTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <pre>
 *  Performs integration tests for rolemanagement.
 *
 *  mvn clean install -Parq-wildfly-remote -Dtest=RoleManagementTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@RunWith(Arquillian.class)
@PerformanceTest(resultsThreshold = 10)
public class RoleManagementTestIT extends ArquillianAuthBase {
    private static final String WEB_ARCHIVE_NAME = "auth-service-role";
    private static final String LOGGER_PREFIX = "RoleManagementTestIT";
    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Inject
    @LoggerQualifier(logPrefix = LOGGER_PREFIX)
    private Logger logger;
    @Inject
    @JpaDao
    JpaRoleManagementDao jpaRoleManagementDao;
    @Inject
    @JpaDao
    JpaGrantManagementDao jpaGrantManagementDao;
    @Inject
    UserTransaction utx;

    // 'testable = true', means all the tests are running inside of the container.
    @Deployment
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = ArquillianAuthBase.createTestArchiveWithMysqlDb(WEB_ARCHIVE_NAME);
        webArchive.addClass(RoleManagementTestIT.class);
        webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
        webArchive.addClasses(AppInvalidArgumentException.class, AppInvalidPasswordException.class, AppAuthRuntimeException.class);
        webArchive.addClasses(JpaRoleManagementDao.class, JpaRoleManagementDaoImpl.class);
        webArchive.addClasses(JpaGrantManagementDao.class, JpaGrantManagementDaoImpl.class);
        webArchive.addClasses(DatabaseMySqlProducer.class, MySQLDatabase.class, JpaGenericDao.class, AbstractJpaDao.class, JpaDao.class);

        System.out.println("archiveContent: " + webArchive.toString(true));

        return webArchive;
    }

    @Before
    public void setup() throws Exception {
        assertThat(this.jpaRoleManagementDao, notNullValue());
    }

    @After
    public void cleanup() {
        //
    }

    /**
     * <pre>
     * 	mvn clean install -Parq-wildfly-remote -Dtest=RoleManagementTestIT#test01_createRole_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(1)
    @Performance(time = 1000)
    public void test01_createRole_Pass() throws Exception {
        this.logger.info("test01_createRole_Pass");

        // The test starts in a servlet container, so a transaction has to be introduced.
        this.utx.begin();

        final Role role = new Role(ArquillianAuthBase.TEST_USERNAME);
        this.jpaRoleManagementDao.persist(role);

        this.utx.commit();

        assertThat(role.getId(), notNullValue());
    }

    /**
     * <pre>
     * 	mvn clean install -Parq-wildfly-remote -Dtest=RoleManagementTestIT#test10_removeAllGrantsFromRole_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(1)
    @Performance(time = 3000)
    public void test10_removeAllGrantsFromRole_Pass() throws Exception {
        this.logger.info("test10_removeAllGrantsFromRole_Pass");

        // _________________
        // Test preparation:
        // The test starts in a servlet container, so a transaction has to be introduced.
        this.utx.begin();

        final Role role = new Role(ArquillianAuthBase.TEST_USERNAME + "-10");
        final Grant grant = new Grant("admin", "Can perform the whole enchilada");

        this.jpaGrantManagementDao.persist(grant);
        assertThat(grant.getId(), notNullValue());

        role.addGrant(grant);
        this.jpaRoleManagementDao.persist(role);
        assertThat(role.getId(), notNullValue());

        this.utx.commit();
        //
        //
        // Start the test case.
        // The test starts in a servlet container, so a transaction has to be introduced.
        this.utx.begin();

        final Set<Grant> grantSet = new HashSet<>(1);
        grantSet.add(grant);
        final boolean isRemoved = this.jpaRoleManagementDao.removeAllGrantsFromRole(role.getId(), grantSet);
        assertThat(isRemoved, is(equalTo(true)));

        this.utx.commit();

        this.utx.begin();
        // Start a transaction, the grants are lazy loaded. With access to size(), the grants will be loaded from db.
        // Test whether the role is still present.
        final Role retRole = this.jpaRoleManagementDao.findById(role.getId());
        assertThat(retRole, notNullValue());

        assertThat(retRole.getGrants().size(), is(equalTo(0)));
        this.utx.commit();
    }

    /**
     * <pre>
     * 	mvn clean install -Parq-wildfly-remote -Dtest=RoleManagementTestIT#test20_removeAllGrantsFromRole_idParamNull_Fail
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(20)
    @Performance(time = 1000)
    public void test20_removeAllGrantsFromRole_idParamNull_Fail() throws Exception {
        this.logger.info("test20_removeAllGrantsFromRole_idParamNull_Fail");

        this.thrown.expect(javax.validation.ConstraintViolationException.class);

        final Set<Grant> grantSet = new HashSet<>(1);
        this.jpaRoleManagementDao.removeAllGrantsFromRole(null, grantSet);
    }

    /**
     * <pre>
     * 	mvn clean install -Parq-wildfly-remote -Dtest=RoleManagementTestIT#test21_removeAllRoles_setParamNull_Fail
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(21)
    @Performance(time = 1000)
    public void test21_removeAllRoles_setParamNull_Fail() throws Exception {
        this.logger.info("test21_removeAllRoles_setParamNull_Fail");

        this.thrown.expect(javax.validation.ConstraintViolationException.class);

        this.jpaRoleManagementDao.removeAllGrantsFromRole(1L, null);
    }

    /**
     * <pre>
     * 	mvn clean install -Parq-wildfly-remote -Dtest=RoleManagementTestIT#test22_removeAllRoles_catchValidationEx_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(22)
    @Performance(time = 1000)
    public void test22_removeAllRoles_catchValidationEx_Pass() throws Exception {
        this.logger.info("test22_removeAllRoles_catchValidationEx_Pass");

        try {
            this.jpaRoleManagementDao.removeAllGrantsFromRole(1L, null);
        } catch(Exception ex) {
            ConstraintViolationException c = (ConstraintViolationException)ex;
            Set<ConstraintViolation<?>> constraintViolations = c.getConstraintViolations();
            // Get comma separated error messages.
            final String errorMessages = constraintViolations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));

            this.logger.error("test22_removeAllRoles_catchValidationEx_Pass - ExtractedMessages=" + errorMessages);

            assertThat(errorMessages, is(equalTo("darf nicht null sein")));
        }
    }
}
