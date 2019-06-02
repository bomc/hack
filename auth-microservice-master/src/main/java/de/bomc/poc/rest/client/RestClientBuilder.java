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
package de.bomc.poc.rest.client;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import de.bomc.poc.config.EnvConfigKeys;
import de.bomc.poc.config.qualifier.EnvConfigQualifier;
import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.rest.client.config.RestClientConfigKeys;
import de.bomc.poc.rest.client.config.qualifier.RestClientConfigQualifier;
import de.bomc.poc.rest.filter.authorization.AuthorizationTokenHeaderRequestFilter;
import de.bomc.poc.rest.filter.uid.MDCFilter;
import de.bomc.poc.rest.filter.uid.UIDHeaderRequestFilter;
import de.bomc.poc.rest.logger.ResteasyClientLogger;
import de.bomc.poc.zookeeper.config.ZookeeperConfigAccessor;

/**
 * A class that generates a resteasy client with resteasy proxy framework.
 *
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class RestClientBuilder {
	private static final String LOG_PREFIX = "RestClientBuilder#";
	// Config parameter definitions.
	public static final String CONNECTION_TIMEOUT_KEY = "connectionTimeout";
	public static final String SOCKET_TIMEOUT_KEY = "socketTimeout";
	private static final String DEFAULT_CONNECTION_TIMEOUT_MS = "10000";
	private static final String DEFAULT_SOCKET_TIMEOUT_MS = "2000";
	private String defaultConnectionTimeout = DEFAULT_CONNECTION_TIMEOUT_MS;
	private String defaultSocketTimeout = DEFAULT_SOCKET_TIMEOUT_MS;
	/**
	 * Logger.
	 */
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	private ZookeeperConfigAccessor zookeeperConfigAccessor;
	// The base path for this service, is built at runtime by the
	// EnvConfigSingletonEJB.
	@NotNull
	@Inject
	@EnvConfigQualifier(key = EnvConfigKeys.ZNODE_BASE_PATH)
	private String zNodeBasePath;
	// Under this path the configuration available for this rest client. This
	// config parameter is static.
	@NotNull
	@Inject
	@RestClientConfigQualifier(key = RestClientConfigKeys.ZK_CONFIG_REST_CLIENT_PATH_KEY)
	private String zkConfigRestClientPath;

	public RestClientBuilder() {
		//
		// Used by cdi provider.
	}

	/**
	 * Read the available configuration from zookeeper. This method is every
	 * time invoked when a restClient is produced.
	 */
	@PostConstruct
	private void init() {
		this.logger.debug(LOG_PREFIX + "init - [zookeeperConfigAccessorImpl=" + this.zookeeperConfigAccessor + "]");

		if (this.zookeeperConfigAccessor != null && this.zookeeperConfigAccessor.isConnected()) {
			try {
				final Map<Object, Object> readJSONMap = this.zookeeperConfigAccessor
						.readJSON(zNodeBasePath + zkConfigRestClientPath);

				if (!readJSONMap.isEmpty()) {
					this.defaultConnectionTimeout = this.checkValueFromZK(readJSONMap.get(CONNECTION_TIMEOUT_KEY));
					this.defaultSocketTimeout = this.checkValueFromZK(readJSONMap.get(SOCKET_TIMEOUT_KEY));
				}
			} catch (Exception ex) {
				final String errorMessage = LOG_PREFIX
						+ "init - Reading config from zookeeper failed, starting with default parameter [connectionTimout="
						+ this.defaultConnectionTimeout + ", sessionTimeout=" + this.defaultSocketTimeout + "]";
				// Don't print the log message again, the log message is already print out in the underlying method.
				this.logger.warn(errorMessage);
			}
		} else {
			this.logger.warn(LOG_PREFIX + "init - not connected with zookeeper, start with default parameter.");
		}
	}

	/**
	 * Creates a resteasy client.
	 * 
	 * @param authorizatonToken
	 *            the authorizaton token given by the eGov plattform.
	 * @param baseUri
	 *            the base uri of the request.
	 * @return a configured resteasy client.
	 */
	public ResteasyWebTarget buildResteasyClient(final String authorizatonToken, final URI baseUri) {
		this.logger.debug("RestClientBuilder#buildResteasyClient [authorizatonToken=" + authorizatonToken + ", baseUri="
				+ baseUri + ", MDC.requestId=" + MDC.get(MDCFilter.HEADER_REQUEST_ID_ATTR) + "]");

		try {
			final ResteasyClient client = new ResteasyClientBuilder()
					.register(new UIDHeaderRequestFilter(MDC.get(MDCFilter.HEADER_REQUEST_ID_ATTR).toString()))
					// TODO JacksonJaxbJsonProvider, JacksonJsonProvider
					// necessary?
					.register(JacksonJaxbJsonProvider.class).register(JacksonJsonProvider.class)
					.register(new ResteasyClientLogger(this.logger, true))
					.establishConnectionTimeout(Long.parseLong(this.defaultConnectionTimeout), TimeUnit.SECONDS)
					.socketTimeout(Long.parseLong(this.defaultSocketTimeout), TimeUnit.SECONDS).build();

			if (authorizatonToken != null) {
				client.register(new AuthorizationTokenHeaderRequestFilter(authorizatonToken));
			}

			return client.target(baseUri);
		} catch (Exception ex) {
			this.logger.error("RestClientBuilder#buildResteasyClient - failed! ", ex);

			throw new AppAuthRuntimeException("RestClientBuilder#buildResteasyClient - failed! " + ex.getMessage());
		}
	}

	/**
	 * Check if value is not null or empty and is a numeric value. Further
	 * JSONObject returns string in form ""value"". First and last '"' must be
	 * deleted.
	 * 
	 * @param inputData
	 * @return a extracted value.
	 */
	private String checkValueFromZK(final Object objectValue) {
		//
		// Sanity null check and object value is as string not empty.
		if (objectValue != null && !objectValue.toString().isEmpty()) {
			//
			// Remove first and last character '"' from string value.
			final String stringValue = objectValue.toString().substring(1, objectValue.toString().length() - 1);
			//
			// Check if value is numeric.
			if (stringValue.matches("[-+]?\\d+(\\.\\d+)?")) {
				// stringValue meets all constraints.
				return stringValue;
			} else {
				throw new AppAuthRuntimeException("Given value is not numeric. [stringValue=" + stringValue + "]");
			}
		} else {
			throw new AppAuthRuntimeException("Given value is null or empty. [objectValue=" + objectValue + "]");
		}
	}
}
