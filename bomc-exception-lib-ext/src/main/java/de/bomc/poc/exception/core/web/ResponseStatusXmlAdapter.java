/**
 * Project: bomc-exception-lib-ext
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
package de.bomc.poc.exception.core.web;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class transforming the javax.ws.rs.core.Response.Status to a string.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
public class ResponseStatusXmlAdapter extends XmlAdapter<String, Response.Status> {

	public ResponseStatusXmlAdapter() {
		//
		// Used by REST processing.
	}

	@Override
	public String marshal(final Response.Status status) throws Exception {
		return status.name();
	}

	@Override
	public Response.Status unmarshal(final String statusAsString) throws Exception {
		return Response.Status.valueOf(statusAsString);
	}
}
