package de.bomc.poc.config.zk.producer;

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
// TODO verschieben nach zk-lib?
public class ZkConfigProducer {
	private static final String LOG_PREFIX = "ZkConfigProducer#";
	
    static final String DEFAULT_Z_NODE_BASE_PATH = "/discovery/service";
    static final String DEFAULT_Z_NODE_CONFIG_PATH = "/config";

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
                        return DEFAULT_Z_NODE_BASE_PATH;
                    case Z_NODE_CONFIG_PATH:
                    	return DEFAULT_Z_NODE_CONFIG_PATH;
                }
            }
        }

        throw new AppZookeeperException(LOG_PREFIX + "produceConfigurationValue - No key for injection point: " + injectionPoint);
    }
}
