package de.bomc.poc.tls.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.bomc.poc.tls.controller.RestEndpointController;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${bomc.security.user}")
	private String validUser;
	
	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.csrf()
				.disable()
			.authorizeRequests()
            .antMatchers(RestEndpointController.BASIC_USER_TLS_PATH).hasRole("BASIC_USER")
            .antMatchers(RestEndpointController.ADMIN_USER_TLS_PATH).hasRole("ADMIN_USER")
			.anyRequest()
				.authenticated()
			.and()
				.x509()
					.subjectPrincipalRegex("CN=(.*?)(?:,|$)")
						.userDetailsService(userDetailsService())
			.and()				
				//SessionCreationPolicy.NEVER tells Spring to not bother creating sessions since all requests must have a certificate.
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
		// @formatter:on
	}

	/*
	 * Create an in-memory authentication manager. The method creates one user
	 * ('localhost') which is the CN of the client certificate) which has a role of
	 * USER.
	 */
	@Override
	public UserDetailsService userDetailsService() {
		
		return (UserDetailsService) username -> {
			if (username.equals(validUser)) {
				// Creates a user with password not set. Add roles in form "ROLE_BASIC_USER, ROLE_ADMIN_USER, ROLE_XY_USER"
				return  new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList(RoleEnum.ROLE_BASIC_USER.name()));
			} else {
				throw new UsernameNotFoundException(String.format("User %s not found", username));
			}
		};
	}

}
