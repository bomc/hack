package de.bomc.poc.tls;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = { "de.bomc.poc.*" })
public class TlsApplication {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		
		final ConfigurableApplicationContext configurableApplicationContext = SpringApplication
		        .run(TlsApplication.class, args);
	}

}
