package de.bomc.poc.rest.endpoints.impl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.api.HystrixDTO;
import de.bomc.poc.rest.endpoints.v1.VersionRESTResource;
import de.bomc.poc.service.impl.VersionSingletonEJB;

/**
 * Reads the current version of this project from 'version.properties'-file.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
public class VersionRESTResourceImpl implements VersionRESTResource {
	private static final String LOG_PREFIX = "VersionRESTResourceImpl#";
	private static final long MIN_RANGE = 5L;
	private static final long MAX_RANGE = 10L;
	@Inject
	@LoggerQualifier
	private Logger logger;
	@EJB
	private VersionSingletonEJB versionSingetonEJB;

	/**
	 * <pre>
	 *	http://192.168.4.1:8180/bomc-hystrix-broken/rest/version/current-version
	 * </pre>
	 */
	@Override
	public Response getVersion() {
		this.logger.debug(LOG_PREFIX + "getVersion");

		Response response = null;
		String message = null;

		try {
			final long sleep = this.getRandomNumberInRange(MIN_RANGE, MAX_RANGE);

			final String version = this.versionSingetonEJB.readVersionFromClasspath();

			if (sleep == 10) {
				// _______________________________________________________
				// Use a timeout.
				this.logger.debug(LOG_PREFIX + "getVersion (timeout) - [sleep=" + sleep + "(in seconds)]");
				// Sleep to simulate broken service.
				TimeUnit.SECONDS.sleep(sleep);
				this.logger.debug(LOG_PREFIX + "getVersion - wakeup!");

				// _______________________________________________________
				// THIS CODE WILL NEVER INVOKE, FOR HYSTRIX TIMEOUT 7500ms.
				response = Response.ok().entity(this.getHystrixDTO(version, "Simulate an timeout.", true)).build();
			} else if (sleep == 5) {
				// _______________________________________________________
				// Throw a Exception.
				this.logger.debug(LOG_PREFIX + "getVersion - Do some trouble by throwing an exception.");
				throw new RuntimeException("Do some trouble by throwing a exception.");
			} else if (sleep == 6) {
				// _______________________________________________________
				// Simulate a intern server error.
				final HystrixDTO hystrixDTO = this.getHystrixDTO(version,
						"Simulate an server error, with a state not similar to the http response code family OK.",
						true);
				this.logger.debug(LOG_PREFIX + "getVersion - [fallbackDTO=" + hystrixDTO + "]");
				response = Response.serverError().entity(this.getHystrixDTO(version, "Simulate an timeout.", true))
						.build();
			} else {
				// _______________________________________________________
				// Send a regular answer.
				final HystrixDTO hystrixDTO = this.getHystrixDTO(version, null, false);
				this.logger.debug(
						LOG_PREFIX + "getVersion (regular) - [sleep=" + sleep + ", hystrixDTO=" + hystrixDTO + "]");
				response = Response.ok().entity(hystrixDTO).build();
			}
		} catch (final IOException ioex) {
			this.logger.error(LOG_PREFIX + "getVersion - failed!", ioex);
			message = ioex.getMessage();
		} catch (final InterruptedException iex) {
			this.logger.warn(LOG_PREFIX + "getVersion - sleep interrupted ignore.");
			message = iex.getMessage();
		} catch (Exception ex) {
			this.logger.error(LOG_PREFIX + "getVersion - ", ex);
			message = ex.getMessage();
		}

		if (response == null) {
			final HystrixDTO hystrixDTO = this.getHystrixDTO(null, "Unexpected error=" + message, true);
			this.logger.debug(LOG_PREFIX + "getVersion - [hystrixDTO=" + hystrixDTO + "]");
			response = Response.serverError().entity(hystrixDTO).build();
		}

		return response;
	}

	private long getRandomNumberInRange(long min, long max) {
		final long sleep = (long) (Math.random() * ((max - min) + 1)) + min;

		return sleep;
	}

	private HystrixDTO getHystrixDTO(final String version, final String errorMsg, final boolean fallback) {
		final HystrixDTO hystrixDTO = new HystrixDTO(version);
		hystrixDTO.setErrorMsg(errorMsg);
		hystrixDTO.setFallback(fallback);

		return hystrixDTO;
	}
}
