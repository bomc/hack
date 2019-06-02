/**
 * Project: MY_POC_MICROSERVICE
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.exception.handling;

import javax.ws.rs.core.Response;

/**
 * A interface that describes the ApiError types.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public interface ApiError {

	Response.Status getStatus();

	String getErrorCode();

	String getMessageKey();

}
