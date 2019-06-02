package de.bomc.poc.api.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A data transfer object for account data handling.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
public class AccountDTO implements Serializable {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 5850268037114720085L;

	// _______________________________________________
	// Account data.
	private Long account_id;
	private String account_name;
	// _______________________________________________
	// Person data.
	private Long user_id;
	private String user_username;
	private String legalUser_companyId;
	private LocalDate naturalUser_birthDate;
	// _______________________________________________
	// Account Person data.
	private Boolean accountUser_ownerFlag;
	private String accountUser_authType;
	// _______________________________________________
	// Address data.
	private Long address_id;
	private String address_city;
	private String address_country;
	private String address_street;
	private String address_zipCode;

	/**
	 * Default co
	 */
	public AccountDTO() {
		//
		// Indicates a POJO.
	}

    // _______________________________________________
    // Getter, setter for attributes, should only be used during mapping.
    // -----------------------------------------------
	
	/**
	 * @return the account_id
	 */
	public Long getAccount_id() {
		return account_id;
	}

	/**
	 * @param account_id
	 *            the account_id to set
	 */
	public void setAccount_id(final Long account_id) {
		this.account_id = account_id;
	}

	/**
	 * @return the account_name
	 */
	public String getAccount_name() {
		return account_name;
	}

	/**
	 * @param account_name
	 *            the account_name to set
	 */
	public void setAccount_name(final String account_name) {
		this.account_name = account_name;
	}

	/**
	 * @return the user_id
	 */
	public Long getUser_id() {
		return user_id;
	}

	/**
	 * @param user_id
	 *            the user_id to set
	 */
	public void setUser_id(final Long user_id) {
		this.user_id = user_id;
	}

	/**
	 * @return the user_username
	 */
	public String getUser_username() {
		return user_username;
	}

	/**
	 * @param user_username
	 *            the user_username to set
	 */
	public void setUser_username(final String user_username) {
		this.user_username = user_username;
	}

	/**
	 * @return the legalUser_companyId
	 */
	public String getLegalUser_companyId() {
		return legalUser_companyId;
	}

	/**
	 * @param legalUser_companyId
	 *            the legalUser_companyId to set
	 */
	public void setLegalUser_companyId(final String legalUser_companyId) {
		this.legalUser_companyId = legalUser_companyId;
	}

	/**
	 * @return the naturalUser_birthDate
	 */
	public LocalDate getNaturalUser_birthDate() {
		return naturalUser_birthDate;
	}

	/**
	 * @param naturalUser_birthDate
	 *            the naturalUser_birthDate to set
	 */
	public void setNaturalUser_birthDate(final LocalDate naturalUser_birthDate) {
		this.naturalUser_birthDate = naturalUser_birthDate;
	}

	/**
	 * @return the accountUser_ownerFlag
	 */
	public Boolean getAccountUser_ownerFlag() {
		return accountUser_ownerFlag;
	}

	/**
	 * @param accountUser_ownerFlag
	 *            the accountUser_ownerFlag to set
	 */
	public void setAccountUser_ownerFlag(final Boolean accountUser_ownerFlag) {
		this.accountUser_ownerFlag = accountUser_ownerFlag;
	}

	/**
	 * @return the accountUser_authType
	 */
	public String getAccountUser_authType() {
		return accountUser_authType;
	}

	/**
	 * @param accountUser_authType
	 *            the accountUser_authType to set
	 */
	public void setAccountUser_authType(final String accountUser_authType) {
		this.accountUser_authType = accountUser_authType;
	}

	/**
	 * @return the address_id
	 */
	public Long getAddress_id() {
		return address_id;
	}

	/**
	 * @param address_id
	 *            the address_id to set
	 */
	public void setAddress_id(final Long address_id) {
		this.address_id = address_id;
	}

	/**
	 * @return the address_city
	 */
	public String getAddress_city() {
		return address_city;
	}

	/**
	 * @param address_city
	 *            the address_city to set
	 */
	public void setAddress_city(final String address_city) {
		this.address_city = address_city;
	}

	/**
	 * @return the address_country
	 */
	public String getAddress_country() {
		return address_country;
	}

	/**
	 * @param address_country
	 *            the address_country to set
	 */
	public void setAddress_country(final String address_country) {
		this.address_country = address_country;
	}

	/**
	 * @return the address_street
	 */
	public String getAddress_street() {
		return address_street;
	}

	/**
	 * @param address_street
	 *            the address_street to set
	 */
	public void setAddress_street(final String address_street) {
		this.address_street = address_street;
	}

