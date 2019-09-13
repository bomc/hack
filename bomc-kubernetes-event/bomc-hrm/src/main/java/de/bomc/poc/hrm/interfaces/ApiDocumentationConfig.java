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
package de.bomc.poc.hrm.interfaces;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class ApiDocumentationConfig {

	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("hrm-api-1.0")
				// Specifies the title, description, etc of the Rest API.
				.apiInfo(this.apiInfo())
				// Provides a way to control the endpoints exposed by swagger.
				.select()
				// Specify the package where are the declared controllers. Swagger only picks up
				// controllers declared in this package.
				.apis(RequestHandlerSelectors.basePackage("de.bomc.poc.hrm"))
				// Specify only paths starting with /customer should be picked up.
				.paths(PathSelectors.any()) // (this.path())
//				.produces(Collections.singleton("application/json;charset=UTF-8"))
				.build();
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
	@SuppressWarnings("unused")
	private Predicate<String> path() {
		//
		// Match all paths except /error
		return Predicates.and(PathSelectors.regex("/customer.*/"), Predicates.not(PathSelectors.regex("/error.*")));
	}

}
