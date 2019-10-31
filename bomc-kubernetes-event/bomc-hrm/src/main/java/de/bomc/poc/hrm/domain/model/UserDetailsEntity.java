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
package de.bomc.poc.hrm.domain.model;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bomc.poc.hrm.domain.model.values.CoreTypeDefinitions;
import de.bomc.poc.hrm.domain.model.values.ImageProvider;
import lombok.Builder;
import lombok.ToString;

/**
 * Detailed information about an <code>UserEntity</code>.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
// LOMBOK
@ToString
@Builder
// JPA
@Embeddable
public class UserDetailsEntity implements ImageProvider, Serializable {

	private static final String LOG_PREFIX = "UserDetailsEntity#";
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsEntity.class.getName());
	private static final long serialVersionUID = 664778075559767489L;
	/**
	 * Some comment text of the <code>UserEntity</code>.
	 */
	@Column(name = "c_comment")
	@Size(min = CoreTypeDefinitions.DESCRIPTION_LENGTH, message = "must not be longer than " + CoreTypeDefinitions.DESCRIPTION_LENGTH +  " characters!")
	private String comment;
	/**
	 * Phone number assigned to the <code>UserEntity</code>. Matches german phone
	 * and fax numbers (including cell phone numbers) in various formats like:
	 * 004989123456, +49 89 123456, +49(89)123456, 089-1234-5678, 089 1234 5678,
	 * (089)1234-5678 Max. number of digits is 21.
	 */
	@Pattern(regexp = "^(((((((00|\\+)49[ \\-/]?)|0)[1-9][0-9]{1,4})[ \\-/]?)|((((00|\\+)49\\()|\\(0)[1-9][0-9]{1,4}\\)[ \\-/]?))[0-9]{1,7}([ \\-/]?[0-9]{1,5})?)$")
	@Column(name = "c_phone_no")
	private String phoneNo;
	/**
	 * An image of the <code>UserEntity</code>. Lazy fetched.
	 */
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "c_image")
	@Type(type="org.hibernate.type.BinaryType")
	private byte[] image;
	/**
	 * Sex of the <code>UserEntity</code>.
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "c_sex")
	private SEX sex;

	/* ----------------------------- constants ------------------- */

	/**
	 * The <code>UserEntity</code>s sex.
	 */
	public enum SEX {
		/**
		 * Male.
		 */
		MALE,
		/**
		 * Female.
		 */
		FEMALE
	}

	/* ----------------------------- constructors ---------------- */

	/**
	 * Create a new <code>UserDetailsEntity</code> instance.
	 */
	public UserDetailsEntity() {
		super();

		LOGGER.debug(LOG_PREFIX + "co");
	}
	
	/**
	 * Create a new <code>UserDetailsEntity</code> instance.
	 * 
	 * @param comment the given comment.
	 */
	public UserDetailsEntity(final String comment) {
		this.comment = comment;
	}
	
	/**
	 * Create a new <code>UserDetailsEntity</code> instance.
	 * 
	 * @param comment the given comment.
	 * @param phoneNo the given phoneNo.
	 * @param image   the given image.
	 * @param sex     the given sex enum.
	 */
	public UserDetailsEntity(final String comment, final String phoneNo, final byte[] image,
			final UserDetailsEntity.SEX sex) {
		this.comment = comment;
		this.phoneNo = phoneNo;
		this.image = image == null ? new byte[0] : Arrays.copyOf(image, image.length);
		this.sex = sex;
	}

	/* ----------------------------- methods --------------------- */

	/**
	 * Return the <code>UserEntity</code>s current phone number.
	 * 
	 * @return The phone number
	 */
	public String getPhoneNo() {
		LOGGER.debug(LOG_PREFIX + "getPhoneNo [phoneNo=" + phoneNo + "]");

		return this.phoneNo;
	}

	/**
	 * Change the phone number of the <code>UserEntity</code>.
	 * 
	 * @param phoneNo The new phone number
	 */
	public void setPhoneNo(final String phoneNo) {
		LOGGER.debug(LOG_PREFIX + "setPhoneNo [phoneNo=" + phoneNo + "]");

		this.phoneNo = phoneNo;
	}

	/**
	 * Return a comment text of the <code>UserEntity</code>.
	 * 
	 * @return The comment text
	 */
	public String getComment() {
		LOGGER.debug(LOG_PREFIX + "getComment [commit=" + this.comment + "]");

		return this.comment;
	}

	/**
	 * Change the comment text of the <code>UserEntity</code>.
	 * 
	 * @param comment The new comment text
	 */
	public void setComment(final String comment) {
		LOGGER.debug(LOG_PREFIX + "setComment [commit=" + this.comment + "]");

		this.comment = comment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getImage() {
		LOGGER.debug(LOG_PREFIX + "getImage");

		if (this.image == null) {
			return new byte[0];
		}

		return Arrays.copyOf(this.image, this.image.length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setImage(final byte[] img) {
		LOGGER.debug(LOG_PREFIX + "setImage");

		this.image = img == null ? new byte[0] : Arrays.copyOf(img, img.length);
	}

	/**
	 * Return the <code>UserEntity</code>'s sex.
	 * 
	 * @return The <code>UserEntity</code>'s sex
	 */
	public SEX getSex() {
		if (this.sex != null) {
			LOGGER.debug(LOG_PREFIX + "getSex [sex=" + this.sex.name() + "]");
		} else {
			LOGGER.debug(LOG_PREFIX + "getSex [sex=null]");
		}

		return this.sex;
	}

	/**
	 * Change the <code>UserEntity</code>'s sex (only for compliance).
	 * 
	 * @param sex The new sex
	 */
	public void setSex(final SEX sex) {
		if (sex != null) {
			LOGGER.debug(LOG_PREFIX + "setSex [sex=" + sex.name() + "]");
		} else {
			LOGGER.debug(LOG_PREFIX + "setSex [sex=null]");
		}

		this.sex = sex;
	}
}
