package de.bomc.poc.exception.test.arq.mock.application;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.exception.cdi.qualifier.ExceptionHandlerQualifier;
import de.bomc.poc.exception.core.web.ApiErrorResponseObject;
import de.bomc.poc.exception.test.TestApiError;

/**
 * This endpoint works as a mock and is the implementation of the
 * {@link MockWebAppExResourceInterface}. This mock is using for testing the
 * extended {@link MockResponseObject} with the {@link ApiErrorResponseObject}
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 */
@ExceptionHandlerQualifier
@Interceptors(ExceptionHandlerInterceptor.class)
public class MockWebAppExResource implements MockWebAppExResourceInterface {

	private static final Logger LOGGER = Logger.getLogger(MockWebAppExResource.class);
	private static final String LOG_PREFIX = "MockWebAppExResource#";

	@Inject
	private MockExceptionHandler<TestApiError> exceptionHandler;
	
	@Override
	public Response checkMockResponseObject(final Long id) {
		LOGGER.debug(LOG_PREFIX + "checkMockResponseObject [id=" + id + "]");

		if (id % 2 == 0) {
			final TestApiError testApiError = TestApiError.TEST_API_00101;

			final Map<String, String> exMap = new HashMap<String, String>();
			exMap.put("key1", "value1");
			exMap.put("key2", "value2");
			
			throw exceptionHandler.handleException(testApiError, true, exMap);
		} else if (id % 3 == 0) {
			final TestApiError testApiError = TestApiError.TEST_API_00101;

			final MockResponseObject mockResponseObject = new MockResponseObject(Long.toString(id), "uuid",
					Response.Status.BAD_GATEWAY, testApiError);

			return Response.status(Response.Status.BAD_REQUEST).entity(mockResponseObject).build();
		}

		final MockResponseObject mockResponseObject = new MockResponseObject();
		mockResponseObject.setResponseValue(Long.toString(id));

		return Response.status(Response.Status.OK).entity(mockResponseObject).build();
	}
}
