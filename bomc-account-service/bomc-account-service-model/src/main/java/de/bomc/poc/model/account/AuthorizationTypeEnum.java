package de.bomc.poc.model.account;

/**
 * Defines the authorization type of this account.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 09.08.2016
 */
public enum AuthorizationTypeEnum {

	WEAK("Uses username and password for authorization."), 
	STRONG("Uses username, password and... for authorization."), 
	VERY_STONG("Uses MTAN method for authorization."), 
	UNKNOWN("Only for test purposes!");
	
	private final String authorizationType;

	AuthorizationTypeEnum(final String userType) {
		this.authorizationType = userType;
	}

	public String getAutohrizationType() {
		return this.authorizationType;
	}

	@Override
	public String toString() {
		return "AuthorizationType [authorizationType=" + this.authorizationType + "]";
	}
}