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
package de.bomc.poc.api.generic.transfer.response;

import de.bomc.poc.api.jaxb.JaxbGenMapAdapter;
import de.bomc.poc.api.generic.Parameter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The response object for REST responses.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class ResponseObjectDTO implements Serializable {

	private static final long serialVersionUID = -8322713874992761399L;
	@XmlJavaTypeAdapter(JaxbGenMapAdapter.class)
	private Map<String, Parameter> parameters;

	private ResponseObjectDTO() {
		//
		// Used by Jaxb.
	}

	private ResponseObjectDTO(final Parameter parameter) {
		this.parameters = new HashMap<>();
		this.parameters.put(parameter.getName(), parameter);
	}

	public static ResponseObjectDTO with(final Parameter parameter) {
		return new ResponseObjectDTO(parameter);
	}

	public ResponseObjectDTO and(final Parameter parameter) {
		this.parameters.put(parameter.getName(), parameter);

		return this;
	}

	/**
	 * @return a list with all {@link Parameter}.
	 */
	public List<Parameter> parameters() {
		return Collections.unmodifiableList(new ArrayList<>(this.parameters.values()));
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ResponseObjectDTO)) {
			return false;
		}

		final ResponseObjectDTO that = (ResponseObjectDTO) o;

		return !(this.parameters != null ? !this.parameters.equals(that.parameters) : that.parameters != null);
	}

	@Override
	public int hashCode() {
		return this.parameters != null ? this.parameters.hashCode() : 0;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		// Lambda funktioniert nicht mit dem RestEasyProxyFramework, hmm...
		// this.parameters.forEach((k, v) -> sb.append("[key=" + k + ", value="
		// + v + "]"));

		for (final Map.Entry<String, Parameter> entry : this.parameters.entrySet()) {
			sb.append("\t[key=").append(entry.getKey()).append(", value=").append(entry.getValue()).append("]\n");
		}

		return sb.toString();
	}
}
