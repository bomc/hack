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
import de.bomc.poc.auth.dao.jpa.user.JpaUserManagementDao;
import de.bomc.poc.auth.dao.jpa.user.impl.JpaUserManagementDaoImpl;
import de.bomc.poc.auth.model.usermanagement.Grant;
import de.bomc.poc.auth.model.usermanagement.Role;
import de.bomc.poc.auth.model.usermanagement.User;
import de.bomc.poc.auth.model.usermanagement.UserDetails;
import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.exception.app.AppInvalidArgumentException;
import de.bomc.poc.exception.app.AppInvalidPasswordException;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.test.auth.arq.ArquillianAuthBase;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.performance.annotation.Performance;
import org.jboss.arquillian.performance.annotation.PerformanceTest;
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <pre>
 * Performs integration tests for usermanagement with arquillian dbUnit.
 * mvn clean install -Parq-wildfly-remote -Dtest=UserManagementDbUnitTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@RunWith(Arquillian.class)
@PerformanceTest(resultsThreshold = 10)
public class UserManagementDbUnitTestIT extends ArquillianAuthBase {
    private static final String WEB_ARCHIVE_NAME = "auth-service-user-dbunit";
    private static final String LOGGER_PREFIX = "UserManagementDbUnitTestIT";
    @Inject
    @LoggerQualifier(logPrefix = LOGGER_PREFIX)
    private Logger logger;
    @Inject
    @JpaDao
    JpaUserManagementDao jpaUserManagementDao;
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
        webArchive.addClass(UserManagementDbUnitTestIT.class);
        webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
        webArchive.addClasses(AppInvalidArgumentException.class, AppInvalidPasswordException.class, AppAuthRuntimeException.class);
        webArchive.addClasses(JpaUserManagementDao.class, JpaUserManagementDaoImpl.class);
        webArchive.addClasses(JpaRoleManagementDao.class, JpaRoleManagementDaoImpl.class);
        webArchive.addClasses(JpaGrantManagementDao.class, JpaGrantManagementDaoImpl.class);
        webArchive.addClasses(DatabaseMySqlProducer.class, MySQLDatabase.class, JpaGenericDao.class, AbstractJpaDao.class, JpaDao.class);

        System.out.println("UserManagementDbUnitTestIT#archiveContent: " + webArchive.toString(true));

