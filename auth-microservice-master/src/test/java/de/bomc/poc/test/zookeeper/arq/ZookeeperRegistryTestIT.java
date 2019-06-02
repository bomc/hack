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

import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.concurrency.producer.ManagedConcurrencyProducer;
import de.bomc.poc.concurrency.qualifier.ManagedScheduledExecutorServiceQualifier;
import de.bomc.poc.concurrency.qualifier.ManagedThreadFactoryQualifier;
import de.bomc.poc.config.EnvConfigKeys;
import de.bomc.poc.config.EnvConfigSingletonEJB;
import de.bomc.poc.config.producer.EnvConfigProducer;
import de.bomc.poc.config.qualifier.EnvConfigQualifier;
import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.exception.app.AppInitializationException;
import de.bomc.poc.exception.app.AppZookeeperException;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.rest.logger.ResteasyClientLogger;
import de.bomc.poc.zookeeper.ZookeeperStarterSingletonEJB;
import de.bomc.poc.zookeeper.curator.producer.CuratorFrameworkProducer;
import de.bomc.poc.zookeeper.curator.qualifier.CuratorFrameworkQualifier;
import de.bomc.poc.zookeeper.registry.services.ZookeeperServiceRegistry;
import de.bomc.poc.zookeeper.registry.services.qualifier.ZookeeperServicesQualifier;
import de.bomc.poc.zookeeper.InstanceMetaData;
import de.bomc.poc.zookeeper.registry.ServiceRegistry;
import org.apache.curator.x.discovery.ServiceInstance;
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

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This test the startup of the {@link ZookeeperStarterSingletonEJB} and the registration, respectively the unregistration.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
@RunWith(Arquillian.class)
public class ZookeeperRegistryTestIT extends ArquillianZookeeperBase {
	private static final String LOG_PREFIX = "test#zookeeper#registry";
	private static final String WEB_ARCHIVE_NAME = "auth-zookeeper-registry-war";

	@Inject
	@LoggerQualifier(logPrefix = LOG_PREFIX)
	private Logger logger;

	@Context
	private UriInfo uriInfo;

	@Deployment
	public static Archive<?> createDeployment() {
        final WebArchive webArchive = ArquillianZookeeperBase.createTestArchive(WEB_ARCHIVE_NAME + ".war")
				.addClasses(ZookeeperRegistryTestIT.class)
				.addClasses(LoggerProducer.class, LoggerQualifier.class)
				.addClasses(AppAuthRuntimeException.class, AppInitializationException.class, AppZookeeperException.class)
				.addClasses(EnvConfigKeys.class, EnvConfigQualifier.class, EnvConfigSingletonEJB.class, EnvConfigProducer.class)
				.addClasses(ZookeeperStarterSingletonEJB.class, ZookeeperServicesQualifier.class, ServiceRegistry.class, ZookeeperServiceRegistry.class, CuratorFrameworkProducer.class, CuratorFrameworkQualifier.class,
					InstanceMetaData.class)
				.addClasses(ManagedConcurrencyProducer.class, ManagedScheduledExecutorServiceQualifier.class, ManagedThreadFactoryQualifier.class)
				.addClasses(JaxRsActivator.class)
				.addClass(ResteasyClientLogger.class)
				.addAsWebInfResource(getBeansXml(), "beans.xml")
				.addAsResource("zookeeper.properties");

		// Add dependencies
		final MavenResolverSystem resolver = Maven.resolver();

		// shared module library

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
				.resolve("org.apache.curator:curator-framework:jar:?")
//				.withMavenCentralRepo(false)
				.withTransitivity()
				.asFile());
		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
				.resolve("org.apache.curator:curator-recipes:jar:?")
//				.withMavenCentralRepo(false)
				.withTransitivity()
				.asFile());
		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
				.resolve("org.apache.curator:curator-x-discovery:jar:?")
//				.withMavenCentralRepo(false)
				.withTransitivity()
				.asFile());

		System.out.println("archiveContent: " + webArchive.toString(true));

		return webArchive;
	}

	@EJB
	ZookeeperStarterSingletonEJB ejb;

	/**
	 * NOTE: Zookeeper must be accessible on 'localhost:2181'.
	 * 
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=ZookeeperRegistryTestIT#test01_zookeeperRegistryStartup_Pass
	 * </pre>
	 */
	@Test
	@InSequence(1)
	public void test01_zookeeperRegistryStartup_Pass() {
		this.logger.debug(LOG_PREFIX + "test01_zookeeperRegistryStartup_Pass");

		assertThat(this.ejb.getServiceRegistry().isConnected(), is(equalTo(true)));

		final Collection<ServiceInstance<InstanceMetaData>> c = this.ejb.getServiceRegistry().getService(JaxRsActivator.APPLICATION_PATH);

		final ServiceInstance<InstanceMetaData> serviceInstance = c.iterator().next();

		this.logger.debug(LOG_PREFIX + "test01_zookeeperRegistryStartup_Pass [serviceInstance=" + serviceInstance.toString() + "]");

		assertThat(serviceInstance, notNullValue());
		assertThat(JaxRsActivator.APPLICATION_PATH, is(equalTo(serviceInstance.getName())));
		
		// cleanup
		this.ejb.getServiceRegistry().unregisterService();
	}
}
