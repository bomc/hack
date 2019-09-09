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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bomc.poc.hrm.domain.model.values.CoreTypeDefinitions;
import de.bomc.poc.hrm.domain.model.values.ImageProvider;
import lombok.ToString;

/**
 * Detailed information about an <code>User</code>.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
// LOMBOK
@ToString
// JPA
@Embeddable
public class UserDetails implements ImageProvider, Serializable {

	private static final String LOG_PREFIX = "UserDetails#";
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetails.class.getName());
	private static final long serialVersionUID = 664778075559767489L;
	/**
	 * Some comment text of the <code>User</code>.
	 */
	@Size(max = CoreTypeDefinitions.DESCRIPTION_LENGTH)
	@Column(name = "C_COMMENT")
	private String comment;
	/**
	 * Phone number assigned to the <code>User</code>. Matches german phone and
	 * fax numbers (including cell phone numbers) in various formats like:
	 * 004989123456, +49 89 123456, +49(89)123456, 089-1234-5678, 089 1234 5678,
	 * (089)1234-5678 Max. number of digits is 21.
	 */
	@Pattern(regexp = "^(((((((00|\\+)49[ \\-/]?)|0)[1-9][0-9]{1,4})[ \\-/]?)|((((00|\\+)49\\()|\\(0)[1-9][0-9]{1,4}\\)[ \\-/]?))[0-9]{1,7}([ \\-/]?[0-9]{1,5})?)$")
	@Column(name = "C_PHONE_NO")
	private String phoneNo;
	/**
	 * An image of the <code>User</code>. Lazy fetched.
	 */
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "C_IMAGE")
	private byte[] image;
	/**
	 * Sex of the <code>User</code>.
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "C_SEX")
	private SEX sex;

	/* ----------------------------- constants ------------------- */
	
	/**
	 * The <code>User</code>s sex.
	 * 
	 * @author <a href="mailto:bomc@bomc.org">bomc</a>
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
	 * Create a new <code>UserDetails</code> instance.
	 */
	public UserDetails() {
		super();
		
		LOGGER.debug(LOG_PREFIX + "co");
	}

	/* ----------------------------- methods --------------------- */
    
	/**
	 * Return the <code>User</code>s current phone number.
	 * 
	 * @return The phone number
	 */
	public String getPhoneNo() {
		LOGGER.debug(LOG_PREFIX + "getPhoneNo [phoneNo=" + phoneNo + "]");
		
		return this.phoneNo;
	}

	/**
	 * Change the phone number of the <code>User</code>.
	 * 
	 * @param phoneNo
	 *            The new phone number
	 */
	public void setPhoneNo(final String phoneNo) {
		LOGGER.debug(LOG_PREFIX + "setPhoneNo [phoneNo=" + phoneNo + "]");
		
		this.phoneNo = phoneNo;
	}

	/**
	 * Return a comment text of the <code>User</code>.
	 * 
	 * @return The comment text
	 */
	public String getComment() {
		LOGGER.debug(LOG_PREFIX + "getComment [commit=" + this.comment + "]");
		
		return this.comment;
	}

	/**
	 * Change the comment text of the <code>User</code>.
	 * 
	 * @param comment
	 *            The new comment text
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
	 * Return the <code>User</code>'s sex.
	 * 
	 * @return The <code>User</code>'s sex
	 */
	public SEX getSex() {
		LOGGER.debug(LOG_PREFIX + "getSex [sex=" + this.sex.name() + "]");
		
		return this.sex;
	}

	/**
	 * Change the <code>User</code>'s sex (only for compliance).
	 * 
	 * @param sex
	 *            The new sex
	 */
	public void setSex(final SEX sex) {
		LOGGER.debug(LOG_PREFIX + "setSex [sex=" + sex.name() + "]");
		
		this.sex = sex;
	}
}
