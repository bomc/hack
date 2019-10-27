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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

import de.bomc.poc.hrm.domain.model.basis.AbstractEntity;
import lombok.ToString;

/**
 * Is a representation of an <code>UserEntity</code> together with the password.
 * <p>
 * When an <code>UserEntity</code> changes her password, the current password is added
 * to a history list of passwords. This is necessary to omit <code>UserEntity</code>s
 * from setting formerly used passwords.
 * </p>
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
// LOMBOK
@ToString
// JPA
@Entity
@Table(name = "t_user_password", schema = "bomcdb")
public class UserPasswordEntity extends AbstractEntity<UserPasswordEntity> implements Serializable {

	private static final String LOG_PREFIX = "UserPasswordEntity#";
	private static final Logger LOGGER = LoggerFactory.getLogger(UserPasswordEntity.class.getName());
	private static final long serialVersionUID = 1678609250279381615L;

	/* --------------------- columns -------------------------------- */

	/**
	 * {@link UserEntity} assigned to this password.
	 */
	@ManyToOne
	@JoinColumn(name = "c_user_join", insertable = false, updatable = false)
	private UserEntity userEntity;
	/**
	 * Password.
	 */
	@Column(name = "c_password")
	private String password;
	/**
	 * Date of the last password change.
	 */
	@OrderBy
	@Column(name = "c_password_changed")
	@JsonFormat(pattern="dd.MM.yyyy HH.mm.ss")
	private LocalDateTime passwordChanged = LocalDateTime.now();

	/* --------------------- constructors ---------------------------- */

	/**
	 * Constructor only for the persistence provider.
	 */
	protected UserPasswordEntity() {
		// Used by JPA provider.
		
		LOGGER.debug(LOG_PREFIX + "co");
	}

	/**
	 * Create a new <code>UserPasswordEntity</code>.
	 * 
	 * @param userEntity
	 *            The {@link UserEntity} to assign
	 * @param password
	 *            The <code>password</code> as String to assign
	 */
	public UserPasswordEntity(@NotNull final UserEntity userEntity, @NotEmpty final String password) {
		LOGGER.debug(LOG_PREFIX + "co [userEntity.name=" + userEntity.getUsername() + ", password=" + password + "]");
		
		this.userEntity = userEntity;
		this.password = password;
	}

	/* ----------------------------- methods ------------------------- */

    /**
     * @return the type of this entity.
     */
    @Override
    protected Class<UserPasswordEntity> getEntityClass() {
        return UserPasswordEntity.class;
    }
    
	/**
	 * Return the {@link UserEntity} of this password.
	 * 
	 * @return The {@link UserEntity} of this password
	 */
	public UserEntity getUser() {
		LOGGER.debug(LOG_PREFIX + "getUser [userEntity=" + this.userEntity.getUsername() + "]");
		
		return this.userEntity;
	}

	/**
	 * Change the {@link UserEntity}.
	 * 
	 * @param userEntity
	 *            The new {@link UserEntity}
	 */
	public void setUser(final UserEntity userEntity) {
		LOGGER.debug(LOG_PREFIX + "setUser [userEntity=" + ((userEntity == null) ? null : userEntity.getUsername()) + "]");

		this.userEntity = userEntity;
	}
	
	/**
	 * Return the current password.
	 * 
	 * @return The current password
	 */
	public String getPassword() {
		LOGGER.debug(LOG_PREFIX + "getPassword [password=" + this.password + "]");
		
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

}
