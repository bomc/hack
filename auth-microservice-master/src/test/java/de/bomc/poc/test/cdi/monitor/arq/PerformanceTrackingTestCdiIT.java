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
package de.bomc.poc.test.cdi.monitor.arq;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.management.JMException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.exception.app.AppMonitorException;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.monitor.controller.MBeanController;
import de.bomc.poc.monitor.interceptor.PerformanceTrackingInterceptor;
import de.bomc.poc.monitor.jmx.AuthMBean;
import de.bomc.poc.monitor.jmx.AuthRootServer;
import de.bomc.poc.monitor.jmx.MBeanInfo;
import de.bomc.poc.monitor.jmx.performance.PerformanceEntry;
import de.bomc.poc.monitor.jmx.performance.PerformanceTracking;
import de.bomc.poc.monitor.jmx.performance.PerformanceTrackingMXBean;
import de.bomc.poc.monitor.qualifier.PerformanceTrackingQualifier;

/**
 * <pre>
 *	mvn clean install -Parq-cdi-embedded -Dtest=PerformanceTrackingTestCdiIT
 *
 *  Performs cdi tests for the {@link de.bomc.poc.monitor.interceptor.PerformanceTrackingInterceptor}.
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@RunWith(Arquillian.class)
public class PerformanceTrackingTestCdiIT {
	private static final String LOG_PREFIX = "PerformanceTrackingTestCdiIT#";
	private static final String WEB_ARCHIVE_NAME = "poc-cdi-performanece-tracking-test";
	private PerformanceTracking performanceTracking;
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Deployment
	public static WebArchive createDeployment() {
		final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, WEB_ARCHIVE_NAME + ".war");
		webArchive.addClasses(LoggerProducer.class, LoggerQualifier.class);
		webArchive.addAsWebInfResource(getBeansXml(), "beans.xml");
		webArchive.addClasses(PerformanceTracking.class, PerformanceTrackingMXBean.class, PerformanceEntry.class,
				PerformanceTrackingQualifier.class, PerformanceTrackingInterceptor.class, AppMonitorException.class,
				AppAuthRuntimeException.class, MBeanController.class, MBeanInfo.class, AuthMBean.class,
				SutMethodCdiBeanMock.class, SutTypeCdiBeanMock.class);

		System.out.println("archiveContent: " + webArchive.toString(true));

		return webArchive;
	}

	private static StringAsset getBeansXml() {
		return new StringAsset(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\" "
						+ "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
						+ "     xsi:schemaLocation=\"\n" + "        http://xmlns.jcp.org/xml/ns/javaee\n"
						+ "        http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\" bean-discovery-mode=\"all\">\n"
						+ "    <!-- 'annotated' - loosely translated, means that only components with a class-level annotation are processed.\n"
						+ "         'all'       - all components are processed, just like they were in Java EE 6 with the explicit beans.xml.\n"
						+ "         'none'      - CDI is effectively disabled. -->\n" 
						+ "	<interceptors>\n"
						+ "		<class>de.bomc.poc.monitor.interceptor.PerformanceTrackingInterceptor</class>"
						+ "	</interceptors>\n" 
						+ "</beans>");
	}

	@Inject
	private SutMethodCdiBeanMock sutMethodCdiBeanMock;
	@Inject
	private SutTypeCdiBeanMock sutTypeCdiBeanMock;
	@Inject
	private MBeanController mBeanController;

	@Before
	public void init() throws JMException {
		this.logger.debug(LOG_PREFIX + "init");

		assertThat(this.logger, notNullValue());
		assertThat(this.mBeanController, notNullValue());

		final MBeanInfo authServerBean = new AuthRootServer();
		this.performanceTracking = new PerformanceTracking();

		// Register first parent...
		this.mBeanController.register(authServerBean, null);

		// ...now register leaf with reference to the parent.
		this.mBeanController.register((MBeanInfo) this.performanceTracking, authServerBean);
	}

	@After
	public void cleanup() throws JMException {
		this.logger.debug(LOG_PREFIX + "cleanup");
		
		assertThat(this.mBeanController, notNullValue());

		this.mBeanController.unregisterAll();
	}
	
	/**
	 * <pre>
	 * 	mvn clean install -Parq-cdi-embedded -Dtest=PerformanceTrackingTestCdiIT#test01_methodPerformanceIntercepting_Pass
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	@InSequence(1)
	public void test01_methodPerformanceIntercepting_Pass() throws Exception {
		this.logger.debug(LOG_PREFIX + "test01_methodPerformanceIntercepting_Pass");

		for (int i = 0; i < 10; i++) {
			this.sutMethodCdiBeanMock.methodUnderTest1();
			this.sutMethodCdiBeanMock.methodUnderTest2();
		}

		// Wait for a while
		TimeUnit.SECONDS.sleep(1);

//		final Map<MBeanInfo, String> path2BeanMap = this.mBeanController.getPath2Bean();
		final String beanPath = this.mBeanController.getBeanPathByMBean(this.performanceTracking);
		final ObjectName objectName = this.mBeanController.makeObjectName(beanPath, this.performanceTracking);

		assertThat(this.mBeanController.getAttribute(objectName, "Count"), is(equalTo(2)));
		
		TimeUnit.SECONDS.sleep(1);
	}

	/**
	 * <pre>
	 * 	mvn clean install -Parq-cdi-embedded -Dtest=PerformanceTrackingTestCdiIT#test02_typePerformanceIntercepting_Pass
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	@InSequence(2)
	public void test02_typePerformanceIntercepting_Pass() throws Exception {
		this.logger.debug(LOG_PREFIX + "test02_typePerformanceIntercepting_Pass");

		for (int i = 0; i < 10; i++) {
			this.sutTypeCdiBeanMock.methodUnderTest1();
			this.sutTypeCdiBeanMock.methodUnderTest2();
		}

		// Wait for a while
		TimeUnit.SECONDS.sleep(1);

		assertThat(this.performanceTracking, notNullValue());
		
//		final Map<MBeanInfo, String> path2BeanMap = this.mBeanController.getPath2Bean();
		final String beanPath = this.mBeanController.getBeanPathByMBean(this.performanceTracking);
		final ObjectName objectName = this.mBeanController.makeObjectName(beanPath, this.performanceTracking);

		assertThat(objectName, notNullValue());
		assertThat(this.mBeanController.getAttribute(objectName, "Count"), is(equalTo(2)));
	}
	
	/**
	 * <pre>
	 * 	mvn clean install -Parq-cdi-embedded -Dtest=PerformanceTrackingTestCdiIT#test10_methodPerformanceIntercepting_Fail
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	@InSequence(10)
	public void test10_methodPerformanceIntercepting_Fail() throws Exception {
		this.logger.debug(LOG_PREFIX + "test10_methodPerformanceInterceping_Pass");

		this.thrown.expect(AppMonitorException.class);

		this.sutMethodCdiBeanMock.methodUnderTest3();
	}
}
