package de.bomc.poc.model;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.bomc.poc.model.test.AbstractUnitTest;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * ___________________________________________________
 * To test this, correct the persistence.xml file classes tag.
 * ---------------------------------------------------
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 16.11.2016
 */
@org.junit.Ignore
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MappingTest extends AbstractUnitTest {

	@Before
	public void setup() {
		this.entityManager = this.emProvider.getEntityManager();
		assertThat(this.entityManager, notNullValue());
	}

	@Test
	public void test01_create() {

		// ___________________________________________
		// Create a accountPersonRole.
		this.emProvider.tx().begin();

		final Person person = new Person("sam", "mas", "sam@gmail.com");
		this.entityManager.persist(person);

		final Account account = new Account("Designer");
		this.entityManager.persist(account);

		Role role = new Role("myRole");
		this.entityManager.persist(role);

		final AccountPersonRole accountPersonRole = new AccountPersonRole();
		accountPersonRole.setAccount(account);
		accountPersonRole.setPerson(person);
		accountPersonRole.setRole(role);
		accountPersonRole.setActivated(true);

		person.addAccountPersonRole(accountPersonRole);
		account.addAccountPersonRole(accountPersonRole);
		role.addAccountPersonRole(accountPersonRole);

		this.entityManager.persist(accountPersonRole);

		this.emProvider.tx().commit();
		
		// ___________________________________________
		// Create a new accountPersonRole.
		this.emProvider.tx().begin();
		final Person retPerson = this.entityManager.find(Person.class, person.getId());
		final Role retRole = this.entityManager.find(Role.class, role.getId());

		final Account newAccount = new Account("Admin");
		this.entityManager.persist(newAccount);

		final AccountPersonRole newAccountPersonRole = new AccountPersonRole();
		newAccountPersonRole.setAccount(newAccount);
		newAccountPersonRole.setPerson(retPerson);
		newAccountPersonRole.setRole(retRole);
		newAccountPersonRole.setActivated(true);

		retPerson.addAccountPersonRole(newAccountPersonRole);
		newAccount.addAccountPersonRole(newAccountPersonRole);
		retRole.addAccountPersonRole(newAccountPersonRole);

		this.entityManager.persist(newAccountPersonRole);

		this.emProvider.tx().commit();

		// ___________________________________________
		// Delete accountPersonRole
		final AccountPersonRole delAccountPersonRole = this.entityManager.find(AccountPersonRole.class, accountPersonRole.getPrimaryKey());
		
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + delAccountPersonRole.toString());

		this.emProvider.tx().begin();
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%1");
//		this.entityManager.remove(account);
//		this.entityManager.remove(person);
//		this.entityManager.remove(role);
		delAccountPersonRole.getAccount().getAccountPersonRoleSet().clear();
		delAccountPersonRole.getRole().getAccountPersonRoleSet().clear();
		delAccountPersonRole.getPerson().getAccountPersonRoleSet().clear();
		
		this.entityManager.remove(delAccountPersonRole);
		
		this.emProvider.tx().commit();
		 
	}

	@Entity
	@Table(name = "T_PERSON")
	public class Person {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		@Column(name = "C_ID")
		private long id;
		private String username;
		private String password;
		private String email;
		@OneToMany(mappedBy = "primaryKey.person", cascade = CascadeType.ALL)
		private Set<AccountPersonRole> accountPersonRoleSet = new LinkedHashSet<AccountPersonRole>();

		public Person() {
			// 
			// Default co, is used by jpa provider.
		}

		public Person(final String username, final String password, final String email) {
			this.username = username;
			this.password = password;
			this.email = email;
		}

		public Long getId() {
			return id;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(final String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(final String password) {
			this.password = password;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(final String email) {
			this.email = email;
		}

		public Set<AccountPersonRole> getAccountPersonRoleSet() {
			return accountPersonRoleSet;
		}

		public void setAccountPersonRoleSet(final Set<AccountPersonRole> accountPersonRoleSet) {
			this.accountPersonRoleSet = accountPersonRoleSet;
		}

		public void addAccountPersonRole(final AccountPersonRole accountPersonRole) {
			this.accountPersonRoleSet.add(accountPersonRole);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Person [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email + "]";
		}
	}

	@Entity
	@Table(name = "T_ACCOUNT")
	public class Account {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		@Column(name = "C_ID")
		private long id;
		private String name;
		@OneToMany(mappedBy = "primaryKey.account", cascade = CascadeType.ALL)
		private Set<AccountPersonRole> accountPersonRoleSet = new LinkedHashSet<AccountPersonRole>();

		public Account() {
			// 
			// Default co, is used by jpa provider.
		}

		public Account(final String name) {
			this.name = name;
		}

		public Long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public Set<AccountPersonRole> getAccountPersonRoleSet() {
			return accountPersonRoleSet;
		}

		public void setAccountPersonRoleSet(final Set<AccountPersonRole> accountPersonRoleSet) {
			this.accountPersonRoleSet = accountPersonRoleSet;
		}

		public void addAccountPersonRole(final AccountPersonRole accountPersonRole) {
			this.accountPersonRoleSet.add(accountPersonRole);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Account [id=" + id + ", name=" + name + "]";
		}
	}

	@Entity
	@Table(name = "T_ROLE")
	public class Role {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		@Column(name = "C_ID")
		private Long id;
		private String name;
		@OneToMany(mappedBy = "primaryKey.role", cascade = CascadeType.ALL)
		private Set<AccountPersonRole> accountPersonRoleSet = new LinkedHashSet<AccountPersonRole>();

		public Role() {
			// 
			// Default co, is used by jpa provider.
		}

		public Role(final String name) {
			this.name = name;
		}

		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public Set<AccountPersonRole> getAccountPersonRoleSet() {
			return accountPersonRoleSet;
		}

		public void setAccountPersonRoleSet(final Set<AccountPersonRole> accountPersonRoleSet) {
			this.accountPersonRoleSet = accountPersonRoleSet;
		}

		public void addAccountPersonRole(final AccountPersonRole accountPersonRole) {
			this.accountPersonRoleSet.add(accountPersonRole);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Role [id=" + id + ", name=" + name + "]";
		}
	}

	@Entity
	@Table(name = "T_ACCOUNT_PERSON_ROLE")
	@AssociationOverrides({ 
		@AssociationOverride(name = "primaryKey.person", joinColumns = @JoinColumn(name = "JOIN_PERSON_ID", insertable = false, updatable = false)),
		@AssociationOverride(name = "primaryKey.role", joinColumns = @JoinColumn(name = "JOIN_ROLE_ID", insertable = false, updatable = false)),
		@AssociationOverride(name = "primaryKey.account", joinColumns = @JoinColumn(name = "JOIN_ACCOUNT_ID", insertable = false, updatable = false))
	})
	public class AccountPersonRole {

		// composite-id key
		private AccountPersonRolePK primaryKey = new AccountPersonRolePK();
		// additional fields
		private boolean activated;

		public AccountPersonRole() {
			// 
			// Default co, is used by jpa provider.
		}
		
		@EmbeddedId
		public AccountPersonRolePK getPrimaryKey() {
			return primaryKey;
		}

		public void setPrimaryKey(final AccountPersonRolePK primaryKey) {
			this.primaryKey = primaryKey;
		}

		@Transient
		public Person getPerson() {
			return getPrimaryKey().getPerson();
		}

		public void setPerson(final Person person) {
			getPrimaryKey().setPerson(person);
		}

		@Transient
		public Account getAccount() {
			return getPrimaryKey().getAccount();
		}

		public void setAccount(final Account account) {
			getPrimaryKey().setAccount(account);
		}

		@Transient
		public Role getRole() {
			return getPrimaryKey().getRole();
		}

		public void setRole(final Role role) {
			getPrimaryKey().setRole(role);
		}

		public boolean isActivated() {
			return activated;
		}

		public void setActivated(final boolean activated) {
			this.activated = activated;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "AccountPersonRole [primaryKey=" + primaryKey + ", activated=" + activated + "]";
		}
	}
	
	@Embeddable
	public class AccountPersonRolePK implements Serializable {

		private static final long serialVersionUID = -246901047331064431L;
		private Person person;
		private Account account;
		private Role role;

		public AccountPersonRolePK() {
			// 
			// Default co, is used by jpa provider.
		}
		
		@ManyToOne(cascade = CascadeType.ALL)
		public Person getPerson() {
			return person;
		}

		public void setPerson(final Person person) {
			this.person = person;
		}

		@ManyToOne(cascade = CascadeType.ALL)
		public Account getAccount() {
			return account;
		}

		public void setAccount(final Account account) {
			this.account = account;
		}

		@ManyToOne(cascade = CascadeType.ALL)
		public Role getRole() {
			return role;
		}

		public void setRole(final Role role) {
			this.role = role;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "AccountPersonRolePK [person=" + person + ", account=" + account + ", role=" + role + "]";
		}
	}
}
