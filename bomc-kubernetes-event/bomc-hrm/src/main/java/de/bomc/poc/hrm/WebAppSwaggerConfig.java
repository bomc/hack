/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: micha
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This configuration should allow the same URL call for the development
 * environment as in the production environment,
 * 'bomc-hrm/bomc-api/swagger-ui.html'. This configuration allows a redirect for
 * the dev profile because the default call is 'bomc-api/swagger-ui.html'. In
 * production the rewriting is done by istio-virtualservice configuration. See:
 * {@link HrmSwaggerApiDocumentationConfig}}.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 21.11.2019
 */
@Configuration
@Profile({ "dev", "prod" })
public class WebAppSwaggerConfig implements WebMvcConfigurer {

	// _______________________________________________
	// NOTE: The given prefix must match with the istio-configuration.
	// See: istio-bomc-app-virtualservice.yml
	// -----------------------------------------------
	private static final String SWAGGER_URI_PREFIX = "/bomc-api";

	/**
	 * Enables the adopting of the swagger-ui path from
	 * 'http://localhost:8080/bomc-hrm/swagger-ui.html' to
	 * 'http://localhost:8080/bomc-hrm/bomc-api/swagger-ui.html'.
	 */
	@Override
	public void addViewControllers(final ViewControllerRegistry registry) {

		registry.addRedirectViewController(SWAGGER_URI_PREFIX + "/v2/api-docs", "/v2/api-docs")
		        .setKeepQueryParams(true);
		registry.addRedirectViewController(SWAGGER_URI_PREFIX + "/swagger-resources/configuration/ui",
		        "/swagger-resources/configuration/ui");
		registry.addRedirectViewController(SWAGGER_URI_PREFIX + "/swagger-resources/configuration/security",
		        "/swagger-resources/configuration/security");
		registry.addRedirectViewController(SWAGGER_URI_PREFIX + "/swagger-resources", "/swagger-resources");
		registry.addRedirectViewController(SWAGGER_URI_PREFIX, "/swagger-ui.html");
	}

	/**
	 * Enables the adopting of the swagger-ui path from
	 * 'http://localhost:8080/bomc-hrm/swagger-ui.html' to
	 * 'http://localhost:8080/bomc-hrm/bomc-api/swagger-ui.html'.
	 */
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {

		registry.addResourceHandler(SWAGGER_URI_PREFIX + "/swagger-ui.html**")
		        .addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");

		registry.addResourceHandler(SWAGGER_URI_PREFIX + "/webjars/**")
		        .addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}
