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
package de.bomc.poc.test.cdi.rest.arq;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Field;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bomc.poc.concurrency.producer.ManagedConcurrencyProducer;
import de.bomc.poc.concurrency.qualifier.ManagedScheduledExecutorServiceQualifier;
import de.bomc.poc.concurrency.qualifier.ManagedThreadFactoryQualifier;
import de.bomc.poc.config.qualifier.EnvConfigQualifier;
import de.bomc.poc.exception.app.AppZookeeperException;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.rest.client.RestClientBuilder;
import de.bomc.poc.rest.client.config.producer.RestClientConfigProducer;
import de.bomc.poc.test.GlobalArqTestProperties;
import de.bomc.poc.zookeeper.config.ZookeeperConfigAccessor;
import de.bomc.poc.zookeeper.config.impl.ZookeeperConfigAccessorImpl;
import de.bomc.poc.zookeeper.curator.producer.CuratorFrameworkProducer;
import de.bomc.poc.zookeeper.curator.qualifier.CuratorFrameworkQualifier;

/**
 * <pre>
 *  mvn clean install -Parq-cdi-embedded
 *  
 *  Performs cdi tests for the {@link RestClientConfigProducer}.
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@RunWith(Arquillian.class)
public class RestClientTestCdiIT {
	private static final String LOG_PREFIX = "test#cdi#rest#";
	protected static final String WEB_ARCHIVE_NAME = "poc-cdi-rest-test";
	@Inject
	@LoggerQualifier
	Logger logger;
	@Inject
	RestClientBuilder restClientBuilder;

	@Deployment
	public static WebArchive createDeployment() {
		final WebArchive webArchive = ShrinkWrap.create(WebArchive.class,
				GlobalArqTestProperties.WEB_ARCHIVE_NAME_AUTH_MICROSERVICE + ".war");
		webArchive.addClass(RestClientTestCdiIT.class);
		webArchive.addClasses(LoggerProducer.class, LoggerQualifier.class);
		webArchive.addPackages(true, RestClientBuilder.class.getPackage().getName());
		webArchive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		// Dependencies for zookeeper config reading.
		webArchive.addClasses(ZookeeperConfigAccessorImpl.class, ZookeeperConfigAccessor.class, AppZookeeperException.class,
				CuratorFrameworkQualifier.class, CuratorFrameworkProducer.class, ManagedThreadFactoryQualifier.class,
				ManagedConcurrencyProducer.class, ManagedScheduledExecutorServiceQualifier.class);
		webArchive.addClasses(EnvConfigQualifier.class, EnvConfigRestClientMockProducer.class, GlobalArqTestProperties.class);

		// Add dependencies
		final MavenResolverSystem resolver = Maven.resolver();

		// libs.
		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
				.resolve("org.apache.httpcomponents:httpclient:jar:?")
//				.withMavenCentralRepo(false)
				.withTransitivity()
				.asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
				.resolve("org.apache.curator:curator-framework:jar:?")
//				.withMavenCentralRepo(false)
				.withTransitivity()
				.asFile());

		System.out.println("archiveContent: " + webArchive.toString(true));

		return webArchive;
	}

	@Before
	public void init() {
		assertThat(this.restClientBuilder, notNullValue());
	}

	/**
	 * <pre>
	 * ________________________________________________________
	 * NOTE: 
	 * Test values depends on persistent values from zookeeper.
	 * ____________________________________________________________________
	 * TODO:
	 * Extent test case to write first data to zookeeper and then read data
	 * to ensure data integrity.
	 * back.
	 * 
	 * <pre>
	 *	mvn clean install -Parq-cdi-embedded -Dtest=RestClientTestCdiIT#test01_restClientCheckInjectedConfig_Pass
	 * </pre>
	 */
	@Test
	@Ignore("This test is not working in cdi env, because the CuratorFrameworkProducer is injecting a ManagedThreadFactor, which is a resouce from wildlfy.")
	public void test01_restClientCheckInjectedConfig_Pass() throws Exception {
		this.logger.debug(LOG_PREFIX + "LoggerTestIT#test01_restClientCheckInjectedConfig_Pass");

		Field defaultConnectionTimeoutField = RestClientBuilder.class.getDeclaredField("defaultConnectionTimeout");
		defaultConnectionTimeoutField.setAccessible(true);
		String defaultConnectionTimeoutValue = (String) defaultConnectionTimeoutField.get(restClientBuilder);

		// Value will be defined in 'RestClientConfigProducer'.
		assertThat(defaultConnectionTimeoutValue, is(equalTo("15000")));

		Field defaultSocketTimeoutField = RestClientBuilder.class.getDeclaredField("defaultSocketTimeout");
		defaultSocketTimeoutField.setAccessible(true);
		String defaultSocketTimeoutFieldValue = (String) defaultSocketTimeoutField.get(restClientBuilder);

		// Value will be defined in 'RestClientConfigProducer'.
		assertThat(defaultSocketTimeoutFieldValue, is(equalTo("11000")));
	}
}
