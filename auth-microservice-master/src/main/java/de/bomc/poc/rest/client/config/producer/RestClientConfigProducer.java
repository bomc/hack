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
 * Copyright (c): BOMC, 2015
 */
package de.bomc.poc.rest.client.config.producer;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.rest.client.RestClientBuilder;
import de.bomc.poc.rest.client.config.RestClientConfigKeys;
import de.bomc.poc.rest.client.config.qualifier.RestClientConfigQualifier;

/**
 * A producer for the {@link RestClientBuilder} configuration.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class RestClientConfigProducer {
	private static final String ZK_CONFIG_REST_CLIENT_PATH = "/config/rest/client";

	@Produces
	@SuppressWarnings("incomplete-switch")
	@RestClientConfigQualifier(key = RestClientConfigKeys.PRODUCER)
	public String produceConfigurationValue(InjectionPoint injectionPoint) {
		final Annotated annotated = injectionPoint.getAnnotated();
		final RestClientConfigQualifier annotation = annotated.getAnnotation(RestClientConfigQualifier.class);

		if (annotation != null) {
			final RestClientConfigKeys key = annotation.key();
			if (key != null) {
				switch (key) {
				case ZK_CONFIG_REST_CLIENT_PATH_KEY:
					return ZK_CONFIG_REST_CLIENT_PATH;
				}
			}
		}

		throw new AppAuthRuntimeException(
				"RestClientConfigProducer#produceConfigurationValue - No key for injection point: " + injectionPoint);
	}
}
