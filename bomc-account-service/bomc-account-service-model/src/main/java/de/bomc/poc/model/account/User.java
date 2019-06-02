package de.bomc.poc.model.account;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.log4j.Logger;

import de.bomc.poc.model.AbstractEntity;
import de.bomc.poc.model.AbstractMetadataEntity;

/**
 * This class <code>Person</code> is a sample entity.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 09.08.2016
 */
@Entity
@Table(name = "T_USER")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamedQueries({ @NamedQuery(name = User.NQ_FIND_ALL, query = "select u from User u"),
		@NamedQuery(name = User.NQ_FIND_BY_USERNAME, query = "select u from User u where u.username = ?1"),
		@NamedQuery(name = User.NQ_FIND_BY_AUTH_TYPE, query = "select u from User u join u.accounts ua join ua.authTypes uaa where uaa = ?1"),
		@NamedQuery(name = User.NQ_FIND_ASSOCIATED_ACCOUNT_BY_ID, query = "select u from User u join u.accounts ua where ua.account.id = ?1") })
public class User extends AbstractEntity<User> implements Serializable {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 4283786193325586932L;
	/**
	 * The logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(User.class);
	/**
	 * A log prefix.
	 */
	private static final String LOG_PREFIX = User.class + "#";

	/* --------------------- constants ------------------------------ */
	/**
	 * The default prefix String for each created <code>Person</code>-NamedQuery.
	 */
	protected static final String USER_PREFIX = "USER.";
	/**
	 * <pre>
	 * Query to find <code>Person</code> by username.
	 * <li>Query parameter index <strong>1</strong> : The username of the <code>Person</code> to search for.</li>
	 * <br/>
	 * </pre>
	 */
	public static final String NQ_FIND_BY_USERNAME = USER_PREFIX + "findByUsername";
	/**
	 * <pre>
	 * Query to find all Person.
	 * </pre>
	 */
	public static final String NQ_FIND_ALL = USER_PREFIX + AbstractMetadataEntity.FIND_ALL;
	/**
	 * <pre>
	 * Query to find all <code>Person</code> that referenced a <AuthEnumType>.
	 * <li>Query parameter index <strong>1</strong> : Find all <code>Person</code> by the associated <code>AuthEnumType</code>.</li>
	 * <br/>
	 * </pre>
	 */
	public static final String NQ_FIND_BY_AUTH_TYPE = USER_PREFIX + "findByAuthType";
	/**
	 * <pre>
	 * Query to find the associated <code>Person</code> to the associated <code>Account</code> by id.
	 * <li>Query parameter index <strong>1</strong> : The <code>Account</code> id to search for.</li>
	 * <br/>
	 * </pre>
	 */
	public static final String NQ_FIND_ASSOCIATED_ACCOUNT_BY_ID = USER_PREFIX + "findAssociatedAccountById";

	/* --------------------- columns -------------------------------- */
	/**
	 * Unique identifier of this <code>Person</code> (not nullable).
	 */
	@NotNull
	@Size(min = 5, max = 32)
	@Column(name = "C_USERNAME", unique = true, nullable = false)
	protected String username;

	/* --------------------- collections ---------------------------- */
	/**
	 * The <code>Address</code>es of this user (handles bi-directional
	 * relationship).
	 */
	@OrderBy("country DESC")
	@OneToMany(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
	private final Set<Address> addresses = new LinkedHashSet<>();
	@OneToMany(mappedBy = "user")
	private final Set<AccountUser> accounts = new LinkedHashSet<>();

	/* --------------------- constructors --------------------------- */
	/**
	 * Accessed by persistence provider.
	 */
	protected User() {
		// Accessed by the jpa provider.
		LOGGER.debug(LOG_PREFIX + "co");
	}

	/**
	 * Create a new <code>Person</code> with an username.
	 * 
	 * @param username
	 *            The unique name of the user
	 */
	public User(final String username) {
		this.username = username;
	}

	/* --------------------- methods -------------------------------- */
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}

	/**
	 * Return the unique username of the <code>Person</code>.
	 * 
	 * @return The current username
	 */
	public String getUsername() {
		LOGGER.debug(LOG_PREFIX + "getUsername [username=" + this.username + "]");

		return this.username;
	}