	/**
	 * @return the address_zipCode
	 */
	public String getAddress_zipCode() {
		return address_zipCode;
	}

	/**
	 * @param address_zipCode
	 *            the address_zipCode to set
	 */
	public void setAddress_zipCode(final String address_zipCode) {
		this.address_zipCode = address_zipCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AccountDTO [account_id=" + account_id + ", account_name=" + account_name + ", user_id=" + user_id
				+ ", user_username=" + user_username + ", legalUser_companyId=" + legalUser_companyId
				+ ", naturalUser_birthDate=" + naturalUser_birthDate + ", accountUser_ownerFlag="
				+ accountUser_ownerFlag + ", accountUser_authType=" + accountUser_authType + ", address_id="
				+ address_id + ", address_city=" + address_city + ", address_country=" + address_country
				+ ", address_street=" + address_street + ", address_zipCode=" + address_zipCode + "]";
	}

	// _______________________________________________
	// Implementation of the builder pattern as inner class.
	// -----------------------------------------------

	public static IUserUsername account_Name(final String account_Name) {
		return new AccountDTO.Builder(account_Name);
	}

	public interface IUserUsername {

		ILegalUserCompanyId user_Username(String user_username);
	}

	public interface ILegalUserCompanyId {

		INaturalUserBirthDate legalUser_CompanyId(String legalUser_companyId);
	}

	public interface INaturalUserBirthDate {

		IAccountUserOwnerFlag naturalUser_BirthDate(LocalDate naturalUser_birthDate);
	}

	public interface IAccountUserOwnerFlag {

		IAccountUserAuthType accountUser_OwnerFlag(Boolean accountUser_ownerFlag);
	}

	public interface IAccountUserAuthType {

		IAddressCity accountUser_AuthType(String accountUser_authType);
	}

	public interface IAddressCity {

		IAddressCountry address_City(String address_city);
	}

	public interface IAddressCountry {

		IAddressStreet address_Country(String address_country);
	}

	public interface IAddressStreet {

		IAddressZipCode address_Street(String address_street);
	}

	public interface IAddressZipCode {

		IBuild address_ZipCode(String address_Zip);
	}
	
	public interface IBuild {

		IBuild address_ZipCode(String address_zipCode);

		AccountDTO build();
	}

	/**
	 * The builder implementation for AccountDTO.
	 */
	private static class Builder
			implements IUserUsername, ILegalUserCompanyId, INaturalUserBirthDate, IAccountUserOwnerFlag,
			IAccountUserAuthType, IAddressCity, IAddressCountry, IAddressStreet, IAddressZipCode, IBuild {

		private final AccountDTO instance = new AccountDTO();

		public Builder(final String account_name) {
			this.instance.account_name = account_name;
		}

//		public ILegalUserCompanyId account_Name(final String account_name) {
//			this.instance.account_name = account_name;
//			return this;
//		}

		@Override
		public ILegalUserCompanyId user_Username(final String user_username) {
			this.instance.user_username = user_username;
			return this;
		}

		@Override
		public INaturalUserBirthDate legalUser_CompanyId(final String legalUser_companyId) {
			this.instance.legalUser_companyId = legalUser_companyId;
			return this;
		}

		@Override
		public IAccountUserOwnerFlag naturalUser_BirthDate(final LocalDate naturalUser_birthDate) {
			this.instance.naturalUser_birthDate = naturalUser_birthDate;
			return this;
		}

		@Override
		public IAccountUserAuthType accountUser_OwnerFlag(final Boolean accountUser_ownerFlag) {
			this.instance.accountUser_ownerFlag = accountUser_ownerFlag;
			return this;
		}

		@Override
		public IAddressCity accountUser_AuthType(final String accountUser_authType) {
			this.instance.accountUser_authType = accountUser_authType;
			return this;
		}

		@Override
		public IAddressCountry address_City(final String address_city) {
			this.instance.address_city = address_city;
			return this;
		}

		@Override
		public IAddressStreet address_Country(final String address_country) {
			this.instance.address_country = address_country;
			return this;
		}

		@Override
		public IAddressZipCode address_Street(final String address_street) {
			this.instance.address_street = address_street;
			return this;
		}

		@Override
		public IBuild address_ZipCode(final String address_zipCode) {
			this.instance.address_zipCode = address_zipCode;
			return this;
		}

		@Override
		public AccountDTO build() {
			return this.instance;
		}

	}
}
