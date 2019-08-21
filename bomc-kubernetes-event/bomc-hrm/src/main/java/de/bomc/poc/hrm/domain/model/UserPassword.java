/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bomc.poc.hrm.domain.model.basis.AbstractEntity;

/**
 * Is a representation of an <code>User</code> together with the password.
 * <p>
 * When an <code>User</code> changes her password, the current password is added
 * to a history list of passwords. This is necessary to omit <code>User</code>s
 * from setting formerly used passwords.
 * </p>
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Entity
@Table(name = "T_USER_PASSWORD")
public class UserPassword extends AbstractEntity<UserPassword> implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserPassword.class.getName());
	private static final long serialVersionUID = 1678609250279381615L;

	/* --------------------- columns -------------------------------- */

	/**
	 * {@link User} assigned to this password.
	 */
	@ManyToOne
	private User user;
	/**
	 * Password.
	 */
	@Column(name = "C_PASSWORD")
	private String password;
	/**
	 * Date of the last password change.
	 */
	@Column(name = "C_PASSWORD_CHANGED")
	@OrderBy
	private LocalDateTime passwordChanged = LocalDateTime.now();

	/* --------------------- constructors ---------------------------- */

	/**
	 * Constructor only for the persistence provider.
	 */
	protected UserPassword() {
		// Used by JPA provider.
		
		LOGGER.debug("UserPassword#co");
	}

	/**
	 * Create a new <code>UserPassword</code>.
	 * 
	 * @param user
	 *            The {@link User} to assign
	 * @param password
	 *            The <code>password</code> as String to assign
	 */
	public UserPassword(@NotNull final User user, @NotEmpty final String password) {
		LOGGER.debug("UserPassword#co [user.name=" + user.getUsername() + ", password=" + password + "]");
		
		this.user = user;
		this.password = password;
	}

	/* ----------------------------- methods ------------------------- */

    /**
     * @return the type of this entity.
     */
    @Override
    protected Class<UserPassword> getEntityClass() {
        return UserPassword.class;
    }
    
	/**
	 * Return the {@link User} of this password.
	 * 
	 * @return The {@link User} of this password
	 */
	public User getUser() {
		LOGGER.debug("UserPassword#getUser [user=" + this.user.getUsername() + "]");
		
		return this.user;
	}

	/**
	 * Change the {@link User}.
	 * 
	 * @param user
	 *            The new {@link User}
	 */
	public void setUser(final User user) {
		LOGGER.debug("UserPassword#setUser [user=" + ((user == null) ? null : user.getUsername()) + "]");

		this.user = user;
	}
	
	/**
	 * Return the current password.
	 * 
	 * @return The current password
	 */
	public String getPassword() {
		LOGGER.debug("UserPassword#getPassword [password=" + this.password + "]");
		
		return this.password;
	}

	/**
	 * Return the date of the last password change.
	 * 
	 * @return The date when the password has changed
	 */
	public LocalDateTime getPasswordChanged() {
		LOGGER.debug("UserPasswordgetPasswordChanged [passwordChanged=" + this.passwordChanged + "]");
		
		return passwordChanged;
	}

	/**
	 * Returns a string describing the {@link UserDetails}.
	 *
	 * @return the description of the userDetails.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("UserPassword [id=").append(this.getId())
			.append(", username=").append(this.user.getUsername())
			.append(", password=").append(password)
			.append(", version=").append(this.getVersion())
			.append("]");

		return sb.toString();
	}
}
