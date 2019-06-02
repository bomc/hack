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
package de.bomc.poc.test.auth.arq.business;

import de.bomc.poc.api.mapper.transfer.GrantDTO;
import de.bomc.poc.api.mapper.transfer.RoleDTO;
import de.bomc.poc.auth.business.UsermanagementLocal;
import de.bomc.poc.auth.business.service.UsermanagementEJB;
import de.bomc.poc.auth.dao.jpa.generic.JpaGenericDao;
import de.bomc.poc.auth.dao.jpa.generic.impl.AbstractJpaDao;
import de.bomc.poc.auth.dao.jpa.producer.DatabaseMySqlProducer;
import de.bomc.poc.auth.dao.jpa.qualifier.JpaDao;
import de.bomc.poc.auth.dao.jpa.qualifier.MySQLDatabase;
import de.bomc.poc.auth.dao.jpa.role.JpaRoleManagementDao;
import de.bomc.poc.auth.dao.jpa.role.impl.JpaRoleManagementDaoImpl;
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
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

/**
 * Tests the {@link de.bomc.poc.auth.business.service.UsermanagementEJB}. The test invokes all layers down to the db.
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=UsermangementServiceTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@PerformanceTest(resultsThreshold = 10)
@RunWith(Arquillian.class)
public class UsermangementServiceTestIT extends ArquillianAuthBase {

    private static final String WEB_ARCHIVE_NAME = "auth-service";
    @Inject
    @LoggerQualifier
    private Logger logger;

    // 'testable = true', means all the tests are running inside the container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchiveWithMysqlDb(WEB_ARCHIVE_NAME);
        webArchive.addClass(UsermangementServiceTestIT.class);
        webArchive.addClasses(LoggerProducer.class, LoggerQualifier.class);
        webArchive.addClasses(UsermanagementEJB.class, UsermanagementLocal.class);
        webArchive.addClasses(AppInvalidArgumentException.class, AppInvalidPasswordException.class, AppAuthRuntimeException.class);
        webArchive.addClasses(JpaRoleManagementDao.class, JpaRoleManagementDaoImpl.class);
        webArchive.addClasses(DatabaseMySqlProducer.class, MySQLDatabase.class, JpaGenericDao.class, AbstractJpaDao.class, JpaDao.class);
        webArchive.addPackages(true, "de.bomc.poc.api"); // TODO: Use package import here.
        webArchive.addPackages(true, "de.bomc.poc.auth.model"); // TODO: Use package import here.

        // Add dependencies
        final MavenResolverSystem resolver = Maven.resolver();

        // Mapstruct lib.
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("org.mapstruct:mapstruct-jdk8:jar:?")
//                                          .withMavenCentralRepo(false)
                                          .withoutTransitivity()
                                          .asFile());

        System.out.println("UsermangementServiceTestIT#createDeployment: " + webArchive.toString(true));

        return webArchive;
    }

    @EJB
    UsermanagementLocal ejb;

    /**
     * Test reading all roles from Db by given username.
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=UsermangementServiceTestIT#test01_readAllRolesFromDbByGivenUsername_Pass
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
	public void test01_readAllRolesFromDbByGivenUsername_Pass() {
		this.logger.debug("UsermangementServiceTestIT#test01_readAllRolesFromDbByGivenUsername_Pass");

		// Read given uername from T_USER.yml file.
		final List<RoleDTO> roleDTOList = ejb.findAllRolesByUsername("myUsername");
		assertThat(roleDTOList.size(), is(equalTo(1)));

		this.logger.debug(
				"UsermangementServiceTestIT#test01_readAllRolesFromDbByGivenUsername_Pass ----- iterate result -----");
		roleDTOList.forEach(roleDTO -> {
			List<GrantDTO> grantDTOList = roleDTO.getGrantDTOList();

			final GrantDTO grantDTO1 = new GrantDTO("myGrant1", "description to myGrant1");
			grantDTO1.setId(1L);
			final GrantDTO grantDTO2 = new GrantDTO("myGrant2", "description to myGrant2");
			grantDTO2.setId(2L);
			final GrantDTO[] grantDTOArray = { grantDTO1, grantDTO2 };

			// Checks that all of the items match the expected items, in any
			// order.
			assertThat(grantDTOList, containsInAnyOrder(grantDTOArray));
			grantDTOList.forEach(System.out::println);
		});
	}
}
