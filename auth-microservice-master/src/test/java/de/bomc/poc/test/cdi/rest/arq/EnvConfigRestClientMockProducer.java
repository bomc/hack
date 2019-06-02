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
package de.bomc.poc.test.cdi.rest.arq;

import de.bomc.poc.config.EnvConfigKeys;
import de.bomc.poc.config.qualifier.EnvConfigQualifier;
import de.bomc.poc.test.GlobalArqTestProperties;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * A mock producer for injecting the zookeeper connection parameter.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
public class EnvConfigRestClientMockProducer {

	/**
	 * <pre>
	 * A mock implementation for the base zNode.
	 * ___________________________________________________________________________________________________________________
	 * NOTE:
	 * It is important for this test case to get the webArchive name is used from GlobalArqTestProperties.BASE_ROOT_Z_NODE
	 * </pre>
	 * 
	 * @param injectionPoint
	 *            the given injection point.
	 * @return the parameter depends on the key, or a IllegalStateException if a
	 *         wrong or no key is defined.
	 */
	@Produces
	@SuppressWarnings("incomplete-switch")
	@EnvConfigQualifier(key = EnvConfigKeys.PRODUCER)
	public String produceConfigurationValue(InjectionPoint injectionPoint) {

		final Annotated annotated = injectionPoint.getAnnotated();
		final EnvConfigQualifier annotation = annotated.getAnnotation(EnvConfigQualifier.class);

		if (annotation != null) {
			final EnvConfigKeys key = annotation.key();
			if (key != null) {
				switch (key) {
				// The base zNode shows: /webArchiveName/local/node0/
				case ZNODE_BASE_PATH:
					return "/" + RestClientTestCdiIT.WEB_ARCHIVE_NAME + GlobalArqTestProperties.RELATIVE_ROOT_Z_NODE;
				}
			}
		}

		throw new IllegalStateException("No key for injection point: " + injectionPoint);
	}
}
