/**
 * Project: MY_POC
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
package de.bomc.poc.api.mapper.transfer;

import org.apache.log4j.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * The Data Transfer Object for grant data.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GrantDTO implements Serializable {
    /**
	 * The serial UID. 
	 */
	private static final long serialVersionUID = -2288987669335833147L;
	private static final Logger LOGGER = Logger.getLogger(GrantDTO.class);
    private Long id;
    private String name;
    private String description;

    /**
     * Creates a new instance of <code>GrantDTO</code> with default co.
     */
    public GrantDTO() {
        LOGGER.debug("GrantDTO#co");
    }

    public GrantDTO(final String name, final String description) {
        LOGGER.debug("GrantDTO#co [name=" + name + ", description=" + description + "]");

        this.name = name;
        this.description = description;
    }

    public Long getId() {
        LOGGER.debug("UserDTO#getId [id=" + this.id + "]");

        return this.id;
    }

    public void setId(final Long id) {
        LOGGER.debug("UserDTO#setId [id=" + id + "]");

        this.id = id;
    }

    public String getName() {
        LOGGER.debug("GrantDTO#getUsername [name=" + this.name + "]");

        return this.name;
    }

    public void setName(final String name) {
        LOGGER.debug("GrantDTO#setName [name=" + name + "]");

        this.name = name;
    }

    public String getDescription() {
        LOGGER.debug("GrantDTO#getDescription [description=" + this.description + "]");

        return this.description;
    }

    public void setDescription(final String description) {
        LOGGER.debug("GrantDTO#setDescription [description=" + description + "]");

        this.description = description;
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		
		return result;
	}

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GrantDTO other = (GrantDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
    public String toString() {
        return "GrantDTO [id=" + this.id + ", name=" + this.name + ", description=" + this.description + "]";
    }
}
