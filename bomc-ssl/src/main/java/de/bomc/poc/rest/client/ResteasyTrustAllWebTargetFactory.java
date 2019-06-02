package de.bomc.poc.rest.client;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

import javax.net.ssl.SSLContext;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * Kommentar.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.02.2016
 */
public class ResteasyTrustAllWebTargetFactory {

    public static final Logger LOGGER = Logger.getLogger(ResteasyTrustAllWebTargetFactory.class);
    public static final String LDAP_HOST_SERVICE_ENDPOINT = "https://auth.egov.test.bs.ch";
    public static final String PATH = "/auth-restapi/users/Test3";

    public static ResteasyWebTarget buildWebTarget() {
        ResteasyClient client = null;
        try {
            client = new ResteasyClientBuilder().hostnameVerification(ResteasyClientBuilder.HostnameVerificationPolicy.ANY)
                                                .httpEngine(new ApacheHttpClient4Engine(createAllTrustClient()))
                                                .establishConnectionTimeout(10, TimeUnit.SECONDS)
                                				.socketTimeout(10, TimeUnit.MINUTES).connectionPoolSize(3)
                                                .build();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return client.target(ResteasyTrustAllWebTargetFactory.LDAP_HOST_SERVICE_ENDPOINT)
                     .path(ResteasyTrustAllWebTargetFactory.PATH);
    }

    private static DefaultHttpClient createAllTrustClient() throws GeneralSecurityException {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

        TrustStrategy trustStrategy = new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        };

        SSLSocketFactory factory = new SSLSocketFactory(trustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        registry.register(new Scheme("https", 443, factory));

        PoolingClientConnectionManager mgr = new PoolingClientConnectionManager(registry);
        mgr.setMaxTotal(1000);
        mgr.setDefaultMaxPerRoute(1000);

        DefaultHttpClient client = new DefaultHttpClient(mgr, new DefaultHttpClient().getParams());
        client.setRedirectStrategy(new RedirectStrategy() {
            @Override
            public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
                return null;
            }

            @Override
            public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
                return true;
            }
        });

        return client;
    }

    // ______________________________________________________________________________
    // With new HttpClient 4.5, many classes are deprecated.
    // ------------------------------------------------------------------------------
//    public ResteasyWebTarget buildWebTarget_() {
//        ResteasyClient client = null;
//        try {
//            client = new ResteasyClientBuilder().hostnameVerification(ResteasyClientBuilder.HostnameVerificationPolicy.ANY)
//                                                .httpEngine(new ApacheHttpClient4Engine(createAllTrustClient_()))
//                                                .build();
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        }
//
//        return client.target(ResteasyTrustAllWebTargetFactory.LDAP_HOST_SERVICE_ENDPOINT)
//                     .path(ResteasyTrustAllWebTargetFactory.PATH);
//    }

//    private HttpClient createAllTrustClient_() throws GeneralSecurityException {
//        HttpClientConnectionManager connectionManager = getСonfiguredConnectionManager();
//
//        RequestConfig config = getDefaultRequestConfig();
//
//        final CloseableHttpClient
//            httpClient =
//            HttpClients.custom()
//                       .setConnectionManager(connectionManager)
//                       .setDefaultRequestConfig(config)
//                       .build();
//
//        return httpClient;
//    }

//    private RequestConfig getDefaultRequestConfig() {
//        int socketTimeout = 10000;
//        int connectionTimeout = 5000;
//
//        return RequestConfig.custom()
//                            .setSocketTimeout(socketTimeout)
//                            .setConnectTimeout(connectionTimeout)
//                            .build();
//    }

//    private HttpClientConnectionManager getСonfiguredConnectionManager() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//        SSLContextBuilder builder = SSLContexts.custom();
//        builder.loadTrustMaterial(null, new TrustStrategy() {
//            @Override
//            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                return true;
//            }
//        });
//        SSLContext sslContext = builder.build();
//
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//        Registry<ConnectionSocketFactory>
//            socketFactoryRegistry =
//            RegistryBuilder.<ConnectionSocketFactory>create()
//                           .register("https", sslsf)
//                           .build();
//
//        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
//
//        final String connectionSize = "1000";
//        connectionManager.setMaxTotal(Integer.valueOf(connectionSize));
//
//        return connectionManager;
//    }
}
