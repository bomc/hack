/**
 * <pre>
 *
 * Last change:
 *
 *  by: $Author$
 *
 *  date: $Date$
 *
 *  revision: $Revision$
 *
 *    © Bomc 2018
 *
 * </pre>
 */
package de.bomc.poc.hystrix.web;

import javax.servlet.annotation.WebServlet;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

/**
 * 
 * Class summary from original source, modified suitably for package name
 * changes:
 * 
 * Streams Hystrix metrics in text/event-stream format.
 * <p>
 * Install by:
 * <p>
 * 1) Including runtime-*.jar in your classpath.
 * <p>
 * 2) Adding the following to web.xml:
 * 
 * <pre>
 * {@code
 * <servlet>
 *  <description></description>
 *  <display-name>HystrixMetricsStreamServlet</display-name>
 *  <servlet-name>HystrixMetricsStreamServlet</servlet-name>
 *  <servlet-class>de.bomc.poc.hystrix.web.VersionHystrixMetricsStreamServlet</servlet-class>
 * </servlet>
 * <servlet-mapping>
 *  <servlet-name>HystrixMetricsStreamServlet</servlet-name>
 *  <url-pattern>/hystrix.stream</url-pattern>
 * </servlet-mapping>
 * }
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 17.01.2018
 */
@WebServlet(name = "VersionHystrixMetricsStreamServlet", urlPatterns = "/hystrix.stream", loadOnStartup = 1)
public class VersionHystrixMetricsStreamServlet extends HystrixMetricsStreamServlet {

	/**
	 * The serial UUID.
	 */
	private static final long serialVersionUID = 2129275205660449734L;

}
