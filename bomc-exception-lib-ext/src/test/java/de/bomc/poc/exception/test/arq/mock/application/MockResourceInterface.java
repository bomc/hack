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
package de.bomc.poc.exception.test.arq.mock.application;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Describes the MockResource REST endpoint.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
@Path("mock")
@Consumes({ MockResourceInterface.MEDIA_TYPE_JSON_V })
@Produces({ MockResourceInterface.MEDIA_TYPE_JSON_V })
public interface MockResourceInterface {

	String MEDIA_TYPE_JSON_V = "application/vnd.exception-lib-v1+json";
	String MESSAGE_DECIMAL_MAX = "The value must be a decimal value lower than or equal to the number in the value element.";

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

	@GET
	@Path("/constraint-validation-on-signature-exception/{id}/{text}")
	Response getConstraintValidationOnSignatureException(
			@NotNull @DecimalMax(value = "1", message = MESSAGE_DECIMAL_MAX) @PathParam("id") Long id,
			@NotNull @Size(min = 2, max = 10) @PathParam("text") String text);

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("download/app-runtime-exception")
	Response doDownloadWithException();
}
