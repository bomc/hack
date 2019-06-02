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
package de.bomc.poc.test;

/**
 * Holds global properties for testing.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public interface GlobalArqTestProperties {

	/**
	 * A systemProperty that is used, to get the host name of a running wildfly
	 * instance, during arquillian tests at runtime..
	 */
	String WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY = "jboss.bind.address";
	/**
	 * A systemProperty that is used, to get the port-offset of a running wildfly
	 * instance, during arquillian tests at runtime..
	 */
	String WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY = "jboss.socket.binding.port-offset";
	/**
	 * The default http wildfly port.
	 */
	int WILDFLY_DEFAULT_HTTP_PORT = 8080;

	/**
	 * Connection string to connect to zookeeper instance.
	 */
	String CONNECTION_STRING = "127.0.0.1:2181";
	/**
	 * Configuration parameter for initializing zookeeper.
	 */
	int CONNECTION_TIMEOUT_MS = 10000;
	/**
	 * Configuration parameter for initializing zookeeper.
	 */
	int SESSION_TIMEOUT_MS = 20000;
	/**
	 * The root zNode for zookeeper.
	 */
	String WEB_ARCHIVE_NAME_AUTH_MICROSERVICE = "auth-microservice";
	/**
	 * The relative root zNode for zookeeper.
	 */
	String RELATIVE_ROOT_Z_NODE = "/local/node0";
}
