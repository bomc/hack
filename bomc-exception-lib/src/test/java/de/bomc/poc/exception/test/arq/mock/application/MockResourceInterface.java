package de.bomc.poc.exception.test.arq.mock.application;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Describes the MockResource REST endpoint.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 6790 $ $Author: tzdbmm $ $Date: 2016-07-19 09:06:34 +0200 (Di, 19 Jul 2016) $
 * @since 12.07.2016
 */
@Path("mock")
@Produces(MediaType.APPLICATION_JSON)
public interface MockResourceInterface {

    @GET
    @Path("/with-api-error-code/{id}")
    Response getExceptionWithApiError(@PathParam("id") Long id);

    @GET
    @Path("/with-status-and-error-code/{id}")
    Response getExceptionWithStatusAndErrorCode(@PathParam("id") Long id);

    @GET
    @Path("/with-wrapped-exception/{id}")
    Response getExceptionWithWrappedException(@PathParam("id") Long id);

    @GET
    @Path("/wrapped-exception-in-a-runtime-exception/{id}")
    Response getWrappedExceptionInARuntimeException(@PathParam("id") Long id);

    @GET
    @Path("/not-a-web-runtime-exception/{id}")
    Response getNotAWebRuntimeException(@PathParam("id") Long id);

    @GET
    @Path("/constraint-validation-exception/{id}")
    Response getConstraintValidationException(@PathParam("id") Long id);

    @GET
    @Path("/app-runtime-exception/{id}")
    Response getAppRuntimeException(@PathParam("id") Long id);
}
