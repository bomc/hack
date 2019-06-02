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
package de.bomc.poc.test.zookeeper.arq;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;

import de.bomc.poc.exception.app.AppZookeeperException;

/**
 * A base class for arquillian tests with zookeeper.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
public class ArquillianZookeeperBase {
	static WebArchive createTestArchive(final String webArchiveName) {
		final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, webArchiveName)
				.addClass(ArquillianZookeeperBase.class).addAsWebInfResource(getBeansXml(), "beans.xml");
		// Add dependencies
		MavenResolverSystem resolver = Maven.resolver();
		// API
		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
				.resolve("org.apache.curator:curator-framework:jar:?")
//				.withMavenCentralRepo(false)
				.withTransitivity()
				.asFile());
		return webArchive;
	}

	/**
	 * Create a new instance of <code>CuratorFramework</code>.
	 * 
	 * @param connectString
	 * 
	 * <pre>
	 *	Running ZooKeeper in standalone mode is convenient for
	 *	development, and testing. In production ZooKeeper should run
	 *	in replicated mode. A replicated group of servers in the same
	 *	application is called a quorum, and in replicated mode, all
	 *	servers in the quorum have copies of the same configuration
	 *	file.
	 *	server.<positive id> = <address1>:<port1>:<port2>[:role];[<curator port address>:]<curator port>
	 *
	 *	Examples of legal server statements:
	 *	server.5 = 125.23.63.23:1234:1235;1236
	 *	server.5 = 125.23.63.23:1234:1235:participant;1236
	 *	server.5 = 125.23.63.23:1234:1235:observer;1236
	 *	server.5 = 125.23.63.23:1234:1235;125.23.63.24:1236
	 *	server.5 = 125.23.63.23:1234:1235:participant;125.23.63.23:1236
	 * </pre>
	 * 
	 * @param connectionTimeoutMs
	 *            connection timeout
	 * @param sessionTimeoutMs
	 *            session timeout
	 * @param rootZnode
	 *            Every node in a ZooKeeper tree is referred to as a znode.
	 *            Znodes maintain a stat structure that includes version numbers
	 *            for data changes.
	 * @return a initialized and connected zookepper curator.
	 * @throws AppZookeeperException
	 *             if zookeeper curator initialization failed.
	 */
	CuratorFramework createClient(final String connectString, final int connectionTimeoutMs, final int sessionTimeoutMs, final String rootZnode) throws Exception {

		final CuratorFramework client = CuratorFrameworkFactory.builder().connectString(connectString)
				.retryPolicy(new ExponentialBackoffRetry(1000, 1)).connectionTimeoutMs(connectionTimeoutMs)
				.sessionTimeoutMs(sessionTimeoutMs).maxCloseWaitMs(3000).build();

		// Start the curator. Most mutator methods will not work until the
		// curator is started.
		client.start();
		// Make sure you're connected to zookeeper.
		client.getZookeeperClient().blockUntilConnectedOrTimedOut();

		return client;
	}

	static StringAsset getBeansXml() {

		return new StringAsset(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"\n"
						+ "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
						+ "     xsi:schemaLocation=\"\n" + "        http://xmlns.jcp.org/xml/ns/javaee\n"
						+ "        http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\" bean-discovery-mode=\"all\">\n"
						+ "    <!-- 'annotated' - loosely translated, means that only components with a class-level annotation are processed.\n"
						+ "         'all'       - all components are processed, just like they were in Java EE 6 with the explicit beans.xml.\n"
						+ "         'none'      - CDI is effectively disabled. -->\n" + "\n" 
						+ "</beans>");
	}

	static StringAsset getBeansXmlWithLockInterceptor() {

		return new StringAsset(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"\n"
						+ "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
						+ "     xsi:schemaLocation=\"\n" + "        http://xmlns.jcp.org/xml/ns/javaee\n"
						+ "        http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\" bean-discovery-mode=\"all\">\n"
						+ "    <!-- 'annotated' - loosely translated, means that only components with a class-level annotation are processed.\n"
						+ "         'all'       - all components are processed, just like they were in Java EE 6 with the explicit beans.xml.\n"
						+ "         'none'      - CDI is effectively disabled. -->\n" + "\n" 
						+ "	<interceptors>\n"
						+ "		<class>de.bomc.poc.zookeeper.discovery.interceptor.LockInterceptor</class>\n"
						+ "	</interceptors>\n" 
						+ "</beans>");
	}
}
