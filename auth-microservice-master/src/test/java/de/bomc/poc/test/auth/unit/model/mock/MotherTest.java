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
package de.bomc.poc.test.auth.unit.model.mock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import org.junit.Test;

import de.bomc.poc.auth.model.usermanagement.Role;
import de.bomc.poc.auth.model.usermanagement.User;
import de.bomc.poc.auth.model.usermanagement.UserDetails;
import de.bomc.poc.auth.model.usermanagement.UserDetails.SEX;
import de.bomc.poc.test.auth.unit.AbstractUnitTest;

/**
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class MotherTest extends AbstractUnitTest {

	@Test
	public void test01_createUserMother_Pass() {
		System.out.println("MotherTest#test01_createUserMother_Pass");

		final UserMother userMother = new UserMother(this.emProvider1, "tzdbmm1");
		final User user = userMother.instance();

		assertThat(user.getId(), notNullValue());
	}

	@Test
	public void test02_createRoleMother_Pass() {
		System.out.println("MotherTest#test02_createRoleMother_Pass");

		final RoleMother roleMother = new RoleMother(this.emProvider1, "role1");
		final Role role = roleMother.instance();

		assertThat(role.getId(), notNullValue());
	}
	
	@Test
	public void test03_createUserMotherWithUserDetails_Pass() {
		System.out.println("MotherTest#test03_createUserMotherWithUserDetails_Pass");

		final UserMother userMother = new UserMother(this.emProvider1, "tzdbmm3");
		final User user = userMother.instance();

		final UserDetailsMother userDetailsMother = new UserDetailsMother();
		
		final UserDetails userDetails = userDetailsMother.createInstance();		
		user.setUserDetails(userDetails);

		this.emProvider1.tx().begin();
		this.emProvider1.getEntityManager().persist(user);
		this.emProvider1.tx().commit();
		
		assertThat(user.getId(), greaterThanOrEqualTo(1L));
		assertThat(user.getUserDetails().getSex(), is(equalTo(SEX.MALE)));
	}
}
