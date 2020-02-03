package de.bomc.poc.tls.client;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TlsControllerConfig {

	// Set password.
    private char[] allPassword = "secret".toCharArray();

    @Bean
    public RestTemplate restTemplate() throws Exception {

    	// Setup ssl context.
        final SSLContext sslContext = SSLContextBuilder
                .create()
                .loadKeyMaterial(ResourceUtils.getFile("classpath:client-identity.jks"), allPassword, allPassword)
                .loadTrustMaterial(ResourceUtils.getFile("classpath:client-truststore.jks"), allPassword)
                .build();

        final HttpClient client = HttpClients.custom()
                .setSSLContext(sslContext)
                .build();

        final HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setHttpClient(client);
        
        final RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        
        return restTemplate;
    }

}