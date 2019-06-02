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
package de.bomc.poc.upload.configuration;

/**
 * Defines keys for configuration properties from configuration.properties file.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 21.12.2016
 */
public enum ConfigKeys {

    CONNECTION_TTL("connection.ttl"),
    CONNECT_TIMEOUT("connect.timeout"),
    SO_TIMEOUT("so.timeout"),
    LAGACY_SERVICE_HOST("lagacy.service.host"),
    LAGACY_SERVICE_PORT("lagacy.service.port"),
    LAGACY_SERVICE_BASE_PATH("lagacy.service.base.path"),
    LAGACY_SERVICE_SCHEME("lagacy.service.scheme"),
    LAGACY_SERVICE_USERNAME("lagacy.service.username"),
    LAGACY_SERVICE_PASSWORD("lagacy.service.password"),
    PING_INTERVAL("ping.interval"),
    PRODUCER("producer");

    private final String propertyValue;

    ConfigKeys(final String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyValue() {
        return this.propertyValue;
    }
}
