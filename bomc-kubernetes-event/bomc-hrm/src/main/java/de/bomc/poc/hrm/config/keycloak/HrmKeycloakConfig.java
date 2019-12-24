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
package de.bomc.poc.hrm.config.keycloak;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Starting from Keycloak Spring Boot Adapter 7.0.0, due to some issues, the
 * automatic discovery of the Keycloak configuration from the
 * application.properties (or application.yml) file will not work. To overcome
 * this problem, we need to define a KeycloakSpringBootConfigResolver bean
 * explicitly in a -at Configuration class. see:
 * https://www.thomasvitale.com/spring-security-keycloak/
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 02.12.2019
 */
@Configuration
@Profile({ "prod" })
public class HrmKeycloakConfig {

	/**
	 * Load Keycloak configuration from application.properties or application.yml,
	 * rather than keycloak.json.
	 */
	@Bean
	public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
		return new KeycloakSpringBootConfigResolver();
	}

}
