package de.bomc.poc.tls.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		
		http.authorizeRequests().anyRequest().authenticated().and().x509().subjectPrincipalRegex("CN=(.*?)(?:,|$)")
				.userDetailsService(userDetailsService());
	}

	@Bean
	public UserDetailsService userDetailsService() {
		
		return (username -> {
			if (username.equals("localhost")/* || username.equals("codependent-client2")*/) {
				return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
			} else {
				return null;
			}
		});
	}

//    /*
//     * Enable x509 client authentication.
//     */
//    @Override
//    protected void configure(final HttpSecurity http) throws Exception {
//        http.x509();
//    }
//
//    /*
//     * Create an in-memory authentication manager. We create 1 user (localhost which
//     * is the CN of the client certificate) which has a role of USER.
//     */
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//    	
//        auth.inMemoryAuthentication()
//                .withUser("localhost_")
//                .password("none")
//                .roles("USER");
//    }
    
}
