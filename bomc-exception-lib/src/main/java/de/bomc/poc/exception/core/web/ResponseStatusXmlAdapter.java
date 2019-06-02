package de.bomc.poc.exception.core.web;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class transforming the javax.ws.rs.core.Response.Status to a string.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: 6790 $ $Author: tzdbmm $ $Date: 2016-07-19 09:06:34 +0200 (Di, 19 Jul 2016) $
 * @since 12.07.2016
 */
class ResponseStatusXmlAdapter extends XmlAdapter<String, Response.Status> {

    @Override
    public String marshal(final Response.Status status) throws Exception {
        return status.name();
    }

    @Override
    public Response.Status unmarshal(final String statusAsString) throws Exception {
        return Response.Status.valueOf(statusAsString);
    }
}
