package de.bomc.poc.exception.core.web;

import javax.ws.rs.core.Response;

import de.bomc.poc.exception.core.ErrorCode;

/**
 * Decribes api errors for REST invocations.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 */
public interface ApiError extends ErrorCode {

    Response.Status getStatus();
}
