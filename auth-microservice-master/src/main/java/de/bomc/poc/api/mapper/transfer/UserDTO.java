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

import de.bomc.poc.api.jaxb.LocalDateTimeAdapter;
import org.apache.log4j.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The Data Transfer Object for user data.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserDTO implements Serializable {
    /**
     * The serial UID.
     */
    private static final long serialVersionUID = 5964403449063366540L;
    private static final Logger LOGGER = Logger.getLogger(UserDTO.class);
    private static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    private Long id;
    private String username;
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime expirationDate;

    /**
     * Creates a new instance of <code>UserDTO</code> with default co.
     */
    public UserDTO() {
        LOGGER.debug("UserDTO#co");
    }

    public UserDTO(final String username) {
        LOGGER.debug("UserDTO#co [username=" + username + "]");

        this.username = username;
    }

    public String getUsername() {
        LOGGER.debug("UserDTO#getUsername [username=" + this.username + "]");

        return this.username;
    }

    public void setUsername(final String username) {
        LOGGER.debug("UserDTO#setUsername [username=" + username + "]");

        this.username = username;
    }

    public LocalDateTime getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(final LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getId() {
        LOGGER.debug("UserDTO#getId [id=" + this.id + "]");

        return this.id;
    }

    public void setId(final Long id) {
        LOGGER.debug("UserDTO#setId [id=" + id + "]");

        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((this.username == null) ? 0 : this.username.hashCode());
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

        UserDTO other = (UserDTO)obj;

        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }

        if (this.username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!this.username.equals(other.username)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "UserDTO [id=" + this.id + ", username=" + this.username + ", expirationDate=" + this.expirationDate.format(DateTimeFormatter.ofPattern(UserDTO.DATE_TIME_FORMATTER)) + "]";
    }
}
