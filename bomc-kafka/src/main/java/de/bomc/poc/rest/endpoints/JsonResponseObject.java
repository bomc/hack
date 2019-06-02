package de.bomc.poc.rest.endpoints;

import javax.json.bind.annotation.JsonbProperty;

public class JsonResponseObject {

	@JsonbProperty
	private String responseString;

	/**
	 * Creates a new instance of <code>JsonResponseObject</code>.
	 * 
	 * @param responseString
	 *            the given string to transform.
	 */
	public JsonResponseObject(String responseString) {
		this.responseString = responseString;
	}

	/**
	 * @return the responseString
	 */
	public String getResponseString() {
		return responseString;
	}

	/**
	 * @param responseString
	 *            the responseString to set
	 */
	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

}
