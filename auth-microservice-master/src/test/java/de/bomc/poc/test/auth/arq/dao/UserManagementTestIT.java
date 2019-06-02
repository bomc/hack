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
import de.bomc.poc.auth.dao.jpa.user.JpaUserManagementDao;
import de.bomc.poc.auth.dao.jpa.user.impl.JpaUserManagementDaoImpl;
import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.exception.app.AppInvalidArgumentException;
import de.bomc.poc.exception.app.AppInvalidPasswordException;
import de.bomc.poc.auth.model.usermanagement.User;
import de.bomc.poc.auth.model.usermanagement.UserPassword;
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
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <pre>
 * Performs integration tests for usermanagement.
 * mvn clean install -Parq-wildfly-remote -Dtest=UserManagementTestIT
 * _______________________________________________________________________________
 * NOTE: BeforeClass / AfterClass are ALWAYS and ONLY executed on the Client side.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@RunWith(Arquillian.class)
@PerformanceTest(resultsThreshold = 10)
public class UserManagementTestIT extends ArquillianAuthBase {
    private static final String WEB_ARCHIVE_NAME = "auth-service-user";
    private static final String LOGGER_PREFIX = "UserManagementTestIT";
    @Inject
    @LoggerQualifier(logPrefix = LOGGER_PREFIX)
    private Logger logger;
    @Inject
    @JpaDao
    JpaUserManagementDao jpaUserManagementDao;
    @Inject
    UserTransaction utx;

    // 'testable = true', means all the tests are running inside of the container.
    @Deployment
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = ArquillianAuthBase.createTestArchiveWithMysqlDb(WEB_ARCHIVE_NAME);
        webArchive.addClass(UserManagementTestIT.class);
        webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
        webArchive.addClasses(AppInvalidArgumentException.class, AppInvalidPasswordException.class, AppAuthRuntimeException.class);
        webArchive.addClasses(JpaUserManagementDao.class, JpaUserManagementDaoImpl.class);
        webArchive.addClasses(DatabaseMySqlProducer.class, MySQLDatabase.class, JpaGenericDao.class, AbstractJpaDao.class, JpaDao.class);

        System.out.println("archiveContent: " + webArchive.toString(true));

        return webArchive;
    }

    @Before
    public void setup() throws Exception {
        assertThat(this.jpaUserManagementDao, notNullValue());
    }

    @After
    public void cleanup() {
        //
    }

    /**
     * <pre>
     * 	mvn clean install -Parq-wildfly-remote -Dtest=UserManagementTestIT#test01_createUser_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(1)
    @Performance(time = 1000)
    public void test01_createUser_Pass() throws Exception {
        this.logger.info("test01_createUser_Pass");

        this.utx.begin();

        final User user = new User(TEST_USERNAME);
        user.setNewPassword(TEST_PASWWORD);
        assertThat(user, notNullValue());
        this.jpaUserManagementDao.persist(user);
        assertThat(user.isNew(), is(equalTo(false)));

        this.utx.commit();

        assertThat(user.getId(), notNullValue());
    }

    /**
     * <pre>
     * 	mvn clean install -Parq-wildfly-remote -Dtest=UserManagementTestIT#test10_findByNameAndPassword_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(10)
    @Performance(time = 1000)
    public void test10_findByNameAndPassword_Pass() throws Exception {
        this.logger.info("test10_findByNameAndPassword_Pass");

        // ______________________
        // Test case preparation.
        this.utx.begin();

        final User user = new User(TEST_USERNAME + "-10");
        user.setNewPassword(TEST_PASWWORD);
        assertThat(user, notNullValue());
        this.jpaUserManagementDao.persist(user);
        assertThat(user.isNew(), is(equalTo(false)));

        this.utx.commit();

        final UserPassword userPassword = new UserPassword(user, TEST_PASWWORD);
        final User retUser = this.jpaUserManagementDao.findByNameAndPassword(userPassword);

        assertThat(retUser.getId(), notNullValue());
        assertThat(user, is(equalTo(retUser)));
    }
}
