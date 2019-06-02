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
package de.bomc.poc.exception.test.unit;

import de.bomc.poc.exception.core.BasisErrorCodeEnum;
import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.ExceptionUtil;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.exception.core.web.ApiErrorResponseObject;
import de.bomc.poc.exception.core.web.WebRuntimeException;
import org.apache.log4j.Logger;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.ws.rs.core.Response;
import java.lang.annotation.Annotation;
import java.util.IllegalFormatException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the ExceptionUtil class.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExceptionUtilTest {

	private static final Logger LOGGER = Logger.getLogger(ExceptionHandlerTest.class);
	private static final String LOG_PREFIX = "ExceptionHandlerTest#";

	@Test
	@SuppressWarnings("null")
	public void test010_exceptionUtilIs_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_exceptionUtilIs_pass");

		try {
			final StringBuffer sb = null;
			sb.append("npe");
		} catch (final NullPointerException npe) {
			assertThat(ExceptionUtil.is(npe, NullPointerException.class), equalTo(true));
		}
	}

	@Test
	public void test020_exceptionUtilIs_pass() {
		LOGGER.debug(LOG_PREFIX + "test020_exceptionUtilIs_pass");

		assertThat(ExceptionUtil.is(null, NullPointerException.class), equalTo(false));
	}

	@Test
	@SuppressWarnings("null")
	public void test030_exceptionUnwrap_pass() {
		LOGGER.debug(LOG_PREFIX + "test030_exceptionUnwrap_pass");

		try {
			final StringBuffer sb = null;
			sb.append("npe");
		} catch (final NullPointerException npe) {
			assertThat(ExceptionUtil.unwrap(npe, NullPointerException.class), equalTo(npe));
		}
	}

	@Test
	public void test040_exceptionUnwrap_pass() {
		LOGGER.debug(LOG_PREFIX + "test040_exceptionUnwrap_pass");

		assertThat(ExceptionUtil.unwrap(null, NullPointerException.class), nullValue());
	}

	@Test
	@SuppressWarnings("null")
	public void test050_exceptionUnwrap_pass() {
		LOGGER.debug(LOG_PREFIX + "test050_exceptionUnwrap_pass");

		try {
			final StringBuffer sb = null;
			sb.append("npe");
		} catch (final NullPointerException npe) {
			assertThat(ExceptionUtil.unwrap(npe, IllegalFormatException.class), nullValue());
		}
	}

	@Test
	public void test060_exceptionUnwrap_pass() {
		LOGGER.debug(LOG_PREFIX + "test060_exceptionUnwrap_pass");

		final WebRuntimeException webRuntimeException = new WebRuntimeException(BasisErrorCodeEnum.CONNECTION_FAILURE_10500);
		final IllegalArgumentException iaeException = new IllegalArgumentException(webRuntimeException);

		assertThat(ExceptionUtil.unwrap(iaeException, WebRuntimeException.class), equalTo(webRuntimeException));
	}

	@Test
	public void test070_exceptionUnwrap_pass() {
		LOGGER.debug(LOG_PREFIX + "test070_exceptionUnwrap_pass");

		final AppRuntimeException appRuntimeException = new AppRuntimeException(BasisErrorCodeEnum.CONNECTION_FAILURE_10500);
		final IllegalArgumentException iaeException = new IllegalArgumentException(appRuntimeException);

		assertThat(ExceptionUtil.unwrap(iaeException, WebRuntimeException.class), nullValue());
	}

	@Test
	public void test080_processErrorResponse_pass() {
		LOGGER.debug(LOG_PREFIX + "test080_processErrorResponse_pass");

		final String uuid = UUID.randomUUID().toString();
		final ErrorCode errorCode = BasisErrorCodeEnum.CONNECTION_FAILURE_10500;

		// prepare testable
		Response testResponse = new MockResponseBuilderImpl().status(Response.Status.INTERNAL_SERVER_ERROR)
				.header(ExceptionUtil.HTTP_HEADER_X_EXCEPTION_UUID, uuid)
				.header(ExceptionUtil.HTTP_HEADER_X_ERROR_CODE, errorCode.getShortErrorCodeDescription())
				.entity(new ApiErrorResponseObject(uuid, Response.Status.INTERNAL_SERVER_ERROR, errorCode)).build();

		Response processedResponse = ExceptionUtil.processErrorResponse(testResponse);

		// asserts
		assertThat(processedResponse, notNullValue());
		assertThat(processedResponse.getEntity(), notNullValue());
		assertThat(processedResponse.getEntity().getClass(), equalTo(ApiErrorResponseObject.class));
		assertThat(processedResponse.getStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
	}

	@Test
	public void test090_processErrorResponseNoEntity_fail() {
		LOGGER.debug(LOG_PREFIX + "test090_processErrorResponseNoEntity_fail");

		final String uuid = UUID.randomUUID().toString();
		final ErrorCode errorCode = BasisErrorCodeEnum.CONNECTION_FAILURE_10500;

		// prepare testable
		Response testResponse = new MockResponseBuilderImpl().status(Response.Status.INTERNAL_SERVER_ERROR)
				.header(ExceptionUtil.HTTP_HEADER_X_EXCEPTION_UUID, uuid)
				.header(ExceptionUtil.HTTP_HEADER_X_ERROR_CODE, errorCode.getShortErrorCodeDescription())
				// .entity(new ApiErrorResponseObject(uuid,
				// Response.Status.INTERNAL_SERVER_ERROR, errorCode))
				.build();

		try {
			ExceptionUtil.processErrorResponse(testResponse);

			Assert.fail("An exception should be thrown!");
		} catch (Exception ex) {

			assertThat(ex.getClass(), equalTo(AppRuntimeException.class));
			assertThat(((AppRuntimeException) ex).getErrorCode(), equalTo(BasisErrorCodeEnum.UNEXPECTED_10000));
		}
	}

	@Test
	public void test100_processErrorResponseNoUuidHeader_fail() {
		LOGGER.debug(LOG_PREFIX + "test100_processErrorResponseNoUuidHeader_fail");

		final String uuid = UUID.randomUUID().toString();
		final ErrorCode errorCode = BasisErrorCodeEnum.CONNECTION_FAILURE_10500;

		// prepare testable
		Response testResponse = new MockResponseBuilderImpl().status(Response.Status.INTERNAL_SERVER_ERROR)
				.header(ExceptionUtil.HTTP_HEADER_X_ERROR_CODE, errorCode.getShortErrorCodeDescription())
				.entity(new ApiErrorResponseObject(uuid, Response.Status.INTERNAL_SERVER_ERROR, errorCode)).build();

		try {
			ExceptionUtil.processErrorResponse(testResponse);

			Assert.fail("An exception should be thrown!");
		} catch (Exception ex) {

			assertThat(ex.getClass(), equalTo(WebRuntimeException.class));
			assertThat(((WebRuntimeException) ex).getErrorCode(), equalTo(BasisErrorCodeEnum.CONNECTION_FAILURE_10500));
		}
	}

	@Test
	public void test110_processErrorResponseNoErrorCodeHeader_fail() {
		LOGGER.debug(LOG_PREFIX + "test110_processErrorResponseNoErrorCodeHeader_fail");

		final String uuid = UUID.randomUUID().toString();
		final ErrorCode errorCode = BasisErrorCodeEnum.CONNECTION_FAILURE_10500;

		// prepare testable
		Response testResponse = new MockResponseBuilderImpl().status(Response.Status.INTERNAL_SERVER_ERROR)
				.header(ExceptionUtil.HTTP_HEADER_X_EXCEPTION_UUID, uuid)
				.entity(new ApiErrorResponseObject(uuid, Response.Status.INTERNAL_SERVER_ERROR, errorCode)).build();

		try {
			ExceptionUtil.processErrorResponse(testResponse);

			Assert.fail("An exception should be thrown!");
		} catch (Exception ex) {

			assertThat(ex.getClass(), equalTo(WebRuntimeException.class));
			assertThat(((WebRuntimeException) ex).getErrorCode(), equalTo(BasisErrorCodeEnum.CONNECTION_FAILURE_10500));
		}
	}

	@Test
	public void test120_processErrorResponseNoErrorResponse_fail() {
		LOGGER.debug(LOG_PREFIX + "test120_processErrorResponseNoErrorResponse_fail");

		// prepare testable
		Response testResponse = new MockResponseBuilderImpl().status(Response.Status.OK).build();

		try {
			ExceptionUtil.processErrorResponse(testResponse);

			Assert.fail("An exception should be thrown!");
		} catch (Exception ex) {

			assertThat(ex.getClass(), equalTo(WebRuntimeException.class));
			assertThat(((WebRuntimeException) ex).getErrorCode(), equalTo(BasisErrorCodeEnum.UNEXPECTED_10000));
		}
	}

	@Test
	public void test130_processErrorResponseNoInternalServerErrorResponse_fail() {
		LOGGER.debug(LOG_PREFIX + "test130_processErrorResponseNoInternalServerErrorResponse_fail");

		// prepare testable
		Response testResponse = new MockResponseBuilderImpl().status(Response.Status.FORBIDDEN).build();

		try {
			ExceptionUtil.processErrorResponse(testResponse);

			Assert.fail("An exception should be thrown!");
		} catch (Exception ex) {

			assertThat(ex.getClass(), equalTo(WebRuntimeException.class));
			assertThat(((WebRuntimeException) ex).getErrorCode(), equalTo(BasisErrorCodeEnum.CONNECTION_FAILURE_10500));
		}
	}

	/* --------------------- Helper Classes ------------------------------ */

	/**
	 * Mock implementation to enable building <code>MockResponse</code> objects.
	 */
	class MockResponseBuilderImpl extends ResponseBuilderImpl {

		public Response.ResponseBuilder status(Response.StatusType status) {
			super.status(status);
			return this;
		}

		public Response build() {
			if (this.status == -1 && this.entity == null) {
				this.status = 204;
			} else if (this.status == -1) {
				this.status = 200;
			}

			return new MockResponse(this.status, this.metadata, this.entity, this.entityAnnotations);
		}
	}

	/**
	 * Mock implementation of <code>BuiltResponse</code> to enable
	 * <code>readEntity</code> method on BuiltResponse, which is not
	 * allowed/possible on <code>BuiltResponse</code> instances.
	 */
	class MockResponse extends BuiltResponse {

		MockResponse(int status, Headers<Object> metadata, Object entity, Annotation[] entityAnnotations) {
			this.setEntity(entity);
			this.status = status;
			this.metadata = metadata;
			this.annotations = entityAnnotations;
		}

		@Override
		public <T> T readEntity(Class<T> type) {
			if (type.isInstance(super.getEntity())) {
				return type.cast(super.getEntity());
			} else {
				throw new ClassCastException();
			} // else
		}
	}
}
