/**
 * Project: Poc-Upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-26 15:24:52 +0100 (Do, 26 Jan 2017) $
 *
 *  revision: $Revision: 10039 $
 *
 * </pre>
 */
package de.bomc.poc.upload.configuration;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.Properties;

/**
 * This EJB set configuration parameter at startup.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 21.12.2016
 */
@Startup
@Singleton
public class ConfigSingletonEJB {

	private static final String LOG_PREFIX = "ConfigSingletonEJB#";
	// Constants which defines default values for configuration properties.
	public static final int DEFAULT_CONNECTION_REQUEST_TTL = 5000;
	public static final int DEFAUL_CONNECT_TIMEOUT = 3000;
	public static final int DEFAULT_SO_TIMEOUT = 20000;
	public static final String DEFAULT_LAGACY_SERVICE_HOST = "172.17.0.2";
	public static final int DEFAULT_LAGACY_SERVICE_PORT = 8080;
	public static final String DEFAULT_LAGACY_SERVICE_BASE_PATH = "/bomc-upload-lagacy/rest";
	public static final String DEFAULT_LAGACY_SERVICE_SCHEME = "http";
	public static final String DEFAULT_LAGACY_SERVICE_USERNAME = "bomc-username";
	public static final String DEFAULT_LAGACY_SERVICE_PASSWORD = "bomc-password";
	public static final int DEFAULT_PING_INTERVAL = 1;
	// Injectable configuration properties.
	private int connectionRequestTimeout = DEFAULT_CONNECTION_REQUEST_TTL;
	private int connectTimeout = DEFAUL_CONNECT_TIMEOUT;
	private int soTimeout = DEFAULT_SO_TIMEOUT;
	private String validationServiceHost = DEFAULT_LAGACY_SERVICE_HOST;
	private int validationServicePort = DEFAULT_LAGACY_SERVICE_PORT;
	private String validationBasePath = DEFAULT_LAGACY_SERVICE_BASE_PATH;
	private String validationServiceScheme = DEFAULT_LAGACY_SERVICE_SCHEME;
	private String validationServiceUsername = DEFAULT_LAGACY_SERVICE_USERNAME;
	private String validationServicePassword = DEFAULT_LAGACY_SERVICE_PASSWORD;
	private int pingInterval = DEFAULT_PING_INTERVAL;
	// Logger.
	@Inject
	@LoggerQualifier
	private Logger logger;
	// Holds the configuration properties
	private Properties properties;
	private String nodeName = "";
	private String serviceName = "";

	@PostConstruct
	public void init() {
		this.logger.debug(LOG_PREFIX + "init");

		// TODO read this later from zookeeper.

		try {
			final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			final ObjectName environmentMBean = new ObjectName("jboss.as:core-service=server-environment");
			this.nodeName = (String) mBeanServer.getAttribute(environmentMBean, "nodeName");
			// Extract service name from node name.
			final String[] nodeNameParts = this.nodeName.split("\\.");
			this.serviceName = nodeNameParts[0];

			final InputStream inputStream = this.getClass().getClassLoader()
					.getResourceAsStream("configuration.properties");

			this.properties = new Properties();
			// Loading the properties from file.
			this.properties.load(inputStream);

			final String serviceNodeName = this.serviceName + ".";
			final String nodeNameDot = this.nodeName + ".";

			// Independent node.name properties.
			this.setConnectTimeout(Integer.parseInt(
					this.properties.getProperty(serviceNodeName + ConfigKeys.CONNECT_TIMEOUT.getPropertyValue())));
			this.setSoTimeout(Integer.parseInt(
					this.properties.getProperty(serviceNodeName + ConfigKeys.SO_TIMEOUT.getPropertyValue())));
			this.setConnectionTTL(Integer.parseInt(
					this.properties.getProperty(serviceNodeName + ConfigKeys.CONNECTION_TTL.getPropertyValue())));
			// Properties that depends on the node.name.
			this.setLagacyBasePath(this.properties
					.getProperty(serviceNodeName + ConfigKeys.LAGACY_SERVICE_BASE_PATH.getPropertyValue()));
			this.setLagacyServiceHost(
					this.properties.getProperty(nodeNameDot + ConfigKeys.LAGACY_SERVICE_HOST.getPropertyValue()));
			this.setLagacyServicePort(Integer.parseInt(
					this.properties.getProperty(nodeNameDot + ConfigKeys.LAGACY_SERVICE_PORT.getPropertyValue())));
			this.setLagacyServiceScheme(
					this.properties.getProperty(serviceNodeName + ConfigKeys.LAGACY_SERVICE_SCHEME.getPropertyValue()));
			// Independent node.name properties.
			this.setLagacyServiceUsername(this.properties
					.getProperty(serviceNodeName + ConfigKeys.LAGACY_SERVICE_USERNAME.getPropertyValue()));
			this.setLagacyServicePassword(this.properties
					.getProperty(serviceNodeName + ConfigKeys.LAGACY_SERVICE_PASSWORD.getPropertyValue()));

			this.setPingInterval(Integer.parseInt(
					this.properties.getProperty(serviceNodeName + ConfigKeys.PING_INTERVAL.getPropertyValue())));
		} catch (final Exception ex) {
			this.logger.warn(
					LOG_PREFIX
							+ "init - reading properties from file failed! Using default properties! Check if 'configuration.properties'-file is in deployment-artefact or if node.name is set, expect ''!",
					ex);
		}

		this.logger.info(LOG_PREFIX + "init - Starting rest client with following rest client parameters - "
				+ this.configParameterToString());
	}

