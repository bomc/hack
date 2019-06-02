package de.bomc.poc.test.arq.togglz;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * A base class for arquillian tests.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
public class ArquillianBase {

    // This base url is used for access wildfly during testing. This means wildfly must be started on the given address.
    protected static final String BASE_URL = "http://192.168.4.1:8180/";

    public static WebArchive createTestArchive(final String webArchiveName) {

        return ShrinkWrap.create(WebArchive.class, webArchiveName + ".war")
                                                .addClass(ArquillianBase.class);
    }
    
    /**
     * The arquillian war is already deployed to wildfly, read now the connection properties from global system properties, to build the base url.
     * @param webArchiveName part of the base url, 'http://localhost:8080/webArchiveName/'.
     * @return the base url, created from system properties.
     */
    protected String buildBaseUrl(final String webArchiveName) {
        // A systemProperty that is used, to get the host name of a running wildfly instance, during arquillian tests at runtime.
        final String WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY = "jboss.bind.address";
        final String bindAddressProperty = System.getProperty(WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY);
        // A systemProperty that is used, to get the port-offset of a running wildfly instance, during arquillian tests at runtime.
        final String WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY = "jboss.socket.binding.port-offset";
        // The wildfly default port for http requests.
        final int WILDFLY_DEFAULT_HTTP_PORT = 8080;
        // Get port of the running wildfly instance.
        final int port = WILDFLY_DEFAULT_HTTP_PORT + Integer.parseInt(System.getProperty(WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY));

        // Build the base Url.
        return "http://" + bindAddressProperty + ":" + port + "/" + webArchiveName;
    }
}
