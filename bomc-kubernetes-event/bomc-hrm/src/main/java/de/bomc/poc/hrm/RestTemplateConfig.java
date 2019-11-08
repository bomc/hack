/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import de.bomc.poc.hrm.application.log.http.LoggingClientHttpRequestInterceptor;

/**
 * Here RestTemplate bean is configured which is finally used to invoke REST
 * APIs. It uses CloseableHttpClient bean instance to build
 * ClientHttpRequestFactory, which is used to create RestTemplate.
 * 
 * HttpComponentsClientHttpRequestFactory is ClientHttpRequestFactory
 * implementation that uses Apache HttpComponents HttpClient to create requests.
 * We have used @Scheduled annotation in httpClient configuration. To support
 * this, we have to add support of scheduled execution of thread. For that, we
 * have used bean ThreadPoolTaskScheduler which internally utilizes
 * ScheduledThreadPoolExecutor to schedule commands to run after a given delay,
 * or to execute periodically.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.11.2019
 */
@Configuration
public class RestTemplateConfig {

	// _______________________________________________
	// Member variables.
	// -----------------------------------------------
	@Autowired
	private CloseableHttpClient httpClient;
	
	@Bean
	public RestTemplate restTemplate() {
		
		final RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());

		// Add new interceptors.
	    List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
	    
	    if (CollectionUtils.isEmpty(interceptors)) {
	        interceptors = new ArrayList<>();
	    }

	    interceptors.add(new LoggingClientHttpRequestInterceptor());
	    restTemplate.setInterceptors(interceptors);
	    
		return restTemplate;
	}

	@Bean
	public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
		
		final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setHttpClient(httpClient);

		return clientHttpRequestFactory;
	}

}
