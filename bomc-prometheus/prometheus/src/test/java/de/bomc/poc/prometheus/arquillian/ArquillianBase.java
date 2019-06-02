package de.bomc.poc.prometheus.arquillian;

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
}
