/**
 * Project: POC PaaS
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

import java.util.Collections;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The api documentation configuration.
 * 
 * EnableSwagger2 enables swagger configuration during application startup.
 * 
 * NOTE: Add 'extends WebMvcConfigurationSupport' to change the default path
 * from 'http://localhost:8080/bomc-hrm/swagger-ui.html' to
 * 'http://localhost:8080/bomc-hrm/bomc-api/swagger-ui.html'.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class ApiDocumentationConfig extends WebMvcConfigurationSupport {

	private static final String SWAGGER_CONTEXT_ROOT = "/bomc-api";

	// Get host and port in case the 'prod' configuration is enabled.
	// The properties are defined in 'application-prod.properties'.
	@Value("${bomc.swagger.host:localhost}")
	private String swaggerHost;
	@Value("${bomc.swagger.port:8080}")
	private String swaggerPort;

	@Bean
	public Docket customerApi(final ServletContext servletContext) {

		return new Docket(DocumentationType.SWAGGER_2).host(this.swaggerHost + ":" + this.swaggerPort)
				.groupName("customer-hrm-api-1.0")
				// Specifies the title, description, etc of the Rest API.
				.apiInfo(this.apiInfo())
//				.produces(Collections.singleton("application/json;charset=UTF-8"))
				// Provides a way to control the endpoints exposed by swagger.
				.select()
				// Specify the package where are the declared controllers. Swagger only picks up
				// controllers declared in this package.
				.apis(RequestHandlerSelectors.basePackage("de.bomc.poc.hrm.interfaces"))
				// Specify only paths starting with /customer should be picked up.
				.paths(this.customerPath()) // PathSelectors.any()
				.build();
	}

	@Bean
	public Docket userApi(final ServletContext servletContext) {

		return new Docket(DocumentationType.SWAGGER_2).host(this.swaggerHost + ":" + this.swaggerPort)
				.groupName("user-hrm-api-1.0")
				// Specifies the title, description, etc of the Rest API.
				.apiInfo(this.apiInfo())
//				.produces(Collections.singleton("application/json;charset=UTF-8"))
				// Provides a way to control the endpoints exposed by swagger.
				.select()
				// Specify the package where are the declared controllers. Swagger only picks up
				// controllers declared in this package.
				.apis(RequestHandlerSelectors.basePackage("de.bomc.poc.hrm.interfaces"))
				// Specify only paths starting with /customer should be picked up.
				.paths(this.userPath()) // PathSelectors.any()
				.build();
	}

	/**
	 * Enables the adopting of the swagger-ui path from
	 * 'http://localhost:8080/bomc-hrm/swagger-ui.html' to
	 * 'http://localhost:8080/bomc-hrm/bomc-api/swagger-ui.html'.
	 */
	@Override
	public void addViewControllers(final ViewControllerRegistry registry) {
		registry.addRedirectViewController(SWAGGER_CONTEXT_ROOT + "/v2/api-docs", "/v2/api-docs")
				.setKeepQueryParams(true);
		registry.addRedirectViewController(SWAGGER_CONTEXT_ROOT + "/swagger-resources/configuration/ui",
				"/swagger-resources/configuration/ui");
		registry.addRedirectViewController(SWAGGER_CONTEXT_ROOT + "/swagger-resources/configuration/security",
				"/swagger-resources/configuration/security");
		registry.addRedirectViewController(SWAGGER_CONTEXT_ROOT + "/swagger-resources", "/swagger-resources");
	}

	/**
	 * Enables the adopting of the swagger-ui path from
	 * 'http://localhost:8080/bomc-hrm/swagger-ui.html' to
	 * 'http://localhost:8080/bomc-hrm/bomc-api/swagger-ui.html'.
	 */
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler(SWAGGER_CONTEXT_ROOT + "/**")
				.addResourceLocations("classpath:/META-INF/resources/");
	}

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo("HRM REST API", "A simple application for testing the PaaS plattform.",
				"1.0.0-SNAPSHOT", "Terms of service", new Contact("bomc", "www.bomc.org", "bomc@bomc.org"),
				"License of API", "API license URL", Collections.emptyList());

		return apiInfo;
	}

	/**
	 * Only select apis that matches the given Predicates.
	 * 
	 * @return a predicate as string.
	 */
	private Predicate<String> customerPath() {
		//
		// Match all paths except /error
		return Predicates.and(PathSelectors.regex("/customer.*"), Predicates.not(PathSelectors.regex("/error.*")));
	}

	/**
	 * Only select apis that matches the given Predicates.
	 * 
	 * @return a predicate as string.
	 */
	private Predicate<String> userPath() {
		//
		// Match all paths except /error
		return Predicates.and(PathSelectors.regex("/user.*"), Predicates.not(PathSelectors.regex("/error.*")));
	}

}
