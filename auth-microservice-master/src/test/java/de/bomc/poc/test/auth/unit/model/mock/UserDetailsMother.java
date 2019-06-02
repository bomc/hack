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

import de.bomc.poc.auth.model.usermanagement.UserDetails;
import de.bomc.poc.auth.model.usermanagement.UserDetails.SEX;

/**
 * A pattern for creating test instances by the object mother pattern.
 * <p/>
 * http://martinfowler.com/bliki/ObjectMother.html
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class UserDetailsMother {

	protected UserDetails createInstance() {
		final UserDetails userDetails = new UserDetails();
		
		userDetails.setComment("my_comment");
		userDetails.setImage("my_image".getBytes());
		userDetails.setPhoneNo("12341231234567");
		userDetails.setSex(SEX.MALE);
		
		return userDetails;
	}
}

