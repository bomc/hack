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
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class transforming the javax.ws.rs.core.Response.Status to a string.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class ResponseStatusAdapter extends XmlAdapter<String, Response.Status> {

	@Override
	public String marshal(final Response.Status status) throws Exception {
		return status.name();
	}

	@Override
	public Response.Status unmarshal(final String statusAsString) throws Exception {
		return Response.Status.valueOf(statusAsString);
	}

}
