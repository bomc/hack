/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-23 10:56:24 +0100 (Mo, 23 Jan 2017) $
 *
 *  revision: $Revision: 9942 $
 *
 * </pre>
 */
package de.bomc.poc.upload.rest.client;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import de.bomc.poc.exception.app.AppUploadRuntimeException;
import de.bomc.poc.exception.errorcode.AppUploadErrorCode;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.upload.configuration.ConfigKeys;
import de.bomc.poc.upload.configuration.qualifier.ConfigQualifier;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

/**
 * A builder that creates a resteasy client for lagacy upload system..
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 20.12.2016
 */
public class RestClientBuilder {

    private static final String LOG_PREFIX = "RestClientBuilder#";
    @Inject
    @LoggerQualifier
    private Logger restClientBuilderLogger;
    @NotNull
    @Inject
    @ConfigQualifier(key = ConfigKeys.CONNECTION_TTL)
    private String connectionTTL;
    @NotNull
    @Inject
    @ConfigQualifier(key = ConfigKeys.CONNECT_TIMEOUT)
    private String connectTimeout;
    @NotNull
    @Inject
    @ConfigQualifier(key = ConfigKeys.SO_TIMEOUT)
    private String soTimeout;
    @NotNull
    @Inject
    @ConfigQualifier(key = ConfigKeys.LAGACY_SERVICE_HOST)
    private String lagacyServiceHost;
    @NotNull
    @Inject
    @ConfigQualifier(key = ConfigKeys.LAGACY_SERVICE_PORT)
    private String lagacyServicePort;
    @NotNull
    @Inject
    @ConfigQualifier(key = ConfigKeys.LAGACY_SERVICE_BASE_PATH)
    private String lagacyBasePath;
    @NotNull
    @Inject
    @ConfigQualifier(key = ConfigKeys.LAGACY_SERVICE_SCHEME)
    private String lagacyServiceScheme;
    @NotNull
    @Inject
    @ConfigQualifier(key = ConfigKeys.LAGACY_SERVICE_USERNAME)
    private String lagacyServiceUsername;
    @NotNull
    @Inject
    @ConfigQualifier(key = ConfigKeys.LAGACY_SERVICE_PASSWORD)
    private String lagacyServicePassword;

    public RestClientBuilder() {
        //
        // Used by cdi provider.
    }

    public ResteasyWebTarget buildUploadRestClient() {

        final String uriStr = this.buildUri();

        this.restClientBuilderLogger.debug(LOG_PREFIX
                                           + "createUploadRestClient [uri="
                                           + uriStr
                                           + ", connectionTTL="
                                           + this.connectionTTL
                                           + ", connectTimeout="
                                           + this.connectTimeout
                                           + ", soTimeout="
                                           + this.soTimeout
                                           + ", lagacyServiceHost="
                                           + this.lagacyServiceHost
                                           + ", lagacyServicePort="
                                           + this.lagacyServicePort
                                           + ", lagacyBasePath="
                                           + this.lagacyBasePath
                                           + ", lagacyServiceScheme="
                                           + this.lagacyServiceScheme
                                           + ", lagacyServiceUsername="
                                           + this.lagacyServiceUsername
                                           + ", lagacyServicePassword="
                                           + StringUtils.substring(this.lagacyServicePassword, 0, 2)
                                           + "***"
                                           + "]");

        final ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilder().connectionTTL(Integer.parseInt(this.connectionTTL), TimeUnit.MILLISECONDS)
                                                                                       .register(new BasicAuthentication(this.lagacyServiceUsername, this.lagacyServicePassword))
                                                                                       .establishConnectionTimeout(Integer.parseInt(this.connectTimeout), TimeUnit.MILLISECONDS)
                                                                                       .socketTimeout(Integer.parseInt(this.soTimeout), TimeUnit.MILLISECONDS);

        final ResteasyClient resteasyClient = resteasyClientBuilder.build();

        return resteasyClient.target(uriStr);
    }

    private String buildUri() {

        final URIBuilder uriBuilder = new URIBuilder();

        try {
            return uriBuilder.setScheme(this.lagacyServiceScheme)
                             .setHost(this.lagacyServiceHost)
                             .setPort(Integer.valueOf(this.lagacyServicePort))
                             .setPath(this.lagacyBasePath)
                             .build()
                             .toString();
        } catch (final URISyntaxException ex) {
            final String
                errMsg =
                LOG_PREFIX
                + "could not create URI with given parameter [lagacyServiceScheme="
                + this.lagacyServiceScheme
                + ", lagacyServiceHost="
                + this.lagacyServiceHost
                + ", lagacyServicePort="
                + this.lagacyServicePort
                + ", lagacyBasePath="
                + this.lagacyBasePath
                + "]";

            final AppUploadRuntimeException appUploadRuntimeException = new AppUploadRuntimeException(errMsg, ex, AppUploadErrorCode.UPLOAD_SERVICE_INITIALIZATION_00101);
            this.restClientBuilderLogger.error(errMsg + appUploadRuntimeException.stackTraceToString());
            appUploadRuntimeException.setIsLogged(true);

            throw appUploadRuntimeException;
        }
    }
}
