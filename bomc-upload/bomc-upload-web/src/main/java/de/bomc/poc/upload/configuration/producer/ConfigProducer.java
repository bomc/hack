/**
 * Project: Poc-Upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-18 07:45:48 +0100 (Mi, 18 Jan 2017) $
 *
 *  revision: $Revision: 9692 $
 *
 * </pre>
 */
package de.bomc.poc.upload.configuration.producer;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import de.bomc.poc.upload.configuration.ConfigKeys;
import de.bomc.poc.upload.configuration.ConfigSingletonEJB;
import de.bomc.poc.upload.configuration.qualifier.ConfigQualifier;

/**
 * A producer for injecting configuration parameter.
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 21.12.2016
 */
public class ConfigProducer {

    @Inject
    private ConfigSingletonEJB configSingletonEJB;
    // Contains mapping property name to value.
    final Map<String, String> keyPropertiesMap = new HashMap<>();

    /**
     * Produces the configuration parameter depends on the annotated key.
     * @param injectionPoint the given injection point.
     * @return the parameter depends on the key, or a IllegalStateException if a wrong or no key is defined.
     */
    @Produces
    @SuppressWarnings({"incomplete-switch"})
    @ConfigQualifier(key = ConfigKeys.PRODUCER)
    public String produceConfigurationValue(final InjectionPoint injectionPoint) {

        final Annotated annotated = injectionPoint.getAnnotated();
        final ConfigQualifier annotation = annotated.getAnnotation(ConfigQualifier.class);

        if (annotation != null) {
            final ConfigKeys key = annotation.key();
            if (key != null) {
                switch (key) {
                    case CONNECTION_TTL:
                        return Integer.toString(this.configSingletonEJB.getConnectionRequestTimeout());
                    case CONNECT_TIMEOUT:
                        return Integer.toString(this.configSingletonEJB.getConnectTimeout());
                    case SO_TIMEOUT:
                        return Integer.toString(this.configSingletonEJB.getSoTimeout());
                    case LAGACY_SERVICE_HOST:
                        return this.configSingletonEJB.getLagacyServiceHost();
                    case LAGACY_SERVICE_PORT:
                        return Integer.toString(this.configSingletonEJB.getLagacyServicePort());
                    case LAGACY_SERVICE_BASE_PATH:
                        return this.configSingletonEJB.getLagacyBasePath();
                    case LAGACY_SERVICE_SCHEME:
                        return this.configSingletonEJB.getLagacyServiceScheme();
                    case LAGACY_SERVICE_USERNAME:
                        return this.configSingletonEJB.getLagacyServiceUsername();
                    case LAGACY_SERVICE_PASSWORD:
                        return this.configSingletonEJB.getLagacyServicePassword();
                    case PING_INTERVAL:
                        return Long.toString(this.configSingletonEJB.getPingInterval());
                }
            }
        }

        throw new IllegalStateException("No key for injection point: " + injectionPoint);
    }
}

