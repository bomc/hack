package de.bomc.poc.ping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import de.bomc.poc.ping.application.service.PingService;

@SpringBootApplication(scanBasePackages = { "de.bomc.poc.*" })
public class PingApplication {

	@Autowired
	private PingService pingService;
        
    public static void main(String[] args) {
        SpringApplication.run(PingApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {

    	final String retVal = this.pingService.getPing();
    	
        return RouterFunctions.route()
                .GET("/", serverRequest -> ServerResponse.ok().body(BodyInserters.fromValue(retVal)))
                .build();
    }
}
