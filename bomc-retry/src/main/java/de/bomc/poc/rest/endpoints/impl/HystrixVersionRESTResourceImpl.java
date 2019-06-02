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
package de.bomc.poc.rest.endpoints.impl;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.bomc.poc.hystrix.ejb.HystrixCommandInvokerEJB;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.api.HystrixDTO;
import de.bomc.poc.rest.endpoints.v1.HystrixVersionRESTResource;

/**
 * Reads the current version of this project from 'version.properties'-file.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 17.01.2018
 */
public class HystrixVersionRESTResourceImpl implements HystrixVersionRESTResource {
	private static final String LOG_PREFIX = "HystrixVersionRESTResourceImpl#";
	//
	// Member variables
	@Inject
	@LoggerQualifier
	private Logger logger;
	@EJB
	private HystrixCommandInvokerEJB hystrixCommandInvokerEJB;

	/**
	 * <pre>
	 *	curl -i -H "Content-Type: application/vnd.version-v1+json" -X GET "192.168.4.1:8180/bomc-retry/rest/version/current-version"
	 * </pre>
	 */
	@Override
	public Response getVersion() {
		this.logger.debug(LOG_PREFIX + "getVersion");

		final HystrixDTO hystrixDTO = this.hystrixCommandInvokerEJB.getVersion();
		
		return Response.ok().entity(hystrixDTO).build();
	}
}
