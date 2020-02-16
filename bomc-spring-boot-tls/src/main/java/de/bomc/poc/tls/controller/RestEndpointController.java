package de.bomc.poc.tls.controller;

import java.security.Principal;
import java.util.logging.Logger;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.bomc.poc.tls.aspect.LogCertificate;

@RestController
public class RestEndpointController {

	private static final String LOG_PREFIX = RestEndpointController.class.getName() + "#";
	private static final Logger LOGGER = Logger.getLogger(RestEndpointController.class.getName());
	
	public static final String BASIC_USER_TLS_PATH = "/api/tls-basic-user";
	public static final String ADMIN_USER_TLS_PATH = "/api/tls-admin-user";
	
	public static final String BASIC_USER_RESPONSE = "tls-basic-user";
	public static final String ADMIN_USER_RESPONSE = "tls-admin-user";
	
	@GetMapping(BASIC_USER_TLS_PATH)
	@LogCertificate(detailed = true)
	public String tlsBasicUser(final Principal principal){
		
		final UserDetails currentUser = (UserDetails) ((Authentication) principal).getPrincipal();
		
		LOGGER.info(LOG_PREFIX + "tlsBasicUser [principal.name=" + principal.getName() + ", currentUser=" + currentUser + "]");
		
		return BASIC_USER_RESPONSE;
	}
	
	@GetMapping(ADMIN_USER_TLS_PATH)
	@LogCertificate(detailed = true)
	public String tlsAdminUser(final Principal principal){
		LOGGER.info(LOG_PREFIX + "tlsAdminUser [principal.name=" + principal.getName() + "]");
		
		return ADMIN_USER_RESPONSE;
	}
}
