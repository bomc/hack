package de.bomc.poc.exception.test.arq.mock.application;

import javax.ws.rs.core.Response;

import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.web.ApiErrorResponseObject;

/**
 * This response object is using for JAX/RS handling with the
 * {@link ApiErrorResponseObject}.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 */
public class MockResponseObject extends ApiErrorResponseObject {

	private String responseValue;

	public MockResponseObject() {
		//
	}

	public MockResponseObject(final String responseValue) {
		this.responseValue = responseValue;
	}

	public MockResponseObject(final String responseValue, final String uuid, final Response.Status status,
			final ErrorCode errorCode) {
		super(uuid, status, errorCode);

		this.responseValue = responseValue;
	}

	/**
	 * @return the responseValue
	 */
	public String getResponseValue() {
		return responseValue;
	}

	/**
	 * @param responseValue
	 *            the responseValue to set
	 */
	public void setResponseValue(String responseValue) {
		this.responseValue = responseValue;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MockResponseObject [responseValue=" + responseValue + ", [" + super.toString() + "]]";
	}
}
