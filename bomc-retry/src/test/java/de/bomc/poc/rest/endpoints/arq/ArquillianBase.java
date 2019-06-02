/**
 * <pre>
 *
 * Last change:
 *
 *  by: $Author$
 *
 *  date: $Date$
 *
 *  revision: $Revision$
 *
 *    © Bomc 2018
 *
 * </pre>
 */
package de.bomc.poc.rest.endpoints.arq;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * A base class for arquillian tests.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 13.01.2018
 */
public class ArquillianBase {

    // This base url is used for access wildfly during testing. This means wildfly must be started on the given address.
    protected static final String BASE_URL = "http://127.0.0.1:8180/";

    public static WebArchive createTestArchive(final String webArchiveName) {

        return ShrinkWrap.create(WebArchive.class, webArchiveName + ".war")
                                                .addClass(ArquillianBase.class);
    }
}