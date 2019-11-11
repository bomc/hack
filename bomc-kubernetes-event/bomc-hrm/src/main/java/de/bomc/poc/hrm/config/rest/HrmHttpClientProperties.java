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
package de.bomc.poc.hrm.config.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.Setter;

//LOMBOK
@Getter
@Setter
//SPRING
@Configuration
@PropertySource("classpath:http-client.properties")
public class HrmHttpClientProperties {

	// _______________________________________________
	// Member variables
	// -----------------------------------------------
	//
	// Determines the timeout in milliseconds until a connection is established.
	@Value("${bomc.hrm.http.client.connection.timeout.in.millis:2500}")
	private int connectionTimeoutInMillis;
	//The timeout for waiting for data.
	@Value("${bomc.hrm.http.client.socket.timeout:2000}")
	private int socketTimeout;
	// The timeout when requesting a connection from the connection manager.
	@Value("${bomc.hrm.http.client.request.timeout:5000}")
	private int requestTimeout;
	// The maximum number of connections allowed across all routes.
	@Value("${bomc.hrm.http.client.max.total.connections:50}")
	private int maxTotalConnections;	
	// Sets the time that a server should wait for user requests before a new TCP connection needs to be established.
	@Value("${bomc.hrm.http.client.keep.alive.time.millis:10000}")
	private int keppAliveTime;
	// Configures the idle time a connection should wait before close.
	@Value("${bomc.hrm.http.client.close.idle.connection.wait.time.secs:10}")
	private int closeIdleConnectionWaitTime;
	// Client logging enabled.		
	@Value("${bomc.hrm.http.client.logging.enabled:true}")
	private boolean isLoggingEnabled;
	
}
