/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.objmother;

import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.domain.model.UserDetailsEntity;

/**
 * A helper class to create a {@link UserDetailsEntity} in context of the
 * 'ObjectMother' pattern.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 26.09.2019
 */
public class UserDetailsMother {

	public static UserDetailsEntity instance() {
		final UserDetailsEntity userDetailsEntity = new UserDetailsEntity();
		
		userDetailsEntity.setComment(AbstractBaseUnit.USER_DETAILS_COMMENT);
		userDetailsEntity.setImage(AbstractBaseUnit.USER_DETAILS_IMAGE);
		userDetailsEntity.setPhoneNo(AbstractBaseUnit.USER_DETAILS_PHONE_NO);
		userDetailsEntity.setSex(AbstractBaseUnit.USER_DETAIL_SEX);
		
		return userDetailsEntity;
	}
}
