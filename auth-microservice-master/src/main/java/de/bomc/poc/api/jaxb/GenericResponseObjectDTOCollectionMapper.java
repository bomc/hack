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

import de.bomc.poc.api.generic.transfer.response.ResponseObjectDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * A mapper class for un- and marshalling of {@link ResponseObjectDTO} in a list.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@XmlRootElement(name = "generic-entity")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericResponseObjectDTOCollectionMapper {

	@XmlElement(name = "ResponseObjectDTO")
	@XmlElementWrapper(name = "collection")
	private List<ResponseObjectDTO> responseObjectDTOList = new ArrayList<>();

	public GenericResponseObjectDTOCollectionMapper() {
		//
	}

	public GenericResponseObjectDTOCollectionMapper(final List<ResponseObjectDTO> responseObjectDTOList) {
		this.responseObjectDTOList = new ArrayList<>(responseObjectDTOList);
	}

	public List<ResponseObjectDTO> getResponseObjectDTOList() {
		return this.responseObjectDTOList;
	}

	public void setResponseObjectDTOList(final List<ResponseObjectDTO> responseObjectDTOList) {
		this.responseObjectDTOList = responseObjectDTOList;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof GenericResponseObjectDTOCollectionMapper)) {
			return false;
		}

		final GenericResponseObjectDTOCollectionMapper that = (GenericResponseObjectDTOCollectionMapper) o;

		return !(this.responseObjectDTOList != null ? !this.responseObjectDTOList.equals(that.responseObjectDTOList)
				: that.responseObjectDTOList != null);
	}

	@Override
	public int hashCode() {
		return this.responseObjectDTOList != null ? this.responseObjectDTOList.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "GenericResponseObjectDTOCollectionMapper [responseObjectDTOList.size=" + this.responseObjectDTOList.size() + "]";
	}
}
