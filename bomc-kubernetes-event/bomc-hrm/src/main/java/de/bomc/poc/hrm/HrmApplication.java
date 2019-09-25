/**
 * Project: POC PaaS
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
package de.bomc.poc.hrm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Boot the application.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@SpringBootApplication(scanBasePackages = { "de.bomc.poc.*" })
public class HrmApplication {

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		final ConfigurableApplicationContext configurableApplicationContext = SpringApplication
				.run(HrmApplication.class, args);
	}
}
