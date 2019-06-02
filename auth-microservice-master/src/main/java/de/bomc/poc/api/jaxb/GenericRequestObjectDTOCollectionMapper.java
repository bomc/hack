/**
 * Project: MY_POC
 * <p/>
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
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.api.jaxb;

import de.bomc.poc.api.generic.transfer.request.RequestObjectDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * A mapper class for un- and marshalling of {@link RequestObjectDTO} in a list.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@XmlRootElement(name = "generic-entity")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericRequestObjectDTOCollectionMapper {

	@XmlElement(name = "RequestObjectDTO")
	@XmlElementWrapper(name = "collection")
	private List<RequestObjectDTO> requestObjectDTOList = new ArrayList<>();

	public GenericRequestObjectDTOCollectionMapper() {
		//
	}

	public GenericRequestObjectDTOCollectionMapper(final List<RequestObjectDTO> requestObjectDTOList) {
		this.requestObjectDTOList = new ArrayList<>(requestObjectDTOList);
	}

	public List<RequestObjectDTO> getRequestObjectDTOList() {
		return this.requestObjectDTOList;
	}

	public void setRequestObjectDTOList(final List<RequestObjectDTO> requestObjectDTOList) {
		this.requestObjectDTOList = requestObjectDTOList;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof GenericRequestObjectDTOCollectionMapper)) {
			return false;
		}

		final GenericRequestObjectDTOCollectionMapper that = (GenericRequestObjectDTOCollectionMapper) o;

		return !(this.requestObjectDTOList != null ? !this.requestObjectDTOList.equals(that.requestObjectDTOList)
				: that.requestObjectDTOList != null);
	}

	@Override
	public int hashCode() {
		return this.requestObjectDTOList != null ? this.requestObjectDTOList.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "GenericRequestObjectDTOCollectionMapper [requestObjectDTOList.size=" + this.requestObjectDTOList.size() + "]";
	}
}