	/**
	 * Change the username of the <code>Person</code>.
	 * 
	 * @param username
	 *            The new username to set
	 */
	public void setUsername(final String username) {
		LOGGER.debug(LOG_PREFIX + "setUsername [username=" + username + "]");

		this.username = username;
	}

	// _____________________________________________________________
	// Address handling.
	// -------------------------------------------------------------
	/**
	 * Get all added addresses.
	 * 
	 * @return all added addresses.
	 */
	public Set<Address> getAddresses() {
		return Collections.unmodifiableSet(this.addresses);
	}

	/**
	 * Add {@link Address} to this user.
	 * 
	 * @param address
	 *            the address to add.
	 */
	public void addAddress(final Address address) {
		address.addUser(this);
	}

	/**
	 * Remove {@link Address} fron this user.
	 * 
	 * @param address
	 *            the address to remove.
	 */
	public void removeAddress(final Address address) {
		address.removeUser();
	}

	/**
	 * <pre>
	 * Add a {@link Address} to the <code>Person</code>. 
	 * The relationship is bidirectional, 
	 * the relationship has to be added manually on the other side.
	 * </pre>
	 * 
	 * @param address
	 *            The {@link Address} to remove.
	 */
	protected void internalAddAddress(final Address address) {
		this.addresses.add(address);
	}

	/**
	 * <pre>
	 * Add a {@link Address} to the <code>Person</code>. 
	 * The relationship is bidirectional, 
	 * the relationship has to be removed manually on the other side.
	 * </pre>
	 * 
	 * @param address
	 *            The {@link Address} to remove.
	 */
	public void internalRemoveAddress(final Address address) {
		this.addresses.remove(address);
	}

	/**
	 * <pre>
	 * Set the {@link Address}es of this <code>Person</code>. 
	 * Existing addresses will be not overridden.
	 * </pre>
	 * 
	 * @param addresses
	 *            The new list of {@link Address}es to add.
	 */
	public void setAddresses(final Set<Address> addresses) {
		LOGGER.debug(LOG_PREFIX + "setAddresses [addresses.size=" + addresses.size() + "]");

		addresses.stream().forEach(address -> this.addAddress(address));
	}

	// _____________________________________________________________
	// Account handling.
	// -------------------------------------------------------------
	/**
	 * Get all added {link AccountUser} relationships.
	 * 
	 * @return all added accountUser relationship.
	 */
	public Set<AccountUser> getAccounts() {
		return Collections.unmodifiableSet(this.accounts);
	}

	/**
	 * Add {@link AccountUser} relationship to this user.
	 * 
	 * @param accountUser
	 *            the accountUser relationship to add.
	 */
	public void addAccount(@NotNull final AccountUser account) {
		account.setUser(this);
	}

	/**
	 * Remove {@link AccountUser} relationship from this user.
	 * 
	 * @param accountUser
	 *            the accountUser relationship to remove.
	 */
	public void removeAccount(@NotNull final AccountUser account) {
		account.setUser(null);
	}

	/**
	 * <pre>
	 * Add a {@link AccountUser} relationship to this user.
	 * The relationship is bidirectional, 
	 * the relationship has to be added manually on the other side.
	 * </pre>
	 * 
	 * @param accountKey
	 *            the accountKey to remove.
	 */
	protected void internalAddAccount(@NotNull final AccountUser account) {
		this.accounts.add(account);
	}

	/**
	 * <pre>
	 * Remove a {@link AccountUser} relationship from this user.
	 * The relationship is bidirectional, 
	 * the relationship has to be removed manually on the other side.
	 * </pre>
	 * 
	 * @param accountUser
	 *            the accountUser relationship to remove.
	 */
	protected void internalRemoveAccount(@NotNull final AccountUser account) {
		this.accounts.remove(account);
	}

	/**
	 * Uses additional the username for calculation, because the username is
	 * unique and must not be null.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;

		int result = super.hashCode();
		result = prime * result + ((username == null) ? 0 : username.hashCode());

		return result;
	}

	/**
	 * Uses additional the username for calculation, because the username is
	 * unique and must not be null.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof User)) {
			return false;
		}

		User other = (User) obj;

		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {

		return "Person [id=" + this.getId() + ", version=" + this.getVersion() + ", businessKey-username=" + this.username
				+ "]";
	}
}
