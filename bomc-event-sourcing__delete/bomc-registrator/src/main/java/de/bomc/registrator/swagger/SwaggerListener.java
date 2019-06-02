package de.bomc.registrator.swagger;

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
 * @since 03.07.2018
 */
@WebListener
public class SwaggerListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion("1.00.00-SNAPSHOT");
		beanConfig.setSchemes(new String[] { "http" });
		beanConfig.setTitle("Registrator microservices REST API");
		beanConfig.setDescription(
				"Operations that can be invoked in the microservices to produce a message sending to the kafka middleware.");
		beanConfig.setResourcePackage("de.bomc.registrator");
		beanConfig.setContact("bomc@bomc.org");
		// NOTE: Must be adapted to the context-root, could be the war-name, if no jboss-web.xml is defined with the context-root.
		// 'context-root or war-name/application-path-from-jaxrsActivator'
		// ___________________________________________
		beanConfig.setBasePath("/registrator-app/api");
		beanConfig.setPrettyPrint(true);
		beanConfig.setScan(true);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}
}
