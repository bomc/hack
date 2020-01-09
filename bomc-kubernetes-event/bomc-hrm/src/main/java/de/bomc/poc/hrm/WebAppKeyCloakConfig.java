/**
 * 
 * 
 * Project: hrm
 * <pre>
 *
 * Last change:
 * 
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

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * Keycloak provides a {@code KeycloakWebSecurityConfigurerAdapter} as a
 * convenient base class for creating a WebSecurityConfigurer instance, which is
 * convenient because a configuration class extending
 * WebSecurityConfigurerAdapter is needed for any application secured by Spring
 * Security.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 27.11.2019
 */
@KeycloakConfiguration
// Priority is by WebSecurityConfigurer equals 100, but there are two implementations.
// see 'de.bomc.poc.hrm.WebTestSecurityConfig'. Only one configurer with priority
// 100 is allowed.
@Order(value = 100)
@Profile({ "prod" })
public class WebAppKeyCloakConfig extends KeycloakWebSecurityConfigurerAdapter {

	/**
	 * Registers the KeycloakAuthenticationProvider with the authentication manager.
	 *
	 * Since Spring Security requires that role names start with "ROLE_", a
	 * SimpleAuthorityMapper is used to instruct the KeycloakAuthenticationProvider
	 * to insert the "ROLE_" prefix.
	 *
	 * e.g. Librarian -> ROLE_Librarian
	 *
	 * Should you prefer to have the role all in uppercase, you can instruct the
	 * SimpleAuthorityMapper to convert it by calling:
	 * {@code grantedAuthorityMapper.setConvertToUpperCase(true); }. The result will
	 * be: Librarian -> ROLE_LIBRARIAN.
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) {
		SimpleAuthorityMapper grantedAuthorityMapper = new SimpleAuthorityMapper();
		grantedAuthorityMapper.setPrefix("ROLE_");

		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
		keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(grantedAuthorityMapper);
		auth.authenticationProvider(keycloakAuthenticationProvider);
	}

	/**
	 * Defines the session authentication strategy.
	 *
	 * RegisterSessionAuthenticationStrategy is used because this is a public
	 * application from the Keycloak point of view.
	 */
	@Bean
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	/**
	 * Define an HttpSessionManager bean only if missing.
	 *
	 * This is necessary because since Spring Boot 2.1.0,
	 * spring.main.allow-bean-definition-overriding is disabled by default.
	 */
	@Bean
	@Override
	@ConditionalOnMissingBean(HttpSessionManager.class)
	protected HttpSessionManager httpSessionManager() {
		return new HttpSessionManager();
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		super.configure(http);

		// Get role from application.properties.
		http.cors() //
		        .and() //
		        .csrf() //
		        .disable() //
		        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //
		        // .sessionAuthenticationStrategy(sessionAuthenticationStrategy()) //
		        .and() //
		        .authorizeRequests() //
		        .antMatchers("/ui/customer/customers*").hasRole("bomc-admin") //
		        .anyRequest().permitAll();
	}
}