	public int getConnectionRequestTimeout() {
		return this.connectionRequestTimeout;
	}

	public void setConnectionTTL(final int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
	}

	public int getConnectTimeout() {
		return this.connectTimeout;
	}

	public void setConnectTimeout(final int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getSoTimeout() {
		return this.soTimeout;
	}

	public void setSoTimeout(final int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public String getLagacyServiceHost() {
		return this.validationServiceHost;
	}

	public void setLagacyServiceHost(final String validationServiceHost) {
		this.validationServiceHost = validationServiceHost;
	}

	public int getLagacyServicePort() {
		return this.validationServicePort;
	}

	public void setLagacyServicePort(final int validationServicePort) {
		this.validationServicePort = validationServicePort;
	}

	public String getLagacyBasePath() {
		return this.validationBasePath;
	}

	public void setLagacyBasePath(final String validationBasePath) {
		this.validationBasePath = validationBasePath;
	}

	public String getLagacyServiceScheme() {
		return this.validationServiceScheme;
	}

	public void setLagacyServiceScheme(final String validationServiceScheme) {
		this.validationServiceScheme = validationServiceScheme;
	}

	public String getLagacyServiceUsername() {
		return this.validationServiceUsername;
	}

	public void setLagacyServiceUsername(final String validationServiceUsername) {
		this.validationServiceUsername = validationServiceUsername;
	}

	public String getLagacyServicePassword() {
		return this.validationServicePassword;
	}

	public void setLagacyServicePassword(final String validationServicePassword) {
		this.validationServicePassword = validationServicePassword;
	}

	public int getPingInterval() {
		return this.pingInterval;
	}

	public void setPingInterval(final int pingInterval) {
		this.pingInterval = pingInterval;
	}

	public String configParameterToString() {
		return "ConfigSingletonEJB [node.name=" + this.nodeName + "]" 
				+ "[connectionRequestTimeout=" + this.connectionRequestTimeout 
				+ ", connectTimeout=" + this.connectTimeout 
				+ ", soTimeout=" + this.soTimeout 
				+ ", validationServiceHost=" + this.validationServiceHost 
				+ ", validationServicePort=" + this.validationServicePort 
				+ ", validationBasePath=" + this.validationBasePath
				+ ", validationServiceScheme=" + this.validationServiceScheme 
				+ ", pingInterval=" + this.pingInterval
				+ ", validationServiceUsername=" + this.validationServiceUsername 
				+ ", validationServicePassword=" + StringUtils.substring(this.validationServicePassword, 0, 2) + "***" + "]";
	}
}
