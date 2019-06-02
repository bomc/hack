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
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

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
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Configuration
@EnableSwagger2
public class ApiDocumentationConfig extends WebMvcConfigurationSupport {

	@Bean
	public Docket productApi10() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("hrm-api-1.0")
				.select()
				.apis(RequestHandlerSelectors.basePackage("de.bomc.poc.hrm")).paths(PathSelectors.any())
				.build()
//				.produces(Collections.singleton("application/json;charset=UTF-8"))
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo("PoC REST API", "A simple application for testing the PaaS plattform.", "API TOS",
				"Terms of service", new Contact("bomc", "www.bomc.org", "bomc@bomc.org"),
				"License of API", "API license URL", Collections.emptyList());
		return apiInfo;
	}
	
	@Override
	protected void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}
