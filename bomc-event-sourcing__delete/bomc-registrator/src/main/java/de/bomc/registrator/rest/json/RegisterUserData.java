package de.bomc.registrator.rest.json;

import java.time.LocalDate;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;

import org.apache.log4j.Logger;

/**
 * Contains registration data.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @since 23.07.2018
 */
public class RegisterUserData {

	// _______________________________________________
	// Member constants.
	// -----------------------------------------------
	private static final String LOG_PREFIX = "RegisterUserData#";
	private static final Logger LOGGER = Logger.getLogger(RegisterUserData.class);
	// _______________________________________________
	// Member variables.
	// -----------------------------------------------
	private String fullname;
	private String address;
	private String username;
	// Nillable = true, enables serialization of null values.
	@JsonbProperty(nillable = true)
	private String telephonNumber;
	@JsonbProperty
	@JsonbDateFormat("dd.MM.yyyy")
	private LocalDate registeredDate;

	@JsonbCreator
	public RegisterUserData(@JsonbProperty("fullname") final String fullname,
			@JsonbProperty("address") final String address, @JsonbProperty("usermname") final String username,
			final String telephonNumber) {
		LOGGER.debug(LOG_PREFIX + "co [fullname=" + fullname + ", address=" + address + ", username=" + username
				+ ", telephonNUmber=" + telephonNumber + "]");

		this.fullname = fullname;
		this.address = address;
		this.username = username;

		this.registeredDate = LocalDate.now();
	}

	/**
	 * @return the fullname
	 */
	public String getFullname() {
		return fullname;
	}

	/**
	 * @param fullname
	 *            the fullname to set
	 */
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegisterUserData other = (RegisterUserData) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RegisterUserData [fullname=" + fullname + ", address=" + address + ", username=" + username
				+ ", telephonNumber=" + telephonNumber + ", registeredDate=" + registeredDate + "]";
	}

	public String toJson() {
		final JsonbConfig config = new JsonbConfig().withFormatting(true);
		
		return JsonbBuilder.newBuilder().withConfig(config).build().toJson(this);
	}
}
