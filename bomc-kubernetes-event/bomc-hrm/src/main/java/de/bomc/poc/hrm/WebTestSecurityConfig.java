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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * NOTE: The spring-boot-starter-security starter provides access to Spring
 * Security. When using the -at WebMvcTest annotation approach with Spring
 * Security, MockMvc is automatically configured with the necessary filter chain
 * required to test our security configuration.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 23.12.2019
 */
@Configuration
@EnableWebSecurity
@Profile({ "!dev", "!prod" })
public class WebTestSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(final WebSecurity webSecurity) throws Exception {
		webSecurity.ignoring().antMatchers("/**");
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		//
		// Do nothing.
	}

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//      // Allow swagger to be accessed without authentication
//      web.ignoring().antMatchers("/v2/api-docs")//
//          .antMatchers("/swagger-resources/**")//
//          .antMatchers("/swagger-ui.html")//
//          .antMatchers("/configuration/**")//
//          .antMatchers("/webjars/**")//
//          .antMatchers("/public")
//          
//          // Un-secure H2 Database (for testing purposes, H2 console shouldn't be unprotected in production)
//          .and()
//          .ignoring()
//          .antMatchers("/h2-console/**/**");
//    }

}