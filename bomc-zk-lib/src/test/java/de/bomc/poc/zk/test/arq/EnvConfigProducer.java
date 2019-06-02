package de.bomc.poc.zk.test.arq;

import de.bomc.poc.zk.config.env.EnvConfigKeys;
import de.bomc.poc.zk.config.env.qualifier.EnvConfigQualifier;
import de.bomc.poc.zk.exception.AppZookeeperException;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * A producer for injecting the zookeeper connection parameter.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
public class EnvConfigProducer {

    static final String Z_NODE_BASE_PATH = "/account-microservice/dev/discovery/uri";

    /**
     * Produces the environment parameter depends on the annotated key.
     * @param injectionPoint the given injection point.
     * @return the parameter depends on the key, or a IllegalStateException if a wrong or no key is defined.
     */
    @Produces
    @SuppressWarnings("incomplete-switch")
    @EnvConfigQualifier(key = EnvConfigKeys.PRODUCER)
    public String produceConfigurationValue(final InjectionPoint injectionPoint) {

        final Annotated annotated = injectionPoint.getAnnotated();
        final EnvConfigQualifier annotation = annotated.getAnnotation(EnvConfigQualifier.class);

        if (annotation != null) {
            final EnvConfigKeys key = annotation.key();
            if (key != null) {
                switch (key) {
                    case Z_NODE_BASE_PATH:
                        return Z_NODE_BASE_PATH;
                }
            }
        }

        throw new AppZookeeperException("EnvConfigProducer#produceConfigurationValue - No key for injection point: " + injectionPoint);
    }
}
