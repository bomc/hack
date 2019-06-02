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

import de.bomc.poc.exception.app.AppInvalidPasswordException;
import de.bomc.poc.auth.model.usermanagement.Role;
import de.bomc.poc.auth.model.usermanagement.SystemUser;
import de.bomc.poc.auth.model.usermanagement.User;
import de.bomc.poc.auth.model.usermanagement.UserDetails;
import de.bomc.poc.auth.model.usermanagement.UserDetails.SEX;
import de.bomc.poc.auth.model.usermanagement.UserPassword;
import de.bomc.poc.test.auth.unit.AbstractUnitTest;
import de.bomc.poc.test.auth.unit.model.mock.RoleMother;
import de.bomc.poc.test.auth.unit.model.mock.UserMother;

import org.hamcrest.core.IsInstanceOf;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests the functionality of the {@link User}.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTest extends AbstractUnitTest {

    private static final String TEST_USER_NAME_1 = "Test Bomc1";
    private static final String TEST_USER_NAME_2 = "Test Bomc2";
    private static final String TEST_PASSWORD = "Test Bomc Pwd";
    private static final String TEST_ROLE_NAME = "BomcRole";
    @Rule
    public ExpectedException exception = ExpectedException.none();

    // __________________________________________________________________
    // Simple tests.
    // ------------------------------------------------------------------

    @Test
    public void test01_createUser_Pass() {
        System.out.println("UserTest#test01_createUser_Pass");

        this.entityManager1 = this.emProvider1.getEntityManager();
        assertThat(entityManager1, is(notNullValue()));

        this.emProvider1.tx()
                        .begin();

        final User user = new User(TEST_USER_NAME_1);
        assertThat(user.isNew(), equalTo(true));

        this.entityManager1.persist(user);
        this.entityManager1.flush();

        this.emProvider1.tx()
                        .commit();

        assertThat(user.getId(), notNullValue());
        assertThat(user.isNew(), equalTo(false));

        System.out.println(user.toString());
    }

    @Test
    public void test02_createUser_WithUserPassword_Pass() {
        System.out.println("UserTest#test02_createUser_WithUserPassword_Pass");

        this.entityManager1 = this.emProvider1.getEntityManager();
        assertThat(entityManager1, is(notNullValue()));

        this.emProvider1.tx()
                        .begin();

        final User user = new User(TEST_USER_NAME_1);
        user.setNewPassword(TEST_PASSWORD);

        this.entityManager1.persist(user);
        this.entityManager1.flush();

        this.emProvider1.tx()
                        .commit();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getUsername(), is(equalTo(TEST_USER_NAME_1)));
        assertThat(user.getPassword(), is(equalTo(TEST_PASSWORD)));
        assertThat(user.isNew(), equalTo(false));

        System.out.println(user.toString());
    }

    @Test
    public void test03_createSystemUser_Pass() {
        System.out.println("UserTest#test03_createSystemUser_Pass");

        this.entityManager1 = this.emProvider1.getEntityManager();
        assertThat(entityManager1, is(notNullValue()));

        this.emProvider1.tx()
                        .begin();

        final SystemUser systemUser = new SystemUser(SystemUser.SYSTEM_USERNAME, TEST_PASSWORD);

        this.entityManager1.persist(systemUser);
        this.entityManager1.flush();

        this.emProvider1.tx()
                        .commit();

        assertThat(systemUser.getId(), notNullValue());

        System.out.println(systemUser.toString());
    }

    @Test
    public void test04_createUser_WithUserDetails_Pass() {
        System.out.println("UserTest#test04_createUser_WithUserDetails_Pass");

        this.emProvider1.tx()
                        .begin();

        final User user1 = new User(TEST_USER_NAME_1);

        final UserDetails userDetails = new UserDetails();
        userDetails.setComment("my_comment");
        userDetails.setPhoneNo("00491234123456789");
        userDetails.setSex(SEX.valueOf(SEX.MALE.name()));
        userDetails.setImage("img".getBytes());

        user1.setUserDetails(userDetails);

        this.emProvider1.getEntityManager()
                        .persist(user1);

        this.emProvider1.tx()
                        .commit();

        assertThat(user1.getId(), notNullValue());
        assertThat(user1.getUserDetails()
                        .getComment(), is(equalTo("my_comment")));
    }

    // __________________________________________________________________
    // Test methods.
    // ------------------------------------------------------------------

    /**
     * Test that only valid passwords can be stored and the removal of the oldest password in the history list works.
     */
    @Test
    public void test05_passwordHistory_Pass() {
        System.out.println("UserTest#test05_passwordHistory_Pass");

        final User u1 = new User(TEST_USER_NAME_1);
        for (int i = 0; i <= User.NUMBER_STORED_PASSWORDS + 2; i++) {
            try {
                if (i < User.NUMBER_STORED_PASSWORDS) {
                    u1.setNewPassword(String.valueOf(i));
                } else {
                    System.out.println("UserTest#test05_passwordHistory_Pass - Number of password history exceeded, resetting to 0, " + "set a already inserted password. A AppInvalidPasswordException will be thrown.");
                    u1.setNewPassword("0");
                }
            } catch (AppInvalidPasswordException e) {
                System.out.println("UserTest#test05_passwordHistory_Pass - AppInvalidPasswordException " + e.getMessage());

                if (i < User.NUMBER_STORED_PASSWORDS) {
                    fail("UserTest#test05_passwordHistory_Pass - Number of acceptable passwords not exceeded");
                } else {
                    System.out.println("UserTest#test05_passwordHistory_Pass - OK: Exception because password is already in the list, set password to '" + i + "'");
                    this.setPasswordSafety(u1, String.valueOf(i));
                }
            }
            try {
                // Just wait to setup changeDate correctly. Usually password
                // changes aren't done within the same millisecond.
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Ignore
                System.out.println("UserTest#test05_passwordHistory_Pass - Error: " + e.getMessage());
            }
        }

        u1.getPasswords()
          .forEach(pwd -> System.out.println("UserTest#test05_passwordHistory_Pass - pwd=" + pwd + ", size=" + u1.getPasswords()
                                                                                                                 .size()));

        // Verify that the password list was sorted in the correct order.
        u1.getPasswords()
          .forEach(System.out::println);
        LocalDateTime oldPassword = null;
        for (UserPassword pw : u1.getPasswords()) {
            if (oldPassword == null) {
                oldPassword = pw.getPasswordChanged();
                continue;
            }

            assertThat("Must be sorted ascending.", pw.getPasswordChanged()
                                                      .compareTo(oldPassword), equalTo(1));

            oldPassword = pw.getPasswordChanged();
        }
    }

    private void setPasswordSafety(final User u, final String password) {
        try {
            u.setNewPassword(password);
        } catch (AppInvalidPasswordException e) {
            System.out.println("UserTest#test05_passwordHistory_Pass#setPasswordSafety - Error: " + e.getMessage());
        }
    }

    @Test
    public void test06_setSamePassword_Fail() throws AppInvalidPasswordException {
        System.out.println("UserTest#test06_setSamePassword_Fail");

        this.exception.expect(AppInvalidPasswordException.class);
        this.exception.expectMessage("Trying to set the new password equals to the current password.");

        final User user1 = new User(TEST_USER_NAME_1);

        user1.setNewPassword(TEST_PASSWORD);

        // Set password again, this wll raise a AppInvalidPasswordException.
        user1.setNewPassword(TEST_PASSWORD);
    }

    @Test
    public void test07_overwriteUsername_Pass() {
        System.out.println("UserTest#test07_overwriteUsername_Pass");

        final UserMother userMother1 = new UserMother(this.emProvider1, TEST_USER_NAME_1);
        final User persistedUser1 = userMother1.instance();

        assertThat(persistedUser1, not(equalTo(TEST_USER_NAME_2)));
        persistedUser1.setUsername(TEST_USER_NAME_2);

        this.emProvider1.tx()
                        .begin();
        this.emProvider1.getEntityManager()
                        .persist(persistedUser1);
        this.emProvider1.tx()
                        .commit();

        assertThat(persistedUser1.getUsername(), is(equalTo(TEST_USER_NAME_2)));
    }

    @Test
    public void test08_hasPasswordChanged_Pass() {
        System.out.println("UserTest#test08_hasPasswordChanged_Pass");

        final UserMother userMother1 = new UserMother(this.emProvider1, TEST_USER_NAME_1);
        final User persistedUser1 = userMother1.instance();

        assertThat(persistedUser1, notNullValue());
        this.emProvider1.tx()
                        .begin();
        // Set the first password.
        persistedUser1.setNewPassword(TEST_PASSWORD);

        this.emProvider1.getEntityManager()
                        .persist(persistedUser1);
        this.emProvider1.tx()
                        .commit();
        assertThat(persistedUser1.hasPasswordChanged(), is(equalTo(true)));
    }

    /**
     * Test hashCode() and equals(obj).
     */
    @Test
    public final void test09_hashCodeEquals_Pass() {
        System.out.println("UserTest#test09_hashCodeEquals_Pass");

        User user1 = new User(TEST_USER_NAME_1);
        User user2 = new User(TEST_USER_NAME_1);
        User user3 = new User(TEST_USER_NAME_2);

        // Just the name is considered.
        assertThat(user1, is(equalTo(user2)));
        assertThat(user1, not(equalTo(user3)));
        assertThat(user2, not(equalTo(user3)));

        // Test behavior in hashed collections
        Set<User> users = new HashSet<User>();
        users.add(user1);
        users.add(user2);
        assertThat(users.size(), is(equalTo(1)));

        users.add(user3);
        assertThat(users.size(), is(equalTo(2)));
    }

    // __________________________________________________________________
    // ManyToMany User <-> Role bidirectional.
    // ------------------------------------------------------------------

    @Test
    public void test10_addRoleToUser_removeRoleFromUser_Pass() {
        System.out.println("UserTest#test10_addRoleToUser_removeRoleFromUser_Pass");

        final UserMother userMother = new UserMother(this.emProvider1, TEST_USER_NAME_1);
        final User persistedUser = userMother.instance();

        final RoleMother roleMother = new RoleMother(this.emProvider1, TEST_ROLE_NAME);
        final Role persistedRole = roleMother.instance();
        //
        // Add role to user.
        this.emProvider1.tx().begin();
        boolean isAdded = persistedUser.addRole(persistedRole);
        this.emProvider1.tx().commit();
        assertThat(isAdded, is(equalTo(true)));
        assertThat(persistedUser.getRoles() .size(), is(equalTo(1)));
        //
        // Read role back from db.
        final Role role = this.emProvider1.getEntityManager().find(Role.class, persistedRole.getId());
        assertThat(role, notNullValue());
        //
        // Remove user from role.
        this.emProvider1.tx().begin();
        final boolean isRemoved = role.removeUser(persistedUser);
        this.emProvider1.tx().commit();
        assertThat(isRemoved, is(equalTo(true)));
        assertThat(role.getUsers().size(), is(equalTo(0)));
        //
        // The returnedUser must not be removed in db.
        final User returnedUser = this.emProvider1.getEntityManager().find(User.class, persistedUser.getId());
        assertThat(returnedUser.getId(), notNullValue());
    }

    @Test
    public void test11_removeUserInDb_WhenUserIsReferencedByRoles_Pass() {
        System.out.println("UserTest#test11_removeUserInDb_WhenUserIsReferencedByRoles_Pass");

        final UserMother userMother1 = new UserMother(this.emProvider1, TEST_USER_NAME_1);
        final User persistedUser1 = userMother1.instance();

        final RoleMother roleMother1 = new RoleMother(this.emProvider1, TEST_ROLE_NAME + "-1");
        final Role persistedRole1 = roleMother1.instance();

        final RoleMother roleMother2 = new RoleMother(this.emProvider1, TEST_ROLE_NAME + "-2");
        final Role persistedRole2 = roleMother2.instance();
        //
        // Add first role to user.
        this.emProvider1.tx()
                        .begin();
        boolean isAdded = persistedUser1.addRole(persistedRole1);
        this.emProvider1.tx()
                        .commit();
        assertThat(isAdded, is(equalTo(true)));
        assertThat(persistedUser1.getRoles()
                                 .size(), is(equalTo(1)));
        //
        // Add second role to user.
        this.emProvider1.tx()
                        .begin();
        isAdded = persistedUser1.addRole(persistedRole2);
        this.emProvider1.tx()
                        .commit();
        assertThat(isAdded, is(equalTo(true)));
        assertThat(persistedUser1.getRoles()
                                 .size(), is(equalTo(2)));
        //
        // Delete User in db.
        this.emProvider1.tx()
                        .begin();
        final Long id = persistedUser1.getId();
        this.emProvider1.getEntityManager()
                        .remove(persistedUser1);
        this.emProvider1.tx()
                        .commit();
        final User
            removedUser =
            this.emProvider1.getEntityManager()
                            .find(User.class, id);
        assertThat(removedUser, nullValue());
        // Roles must not be deleted in db.
        final Role
            retPersistedRole1 =
            this.emProvider1.getEntityManager()
                            .find(Role.class, persistedRole1.getId());
        assertThat(retPersistedRole1, notNullValue());
        final Role
            retPersistedRole2 =
            this.emProvider1.getEntityManager()
                            .find(Role.class, persistedRole2.getId());
        assertThat(retPersistedRole2, notNullValue());
    }

    @Test
    public void test12_addRoleSetToUser_Pass() {
        System.out.println("UserTest#test12_addRoleSetToUser_Pass");

        final UserMother userMother1 = new UserMother(this.emProvider1, TEST_USER_NAME_1);
        final User persistedUser1 = userMother1.instance();

        final RoleMother roleMother1 = new RoleMother(this.emProvider1, TEST_PASSWORD + "-1");
        final Role persistedRole1 = roleMother1.instance();

        final RoleMother roleMother2 = new RoleMother(this.emProvider1, TEST_PASSWORD + "-2");
        final Role persistedRole2 = roleMother2.instance();

        this.emProvider1.tx()
                        .begin();
        final Set<Role> roleSet = new HashSet<>();
        roleSet.add(persistedRole1);
        roleSet.add(persistedRole2);
        persistedUser1.setRoles(roleSet);
        this.emProvider1.getEntityManager()
                        .persist(persistedUser1);
        this.emProvider1.tx()
                        .commit();

        assertThat(persistedRole1.getUsers()
                                 .size(), is(equalTo(1)));
        assertThat(persistedRole2.getUsers()
                                 .size(), is(equalTo(1)));
    }

    @Test
    public void test13_removeUserWithUserPasswords_Pass() {
        System.out.println("UserTest#test13_removeUserWithUserPasswords_Pass");

        final UserMother userMother1 = new UserMother(this.emProvider1, TEST_USER_NAME_1);
        final User persistedUser1 = userMother1.instance();
        assertThat(persistedUser1, notNullValue());

        this.emProvider1.tx()
                        .begin();
        // Set the first password
        persistedUser1.setNewPassword(TEST_PASSWORD + "-1");
        persistedUser1.setNewPassword(TEST_PASSWORD + "-2");
        persistedUser1.setNewPassword(TEST_PASSWORD + "-3");

        this.emProvider1.getEntityManager()
                        .persist(persistedUser1);
        this.emProvider1.tx()
                        .commit();
        assertThat(persistedUser1.getPasswords()
                                 .size(), is(equalTo(3)));

        final List<UserPassword> passwords = persistedUser1.getPasswords();

        this.emProvider1.tx()
                        .begin();
        this.emProvider1.getEntityManager()
                        .remove(persistedUser1);
        this.emProvider1.tx()
                        .commit();
        //
        // The UserPassword instance must be deleted too.
        final UserPassword
            up =
            this.emProvider1.getEntityManager()
                            .find(UserPassword.class, passwords.get(0)
                                                               .getId());
        assertThat(up, nullValue());
    }

    @Test
    public void test14_removeRoleFromUser_Pass() {
        System.out.println("UserTest#test14_removeRoleFromUser_Pass");

        final UserMother userMother1 = new UserMother(this.emProvider1, TEST_USER_NAME_1);
        final User user1 = userMother1.instance();
        assertThat(user1, notNullValue());

        final RoleMother roleMother1 = new RoleMother(this.emProvider1, TEST_ROLE_NAME);
        final Role role1 = roleMother1.instance();
        assertThat(user1, notNullValue());

        this.emProvider1.tx()
                        .begin();
        user1.addRole(role1);
        this.emProvider1.tx()
                        .commit();

        final User
            persistedUser =
            this.emProvider1.getEntityManager()
                            .find(User.class, user1.getId());
        assertThat(persistedUser, notNullValue());
        assertThat(persistedUser.getRoles()
                                .size(), is(equalTo(1)));
        //
        // Remove role from user.
        this.emProvider1.tx()
                        .begin();
        user1.removeRole(role1);
        this.emProvider1.tx()
                        .commit();

        final User
            persistedUserWithRemovedRole =
            this.emProvider1.getEntityManager()
                            .find(User.class, user1.getId());
        assertThat(persistedUserWithRemovedRole, notNullValue());
        assertThat(persistedUserWithRemovedRole.getRoles()
                                               .size(), is(equalTo(0)));
    }

    // __________________________________________________________________
    // JPA cascading lifecycle.
    // ------------------------------------------------------------------
    @Test
    public void test16_lifecycleCascade() {
        System.out.println("UserTest#test16_lifecycleCascade");
        final UserMother userMother1 = new UserMother(this.emProvider1, TEST_USER_NAME_1);
        final User user1 = userMother1.instance();
        assertThat(user1, notNullValue());

        final RoleMother roleMother1 = new RoleMother(this.emProvider1, TEST_ROLE_NAME);
        final Role role1 = roleMother1.instance();
        assertThat(user1, notNullValue());

        //
        // Add
        user1.addRole(role1);

        this.emProvider1.tx()
                        .begin();
        this.emProvider1.getEntityManager()
                        .merge(user1);
        this.emProvider1.tx()
                        .commit();

        Long cnt = (Long)this.emProvider1.getEntityManager().createQuery("select count(r) from Role r where r.name = :rolename")
                                      .setParameter("rolename", TEST_ROLE_NAME)
                                      .getSingleResult();
        assertThat(cnt, is(equalTo(1L)));

        cnt = (Long)this.emProvider1.getEntityManager().createQuery("select count(u) from User u where u.username = :username")
                                 .setParameter("username", TEST_USER_NAME_1)
                                 .getSingleResult();
        assertThat(cnt, is(equalTo(1L)));

        //
        // Test remove, nothing should happpen. The relationship is not annotated with 'CascadeType.Remove.'
        user1.removeRole(role1);

        this.emProvider1.tx()
                        .begin();
        this.emProvider1.getEntityManager()
                        .merge(user1);
        this.emProvider1.tx()
                        .commit();

        cnt = (Long)this.emProvider1.getEntityManager().createQuery("select count(r) from Role r where r.name = :rolename")
                                      .setParameter("rolename", TEST_ROLE_NAME)
                                      .getSingleResult();
        assertThat(cnt, is(equalTo(1L)));

        cnt = (Long)this.emProvider1.getEntityManager().createQuery("select count(u) from User u where u.username = :username")
                                 .setParameter("username", TEST_USER_NAME_1)
                                 .getSingleResult();
        assertThat(cnt, is(equalTo(1L)));
    }

    // __________________________________________________________________
    // Optimistic locking exception.
    // ------------------------------------------------------------------

    @Test
    public void test20_versionOptimisticLocking_Pass() throws Exception {
        System.out.println("UserTest#test20_versionOptimisticLocking_Pass");

        // _____________________________________________________
        // Prepare Test:
        final UserMother userMother1 = new UserMother(this.emProvider1, TEST_USER_NAME_1);
        final User user1 = userMother1.instance();
        assertThat(user1, notNullValue());

        this.entityManager1 = this.emProvider1.getEntityManager();

        // _____________________________________________________
        // Prepare test finished.

        // Start the first tx.
        this.emProvider1.tx()
                        .begin();

        final User userTx1 = this.entityManager1.getReference(User.class, user1.getId());

        System.out.println("UserTest#test20_versionOptimisticLocking_Pass - start 2nd tx, to force the optimistic locking.");

        final CountDownLatch latch = new CountDownLatch(1);
        Thread t = new Thread(new Runnable() {
            public void run() {
                // Start 2nd access in a new transaction.
                entityManager2 = emProvider2.getEntityManager();

                try {
                    // Start tx2...
                    emProvider2.tx()
                               .begin();

                    final User userTx2 = entityManager2.getReference(User.class, user1.getId());
                    // Change the user enabled state.
                    userTx2.setEnabled(false);

                    final User mergedUserTx2 = entityManager2.merge(userTx2);

                    System.out.println("UserTest#test20_versionOptimisticLocking_Pass [mergedUserTx2=" + mergedUserTx2.toString() + "]");

                    entityManager2.flush();
                } finally {
                    emProvider2.tx()
                               .commit();
                    // Signal that tx2 is committed.
                    System.out.println("UserTest#test20_versionOptimisticLocking_Pass - Signal that tx2 is committed.");
                    latch.countDown();
                }
            }
        });

        t.setDaemon(true);
        t.setName("UserTest#test20_versionOptimisticLocking_Pass-ForceIncrement tx2");
        t.start();

        System.out.println("UserTest#test20_versionOptimisticLocking_Pass-ForceIncrement:  wait on BG thread [entityManager.isopen=" + entityManager1.isOpen() + "]");
        boolean latchSet = latch.await(5, TimeUnit.SECONDS);

        assertThat("UserTest#test20_versionOptimisticLocking_Pass background test thread finished (lock timeout is broken)", true, is(equalTo(latchSet)));

        // Set a new value, to force a 'OptimisticLockingException' during the
        // commit.
        userTx1.setExpirationDate(LocalDateTime.of(2025, 11, 29, 10, 10));
        final User mergedUserTx1 = this.entityManager1.merge(userTx1);
        System.out.println("UserTest#test20_versionOptimisticLocking_Pass - [mergedUserTx1=" + mergedUserTx1.toString() + "]");

        try {
            // Force the 'OptimisticLockingException'.
            this.emProvider1.tx()
                            .commit();
            // Should not be invoked.
            fail("#test20_versionOptimisticLocking_Pass - The testcase should be failed, a exception should be thrown.");
        } catch (Throwable expectedToFail) {
            while (expectedToFail != null && !(expectedToFail instanceof javax.persistence.OptimisticLockException)) {
                expectedToFail = expectedToFail.getCause();
            }
            assertThat("UserTest#test20_versionOptimisticLocking_Pass - upgrade to OPTIMISTIC_FORCE_INCREMENT is expected to fail at end of transaction1 since transaction2 already updated the entity", expectedToFail, is(
                IsInstanceOf.instanceOf(OptimisticLockException.class)));
        }
    }
}
