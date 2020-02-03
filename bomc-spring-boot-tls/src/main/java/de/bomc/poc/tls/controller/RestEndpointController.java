package de.bomc.poc.tls.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.bomc.poc.tls.aspect.LogCertificate;

@RestController
public class RestEndpointController {

	public static final String RESPONSE = "bomc";
	
	@GetMapping("/api/tls")
	@LogCertificate(detailed = true)
	public String tls(/*final Principal principal*/){
//		return String.format("Hello %s!", principal.getName());
		
		return RESPONSE;
	}
	
}