        return webArchive;
    }

    @Before
    public void setup() throws Exception {
        assertThat(this.jpaUserManagementDao, notNullValue());
        assertThat(this.jpaRoleManagementDao, notNullValue());
        assertThat(this.jpaGrantManagementDao, notNullValue());
    }

    @After
    public void cleanup() {
        //
    }

    /**
     * <pre>
     * NOTE:
     * ______________________________________________________
     * The .yml-Files has to be inserted in a specific order.
     *
     * orderBy: List of columns to be used for sorting rows to determine
     * order of data sets comparison.
     *
     * excludeColumns: List of columns to be excluded. Alternatively can
     * be defined for all tests in <code>arquillian.xml</code>.
     *
     * mvn clean install -Parq-wildfly-remote -Dtest=UserManagementDbUnitTestIT#test01_readUserData_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(1)
    @Performance(time = 3000)
    @UsingDataSet({"datasets/init/T_ROLE.yml",
                   "datasets/init/T_USER.yml",
                   "datasets/init/COR_ROLE_ROLE_JOIN.yml",
                   "datasets/init/COR_ROLE_USER_JOIN.yml",
                   "datasets/init/T_USER_PASSWORD.yml",
                   "datasets/init/T_USER_PASSWORD_JOIN.yml"})
    @CleanupUsingScript(value = "cleanup.sql", phase = TestExecutionPhase.BEFORE)
    @ShouldMatchDataSet(value = { "datasets/expected/T_USER.yml" }, excludeColumns = { "c_id", "c_version" })
    // TODO: check working with transactions, c_version is after test 1 instead of 0, so there is a transaction already opened, or why c_version is 1...
    public void test01_readUserData_Pass() throws Exception {
        this.logger.info("UserManagementDbUnitTestIT#test01_readUserData_Pass");

        final List<Grant> grantList = this.jpaGrantManagementDao.findAll();
        assertThat(grantList, notNullValue());
        assertThat(grantList.size(), is(equalTo(2)));

        final List<Role> roleList = this.jpaRoleManagementDao.findAll();
        assertThat(roleList, notNullValue());
        assertThat(roleList.size(), is(equalTo(1)));

        final List<User> userList = this.jpaUserManagementDao.findAll();
        assertThat(userList, notNullValue());
        assertThat(userList.size(), is(equalTo(1)));

        final User retUser = userList.get(0);
        retUser.getUserDetails().setSex(UserDetails.SEX.FEMALE);
        this.jpaUserManagementDao.persist(retUser);
    }

    /**
     * <pre>
     * NOTE:
     * ______________________________________________________
     * The .yml-Files has to be inserted in a specific order.
     *
     * orderBy: List of columns to be used for sorting rows to determine
     * order of data sets comparison.
     *
     * excludeColumns: List of columns to be excluded. Alternatively can
     * be defined for all tests in <code>arquillian.xml</code>.
     *
     * mvn clean install -Parq-wildfly-remote -Dtest=UserManagementDbUnitTestIT#test10_readUsernameByNamedParameters_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(10)
    @Performance(time = 3000)
    @UsingDataSet({"datasets/init/T_ROLE.yml",
                   "datasets/init/T_USER.yml",
                   "datasets/init/COR_ROLE_ROLE_JOIN.yml",
                   "datasets/init/COR_ROLE_USER_JOIN.yml",
                   "datasets/init/T_USER_PASSWORD.yml",
                   "datasets/init/T_USER_PASSWORD_JOIN.yml"})
    @CleanupUsingScript(value = "cleanup.sql", phase = TestExecutionPhase.BEFORE)
    public void test10_readUsernameByNamedParameters_Pass() throws Exception {
        this.logger.info("UserManagementDbUnitTestIT#test10_readUsernameByNamedParameters_Pass");

        final String username = "myUsername";
        final String password = "My@123Password";
        
        // Parameters from 'datasets/init/T_USER.yml'.
        final Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        final List<User> userList = this.jpaUserManagementDao.findByNamedParameters(User.NQ_FIND_BY_USERNAME_PASSWORD, params);
        
        assertThat(userList.iterator().next().getUsername(), is(equalTo(username)));
    }
    
    /**
     * <pre>
     * NOTE:
     * ______________________________________________________
     * The .yml-Files has to be inserted in a specific order.
     *
     * orderBy: List of columns to be used for sorting rows to determine
     * order of data sets comparison.
     *
     * excludeColumns: List of columns to be excluded. Alternatively can
     * be defined for all tests in <code>arquillian.xml</code>.
     *
     * mvn clean install -Parq-wildfly-remote -Dtest=UserManagementDbUnitTestIT#test20_readAssignedRolesByUsername_NamedParameters_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(20)
    @Performance(time = 3000)
    @UsingDataSet({"datasets/init/T_ROLE.yml",
                   "datasets/init/T_USER.yml",
                   "datasets/init/COR_ROLE_ROLE_JOIN.yml",
                   "datasets/init/COR_ROLE_USER_JOIN.yml",
                   "datasets/init/T_USER_PASSWORD.yml",
                   "datasets/init/T_USER_PASSWORD_JOIN.yml"})
    @CleanupUsingScript(value = "cleanup.sql", phase = TestExecutionPhase.BEFORE)
    public void test20_readAssignedRolesByUsername_NamedParameters_Pass() throws Exception {
        this.logger.info("UserManagementDbUnitTestIT#test20_readAssignedRolesByUsername_NamedParameters_Pass");

        final String username = "myUsername";

        // Parameters from 'datasets/init/T_USER.yml'.
        final Stream<String> streamOfUsernames = Stream.of(username);
        final String[] values = streamOfUsernames.toArray(String[]::new);

        final List<Role> roleList = this.jpaRoleManagementDao.findByPositionalParameters(Role.NQ_FIND_ALL_BY_USERNAME, (Object[])values);
        assertThat(roleList.iterator()
                           .next()
                           .getId(), is(equalTo(3L)));

        this.logger.info("UserManagementDbUnitTestIT#test20_readAssignedRolesByUsername_NamedParameters_Pass ---------- iterate result ----------");
        roleList.forEach(role -> {
            this.logger.info("UserManagementDbUnitTestIT#test20_readAssignedRolesByUsername_NamedParameters_Pass [role=" + role.toString() + "]");

            final Set<Grant> set = role.getGrants();

            // Parameters from 'datasets/init/cor_role_role_join.yml'.
            assertThat(set.size(), is(equalTo(2)));
            set.forEach(System.out::println);
        });
    }
}

