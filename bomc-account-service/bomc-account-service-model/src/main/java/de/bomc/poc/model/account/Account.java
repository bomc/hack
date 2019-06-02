package de.bomc.poc.model.account;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;

import de.bomc.poc.model.AbstractAccountEntity;
import de.bomc.poc.model.AbstractMetadataEntity;

/**
 * A account that is used by different user types.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 09.08.2016
 */
@Entity
@Table(name = "T_ACCOUNT")
@NamedQueries({ @NamedQuery(name = Account.NQ_FIND_ALL, query = "select a from Account a"),
		@NamedQuery(name = Account.NQ_FIND_BY_NAME, query = "select a from Account a where a.name = ?1"),
		@NamedQuery(name = Account.NQ_FIND_ASSOCIATED_USER_BY_ID, query = "select a from Account a join a.users ua where ua.user.id = ?1") })
public class Account extends AbstractAccountEntity<Account> implements Serializable {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -3148276340333047646L;
	/**
	 * The logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(User.class);
	/**
	 * A log prefix.
	 */
	private static final String LOG_PREFIX = Account.class + "#";

	/* --------------------- constants ------------------------------ */
	/**
	 * The default prefix String for each created
	 * <code>Account</code>-NamedQuery.
	 */
	protected static final String ACCOUNT_PREFIX = "ACCOUNT.";
	/**
	 * <pre>
	 * Query to find all Person.
	 * </pre>
	 */
	public static final String NQ_FIND_ALL = ACCOUNT_PREFIX + AbstractMetadataEntity.FIND_ALL;
	/**
	 * <pre>
	 * Query to find <code>Account</code> by name.
	 * <li>Query parameter index <strong>1</strong> : The name of the <code>Account</code> to search for.</li>
	 * <br/>
	 * </pre>
	 */
	public static final String NQ_FIND_BY_NAME = ACCOUNT_PREFIX + "findByName";
	/**
	 * <pre>
	 * Query to find the associated <code>Account</code> to the associated <code>Person</code> by id.
	 * <li>Query parameter index <strong>1</strong> : The <code>Person</code> id to search for.</li>
	 * <br/>
	 * </pre>
	 */
	public static final String NQ_FIND_ASSOCIATED_USER_BY_ID = ACCOUNT_PREFIX + "findAssociatedUserById";

	/* --------------------- columns -------------------------------- */
	/**
	 * A custom name of this account.
	 */
	@Column(name = "C_NAME", nullable = false, length = 255)
	private String name;

	/* --------------------- associations --------------------------- */
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	private final Set<AccountUser> users = new LinkedHashSet<>();

	/* --------------------- constructors --------------------------- */
	protected Account() {
		LOGGER.debug(LOG_PREFIX + "co");

		// Used by Jpa-Provider.
	}

	/**
	 * Creates a new instance <code>Account</code>
	 * 
	 * @param name
	 *            the name.
	 */
	public Account(final String name) {
		LOGGER.debug(LOG_PREFIX + "co [name=" + name + "]");

		this.name = name;
	}

	/* ----------------------------- methods ------------------------ */
	/**
	 * @return the type of this entity.
	 */
	@Override
	protected Class<Account> getEntityClass() {
		return Account.class;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	// _____________________________________________________________
	// Person handling.
	// -------------------------------------------------------------
	/**
	 * Get all added {link AccountUser} relationships.
	 * 
	 * @return all added users.
	 */
	public Set<AccountUser> getUsers() {
		return Collections.unmodifiableSet(this.users);
	}

	/**
	 * Add a {@link AccountUser} relationship to this account.
	 * 
	 * @param user
	 *            the given relationship to add.
	 */
	public void addUser(@NotNull final AccountUser user) {
		user.setAccount(this);
	}

	/**
	 * Remove the given {@link AccountUser} relationship from this account.
	 * 
	 * @param user
	 *            the gievn relationship to remove.
	 */
	public void removeUser(@NotNull final AccountUser user) {
		user.setAccount(null);
	}

	/**
	 * <pre>
	 * Add a {@link AccountUser} relationship to this account.
	 * The relationship is bidirectional, the relationship has to be added manually on the other side.
	 * </pre>
	 * 
	 * @param user
	 *            the relationship to add.
	 */
	protected void internalAddUser(@NotNull final AccountUser user) {
		this.users.add(user);
	}

	/**
	 * <pre>
	 * Remove a {@link AccountUser} relationship from this account.
	 * The relationship is bidirectional, the relationship has to be added manually on the other side.
	 * </pre>
	 * 
	 * @param user
	 *            the relationship to remove.
	 */
	protected void internalRemoveUser(@NotNull final AccountUser user) {
		this.users.remove(user);
	}

	/**
	 * Find all {@link AccountUser} relationships by the given {@link Person} id.
	 * 
	 * @param id
	 *            the given user id.
	 * @return all accountUser relationships by the given user id.
	 */
	public List<AccountUser> findAllRelationshipsToAccountsByUserId(final Long id) {
		return this.users.stream().filter(accountUser -> accountUser.getUser().getId().equals(id))
				.collect(Collectors.toList());
	}

	/**
	 * Find the Owner of this account. This method must run in context of a
	 * transaction.
	 */
	public AccountUser findOwner() {
		// TODO: write test
		final List<AccountUser> accountUserList = this.users.stream()
				.filter(accountUser -> accountUser.getOwnerFlag() == true).collect(Collectors.toList());

		// TODO: Exception, if no element is available, what is if there are
		// more than one Person, check it! It could be only one owner.

		// Could only be one owner.
		return accountUserList.iterator().next();
	}

	/**
	 * Find the concerned user by id. This method must run in context of a
	 * transaction.
	 * 
	 * @param userId
	 *            the given user id.
	 * @return the searched user by the given user id.
	 */
	public List<AccountUser> findUserById(final Long userId) {
		final List<AccountUser> accountUserList = this.users.stream()
				.filter(accountUser -> accountUser.getUser().getId() == userId).collect(Collectors.toList());

		// Could be only one user.
		return accountUserList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Account [id=" + this.getId() + ", version=" + this.getVersion() + ", name=" + name + "]";
	}
}
