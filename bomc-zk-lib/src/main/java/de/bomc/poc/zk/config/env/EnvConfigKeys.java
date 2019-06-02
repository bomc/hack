package de.bomc.poc.zk.config.env;

/**
 * Environment parameter keys for configuration.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
public enum EnvConfigKeys {
    Z_NODE_BASE_PATH,    // The root zNode path, this is the path under which the service is registered.
    Z_NODE_CONFIG_PATH,	// The path for service configuration.
    PRODUCER          	// Used for identifying the Producer method.
}
