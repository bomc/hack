package de.bomc.poc.zk.test.arq;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A base class for arquillian tests.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 7108 $ $Author: tzdbmm $ $Date: 2016-08-09 10:22:03 +0200 (Di, 09 Aug 2016) $
 * @since 22.07.2016
 */
public class ArquillianBase {
    // The default scheme.
    final String DEFAULT_SCHEME = "http";
    // A systemProperty that is used, to get the host name of a running wildfly instance, during arquillian tests at runtime.
    final String WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY = "jboss.bind.address";
    // A systemProperty that is used, to get the port-offset of a running wildfly instance, during arquillian tests at runtime.
    final String WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY = "jboss.socket.binding.port-offset";
    // A systemProperty that is used, to get the port-offset of a running wildfly instance, during arquillian tests at runtime.
    // This property is set during arquilliqn test with a running wildfly instance.
    final String ARQ_WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY = "arq.wildfly.management.port";
    final String SERVICE_NAME_IN_SERVICE_REGISTRY = "my_service_name_in_registry";

    static WebArchive createTestArchive(final String webArchiveName) {
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, webArchiveName)
                                                .addClass(ArquillianBase.class)
                                                .addAsWebInfResource(getBeansXml(), "beans.xml");

        // Add dependencies
        final MavenResolverSystem resolver = Maven.resolver();

        // shared module library
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("org.apache.curator:curator-framework:jar:?")
                                          .withMavenCentralRepo(false)
                                          .withTransitivity()
                                          .asFile());
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("org.apache.curator:curator-recipes:jar:?")
                                          .withMavenCentralRepo(false)
                                          .withTransitivity()
                                          .asFile());
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("org.apache.curator:curator-x-discovery:jar:?")
                                          .withMavenCentralRepo(false)
                                          .withTransitivity()
                                          .asFile());
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                						  .resolve("org.apache.httpcomponents:httpclient:jar:?")
                						  .withMavenCentralRepo(false)
                						  .withTransitivity()
                						  .asFile());

        return webArchive;
    }

    static StringAsset getBeansXml() {

        return new StringAsset("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                               + "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"\n"
                               + "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                               + "     xsi:schemaLocation=\"\n"
                               + "        http://xmlns.jcp.org/xml/ns/javaee\n"
                               + "        http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\" bean-discovery-mode=\"all\">\n"
                               + "    <!-- 'annotated' - loosely translated, means that only components with a class-level annotation are processed.\n"
                               + "         'all'       - all components are processed, just like they were in Java EE 6 with the explicit beans.xml.\n"
                               + "         'none'      - CDI is effectively disabled. -->\n"
                               + "\n"
                               + "</beans>");
    }

    /**
     * The arquillian war is already deployed to wildfly, read now the connection properties from global system properties, to build the base url.
     * @param webArchiveName part of the base url, 'http://localhost:8080/webArchiveName/'.
     * @return the base url, created from system properties.
     * @throws URISyntaxException is thrown to indicate that a string could not be parsed as a URI reference.
     */
    URI buildUri(final String webArchiveName) throws URISyntaxException {
        // The wildfly default port for http requests.
        final int WILDFLY_DEFAULT_HTTP_PORT = 8080;
        // Get port of the running wildfly instance.
        String wildflyPortOffsetProperty = System.getProperty(this.WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY);

        if(wildflyPortOffsetProperty == null) {
            //
            // The test is running with a already started wildfly instance in a own DOS-window.
            wildflyPortOffsetProperty = System.getProperty(this.ARQ_WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY);
        }

        final int port = WILDFLY_DEFAULT_HTTP_PORT + Integer.parseInt(wildflyPortOffsetProperty);

        // Build the base Url.
        return new URI(this.DEFAULT_SCHEME, null, System.getProperty(this.WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY), port, "/" + webArchiveName, null, null);
    }
}
