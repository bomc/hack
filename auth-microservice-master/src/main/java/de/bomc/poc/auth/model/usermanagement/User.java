/**
 * Project: MY_POC_MICROSERVICE
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
package de.bomc.poc.auth.model.usermanagement;

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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.bomc.poc.exception.app.AppInvalidPasswordException;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotBlank;
import de.bomc.poc.auth.model.AbstractEntity;
import de.bomc.poc.auth.model.validation.constraint.BomcFuture;

/**
 * A User represents a human user of the system. Typically an User is assigned
 * to one or more {@link Role}s to define security constraints.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Entity
@Table(name = "T_USER")
@NamedQueries({ @NamedQuery(name = User.NQ_FIND_ALL, query = "select u from User u left join fetch u.roles"),
		@NamedQuery(name = User.NQ_FIND_ALL_ORDERED, query = "select u from User u left join fetch u.roles order by u.username"),
		@NamedQuery(name = User.NQ_FIND_BY_USERNAME, query = "select u from User u left join fetch u.roles where u.username = ?1"),
		@NamedQuery(name = User.NQ_FIND_BY_USERNAME_PASSWORD, query = "select u from User u left join fetch u.roles where u.username = :username and u.persistedPassword = :password") })
public class User extends AbstractEntity implements Serializable {

	private static final long serialVersionUID = -6309504091081019166L;

	private static final Logger LOGGER = Logger.getLogger(User.class);

	/* --------------------- constants ------------------------------ */

	/**
	 * The default prefix String for each created <code>User</code>. Name is * *
	 * * {@value} .
	 */
	public static final String USER_PREFIX = "USER.";

	/**
	 * Query to find all <code>User</code>s. Name is {@value} .
	 */
	public static final String NQ_FIND_ALL = USER_PREFIX + "findAll";

	/**
	 * Query to find all <code>User</code>s sorted by userName. Name is
	 * {@value} .
	 */
	public static final String NQ_FIND_ALL_ORDERED = USER_PREFIX + "findAllOrdered";

	/**
	 * Query to find <strong>one</strong> <code>User</code> by his userName.
	 * <li>Query parameter index <strong>1</strong> : The username of the
	 * <code>User</code> to search for.</li><br />
	 * Name is {@value} .
	 */
	public static final String NQ_FIND_BY_USERNAME = USER_PREFIX + "findByUsername";

	/**
	 * Query to find <strong>one</strong> <code>User</code> by his userName and
	 * password.
	 * <li>Query parameter name <strong>username</strong> : The userName of the
	 * <code>User</code> to search for.</li>
	 * <li>Query parameter name <strong>password</strong> : The current password
	 * of the <code>User</code> to search for.</li><br />
	 * Name is {@value} .
	 */
	public static final String NQ_FIND_BY_USERNAME_PASSWORD = USER_PREFIX + "findByUsernameAndPassword";

	/**
	 * The number of passwords to be stored in the password history. When an
	 * <code>User</code> changes the password, the old password is stored in a
	 * Collection. Default: {@value} .
	 */
	public static final short NUMBER_STORED_PASSWORDS = 3;

	/* --------------------- columns -------------------------------- */

	/**
	 * Unique identifier of this <code>User</code> (not nullable).
	 */
	@NotBlank
	@Column(name = "C_USERNAME", unique = true, nullable = false)
	@Size(min = 5)
	private String username;
	/**
	 * <code>true</code> if the <code>User</code> is authenticated by an
	 * external system, otherwise <code>false</code>.
	 */
	@Column(name = "C_EXTERN")
	private boolean extern = false;
	/**
	 * Date of the last password change.
	 */
	@Column(name = "C_LAST_PASSWORD_CHANGE")
	private LocalDateTime lastPasswordChange;
	/**
	 * <code>true</code> if this <code>User</code> is locked and has not the
	 * permission to login anymore. This field is set by the backend
	 * application, e.g. when the expirationDate of the account expires.
	 */
	@Column(name = "C_LOCKED")
	private boolean locked = false;
	/**
	 * The <code>User</code>'s password.
	 */
	@Transient
	private String password;
	/**
	 * The <code>User</code>'s password.
	 */
	@Column(name = "C_PASSWORD")
	private String persistedPassword;
	/**
	 * <code>true</code> if the <code>User</code> is enabled. This field can be
	 * managed by the UI application to lock an User manually.
	 */
	@Column(name = "C_ENABLED")
	private boolean enabled = true;
	/**
	 * Date when the account expires. After account expiration, the
	 * <code>User</code> cannot login anymore.
	 */
	@BomcFuture
	@Column(name = "C_EXPIRATION_DATE")
	private LocalDateTime expirationDate;
	/**
	 * The <code>User</code>s fullname. Doesn't have to be unique.
	 */
	@Column(name = "C_FULLNAME")
	private String fullname;

	/* ------------------- collection mapping ------------------- */
	/**
	 * More detail information of the <code>User</code>.
	 */
	@Embedded
	private UserDetails userDetails = new UserDetails();

	/**
	 * List of {@link Role}s assigned to the <code>User</code>. In a JPA context
	 * eager loaded.
	 * 
	 * @see javax.persistence.FetchType#EAGER
	 */
	@OrderBy("name ASC")
	@ManyToMany(mappedBy = "users", cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	private Set<Role> roles = new HashSet<Role>();

	/**
	 * Password history of the <code>User</code>.
	 */
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH })
	@JoinTable(name = "T_USER_PASSWORD_JOIN", joinColumns = @JoinColumn(name = "USER_ID") , inverseJoinColumns = @JoinColumn(name = "PASSWORD_ID") )
	private List<UserPassword> passwords = new ArrayList<UserPassword>();

	/* ----------------------------- constructors ------------------- */
	/**
	 * Accessed by persistence provider.
	 */
	protected User() {
		// Accessed by the jpa provider.
		LOGGER.debug("User#co");

		this.loadLazy();
	}

	/**
	 * Create a new <code>User</code> with an username.
	 * 
	 * @param username
	 *            The unique name of the user
	 */
	public User(final String username) {
		LOGGER.debug("User#co [username=" + username + "]");

		this.username = username;
		this.loadLazy();
	}

	/**
	 * Create a new <code>User</code> with an username.
	 * 
	 * @param username
	 *            The unique name of the user
	 * @param password
	 *            The password of the user
	 */
	protected User(final String username, final String password) {
		LOGGER.debug("User#co [username=" + username + ", password=" + password + "]");

		this.username = username;
		this.password = password;
	}

	/* ----------------------------- methods ------------------- */
	
	@PreRemove
	public void preRemove() {
		LOGGER.debug("User#preRemove");
		
		// Remove the referenced roles.
		this.removeRoles();
	}
	
	/**
	 * After load, the saved password is copied to the transient one. The
	 * transient one can be overridden by the application to force a password
	 * change.
	 */
	@PostLoad
	public void postLoad() {
		LOGGER.debug("User#postLoad");

		this.loadLazy();
	}

	private void loadLazy() {
		LOGGER.debug("User#loadLazy [persistedPassword=" + this.persistedPassword + "]");

		this.password = this.persistedPassword;
	}

	/**
	 * Return the unique username of the <code>User</code>.
	 * 
	 * @return The current username
	 */
	public String getUsername() {
		LOGGER.debug("User#getUsername [username=" + this.username + "]");

		return this.username;
	}

	/**
	 * Change the username of the <code>User</code>.
	 * 
	 * @param username
	 *            The new username to set
	 */
	public /*
			 * protected: is set to public, because mapstruct is using getter
			 * and setter
			 */ void setUsername(final String username) {
		LOGGER.debug("User#setUsername [username=" + username + "]");

		this.username = username;
	}

	/**
	 * Check if the <code>User</code> is locked.
	 * 
	 * @return <code>true</code> if locked, otherwise <code>false</code>
	 */
	public boolean isLocked() {
		LOGGER.debug("User#isLocked [locked=" + this.locked + "]");

		return this.locked;
	}

	/**
	 * LockQualifier the <code>User</code>.
	 * 
	 * @param locked
	 *            <code>true</code> to lock the <code>User</code>,
	 *            <code>false</code> to unlock
	 */
	public void setLocked(final boolean locked) {
		LOGGER.debug("User#setLocked [locked=" + locked + "]");

		this.locked = locked;
	}

	/**
	 * Is the <code>User</code> authenticated by an external system?
	 * 
	 * @return <code>true</code> if so, otherwise <code>false</code>
	 */
	public boolean isExternalUser() {
		LOGGER.debug("User#isExternalUser [extern=" + this.extern + "]");

		return this.extern;
	}

	/**
	 * Change the authentication method of the <code>User</code>.
	 * 
	 * @param externalUser
	 *            <code>true</code> if the <code>User</code> was authenticated
	 *            by an external system, otherwise <code>false</code>.
	 */
	public void setExternalUser(final boolean externalUser) {
		LOGGER.debug("User#setExternalUser [extern=" + externalUser + "]");

		this.extern = externalUser;
	}

	/**
	 * Determines whether the <code>User</code> is enabled or not.
	 * 
	 * @return <code>true</code> if the <code>User</code> is enabled, otherwise
	 *         <code>false</code>
	 */
	public boolean isEnabled() {
		LOGGER.debug("User#isEnabled [enabled=" + this.enabled + "]");

		return this.enabled;
	}

	/**
	 * Enable or disable the <code>User</code>.
	 * 
	 * @param enabled
	 *            <code>true</code> when enabled, otherwise <code>false</code>
	 */
	public void setEnabled(final boolean enabled) {
		LOGGER.debug("User#setEnabled [enabled=" + enabled + "]");

		this.enabled = enabled;
	}

	/**
	 * Return the date when the account expires.
	 * 
	 * @return The expiration date
	 */
	public LocalDateTime getExpirationDate() {
		LOGGER.debug("User#getExpirationDate [expirationDate="
			+ this.formatLocalDateTime(expirationDate) + "]");

		return expirationDate;
	}

	/**
	 * Change the date when the account expires.
	 * 
	 * @param expDate
	 *            The new expiration date to set
	 */
	public void setExpirationDate(@NotNull final LocalDateTime expDate) {
		LOGGER.debug("User#setExpirationDate [expDate=" + this.formatLocalDateTime(expDate) + "]");

		this.expirationDate = expDate;
	}

	/**
	 * Returns a list of granted {@link Role}s.
	 * 
	 * @return The list of granted {@link Role}s
	 */
	public Set<Role> getRoles() {
		LOGGER.debug("User#getRoles [roles.size=" + this.roles.size() + "]");

		return Collections.unmodifiableSet(this.roles);
	}

	/**
	 * Add a new {@link Role} to the list of {@link Role}s.
	 * 
	 * @param role
	 *            The new {@link Role} to add
	 * @return see {@link java.util.Collection#add(Object)}
	 */
	public boolean addRole(@NotNull final Role role) {
		LOGGER.debug("User#addRole [role.name=" + role.getName() + "]");

		final boolean isAdded = this.roles.add(role);
				
		// This is a bidirectional relationship, so the relationship has to be
		// added also on the other side.
		final boolean isInternalAdded = role.internalAddUser(this);
		
		return isAdded && isInternalAdded;
	}
	
    /**
     * Remove a {@link Role} from the <code>User</code>.
     * @param role The {@link Role} to remove.
     * @return true if this set contained the specified element.
     */
    public boolean removeRole(@NotNull final Role role) {
        LOGGER.debug("User#removeRole [" + role.toString() + "]");

        // This is a bidirectional relationship, so the relationship has to be
        // removed also on the other side.
        final boolean isInternalRemove = role.internalRemoveUser(this);
        // Remove role.
        final boolean isRemove = this.roles.remove(role);

        return isInternalRemove && isRemove;
    }

	/**
	 * Add a {@link Role} to the <code>User</code>. The relationship is
	 * bidirectional, the relationship has to be added manually on the other
	 * side.
	 * 
	 * @param role
	 *            The {@link Role} to add.
	 */
	protected boolean internalAddRole(@NotNull final Role role) {
		LOGGER.debug("User#internalAddRole [" + role.toString() + "]");

		final boolean isAdded = this.roles.add(role);

		return isAdded;
	}
    
	/**
	 * Remove a {@link Role} from <code>User</code> for internal use package
	 * scope.
	 * 
	 * @param role
	 *            The {@link Role} to remove.
	 */
	protected boolean internalRemoveRole(@NotNull final Role role) {
		LOGGER.debug("Role#internalRemoveRole [" + role.toString() + "]");

		final boolean isInternalRemoved = this.roles.remove(role);

		return isInternalRemoved;
	}

	/**
	 * Remove all {@link Role}s from this <code>User</code>.
	 */
	public void removeRoles() {
		LOGGER.debug("User#removeRoles");
		
		Iterator<Role> itr = this.roles.iterator();
		
		while(itr.hasNext()) {
			final Role role = itr.next();
			
			final boolean isInternalRemove = role.internalRemoveUser(this);
			LOGGER.debug("User#removeRoles [user.id=" + role.getId() + ", internalRemove=" + isInternalRemove + "]");
			
			itr.remove();
		}
	}
	
	/**
	 * Set the {@link Role}s of this <code>User</code>. Existing {@link Role}s
	 * will be not overridden.
	 * 
	 * @param roles
	 *            The new list of {@link Role}s
	 */
	public void setRoles(final Set<Role> roles) {
		LOGGER.debug("User#setRoles [roles.size=" + roles.size() + "]");

		roles.stream().forEach(role -> {
			this.roles.add(role);
			role.internalAddUser(this);
		});
	}
	
	/**
	 * Return the fullname of the <code>User</code>.
	 * 
	 * @return The current fullname
	 */
	public String getFullname() {
		LOGGER.debug("User#getFullname [fullname=" + this.fullname + "]");

		return this.fullname;
	}

	/**
	 * Change the fullname of the <code>User</code>.
	 * 
	 * @param fullname
	 *            The new fullname to set
	 */
	public void setFullname(final String fullname) {
		LOGGER.debug("User#setFullname [fullname=" + this.fullname + "]");

		this.fullname = fullname;
	}

	/**
	 * Return the details of the <code>User</code>.
	 * 
	 * @return The userDetails
	 */
	public UserDetails getUserDetails() {
		LOGGER.debug("User#getUserDetails [userDetails=" + this.userDetails.toString() + "]");

		return this.userDetails;
	}

	/**
	 * Assign some details to the <code>User</code>.
	 * 
	 * @param userDetails
	 *            The userDetails to set
	 */
	public void setUserDetails(UserDetails userDetails) {
		LOGGER.debug("User#setUserDetails [userDetails=" + this.userDetails.toString() + "]");

		this.userDetails = userDetails;
	}

	/**
	 * Flatten {@link Role}s and {@link Grant}s and return an unmodifiable list
	 * of all {@link Grant}s assigned to this <code>User</code>.
	 * 
	 * @return A list of all {@link Grant}s
	 */
	public List<Grant> getGrants() {
		List<Grant> grants = new ArrayList<>();

		for (Role role : this.getRoles()) {
			grants.addAll(role.getGrants());
		}

		LOGGER.debug("User#getGrants [grants.size=" + grants.size() + "]");

		return Collections.unmodifiableList(grants);
	}
	
	/**
	 * Return a list of recently used passwords.
	 * 
	 * @return A list of recently used passwords
	 */
	public List<UserPassword> getPasswords() {
		LOGGER.debug("User#getPasswords [passwords.size=" + this.passwords.size() + "]");

		return this.passwords;
	}
	
	/**
	 * Returns the current password of the <code>User</code>.
	 * 
	 * @return The current password as String
	 */
	public String getPassword() {
		LOGGER.debug("User#getPassword [password=" + this.password + "]");

		return this.password;
	}

	/**
	 * Return the date when the password has changed recently.
	 * 
	 * @return The date when the password has changed recently
	 */
	public LocalDateTime getLastPasswordChange() {
		LOGGER.debug("User#getLastPassworChange [lastPasswordChange=" + this.lastPasswordChange + "]");

		return this.lastPasswordChange;
	}

	/**
	 * Checks if the new password is a valid and change the password of this
	 * <code>User</code>.
	 * 
	 * @param password
	 *            The new password of this <code>User</code>
	 * @throws AppInvalidPasswordException
	 *             in case changing the password is not allowed or the new
	 *             password is not valid
	 */
	public void setNewPassword(final String password) {
		LOGGER.debug("User#setNewPassword [password=" + password + "]");

		if (this.persistedPassword != null && this.persistedPassword.equals(password)) {
			final String errorMessage = "Trying to set the new password equals to the current password.";
			
			LOGGER.debug("User#setNewPassword - "+ errorMessage);
			throw new AppInvalidPasswordException(errorMessage);
		}
		
		if (this.isPasswordValid(password)) {
			this.storeOldPassword(password);
			this.persistedPassword = password;
			this.password = password;
			this.lastPasswordChange = LocalDateTime.now();
		} else {
			throw new AppInvalidPasswordException("Password confirms not the defined rules.");
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
			LOGGER.debug("User#hasPasswordChanged [hasPasswordChanged=" + hasPasswordChanged + "]");
		} else {
			LOGGER.warn(
					"User#hasPasswordChanged - persistedPassword == null, is a test running? [hasPasswordChanged=false]");
		}

		return hasPasswordChanged;
	}

	/**
	 * Check whether the new password is in the history of former passwords.
	 * 
	 * @param password
	 *            The password to verify
	 * @return <code>true</code> if the password is valid, otherwise
	 *         <code>false</code>
	 */
	protected boolean isPasswordValid(final String password) {
		boolean isPasswordValid = true;

		if (this.passwords.contains(new UserPassword(this, password))) {
			isPasswordValid = false;
		}

		LOGGER.debug("User#isPasswordValid [password=" + password + ", isPasswordValid=" + isPasswordValid + "]");

		return isPasswordValid;
	}

	private void storeOldPassword(final String passwordToStore) {
		if (passwordToStore == null || passwordToStore.isEmpty()) {
			LOGGER.debug("User#storeOldPassword - The old password is null or empty, it would not be stored in history.");

			return;
		}

		this.passwords.add(new UserPassword(this, passwordToStore));
		
		LOGGER.debug("User#storeOldPassword [passwords.size=" + this.passwords.size() + ", passwordToStore=" + passwordToStore + "]");
		
		if (NUMBER_STORED_PASSWORDS < this.passwords.size()) {
			// Get the oldest password from list.
			final java.util.Optional<UserPassword>
				optionalUserPassword =
				passwords.stream()
						 .sorted((userPassword1, userPassword2) -> userPassword1.getPasswordChanged()
																				.compareTo(userPassword2.getPasswordChanged()))
						 .findFirst();

			if (optionalUserPassword.isPresent()) {
				final UserPassword oldestUserPassword = optionalUserPassword.get();
				oldestUserPassword.setUser(null);
				this.passwords.remove(oldestUserPassword);

				LOGGER.debug("User#storeOldPassword# - after remove password from password list. [passwordToRemove=" + oldestUserPassword.getPassword() + ", passwords.size=" + this.passwords.size() + "]");
			} else {
				throw new AppInvalidPasswordException("The oldest password could not be determined!");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Does not call the superclass. Uses the username for calculation.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((this.username == null) ? 0 : this.username.hashCode());

		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Uses the username for comparison.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof User)) {
			return false;
		}
		
		User other = (User) obj;
		
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
		
		return "User [id=" + super.getId()
			   + ", businessKey-username=" + this.username
			   + ", extern=" + this.extern
			   + ", lastPasswordChange=" + this.lastPasswordChange
			   + ", locked=" + this.locked
			   + ", password=" + this.password
			   + ", persistedPassword=" + this.persistedPassword
			   + ", enabled=" + this.enabled
			   + ", expirationDate=" + this.expirationDate
			   + ", fullname=" + this.fullname
			   + ", version=" + this.getVersion()
				// May not be logged here, throws
				// org.hibernate.LazyInitializationException
			    // + ", userDetails=" + userDetails
				// May not be logged here, throws
				// org.hibernate.LazyInitializationException
				// + ", roles.size=" + this.roles.size()
				// May not be logged here, throws
				// org.hibernate.LazyInitializationException
				// + ", passwords=" + this.passwords.size()
			   + "]";
	}
}
