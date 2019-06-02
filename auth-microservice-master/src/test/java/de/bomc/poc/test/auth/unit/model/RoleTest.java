/**
 * Project: MY_POC
 * <p/>
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
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.auth.unit.model;

import de.bomc.poc.auth.model.usermanagement.Grant;
import de.bomc.poc.auth.model.usermanagement.Role;
import de.bomc.poc.auth.model.usermanagement.User;
import de.bomc.poc.test.auth.unit.AbstractUnitTest;
import de.bomc.poc.test.auth.unit.model.mock.GrantMother;
import de.bomc.poc.test.auth.unit.model.mock.RoleMother;
import de.bomc.poc.test.auth.unit.model.mock.UserMother;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the functionality of the {@link Role}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RoleTest extends AbstractUnitTest {

	private static final String TEST_ROLE_NAME_1 = "Test_Role_1";
	private static final String TEST_USER_NAME = "Test_User";
	private static final String TEST_DESCRIPTION = "Test_Description";
	private static final String TEST_GRANT_NAME = "Bomc_Grant";

	// __________________________________________________________________
	// Simple tests.
	// ------------------------------------------------------------------

	@Test
	public void test01_createRole_Pass() {
		System.out.println("RoleTest#test01_createRole_Pass");

		final RoleMother roleMother = new RoleMother(this.emProvider1, TEST_ROLE_NAME_1);
		final Role role = roleMother.instance();

		assertThat(role.getId(), notNullValue());
		assertThat(role.isNew(), equalTo(false));
		assertThat(role.isImmutable(), equalTo(false));

		System.out.println(role.toString());
	}

	@Test
	public void test02_createRole_co_WithRoleNameAndDescription_Pass() {
		System.out.println("RoleTest#test02_createRole_co_WithRoleNameAndDescription_Pass");

		this.entityManager1 = this.emProvider1.getEntityManager();
		assertThat(entityManager1, is(notNullValue()));

		this.emProvider1.tx().begin();

		final Role role = new Role(TEST_ROLE_NAME_1, TEST_DESCRIPTION);
		assertThat(role.isNew(), equalTo(true));

		this.entityManager1.persist(role);
		this.entityManager1.flush();

		this.emProvider1.tx().commit();

		assertThat(role.getId(), notNullValue());
		assertThat(role.isNew(), is(equalTo(false)));
		assertThat(role.isImmutable(), is(equalTo(false)));
		assertThat(role.getDescription(), is(equalTo(TEST_DESCRIPTION)));
		assertThat(role.getName(), is(equalTo(TEST_ROLE_NAME_1)));

		System.out.println(role.toString());
	}

	// __________________________________________________________________
	// ManyToMany Role <-> User bidirectional.
	// ------------------------------------------------------------------

	@Test
	public void test03_addUsersToRole_Pass() {
		System.out.println("RoleTest#test03_addUserToRole_Pass");

		this.entityManager1 = this.emProvider1.getEntityManager();
		assertThat(entityManager1, is(notNullValue()));

		final RoleMother roleMother = new RoleMother(this.emProvider1, TEST_ROLE_NAME_1);
		final Role role1 = roleMother.instance();
		assertThat(role1.getId(), notNullValue());

		UserMother userMother = new UserMother(this.emProvider1, TEST_USER_NAME + "-1");
		final User user1 = userMother.instance();
		assertThat(user1.getId(), notNullValue());

		userMother = new UserMother(this.emProvider1, TEST_USER_NAME + "-2");
		final User user2 = userMother.instance();
		assertThat(user2.getId(), notNullValue());
		//
		// Users to role.
		this.emProvider1.tx().begin();

		boolean isAdded = role1.addUser(user1);
		assertThat(isAdded, is(equalTo(true)));

		isAdded = role1.addUser(user2);
		assertThat(isAdded, is(equalTo(true)));

		this.emProvider1.tx().commit();
		//
		// Check if users are added.
		final Role retRole = this.entityManager1.find(Role.class, role1.getId());
		assertThat(retRole.getUsers().size(), is(equalTo(2)));
		// Check if role is internal added to the users.
		final Set<User> users = retRole.getUsers();

		users.forEach(user -> assertThat(user.getRoles().size(), is(equalTo(1))));
	}

	@Test
	public void test04_removeUsersToRole_Pass() {
		System.out.println("RoleTest#test04_removeUserToRole_Pass");

		this.entityManager1 = this.emProvider1.getEntityManager();
		assertThat(entityManager1, is(notNullValue()));

		final RoleMother roleMother = new RoleMother(this.emProvider1, TEST_ROLE_NAME_1);
		final Role role1 = roleMother.instance();
		assertThat(role1.getId(), notNullValue());

		UserMother userMother = new UserMother(this.emProvider1, TEST_USER_NAME + "-1");
		final User user1 = userMother.instance();
		assertThat(user1.getId(), notNullValue());

		userMother = new UserMother(this.emProvider1, TEST_USER_NAME + "-2");
		final User user2 = userMother.instance();
		assertThat(user2.getId(), notNullValue());
		//
		// Users to role.
		this.emProvider1.tx().begin();

		boolean isAdded = role1.addUser(user1);
		assertThat(isAdded, is(equalTo(true)));

		isAdded = role1.addUser(user2);
		assertThat(isAdded, is(equalTo(true)));

		this.emProvider1.tx().commit();
		//
		// Check if users are added.
		final Role retRole = this.entityManager1.find(Role.class, role1.getId());
		assertThat(retRole.getUsers().size(), is(equalTo(2)));
		// Check if role is internal added to the users.
		final Set<User> users = retRole.getUsers();

		users.forEach(user -> assertThat(user.getRoles().size(), is(equalTo(1))));

		//
		// Remove users from role.
		this.emProvider1.tx().begin();
		//
		// The users must be removed from the role...
		role1.removeUsers();

		this.emProvider1.tx().commit();

		assertThat(role1.getUsers().size(), is(equalTo(0)));
		//
		// ... but the users itself must not be deleted, only the reference of
		// the user should be deleted from the user.
		final User retUser1 = this.emProvider1.getEntityManager().find(User.class, user1.getId());
		assertThat(retUser1, notNullValue());

		final User retUser2 = this.emProvider1.getEntityManager().find(User.class, user2.getId());
		assertThat(retUser2, notNullValue());
	}

	@Test
	public void test05_addRoleSetToUser_Pass() {
		System.out.println("RoleTest#test05_addRoleSetToUser_Pass");

		this.entityManager1 = this.emProvider1.getEntityManager();
		assertThat(entityManager1, is(notNullValue()));

		final RoleMother roleMother = new RoleMother(this.emProvider1, TEST_ROLE_NAME_1);
		final Role role1 = roleMother.instance();
		assertThat(role1.getId(), notNullValue());

		UserMother userMother = new UserMother(this.emProvider1, TEST_USER_NAME + "-1");
		final User user1 = userMother.instance();
		assertThat(user1.getId(), notNullValue());

		userMother = new UserMother(this.emProvider1, TEST_USER_NAME + "-2");
		final User user2 = userMother.instance();
		assertThat(user2.getId(), notNullValue());
		//
		// Users to role.
		this.emProvider1.tx().begin();

		Set<User> userSet = new HashSet<>();
		userSet.add(user1);
		userSet.add(user2);

		role1.setUsers(userSet);

		this.emProvider1.tx().commit();

		assertThat(role1.getUsers().size(), is(equalTo(2)));
		assertThat(user1.getRoles().iterator().next().getId(), is(equalTo(role1.getId())));
		assertThat(user2.getRoles().iterator().next().getId(), is(equalTo(role1.getId())));
	}

	// __________________________________________________________________
	// ManyToMany Role <-> Grant unidirectional, owner is the role of this
	// relationship.
	// ------------------------------------------------------------------

	@Test
	public void test06_addGrantToRole_Pass() {
		System.out.println("RoleTest#test06_addGrantToRole_Pass");

		final RoleMother roleMother = new RoleMother(this.emProvider1, TEST_ROLE_NAME_1);
		final Role role1 = roleMother.instance();
		assertThat(role1.getId(), notNullValue());

		final GrantMother grantMother = new GrantMother(this.emProvider1, TEST_GRANT_NAME);
		final Grant grant1 = grantMother.instance();
		assertThat(grant1.getId(), notNullValue());

		this.emProvider1.tx().begin();
		final boolean isAdded = role1.addGrant(grant1);
		assertThat(isAdded, is(equalTo(true)));
		this.emProvider1.tx().commit();

		final Role retRole = this.emProvider1.getEntityManager().find(Role.class, role1.getId());
		assertThat(retRole.getGrants().size(), is(equalTo(1)));
	}

	@Test
	public void test07_removeRole_WhenGrantsAreAdded_Pass() {
		System.out.println("RoleTest#test07_removeRole_WhenGrantsAreAdded_Pass");

		final RoleMother roleMother = new RoleMother(this.emProvider1, TEST_ROLE_NAME_1);
		final Role role1 = roleMother.instance();
		assertThat(role1.getId(), notNullValue());

		final GrantMother grantMother1 = new GrantMother(this.emProvider1, TEST_GRANT_NAME + "-1");
		final Grant grant1 = grantMother1.instance();
		assertThat(grant1.getId(), notNullValue());

		final GrantMother grantMother2 = new GrantMother(this.emProvider1, TEST_GRANT_NAME + "-2");
		final Grant grant2 = grantMother2.instance();
		assertThat(grant2.getId(), notNullValue());

		Set<Grant> grantSet = new HashSet<>(2);
		grantSet.add(grant1);
		grantSet.add(grant2);

		this.emProvider1.tx().begin();
		role1.setGrants(grantSet);
		this.emProvider1.tx().commit();

		final Role retRole = this.emProvider1.getEntityManager().find(Role.class, role1.getId());
		assertThat(retRole.getGrants().size(), is(equalTo(2)));

		//
		// Now remove the role.
		this.emProvider1.tx().begin();
		final boolean isRemoved = role1.removeGrants(grantSet);
		assertThat(isRemoved, is(equalTo(true)));
		this.emProvider1.tx().commit();
		// The grant may not be deleted.
		final Grant retGrant = this.emProvider1.getEntityManager().find(Grant.class, grant1.getId());
		assertThat(retGrant, notNullValue());
	}

	@Test
	public void test08_removeGrant_FromRole_Pass() {
		System.out.println("RoleTest#test08_removeGrant_FromRole_Pass");

		final RoleMother roleMother = new RoleMother(this.emProvider1, TEST_ROLE_NAME_1);
		final Role role1 = roleMother.instance();
		assertThat(role1.getId(), notNullValue());

		final GrantMother grantMother1 = new GrantMother(this.emProvider1, TEST_GRANT_NAME + "-1");
		final Grant grant1 = grantMother1.instance();
		assertThat(grant1.getId(), notNullValue());

		final GrantMother grantMother2 = new GrantMother(this.emProvider1, TEST_GRANT_NAME + "-2");
		final Grant grant2 = grantMother2.instance();
		assertThat(grant2.getId(), notNullValue());

		Set<Grant> grantSet = new HashSet<>(2);
		grantSet.add(grant1);
		grantSet.add(grant2);

		this.emProvider1.tx().begin();
		role1.setGrants(grantSet);
		this.emProvider1.tx().commit();

		Role retRole = this.emProvider1.getEntityManager().find(Role.class, role1.getId());
		assertThat(retRole.getGrants().size(), is(equalTo(2)));
		//
		// Now remove the grant1.
		this.emProvider1.tx().begin();
		final boolean isRemoved = role1.removeGrant(grant1);
		assertThat(isRemoved, is(equalTo(true)));
		this.emProvider1.tx().commit();
		// The grant2 must not be deleted.
		final Grant retGrant2 = this.emProvider1.getEntityManager().find(Grant.class, grant2.getId());
		assertThat(retGrant2, notNullValue());
		// The Role has only one grant.
		retRole = this.emProvider1.getEntityManager().find(Role.class, role1.getId());
		assertThat(retRole.getGrants().size(), is(equalTo(1)));
	}

	@Test
	public void test09_removeRoleFromDb() {
		System.out.println("RoleTest#test09_removeRoleFromDb");

		final RoleMother roleMother = new RoleMother(this.emProvider1, TEST_ROLE_NAME_1);
		final Role role1 = roleMother.instance();
		assertThat(role1.getId(), notNullValue());

		final GrantMother grantMother1 = new GrantMother(this.emProvider1, TEST_GRANT_NAME + "-1");
		final Grant grant1 = grantMother1.instance();
		assertThat(grant1.getId(), notNullValue());

		final GrantMother grantMother2 = new GrantMother(this.emProvider1, TEST_GRANT_NAME + "-2");
		final Grant grant2 = grantMother2.instance();
		assertThat(grant2.getId(), notNullValue());

		Set<Grant> grantSet = new HashSet<>(2);
		grantSet.add(grant1);
		grantSet.add(grant2);

		this.emProvider1.tx().begin();
		role1.setGrants(grantSet);
		this.emProvider1.tx().commit();

		Role retRole = this.emProvider1.getEntityManager().find(Role.class, role1.getId());
		assertThat(retRole.getGrants().size(), is(equalTo(2)));
		//
		// Delete the role.
		this.emProvider1.tx().begin();
		this.emProvider1.getEntityManager().remove(role1);
		this.emProvider1.tx().commit();

		retRole = this.emProvider1.getEntityManager().find(Role.class, role1.getId());
		assertThat(retRole, nullValue());

		// The grant1 must not be deleted.
		final Grant retGrant1 = this.emProvider1.getEntityManager().find(Grant.class, grant1.getId());
		assertThat(retGrant1, notNullValue());

		// The grant2 must not be deleted.
		final Grant retGrant2 = this.emProvider1.getEntityManager().find(Grant.class, grant2.getId());
		assertThat(retGrant2, notNullValue());
	}

	@Test
	public void test10_roleBuilder_Pass() {
		System.out.println("RoleTest#test10_roleBuilder_Pass");

		Role role1 = new Role.Builder(TEST_ROLE_NAME_1).withDescription(TEST_DESCRIPTION).asImmutable().build();
		assertThat(role1.getName(), is(equalTo(TEST_ROLE_NAME_1)));
		assertThat(role1.getDescription(), is(equalTo(TEST_DESCRIPTION)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test11_namedQueryFindAll_Pass() {
		System.out.println("RoleTest#test11_namedQueryFindAll_Pass");

		UserMother userMother = new UserMother(this.emProvider1, TEST_USER_NAME + "-1");
		final User user1 = userMother.instance();
		assertThat(user1.getId(), notNullValue());

		final RoleMother roleMother = new RoleMother(this.emProvider1, TEST_ROLE_NAME_1);
		final Role role1 = roleMother.instance();
		assertThat(role1.getId(), notNullValue());

		final GrantMother grantMother1 = new GrantMother(this.emProvider1, TEST_GRANT_NAME + "-1");
		final Grant grant1 = grantMother1.instance();
		assertThat(grant1.getId(), notNullValue());

		final GrantMother grantMother2 = new GrantMother(this.emProvider1, TEST_GRANT_NAME + "-2");
		final Grant grant2 = grantMother2.instance();
		assertThat(grant2.getId(), notNullValue());

		final Set<Grant> grantSet = new HashSet<>(2);
		grantSet.add(grant1);
		grantSet.add(grant2);

		this.emProvider1.tx().begin();
		role1.setGrants(grantSet);
		user1.addRole(role1);
		this.emProvider1.tx().commit();

		this.entityManager1 = this.emProvider1.getEntityManager();
		List<Role> roleList = this.entityManager1.createNamedQuery(Role.NQ_FIND_ALL).getResultList();

		System.out.println("RoleTest#test11_namedQueryFindAll_Pass ----- iterate result -----");
		roleList.forEach(role -> {
			Set<Grant> set = role.getGrants();
			set.forEach(System.out::println);
		});
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test12_namedQueryFindByUsername_Pass() {
		System.out.println("RoleTest#test12_namedQueryFindByUsername_Pass");

		UserMother userMother = new UserMother(this.emProvider1, TEST_USER_NAME + "-1");
		final User user1 = userMother.instance();
		assertThat(user1.getId(), notNullValue());

		final RoleMother roleMother = new RoleMother(this.emProvider1, TEST_ROLE_NAME_1);
		final Role role1 = roleMother.instance();
		assertThat(role1.getId(), notNullValue());

		final RoleMother roleMother1 = new RoleMother(this.emProvider1, TEST_ROLE_NAME_1 + "-2");
		final Role role2 = roleMother1.instance();
		assertThat(role2.getId(), notNullValue());

		final GrantMother grantMother1 = new GrantMother(this.emProvider1, TEST_GRANT_NAME + "-1");
		final Grant grant1 = grantMother1.instance();
		assertThat(grant1.getId(), notNullValue());

		final GrantMother grantMother2 = new GrantMother(this.emProvider1, TEST_GRANT_NAME + "-2");
		final Grant grant2 = grantMother2.instance();
		assertThat(grant2.getId(), notNullValue());

		final Set<Grant> grantSet = new HashSet<>(2);
		grantSet.add(grant1);
		grantSet.add(grant2);

		this.emProvider1.tx().begin();
		role1.setGrants(grantSet);
		role2.setGrants(grantSet);
		user1.addRole(role1);
		user1.addRole(role2);
		this.emProvider1.tx().commit();

		this.entityManager1 = this.emProvider1.getEntityManager();
		List<Role> roleList = this.entityManager1.createNamedQuery(Role.NQ_FIND_ALL_BY_USERNAME)
				.setParameter(1, TEST_USER_NAME + "-1").getResultList();

		System.out.println("RoleTest#test12_namedQueryFindByUsername_Pass ----- iterate result -----");
		roleList.forEach(role -> {
			System.out.println("-----" + role.toString());
			Set<Grant> set = role.getGrants();
			set.forEach(System.out::println);
		});
	}

	/**
	 * Test hashCode() and equals(obj).
	 */
	@Test
	public final void test13_hashCodeEquals_Pass() {
		System.out.println("RoleTest#test13_hashCodeEquals_Pass");

		Role role1 = new Role(TEST_ROLE_NAME_1);
		Role role2 = new Role(TEST_ROLE_NAME_1);
		Role role3 = new Role(TEST_ROLE_NAME_1 + "-2");

		// Just the name is considered.
		assertThat(role1, is(equalTo(role2)));
		assertThat(role1, not(equalTo(role3)));
		assertThat(role2, not(equalTo(role3)));

		// Test behavior in hashed collections
		Set<Role> roles = new HashSet<>();
		roles.add(role1);
		roles.add(role2);
		assertThat(roles.size(), is(equalTo(1)));

		roles.add(role3);
		assertThat(roles.size(), is(equalTo(2)));
	}
}
