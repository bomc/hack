package de.bomc.registrator.rest.json;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
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

	@Override
	public String toString() {
		return this.toJson();
	}
	
	public String toJson() {
		final JsonbConfig config = new JsonbConfig().withFormatting(true);
		
		return JsonbBuilder.newBuilder().withConfig(config).build().toJson(this);
	}
}
