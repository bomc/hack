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
package de.bomc.poc.hrm.config.swagger;

import java.time.LocalDate;
import java.util.Collections;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import de.bomc.poc.hrm.config.git.HrmGitConfig;
import de.bomc.poc.hrm.interfaces.CustomerController;
import de.bomc.poc.hrm.interfaces.UserController;
import de.bomc.poc.hrm.interfaces.VersionController;
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
@Import({ BeanValidatorPluginsConfiguration.class }) // Get Java Bean Validation to work with swagger.
public class HrmSwaggerApiDocumentationConfig {

	// _______________________________________________
	// Get host and port in case the 'prod' configuration is enabled.
	// The properties are defined in 'application-prod.properties'.
	// -----------------------------------------------
	@Value("${bomc.swagger.host:localhost}")
	private String swaggerHost;
	@Value("${bomc.swagger.port:8080}")
	private String swaggerPort;

	// _______________________________________________
	// Member variables
	// -----------------------------------------------
	private final HrmGitConfig hrmGitConfig;

	/**
	 * Autowired constructor.
	 *
	 * @param hrmGitConfig contains data for configuration.
	 */
	public HrmSwaggerApiDocumentationConfig(final HrmGitConfig hrmGitConfig) {

		this.hrmGitConfig = hrmGitConfig;
	}

	@Bean
	public Docket customerApi(final ServletContext servletContext) {

		return new Docket(DocumentationType.SWAGGER_2).host(this.swaggerHost + ":" + this.swaggerPort)
		        .groupName("customer-hrm-api-" + hrmGitConfig.getCommitId())
		        // Specifies the title, description, etc of the Rest API.
		        .apiInfo(this.apiInfo())
				.produces(Collections.singleton(CustomerController.MEDIA_TYPE_JSON_V1))
		        // Provides a way to control the endpoints exposed by swagger.
		        .select()
		        // Specify the package where are the declared controllers. Swagger only picks up
		        // controllers declared in this package.
		        .apis(RequestHandlerSelectors.basePackage("de.bomc.poc.hrm.interfaces"))
		        // Specify only paths starting with /customer should be picked up.
		        .paths(this.customerPath()) // PathSelectors.any()
		        .build() //
		        .directModelSubstitute(LocalDate.class, String.class) //
		        .genericModelSubstitutes(ResponseEntity.class) //
		        .pathMapping("/");
	}

	@Bean
	public Docket userApi(final ServletContext servletContext) {

		return new Docket(DocumentationType.SWAGGER_2).host(this.swaggerHost + ":" + this.swaggerPort)
		        .groupName("user-hrm-api-" + hrmGitConfig.getCommitId())
		        // Specifies the title, description, etc of the Rest API.
		        .apiInfo(this.apiInfo()).pathMapping("/")
				.produces(Collections.singleton(UserController.MEDIA_TYPE_JSON_V1))
		        // Provides a way to control the endpoints exposed by swagger.
		        .select()
		        // Specify the package where are the declared controllers. Swagger only picks up
		        // controllers declared in this package.
		        .apis(RequestHandlerSelectors.basePackage("de.bomc.poc.hrm.interfaces"))
		        // Specify only paths starting with /customer should be picked up.
		        .paths(this.userPath()) // PathSelectors.any()
		        .build() //
		        .directModelSubstitute(LocalDate.class, String.class) //
		        .genericModelSubstitutes(ResponseEntity.class)
		        .pathMapping("/");
	}

	@Bean
	public Docket versionApi(final ServletContext servletContext) {

		return new Docket(DocumentationType.SWAGGER_2).host(this.swaggerHost + ":" + this.swaggerPort)
		        .groupName("user-version-api-" + hrmGitConfig.getCommitId())
		        // Specifies the title, description, etc of the Rest API.
		        .apiInfo(this.apiInfo()).pathMapping("/")
				.produces(Collections.singleton(VersionController.MEDIA_TYPE_JSON_V1))
		        // Provides a way to control the endpoints exposed by swagger.
		        .select()
		        // Specify the package where are the declared controllers. Swagger only picks up
		        // controllers declared in this package.
		        .apis(RequestHandlerSelectors.basePackage("de.bomc.poc.hrm.interfaces"))
		        // Specify only paths starting with /customer should be picked up.
		        .paths(this.versionPath()) // PathSelectors.any()
		        .build() //
		        .directModelSubstitute(LocalDate.class, String.class) //
		        .genericModelSubstitutes(ResponseEntity.class) //
		        .pathMapping("/");
	}

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo("HRM REST API", "A simple application for testing the PaaS plattform.",
		        hrmGitConfig.getCommitId(), "Terms of service", new Contact("bomc", "www.bomc.org", "bomc@bomc.org"),
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

	/**
	 * Only select apis that matches the given Predicates.
	 * 
	 * @return a predicate as string.
	 */
	private Predicate<String> versionPath() {
		//
		// Match all paths except /error
		return Predicates.and(PathSelectors.regex("/git.*"), Predicates.not(PathSelectors.regex("/error.*")));
	}
}
