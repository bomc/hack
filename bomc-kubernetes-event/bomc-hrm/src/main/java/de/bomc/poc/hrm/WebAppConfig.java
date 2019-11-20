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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.bomc.poc.hrm.application.log.http.server.RequestGetLoggingInterceptor;

/**
 * Defines callback methods to customize the Java-based configuration
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Configuration
public class WebAppConfig implements WebMvcConfigurer {

	private static final String PATHS = "/**";

	@Autowired
	private RequestGetLoggingInterceptor requestGetLoggingInterceptor;
//	@Autowired
//	private MdcInterceptor mdcInterceptor;

	/**
	 * Add Spring MVC lifecycle interceptors for pre- and post-processing of
	 * controller method invocations. Interceptors can be registered to apply to all
	 * requests or be limited to a subset of URL patterns.
	 *
	 * Note that interceptors registered here only apply to controllers and not to
	 * resource handler requests. To intercept requests for static resources either
	 * declare a MappedInterceptor bean or switch to advanced configuration mode by
	 * extending WebMvcConfigurationSupport and then override
	 * resourceHandlerMapping.
	 */
	@Override
	public void addInterceptors(final InterceptorRegistry interceptorRegistry) {
		//
		// Apply to all URLs.
		interceptorRegistry.addInterceptor(this.requestGetLoggingInterceptor).addPathPatterns(PATHS);
		// Apply to bomc path.
		// interceptorRegistry.addInterceptor(this.mdcInterceptor).addPathPatterns(PATHS);
	}

	@Override
	public void addViewControllers(final ViewControllerRegistry registry) {

//		registry.addViewController("/").setViewName("forward:/index.html");
	}

	/**
	 * Add handlers to serve static resources such as images, js, and, cssfiles from
	 * specific locations under web application root, the classpath, and others.
	 * 
	 * @param registry the resource handler registry.
	 */
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {

		// Add swagger sources.
//		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
//		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

}