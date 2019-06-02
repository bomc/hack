package de.bomc.poc.core.swagger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * Implementation for receiving notification events about ServletContext
 * lifecycle changes.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 09.08.2018
 */
@WebListener
public class SwaggerListener implements ServletContextListener {

	@Override
	public void contextInitialized(final ServletContextEvent sce) {
		final BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion("0.0.1");
		beanConfig.setSchemes(new String[] { "http" });
		beanConfig.setTitle("Core-microservices REST API");
		beanConfig.setDescription("Implements the core domain of the project.");
		beanConfig.setResourcePackage("de.bomc.poc.event.core");
		beanConfig.setContact("bomc@bomc.org");
		// NOTE: Must be adapted to the context-root, could be the war-name, if no jboss-web.xml is defined with the context-root.
		// 'context-root or war-name/application-path-from-jaxrsActivator'
		// ___________________________________________
		beanConfig.setBasePath("/core-app/api");
		beanConfig.setPrettyPrint(true);
		beanConfig.setScan(true);
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce) {
		//
	}
}
