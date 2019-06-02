package de.bomc.poc.model.account;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;

/**
 * Defines the mapping between an {@link Account} and a {@Person} with a
 * {@link AuthorizationTypeEnum}.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 09.08.2016
 */
@Entity
@Table(name = "T_ACCOUNT_USER")
@NamedQueries({ 
	@NamedQuery(name = AccountUser.NQ_FIND_ACCOUNT_USER_WITH_ACCOUNT_AND_USER_DATA_BY_ACCOUNT_ID, query = "select au from AccountUser au join fetch au.account aua join fetch au.user auu left join fetch auu.addresses where au.ownerFlag = true and aua.id = ?1") 
})
public class AccountUser implements Serializable {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 5332639270393017187L;
	/**
	 * The logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(AccountUser.class);
	/**
	 * A log prefix.
	 */
	private static final String LOG_PREFIX = "AccountUser#";

	/* --------------------- constants ------------------------------ */
	/**
	 * The default prefix String for each created
	 * <code>AccountUser</code>-NamedQuery.
	 */
	protected static final String ACCOUNT_USER_PREFIX = "ACCOUNT_USER.";
	/**
	 * <pre>
	 * Query to find the owner of the relationship <code>Person</code> and <code>Account</code> by the given account id.
	 * </pre>
	 */
	public static final String NQ_FIND_ACCOUNT_USER_WITH_ACCOUNT_AND_USER_DATA_BY_ACCOUNT_ID = ACCOUNT_USER_PREFIX + "findAccountUserWithAccountAndUserDataByAccountId";
	
	/* --------------------- columns -------------------------------- */
	@EmbeddedId
	private AccountUserPK id;
	@ElementCollection(targetClass = AuthorizationTypeEnum.class)
	@CollectionTable(name = "T_COLLECT_ACCOUNT_USER", joinColumns = {
			@JoinColumn(name = "T_COLLECT_JOIN_USER", referencedColumnName = "C_FK_USER_ID"),
			@JoinColumn(name = "T_COLLECT_JOIN_ACCOUNT", referencedColumnName = "C_FK_ACCOUNT_ID") })
	@Column(name = "C_USER_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	private Set<AuthorizationTypeEnum> authTypes = new LinkedHashSet<>();
	@Column(name = "C_OWNER", nullable = false)
	private Boolean ownerFlag = false;

	/* --------------------- collections ---------------------------- */
	@ManyToOne
	@JoinColumn(name = "C_FK_USER_ID", insertable = false, updatable = false)
	private User user;
	@ManyToOne
	@JoinColumn(name = "C_FK_ACCOUNT_ID", insertable = false, updatable = false)
	private Account account;

	/* --------------------- constructors --------------------------- */
	/**
	 * Accessed by persistence provider.
	 */
	protected AccountUser() {
		// Accessed by the jpa provider.
		LOGGER.debug(LOG_PREFIX + "co");
	}

	public AccountUser(@NotNull final User user, @NotNull final Account account, @NotNull final Boolean ownerFlag,
			@NotNull final Set<AuthorizationTypeEnum> authTypes) {
		// Create the primary key for this mapping.
		this.id = new AccountUserPK(account.getId(), user.getId());

		this.ownerFlag = ownerFlag;
		this.authTypes.addAll(authTypes);
		// Update relationships to assure referential integrity.
		this.setUser(user);
		this.setAccount(account);
	}

	/* --------------------- methods -------------------------------- */
	@PreRemove
	public void preRemove() {
		LOGGER.debug(LOG_PREFIX + "preRemove");

		// Update relationships to assure referential integrity.
		this.setUser(null);
		this.setAccount(null);
		this.authTypes.clear();
	}

	/**
	 * @return the id
	 */
	public AccountUserPK getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final AccountUserPK id) {
		this.id = id;
	}

	/**
	 * @return the ownerFlag
	 */
	public Boolean getOwnerFlag() {
		return ownerFlag;
	}

	/**
	 * @param ownerFlag
	 *            the ownerFlag to set
	 */
	public void setOwnerFlag(Boolean ownerFlag) {
		this.ownerFlag = ownerFlag;
	}

	/**
	 * @return the authTypesEnum
	 */
	public Set<AuthorizationTypeEnum> getAuthTypes() {
		return this.authTypes;
	}

	/**
	 * @param authTypeEnum
	 *            the authTypes to set
	 */
	public void setAuthTypes(final Set<AuthorizationTypeEnum> authTypes) {
		this.authTypes = authTypes;
	}

	// ______________________________________________________________
	// Person handling.
	// --------------------------------------------------------------
	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * Set the user or remove the user depending on the given parameter.
	 * 
	 * @param user
	 *            the given user to set. If not null a user is added, otherwise
	 *            the user is removed.
	 */
	public void setUser(final User user) {
		if (this.user != null) {
			this.user.internalRemoveAccount(this);
		}

		this.user = user;

		if (user != null) {
			user.internalAddAccount(this);
		}
	}

	// _____________________________________________________________
	// Account handling.
	// -------------------------------------------------------------
	/**
	 * @return the account
	 */
	public Account getAccount() {
		return this.account;
	}

	/**
	 * Set the account or remove the account depending on the given parameter.
	 * 
	 * @param account
	 *            the given account to set. If not null a account is added,
	 *            otherwise the account is removed.
	 */
	public void setAccount(final Account account) {
		if (this.account != null) {
			this.account.internalRemoveUser(this);
		}

		this.account = account;

		if (account != null) {
			account.internalAddUser(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AccountUser [id=" + this.id + ", user=" + this.user + ", ownerFlag= " + this.ownerFlag + ", account="
				+ this.account + "]";
	}

	/* --------------------- embeddable ----------------------------- */
	@Embeddable
	public static class AccountUserPK implements Serializable {

		/**
		 * The serial UID.
		 */
		private static final long serialVersionUID = 1L;
		@Column(name = "C_FK_ACCOUNT_ID")
		protected Long accountId;
		@Column(name = "C_FK_User_ID")
		protected Long userId;

		protected AccountUserPK() {
			// Used by JPA provider.
		}

		public AccountUserPK(final Long accountId, final Long userId) {
			this.accountId = accountId;
			this.userId = userId;
		}

		/**
		 * @return the accountId
		 */
		public Long getAccountId() {
			return accountId;
		}

		/**
		 * @param accountId
		 *            the accountId to set
		 */
		public void setAccountId(Long accountId) {
			this.accountId = accountId;
		}

		/**
		 * @return the userId
		 */
		public Long getUserId() {
			return userId;
		}

		/**
		 * @param userId
		 *            the userId to set
		 */
		public void setUserId(Long userId) {
			this.userId = userId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;

			result = prime * result + ((this.userId == null) ? 0 : this.userId.hashCode());
			result = prime * result + ((this.accountId == null) ? 0 : this.accountId.hashCode());

			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			final AccountUserPK other = (AccountUserPK) obj;
			if (this.userId == null) {
				if (other.userId != null) {
					return false;
				}
			} else if (!this.userId.equals(other.userId)) {
				return false;
			}
			if (this.accountId == null) {
				if (other.accountId != null) {
					return false;
				}
			} else if (!this.accountId.equals(other.accountId)) {
				return false;
			}
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "AccountUserPK [accountId=" + this.accountId + ", userId=" + this.userId + "]";
		}
	} // end embeddable
}
