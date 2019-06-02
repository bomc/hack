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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

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

import de.bomc.poc.auth.dao.jpa.generic.JpaGenericDao;
import de.bomc.poc.auth.dao.jpa.generic.impl.AbstractJpaDao;
import de.bomc.poc.auth.dao.jpa.producer.DatabaseH2Producer;
import de.bomc.poc.auth.dao.jpa.qualifier.H2Database;
import de.bomc.poc.auth.dao.jpa.qualifier.JpaDao;
import de.bomc.poc.auth.dao.jpa.user.JpaUserManagementDao;
import de.bomc.poc.auth.dao.jpa.user.impl.JpaUserManagementDaoImpl;
import de.bomc.poc.auth.model.usermanagement.User;
import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.exception.app.AppInvalidArgumentException;
import de.bomc.poc.exception.app.AppInvalidPasswordException;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.test.auth.arq.ArquillianAuthBase;

/**
 * <pre>
 * Performs integration tests for usermanagement against the in-memory db of wildfly, the import.sql must be included to the archive.
 * mvn clean install -Parq-wildfly-remote -Dtest=UserManagementTestIT
 * _______________________________________________________________________________
 * NOTE: BeforeClass / AfterClass are ALWAYS and ONLY executed on the Client side.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@RunWith(Arquillian.class)
@PerformanceTest(resultsThreshold = 10)
@org.junit.Ignore("To run this test for the 'AbstractJpaDao' the 'PersistenceContext' must be changed to DatabaseH2Producer!")
public class UserManagementInMemoryTestIT extends ArquillianAuthBase {
    private static final String WEB_ARCHIVE_NAME = "auth-service-user-in-memeory";
    private static final String LOG_PREFIX = "UserManagementInMemoryTestIT#";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Inject
    @JpaDao
    JpaUserManagementDao jpaUserManagementDao;
    @Inject
    UserTransaction utx;

    // 'testable = true', means all the tests are running inside of the container.
    @Deployment
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = ArquillianAuthBase.createTestArchiveWithH2Db(WEB_ARCHIVE_NAME);
        webArchive.addClass(UserManagementInMemoryTestIT.class);
        webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
        webArchive.addClasses(AppInvalidArgumentException.class, AppInvalidPasswordException.class, AppAuthRuntimeException.class);
        webArchive.addClasses(JpaUserManagementDao.class, JpaUserManagementDaoImpl.class);
        webArchive.addClasses(DatabaseH2Producer.class, H2Database.class, JpaGenericDao.class, AbstractJpaDao.class, JpaDao.class);

        webArchive.addAsResource("import.sql");
        
        System.out.println(LOG_PREFIX + "archiveContent: " + webArchive.toString(true));

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
     * NOTE: asserts depends on import.sql.
     * <pre>
     * 	mvn clean install -Parq-wildfly-remote -Dtest=UserManagementInMemoryTestIT#test01_findUserById_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(10)
    @Performance(time = 1000)
    public void test01_findUserById_Pass() {
        this.logger.info(LOG_PREFIX + "test01_findUserById_Pass");

        final User retUser = this.jpaUserManagementDao.findById(1L);

        this.logger.debug(LOG_PREFIX + "test01_findUserById_Pass [user=" + retUser.toString() + "]");
        assertThat(retUser.getId(), notNullValue());
    }
}

