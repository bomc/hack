package de.bomc.poc.rest.endpoints.impl;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.bomc.poc.hystrix.command.HystrixVersionCommand;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.api.HystrixDTO;
import de.bomc.poc.rest.endpoints.v1.HystrixVersionRESTResource;

/**
 * Reads the current version of this project from 'version.properties'-file.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
public class HystrixVersionRESTResourceImpl implements HystrixVersionRESTResource {
	private static final String LOG_PREFIX = "HystrixVersionRESTResourceImpl#";
	private static final String BASE_URI = "http://192.168.4.1:8180/bomc-hystrix-broken/rest";
	@Inject
	@LoggerQualifier
	private Logger logger;

	/**
	 * <pre>
	 *	curl -i -H "Content-Type: application/vnd.version-v1+json" -X GET "192.168.4.1:8180/bomc-hystrix-invoker/rest/version/current-version"
	 * </pre>
	 */
	@Override
	public Response getVersion() {
		this.logger.debug(LOG_PREFIX + "getVersion");

		final String requestId = UUID.randomUUID().toString();
		
		HystrixDTO hystrixDTO = null;
		
		try {
			final HystrixVersionCommand hystrixVersionCommand = new HystrixVersionCommand(this.logger, BASE_URI, requestId);
			hystrixDTO = hystrixVersionCommand.execute();

			this.logger.debug(LOG_PREFIX + "getVersion - send response back to invoker [hystrixDTO=" + hystrixDTO + "]");
		} catch (Exception ex) {
			this.logger.debug(LOG_PREFIX + "getVersion - ", ex);
			
			hystrixDTO = new HystrixDTO(null);
			hystrixDTO.setErrorMsg("Unexpected error=" + ex.getMessage());
			hystrixDTO.setFallback(true);
		}
		
		return Response.ok().entity(hystrixDTO).build();
	}
}
