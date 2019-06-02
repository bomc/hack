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
package de.bomc.poc.config.producer;

import de.bomc.poc.config.EnvConfigKeys;
import de.bomc.poc.config.EnvConfigSingletonEJB;
import de.bomc.poc.config.qualifier.EnvConfigQualifier;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

/**
 * A producer for injecting the zookeeper connection parameter.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
public class EnvConfigProducer {

    @Inject
    private EnvConfigSingletonEJB envConfigSingletonEJB;

    /**
     * Produces the environment parameter depends on the annotated key.
     * @param injectionPoint the given injection point.
     * @return the parameter depends on the key, or a IllegalStateException if a wrong or no key is defined.
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
                    case BIND_ADDRESS:
                        return envConfigSingletonEJB.getBindAddress();
                    case BIND_PORT:
                        return Integer.toString(envConfigSingletonEJB.getBindPort());
                    case BIND_SECURE_PORT:
                        return Integer.toString(envConfigSingletonEJB.getBindSecurePort());
                    case NODE_NAME:
                        return envConfigSingletonEJB.getNodeName();
                    case WEB_ARCHIVE_NAME:
                        return envConfigSingletonEJB.getWebArchiveName();
                    case ZNODE_BASE_PATH:
                        return envConfigSingletonEJB.getZnodeBasePath();
                }
            }
        }

        throw new IllegalStateException("No key for injection point: " + injectionPoint);
    }
}
