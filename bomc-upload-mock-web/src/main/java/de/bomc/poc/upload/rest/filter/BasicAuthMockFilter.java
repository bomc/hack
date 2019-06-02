/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-18 07:10:18 +0100 (Mi, 18 Jan 2017) $
 *
 *  revision: $Revision: 9689 $
 *
 * </pre>
 */
package de.bomc.poc.upload.rest.filter;

import org.apache.log4j.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * A interceptor that checks the BASIC authentication requirement.
 * In this class the username and password are equal to the configuration.properties and ConfigurationSingletonEJB.
 * This means in test case the flow is not stopped.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 11.01.2017
 */
@Provider
@PreMatching
public class BasicAuthMockFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(BasicAuthMockFilter.class);
    private static final String LOG_PREFIX = "BasicAuthMockFilter#";
    // Define the valid username and password, these attributes for comparison are defined in the configuration.properties and in the ConfigurationSingletonEJB.
    public static final String USERNAME_TO_MATCH = "bomc-username";
    public static final String PASSWORD_TO_MATCH = "bomc-password";
    private final Pattern credentialsPattern = Pattern.compile("^Basic +(.+)$");

    @Override
    public void filter(final ContainerRequestContext containerRequestContext) throws IOException {
        LOGGER.debug(LOG_PREFIX + "filter");

        final String authorization = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorization != null) {
            final Matcher m = this.credentialsPattern.matcher(authorization);
            if (m.matches()) {

                final String credentialsEncodedBase64 = m.group(1);
                final byte[]
                    decoded =
                    Base64.getDecoder()
                          .decode(credentialsEncodedBase64);
                final String credentials = new String(decoded, StandardCharsets.UTF_8);

                if (credentials.equals(USERNAME_TO_MATCH + ":" + PASSWORD_TO_MATCH)) {

                    LOGGER.debug(LOG_PREFIX + "filter - authentication finish successfull.");

                    return;
                }
            }

            LOGGER.debug(LOG_PREFIX + "filter - authentication failed.");

            final Response response = Response.status(Response.Status.UNAUTHORIZED)
                                              .header(HttpHeaders.WWW_AUTHENTICATE, "Basic Realm=\"(-_-)\"")
                                              .type("text/plain; charset=UTF-8")
                                              .entity("")
                                              .build();

            containerRequestContext.abortWith(response);
        }
    }
}
