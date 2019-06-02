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
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Data Transfer Object for role data.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RoleDTO implements Serializable {

    /**
     * The serial UID.
     */
    private static final long serialVersionUID = -1011050434680943938L;
    private static final Logger LOGGER = Logger.getLogger(RoleDTO.class);
    private Long id;
    private String name;
    private String description;
    @XmlElement(name = "grantDTO")
    @XmlElementWrapper(name = "grantDTOList")
    @JsonProperty("grantDTO")
    private List<GrantDTO> grantDTOList;

    /**
     * Creates a new instance of <code>RoleDTO</code> with default co.
     */
    public RoleDTO() {
        LOGGER.debug("RoleDTO#co");

        grantDTOList = new ArrayList<GrantDTO>(10);
    }

    public RoleDTO(final String name, final String description) {
        this();

        LOGGER.debug("RoleDTO#co [name=" + name + ", description=" + description + "]");

        this.name = name;
        this.description = description;
    }

    public Long getId() {
        LOGGER.debug("RoleDTO#getId [id=" + this.id + "]");

        return this.id;
    }

    public void setId(final Long id) {
        LOGGER.debug("RoleDTO#setId [id=" + id + "]");

        this.id = id;
    }

    public String getName() {
        LOGGER.debug("RoleDTO#getUsername [name=" + this.name + "]");

        return this.name;
    }

    public void setName(final String name) {
        LOGGER.debug("RoleDTO#setName [name=" + name + "]");

        this.name = name;
    }

    public String getDescription() {
        LOGGER.debug("RoleDTO#getDescription [description=" + this.description + "]");

        return this.description;
    }

    public void setDescription(final String description) {
        LOGGER.debug("RoleDTO#setDescription [description=" + description + "]");

        this.description = description;
    }

    public List<GrantDTO> getGrantDTOList() {
        LOGGER.debug("RoleDTO#getGrantDTOList [grantDTOList.size=" + grantDTOList.size() + "]");

        return grantDTOList;
    }

    public void setGrantDTOList(final List<GrantDTO> grantDTOList) {
        LOGGER.debug("RoleDTO#setGrantDTOList [grantDTOList.size=" + grantDTOList.size() + "]");

        this.grantDTOList = grantDTOList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        RoleDTO other = (RoleDTO)obj;

        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }

        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "RoleDTO [id=" + this.id + ", name=" + this.name + ", description=" + this.description + ", grantDTOList.size=" + this.grantDTOList.size() + "]";
    }
}

