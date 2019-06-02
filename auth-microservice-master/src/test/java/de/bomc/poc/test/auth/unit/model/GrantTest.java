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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.bomc.poc.auth.model.usermanagement.Grant;
import de.bomc.poc.auth.model.usermanagement.Role;
import de.bomc.poc.test.auth.unit.AbstractUnitTest;
import de.bomc.poc.test.auth.unit.model.mock.GrantMother;

import java.util.HashSet;
import java.util.Set;

/**
 * Tests the functionality of the {@link Role}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GrantTest extends AbstractUnitTest {

	private static final String TEST_GRANT_NAME = "Test_Grant_Name";
	private static final String TEST_GRANT_DESCRIPTION = "Test_Grant_Description";
	
	@Test
	public void test01_createGrant_Pass() {
		System.out.println("GrantTest#test01_createGrant_Pass");
		
		final GrantMother grantMother = new GrantMother(this.emProvider1, TEST_GRANT_NAME);
		final Grant grant = grantMother.instance();
		
		assertThat(grant.getId(), notNullValue());
		assertThat(grant.isNew(), equalTo(false));
		
		System.out.println(grant.toString());
	}

	@Test
	public void test02_createGrant_co_nameDescription() {
		System.out.println("GrantTest#test02_createGrant_co_nameDescription");

		final Grant grant = new Grant(TEST_GRANT_NAME, TEST_GRANT_DESCRIPTION);
		this.emProvider1.tx().begin();
		this.emProvider1.getEntityManager().persist(grant);
		this.emProvider1.tx().commit();

		assertThat(grant, notNullValue());
	}

	/**
	 * Test hashCode() and equals(obj).
	 */
	@Test
	public final void test10_hashCodeEquals_Pass() {
		System.out.println("GrantTest#test10_hashCodeEquals_Pass");

		Grant grant1 = new Grant(TEST_GRANT_NAME + "-1");
		Grant grant2 = new Grant(TEST_GRANT_NAME + "-1");
		Grant grant3 = new Grant(TEST_GRANT_NAME + "-2");

		// Just the name is considered.
		assertThat(grant1, is(equalTo(grant2)));
		assertThat(grant1, not(equalTo(grant3)));
		assertThat(grant2, not(equalTo(grant3)));

		// Test behavior in hashed collections
		Set<Grant> grants = new HashSet<>();
		grants.add(grant1);
		grants.add(grant2);
		assertThat(grants.size(), is(equalTo(1)));

		grants.add(grant3);
		assertThat(grants.size(), is(equalTo(2)));
	}
}
