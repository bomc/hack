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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

import de.bomc.poc.hrm.application.exception.AppErrorCodeEnum;
import de.bomc.poc.hrm.application.exception.AppRuntimeException;
import de.bomc.poc.hrm.domain.model.basis.AbstractEntity;
import de.bomc.poc.hrm.domain.model.validation.constraint.BomcFuture;
import de.bomc.poc.hrm.domain.model.values.CoreTypeDefinitions;
import lombok.ToString;

/**
 * A UserEntity represents a human user of the system. Typically an UserEntity
 * is assigned to one or more {@link RoleEntity}s to define security
 * constraints.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
// LOMBOK
@ToString(exclude = {"roles", "passwords"})
// JPA
@Entity
@Table(name = "t_user", schema = "bomcdb")
@NamedQueries({
		@NamedQuery(name = UserEntity.NQ_FIND_ALL, query = "select u from UserEntity u left join fetch u.roles"),
		@NamedQuery(name = UserEntity.NQ_FIND_ALL_ORDERED, query = "select u from UserEntity u left join fetch u.roles order by u.username"),
		@NamedQuery(name = UserEntity.NQ_FIND_BY_USERNAME, query = "select u from UserEntity u left join fetch u.roles where u.username = ?1"),
		@NamedQuery(name = UserEntity.NQ_FIND_BY_USERNAME_PASSWORD, query = "select u from UserEntity u left join fetch u.roles where u.username = :username and u.persistedPassword = :password") })
public class UserEntity extends AbstractEntity<UserEntity> implements Serializable {

	private static final String LOG_PREFIX = "UserEntity#";
	private static final Logger LOGGER = LoggerFactory.getLogger(UserEntity.class.getName());
	private static final long serialVersionUID = -6309504091081019166L;

	/* --------------------- constants ------------------------------ */

	/**
	 * The number of passwords to be stored in the password history. When an
	 * <code>UserEntity</code> changes the password, the old password is stored in a
	 * Collection. Default: {@value} .
	 */
	public static final short NUMBER_STORED_PASSWORDS = 3;

	/**
	 * The offset if expiration days to calculate the expiration. Default:
	 * {@value} .
	 */
	public static final long EXPIRATION_OFFSET_DAYS = 365;

	/**
	 * The default prefix String for each created <code>UserEntity</code>. Name is *
	 * * * {@value} .
	 */
	public static final String USER_PREFIX = "USER.";

	/**
	 * Query to find all <code>UserEntity</code>s. Name is {@value} .
	 */
	public static final String NQ_FIND_ALL = USER_PREFIX + "findAll";

	/**
	 * Query to find all <code>UserEntity</code>s sorted by userName. Name is
	 * {@value} .
	 */
	public static final String NQ_FIND_ALL_ORDERED = USER_PREFIX + "findAllOrdered";

	/**
	 * Query to find <strong>one</strong> <code>UserEntity</code> by his userName.
	 * <li>Query parameter index <strong>1</strong> : The username of the
	 * <code>UserEntity</code> to search for.</li><br />
	 * Name is {@value} .
	 */
	public static final String NQ_FIND_BY_USERNAME = USER_PREFIX + "findByUsername";

	/**
	 * Query to find <strong>one</strong> <code>UserEntity</code> by his userName
	 * and password.
	 * <li>Query parameter name <strong>username</strong> : The userName of the
	 * <code>UserEntity</code> to search for.</li>
	 * <li>Query parameter name <strong>password</strong> : The current password of
	 * the <code>UserEntity</code> to search for.</li><br />
	 * Name is {@value} .
	 */
	public static final String NQ_FIND_BY_USERNAME_PASSWORD = USER_PREFIX + "findByUsernameAndPassword";

	/* --------------------- columns -------------------------------- */

	/**
	 * Unique identifier of this <code>UserEntity</code> (not nullable).
	 */
	@NotBlank
	@Column(name = "c_username", unique = true, nullable = false)
	@Size(min = CoreTypeDefinitions.MINIMUM_USERNAME_LENGTH, message = "must be minimum " + CoreTypeDefinitions.MINIMUM_USERNAME_LENGTH + " characters!")
	private String username;
	/**
	 * <code>true</code> if the <code>UserEntity</code> is authenticated by an
	 * external system, otherwise <code>false</code>.
	 */
	@Column(name = "c_extern")
	private boolean extern = false;
	/**
	 * Date of the last password change.
	 */
	@Column(name = "c_last_password_change")
	@JsonFormat(pattern = "dd.MM.yyyy HH.mm.ss")
	private LocalDateTime lastPasswordChange;
	/**
	 * <code>true</code> if this <code>UserEntity</code> is locked and has not the
	 * permission to login anymore. This field is set by the backend application,
	 * e.g. when the expirationDate of the account expires.
	 */
	@Column(name = "c_locked")
	private boolean locked = false;
	/**
	 * The <code>UserEntity</code>'s password.
	 */
	@Transient
	private String password;
	/**
	 * The <code>UserEntity</code>'s password.
	 */
	@Column(name = "c_password")
	private String persistedPassword;
	/**
	 * <code>true</code> if the <code>UserEntity</code> is enabled. This field can
	 * be managed by the UI application to lock an UserEntity manually.
	 */
	@Column(name = "c_enabled")
	private boolean enabled = true;
	/**
	 * Date when the account expires. After account expiration, the
	 * <code>UserEntity</code> cannot login anymore.
	 */
	@BomcFuture
	@Column(name = "c_expiration_date")
	@JsonFormat(pattern = "dd.MM.yyyy HH.mm.ss")
	private LocalDateTime expirationDate = LocalDateTime.now().plusDays(EXPIRATION_OFFSET_DAYS);
	/**
	 * The <code>UserEntity</code>s fullname. Doesn't have to be unique.
	 */
	@NotBlank
	@Size(min = CoreTypeDefinitions.MINIMUM_FULLNAME_LENGTH, max = CoreTypeDefinitions.MAXIMUM_FULLNAME_LENGTH, message = "must be between "
			+ CoreTypeDefinitions.MINIMUM_FULLNAME_LENGTH + " and " + CoreTypeDefinitions.MAXIMUM_FULLNAME_LENGTH
			+ " characters!")
	@Column(name = "c_fullname")
	private String fullname;

	/* ------------------- embedded mapping ------------------- */
	/**
	 * More detail information of the <code>UserEntity</code>.
	 */
	@Embedded
	private UserDetailsEntity userDetailsEntity = new UserDetailsEntity();

	/* ------------------- collection mapping ------------------- */
	/**
	 * List of {@link RoleEntity}s assigned to the <code>UserEntity</code>. In a JPA
	 * context eager loaded.
	 * 
	 * @see javax.persistence.FetchType#EAGER
	 */
	@OrderBy("name ASC")
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "users", cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	private Set<RoleEntity> roles = new HashSet<RoleEntity>();

	/**
	 * Password history of the <code>UserEntity</code>.
	 * 
	 * <pre>
	 * bomc-NOTE: For many-to-one and one-to-one associations, as 
	 * well as to at most one one-to-many relationship,
	 * JOIN FETCH directive is the best way of initializing 
	 * the associations that are going to be needed in the 
	 * view layer.
	 * see https://vladmihalcea.com/the-open-session-in-view-anti-pattern/
	 * </pre>
	 * 
	 */
	@JoinColumn(name = "c_user_join")
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<UserPasswordEntity> passwords = new ArrayList<UserPasswordEntity>();

	/* ----------------------------- constructors ------------------- */
	/**
	 * Accessed by persistence provider.
	 */
	public UserEntity() {
		// Accessed by the jpa provider.
		LOGGER.debug(LOG_PREFIX + "co");

		this.loadLazy();
	}

	/**
	 * Create a new <code>UserEntity</code> with an username.
	 * 
	 * @param username The unique name of the user
	 */
	public UserEntity(final String username) {
		LOGGER.debug(LOG_PREFIX + "co [username=" + username + "]");

		this.username = username;
		this.loadLazy();
	}

	/**
	 * Create a new <code>UserEntity</code> with an username.
	 * 
	 * @param username The unique name of the user
	 * @param password The password of the user
	 */
	protected UserEntity(final String username, final String password) {
		LOGGER.debug(LOG_PREFIX + "co [username=" + username + ", password=" + password + "]");

		this.username = username;
		this.password = password;
	}

	/* ----------------------------- methods ------------------- */

	/**
	 * @return the type of this entity.
	 */
	@Override
	protected Class<UserEntity> getEntityClass() {
		return UserEntity.class;
	}

	@PreRemove
	public void preRemove() {
		LOGGER.debug(LOG_PREFIX + "preRemove");

		// Remove the referenced roles.
		this.removeRoles();
	}

	/**
	 * After load, the saved password is copied to the transient one. The transient
	 * one can be overridden by the application to force a password change.
	 */
	@PostLoad
	public void postLoad() {
		LOGGER.debug(LOG_PREFIX + "postLoad");

		this.loadLazy();
	}

	private void loadLazy() {
		LOGGER.debug(LOG_PREFIX + "loadLazy [persistedPassword=" + this.persistedPassword + "]");

		this.password = this.persistedPassword;
	}

	/**
	 * Return the unique username of the <code>UserEntity</code>.
	 * 
	 * @return The current username
	 */
	public String getUsername() {
		LOGGER.debug(LOG_PREFIX + "getUsername [username=" + this.username + "]");

		return this.username;
	}

	/**
	 * Change the username of the <code>UserEntity</code>.
	 * 
	 * @param username The new username to set
	 */
	public /*
			 * protected: is set to public, because mapstruct is using getter and setter
			 */ void setUsername(final String username) {
		LOGGER.debug(LOG_PREFIX + "setUsername [username=" + username + "]");

		this.username = username;
	}

	/**
	 * Check if the <code>UserEntity</code> is locked.
	 * 
	 * @return <code>true</code> if locked, otherwise <code>false</code>
	 */
	public boolean isLocked() {
		LOGGER.debug(LOG_PREFIX + "isLocked [locked=" + this.locked + "]");

		return this.locked;
	}

	/**
	 * LockQualifier the <code>UserEntity</code>.
	 * 
	 * @param locked <code>true</code> to lock the <code>UserEntity</code>,
	 *               <code>false</code> to unlock
	 */
	public void setLocked(final boolean locked) {
		LOGGER.debug(LOG_PREFIX + "setLocked [locked=" + locked + "]");

		this.locked = locked;
	}

	/**
	 * Is the <code>UserEntity</code> authenticated by an external system?
	 * 
	 * @return <code>true</code> if so, otherwise <code>false</code>
	 */
	public boolean isExternalUser() {
		LOGGER.debug(LOG_PREFIX + "isExternalUser [extern=" + this.extern + "]");

		return this.extern;
	}

	/**
	 * Change the authentication method of the <code>UserEntity</code>.
	 * 
	 * @param externalUser <code>true</code> if the <code>UserEntity</code> was
	 *                     authenticated by an external system, otherwise
	 *                     <code>false</code>.
	 */
	public void setExternalUser(final boolean externalUser) {
		LOGGER.debug(LOG_PREFIX + "setExternalUser [extern=" + externalUser + "]");

		this.extern = externalUser;
	}

	/**
	 * Determines whether the <code>UserEntity</code> is enabled or not.
	 * 
	 * @return <code>true</code> if the <code>UserEntity</code> is enabled,
	 *         otherwise <code>false</code>
	 */
	public boolean isEnabled() {
		LOGGER.debug(LOG_PREFIX + "isEnabled [enabled=" + this.enabled + "]");

		return this.enabled;
	}

	/**
	 * Enable or disable the <code>UserEntity</code>.
	 * 
	 * @param enabled <code>true</code> when enabled, otherwise <code>false</code>
	 */
	public void setEnabled(final boolean enabled) {
		LOGGER.debug(LOG_PREFIX + "setEnabled [enabled=" + enabled + "]");

		this.enabled = enabled;
	}

	/**
	 * Return the date when the account expires.
	 * 
	 * @return The expiration date
	 */
	public LocalDateTime getExpirationDate() {
		LOGGER.debug(LOG_PREFIX + "getExpirationDate [expirationDate="
				+ this.expirationDate /* this.formatLocalDateTime(expirationDate) */ + "]");

		return expirationDate;
	}

	/**
	 * Change the date when the account expires.
	 * 
	 * @param expDate The new expiration date to set
	 */
	public void setExpirationDate(@NotNull final LocalDateTime expDate) {
		LOGGER.debug(
				LOG_PREFIX + "setExpirationDate [expDate=" + expDate /* this.formatLocalDateTime(expDate) */ + "]");

		this.expirationDate = expDate;
	}

	/**
	 * Returns a list of granted {@link RoleEntity}s.
	 * 
	 * @return The list of granted {@link RoleEntity}s
	 */
	public Set<RoleEntity> getRoles() {
		LOGGER.debug(LOG_PREFIX + "getRoles [roles.size=" + this.roles.size() + "]");

		return Collections.unmodifiableSet(this.roles);
	}

	/**
	 * Add a new {@link RoleEntity} to the list of {@link RoleEntity}s.
	 * 
	 * @param roleEntity The new {@link RoleEntity} to add
	 * @return see {@link java.util.Collection#add(Object)}
	 */
	public boolean addRole(@NotNull final RoleEntity roleEntity) {
		LOGGER.debug(LOG_PREFIX + "addRole [role.name=" + roleEntity.getName() + "]");

		final boolean isAdded = this.roles.add(roleEntity);

		// This is a bidirectional relationship, so the relationship has to be
		// added also on the other side.
		final boolean isInternalAdded = roleEntity.internalAddUser(this);

		return isAdded && isInternalAdded;
	}

	/**
	 * Remove a {@link RoleEntity} from the <code>UserEntity</code>.
	 * 
	 * @param roleEntity The {@link RoleEntity} to remove.
	 * @return true if this set contained the specified element.
	 */
	public boolean removeRole(@NotNull final RoleEntity roleEntity) {
		LOGGER.debug(LOG_PREFIX + "removeRole [" + roleEntity.toString() + "]");

		// This is a bidirectional relationship, so the relationship has to be
		// removed also on the other side.
		final boolean isInternalRemove = roleEntity.internalRemoveUser(this);
		// Remove role.
		final boolean isRemove = this.roles.remove(roleEntity);

		return isInternalRemove && isRemove;
	}

	/**
	 * Add a {@link RoleEntity} to the <code>UserEntity</code>. The relationship is
	 * bidirectional, the relationship has to be added manually on the other side.
	 * 
	 * @param roleEntity The {@link RoleEntity} to add.
	 */
	protected boolean internalAddRole(@NotNull final RoleEntity roleEntity) {
		LOGGER.debug(LOG_PREFIX + "internalAddRole [" + roleEntity.toString() + "]");

		final boolean isAdded = this.roles.add(roleEntity);

		return isAdded;
	}

	/**
	 * Remove a {@link RoleEntity} from <code>UserEntity</code> for internal use
	 * package scope.
	 * 
	 * @param roleEntity The {@link RoleEntity} to remove.
	 */
	protected boolean internalRemoveRole(@NotNull final RoleEntity roleEntity) {
		LOGGER.debug("RoleEntity#internalRemoveRole [" + roleEntity.toString() + "]");

		final boolean isInternalRemoved = this.roles.remove(roleEntity);

		return isInternalRemoved;
	}

	/**
	 * Remove all {@link RoleEntity}s from this <code>UserEntity</code>.
	 */
	public void removeRoles() {
		LOGGER.debug(LOG_PREFIX + "removeRoles");

		Iterator<RoleEntity> itr = this.roles.iterator();

		while (itr.hasNext()) {
			final RoleEntity roleEntity = itr.next();

			final boolean isInternalRemove = roleEntity.internalRemoveUser(this);
			LOGGER.debug(LOG_PREFIX + "removeRoles [user.id=" + roleEntity.getId() + ", internalRemove="
					+ isInternalRemove + "]");

			itr.remove();
		}
	}

	/**
	 * Set the {@link RoleEntity}s of this <code>UserEntity</code>. Existing
	 * {@link RoleEntity}s will be not overridden.
	 * 
	 * @param roles The new list of {@link RoleEntity}s
	 */
	public void setRoles(final Set<RoleEntity> roles) {
		LOGGER.debug(LOG_PREFIX + "setRoles [roles.size=" + roles.size() + "]");

		roles.stream().forEach(role -> {
			this.roles.add(role);
			role.internalAddUser(this);
		});
	}

	/**
	 * Return the fullname of the <code>UserEntity</code>.
	 * 
	 * @return The current fullname
	 */
	public String getFullname() {
		LOGGER.debug(LOG_PREFIX + "getFullname [fullname=" + this.fullname + "]");

		return this.fullname;
	}

	/**
	 * Change the fullname of the <code>UserEntity</code>.
	 * 
	 * @param fullname The new fullname to set
	 */
	public void setFullname(final String fullname) {
		LOGGER.debug(LOG_PREFIX + "setFullname [fullname=" + this.fullname + "]");

		this.fullname = fullname;
	}

	/**
	 * Return the details of the <code>UserEntity</code>.
	 * 
	 * @return The userDetailsEntity
	 */
	public UserDetailsEntity getUserDetails() {
		LOGGER.debug(LOG_PREFIX + "getUserDetails [userDetailsEntity=" + this.userDetailsEntity.toString() + "]");

		return this.userDetailsEntity;
	}

	/**
	 * Assign some details to the <code>UserEntity</code>.
	 * 
	 * @param userDetailsEntity The userDetailsEntity to set
	 */
	public void setUserDetails(final UserDetailsEntity userDetails) {
		LOGGER.debug(LOG_PREFIX + "setUserDetails [userDetailsEntity=" + this.userDetailsEntity.toString() + "]");

		this.userDetailsEntity = userDetails;
	}

	/**
	 * Flatten {@link RoleEntity}s and {@link PermissionEntity}s and return an
	 * unmodifiable list of all {@link PermissionEntity}s assigned to this
	 * <code>UserEntity</code>.
	 * 
	 * @return A list of all {@link PermissionEntity}s
	 */
	public List<PermissionEntity> getPermissions() {
		List<PermissionEntity> permissions = new ArrayList<>();

		for (RoleEntity roleEntity : this.getRoles()) {
			permissions.addAll(roleEntity.getPermissions());
		}

		LOGGER.debug(LOG_PREFIX + "getPermissions [permissions.size=" + permissions.size() + "]");

		return Collections.unmodifiableList(permissions);
	}

	/**
	 * Return a list of recently used passwords.
	 * 
	 * @return A list of recently used passwords
	 */
	public List<UserPasswordEntity> getPasswords() {
		LOGGER.debug(LOG_PREFIX + "getPasswords [passwords.size=" + this.passwords.size() + "]");

		return this.passwords;
	}

	/**
	 * Returns the current password of the <code>UserEntity</code>.
	 * 
	 * @return The current password as String
	 */
	public String getPassword() {
		LOGGER.debug(LOG_PREFIX + "getPassword [password=" + this.password + "]");

		return this.password;
	}

	/**
	 * Return the date when the password has changed recently.
	 * 
	 * @return The date when the password has changed recently
	 */
	public LocalDateTime getLastPasswordChange() {
		LOGGER.debug(LOG_PREFIX + "getLastPassworChange [lastPasswordChange=" + this.lastPasswordChange + "]");

		return this.lastPasswordChange;
	}

	/**
	 * Checks if the new password is a valid and change the password of this
	 * <code>UserEntity</code>.
	 * 
	 * @param password The new password of this <code>UserEntity</code>
	 * @throws AppInvalidPasswordException in case changing the password is not
	 *                                     allowed or the new password is not valid
	 */
	public void setNewPassword(final String password) {
		LOGGER.debug(LOG_PREFIX + "setNewPassword [password=" + password + "]");

		if (this.persistedPassword != null && this.persistedPassword.equals(password)) {
			final String errorMessage = "Trying to set the new password equals to the current password.";

			final AppRuntimeException appRuntimeException = new AppRuntimeException(errorMessage,
					AppErrorCodeEnum.APP_INVALID_PASSWORD_10606);
			LOGGER.error(appRuntimeException.stackTraceToString());

			throw appRuntimeException;
		}

		if (this.isPasswordValid(password)) {
			this.storeOldPassword(password);
			this.persistedPassword = password;
			this.password = password;
			this.lastPasswordChange = LocalDateTime.now();
		} else {
			final String errorMessage = "Password confirms not the defined rules.";

			final AppRuntimeException appRuntimeException = new AppRuntimeException(errorMessage,
					AppErrorCodeEnum.APP_PASSWORD_CONFIRMS_NOT_RULES_10607);
			LOGGER.error(appRuntimeException.stackTraceToString());

			throw appRuntimeException;
		}
	}

	/**
	 * Checks whether the password is going to change.
	 * 
	 * @return <code>true</code> when <code>password</code> is different to the
	 *         originally persisted one, otherwise <code>false</code>
	 */
	public boolean hasPasswordChanged() {
		boolean hasPasswordChanged = false;

		if (this.persistedPassword != null) {
			hasPasswordChanged = (this.persistedPassword.equals(this.password));
			LOGGER.debug(LOG_PREFIX + "hasPasswordChanged [hasPasswordChanged=" + hasPasswordChanged + "]");
		} else {
			LOGGER.warn(LOG_PREFIX
					+ "hasPasswordChanged - persistedPassword == null, is a test running? [hasPasswordChanged=false]");
		}

		return hasPasswordChanged;
	}

	/**
	 * Check whether the new password is in the history of former passwords.
	 * 
	 * @param password The password to verify
	 * @return <code>true</code> if the password is valid, otherwise
	 *         <code>false</code>
	 */
	protected boolean isPasswordValid(final String password) {
		boolean isPasswordValid = true;

		if (this.passwords.contains(new UserPasswordEntity(this, password))) {
			isPasswordValid = false;
		}

		LOGGER.debug(
				LOG_PREFIX + "isPasswordValid [password=" + password + ", isPasswordValid=" + isPasswordValid + "]");

		return isPasswordValid;
	}

	private void storeOldPassword(final String passwordToStore) {
		if (passwordToStore == null || passwordToStore.isEmpty()) {
			LOGGER.debug(LOG_PREFIX
					+ "storeOldPassword - The old password is null or empty, it would not be stored in history.");

			return;
		}

		this.passwords.add(new UserPasswordEntity(this, passwordToStore));

		LOGGER.debug(LOG_PREFIX + "storeOldPassword [passwords.size=" + this.passwords.size() + ", passwordToStore="
				+ passwordToStore + "]");

		if (NUMBER_STORED_PASSWORDS < this.passwords.size()) {
			// Get the oldest password from list.
			final java.util.Optional<UserPasswordEntity> optionalUserPassword = passwords.stream()
					.sorted((userPassword1, userPassword2) -> userPassword1.getPasswordChanged()
							.compareTo(userPassword2.getPasswordChanged()))
					.findFirst();

			if (optionalUserPassword.isPresent()) {
				final UserPasswordEntity oldestUserPassword = optionalUserPassword.get();
				oldestUserPassword.setUser(null);
				this.passwords.remove(oldestUserPassword);

				LOGGER.debug(
						LOG_PREFIX + "storeOldPassword# - after remove password from password list. [passwordToRemove="
								+ oldestUserPassword.getPassword() + ", passwords.size=" + this.passwords.size() + "]");
			} else {
				final String errorMessage = "The oldest password could not be determined!";

				final AppRuntimeException appRuntimeException = new AppRuntimeException(errorMessage,
						AppErrorCodeEnum.APP_PASSWORD_OLDEST_PASSWORD_NOT_DETERMIND_10608);
				LOGGER.error(appRuntimeException.stackTraceToString());

				throw appRuntimeException;
			}
		}
	}
}
