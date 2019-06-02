/**
 * Project: MY_POC
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
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.zookeeper.arq;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bomc.poc.concurrency.producer.ManagedConcurrencyProducer;
import de.bomc.poc.concurrency.qualifier.ManagedScheduledExecutorServiceQualifier;
import de.bomc.poc.concurrency.qualifier.ManagedThreadFactoryQualifier;
import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.exception.app.AppInitializationException;
import de.bomc.poc.exception.app.AppZookeeperException;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.test.GlobalArqTestProperties;
import de.bomc.poc.zookeeper.config.ZookeeperConfigAccessor;
import de.bomc.poc.zookeeper.config.impl.ZookeeperConfigAccessorImpl;
import de.bomc.poc.zookeeper.curator.producer.CuratorFrameworkProducer;
import de.bomc.poc.zookeeper.curator.qualifier.CuratorFrameworkQualifier;

/**
 * This test the reading/writing configuration data from/to zookeeper.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@RunWith(Arquillian.class)
public class ZookeeperConfigTestIT extends ArquillianZookeeperBase {
    private static final String LOG_PREFIX = "ZookeeperConfigTestIT#";
    private static final String WEB_ARCHIVE_NAME = "auth-zookeeper-config-war";
    // Could not be injected here, because test client is running outside of the container.
    @Inject
    @LoggerQualifier(logPrefix = LOG_PREFIX)
    private Logger logger;

    @Deployment
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = ArquillianZookeeperBase.createTestArchive(WEB_ARCHIVE_NAME + ".war")
                                                             .addClasses(ZookeeperConfigTestIT.class)
                                                             .addClasses(LoggerProducer.class, LoggerQualifier.class)
                                                             .addClasses(AppAuthRuntimeException.class, AppInitializationException.class, AppZookeeperException.class)
                                                             .addClasses(CuratorFrameworkProducer.class, CuratorFrameworkQualifier.class)
                                                             .addClasses(ManagedConcurrencyProducer.class, ManagedScheduledExecutorServiceQualifier.class, ManagedThreadFactoryQualifier.class)
                                                             .addClasses(ZookeeperConfigAccessorImpl.class, ZookeeperConfigAccessor.class)
                                                             .addAsWebInfResource(getBeansXml(), "beans.xml")
                                                             .addAsResource("zookeeper.properties");

        // Add dependencies
        final MavenResolverSystem resolver = Maven.resolver();

        // shared module library

        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("org.apache.curator:curator-framework:jar:?")
//                                          .withMavenCentralRepo(false)
                                          .withTransitivity()
                                          .asFile());
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("org.apache.curator:curator-recipes:jar:?")
//                                          .withMavenCentralRepo(false)
                                          .withTransitivity()
                                          .asFile());
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("org.apache.curator:curator-x-discovery:jar:?")
//                                          .withMavenCentralRepo(false)
                                          .withTransitivity()
                                          .asFile());

        System.out.println(LOG_PREFIX + "archiveContent: " + webArchive.toString(true));

        return webArchive;
    }

    @Inject
    ZookeeperConfigAccessorImpl zookeeperConfigAccessorImpl;
    
    /**
     * <pre>
     * _________________________________________________________________________________________________
     * NOTE: Zookeeper must be accessible on 'localhost:2181'.
     *       Further this test runs as 'RunAsClient', this means the tests run outside the container.
     *       So the log messages are not part of the server.log.
     *       Log messages of this test are seen on the console output.
     * <p/>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperConfigTestIT#test01_zookeeperConfig_Pass
     * </pre>
     */
    @Test
    @InSequence(1)
	public void test01_zookeeperConfig_Pass() throws Exception {
		logger.debug(LOG_PREFIX + "test01_zookeeperConfig_Pass");

		assertThat(zookeeperConfigAccessorImpl, notNullValue());
		
		Map<Object, Object> dataMap = new HashMap<>();
		dataMap.put("establishConnectionTimeout", 2500);
		dataMap.put("socketTimeout", 2500);

        final String zNode = "/" + WEB_ARCHIVE_NAME + GlobalArqTestProperties.RELATIVE_ROOT_Z_NODE;

		zookeeperConfigAccessorImpl.writeJSON(zNode + "/config", dataMap);

		final Map<Object, Object> readJSONMap = zookeeperConfigAccessorImpl.readJSON(zNode + "/config");
		
		final Set<Map.Entry<Object, Object>> entries = readJSONMap.entrySet();
		entries.stream()
				.forEach(entry -> logger.debug(LOG_PREFIX + "test01_zookeeperConfig_Pass - " + String.valueOf(entry.getKey()) + ", " + String.valueOf(entry.getValue())));
		
		final String establishConnectionTimeout = readJSONMap.get("establishConnectionTimeout").toString();
		final String socketTimeout = readJSONMap.get("socketTimeout").toString();
		
		// The json string response is quoted with '"', this must removed.  
		assertThat(establishConnectionTimeout.replaceAll("^\"|\"$", ""), equalTo("2500"));
		assertThat(socketTimeout.replaceAll("^\"|\"$", ""), equalTo("2500"));
	}

}
