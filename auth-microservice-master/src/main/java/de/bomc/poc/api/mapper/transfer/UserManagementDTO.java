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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A Data Transfer Object for compact user data.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserManagementDTO implements Serializable {
	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 6913885377091913475L;
	private static final Logger LOGGER = Logger.getLogger(UserManagementDTO.class); 
	private UserDTO userDTO;
    @XmlElement(name = "roleDTO")
    @XmlElementWrapper(name = "roleListDTO")
    @JsonProperty("roleDTO")
	private final List<RoleDTO> roleListDTO;
    private String userExpirationDate;
    
	/**
	 * Creates a new instance of <code>UserManagementDTO</code> with default co.
	 */
	public UserManagementDTO() {
		LOGGER.debug("UserManagementDTO#co");

		this.roleListDTO = new ArrayList<RoleDTO>(10);
	}

	/**
	 * Creates a new instance of <code>UserManagementDTO<code>.
	 * 
	 * @param userDTO
	 *            the given userDTO.
	 */
	public UserManagementDTO(final UserDTO userDTO) {
		this();

		LOGGER.debug("UserManagementDTO#co [username=" + userDTO.getUsername() + "]");

		this.userDTO = userDTO;
	}

	/**
	 * Creates a new instance of <code>UserManagementDTO<code>.
	 *
	 * @param userDTO
	 *            the given userDTO.
	 * @param roleListDTO
	 *            the given roleListDTO.
	 */
	public UserManagementDTO(final UserDTO userDTO, final List<RoleDTO> roleListDTO) {
		this(userDTO);

		LOGGER.debug("UserManagementDTO#co [username=" + userDTO.getUsername() + ", role.size="
				+ roleListDTO.size() + "]");

		this.roleListDTO.addAll(roleListDTO);
	}	

	public UserDTO getUserDTO() {
		LOGGER.debug("UserManagementDTO#getUserDTO [username=" + this.userDTO.getUsername() + "]");

		return this.userDTO;
	}

	public void setUserDTO(final UserDTO userDTO) {
		LOGGER.debug("UserManagementDTO#setUserDTO [username=" + userDTO.getUsername() + "]");

		this.userDTO = userDTO;
	}

	public List<RoleDTO> getRoleListDTO() {
		LOGGER.debug("UserManagementDTO#getRoleListDTO [roleListDTO.size=" + this.roleListDTO.size() + "]");

		return this.roleListDTO;
	}

	public void setRoleListDTO(final List<RoleDTO> roleListDTO) {
		LOGGER.debug("UserManagementDTO#setRoleListDTO [roleListDTO.size=" + roleListDTO.size() + "]");

		this.roleListDTO.addAll(roleListDTO);
	}

	public String getUserExpirationDate() {
		return userExpirationDate;
	}

	public void setUserExpirationDate(final String userExpirationDate) {
		LOGGER.debug("UserManagementDTO#setUserExpirationDate [userExpirationDate=" + userExpirationDate + "]");
		this.userExpirationDate = userExpirationDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((userDTO == null) ? 0 : userDTO.hashCode());

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
		UserManagementDTO other = (UserManagementDTO) obj;
		if (userDTO == null) {
			if (other.userDTO != null) {
				return false;
			}
		} else if (!userDTO.equals(other.userDTO)) {
			return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		return "UserManagementDTO ["
				+ "username=" + userDTO.getUsername() 
				+ ", userExpirationDate=" + userExpirationDate
				+ ", roleListDTO.size=" + roleListDTO.size() + "]";
	}

}

