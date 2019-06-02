package de.bomc.poc.togglz;

import java.util.Random;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;

import de.bomc.poc.logging.qualifier.LoggerQualifier;

/**
 * UserProvider implementation which leverages bomc's UserService.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
public class TogglzUserProvider implements UserProvider {

	private static final String LOG_PREFIX = "TogglzUserProvider#";
	
	private Logger logger;

	@SuppressWarnings("unused")
	private final static Random random = new Random();
	
	@Inject
	public TogglzUserProvider() {
		//
	}

	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @param logger
	 *            the logger to set
	 */
	@Inject
	public void setLogger(@LoggerQualifier Logger logger) {
		this.logger = logger;
	}
	
	/**
	 * NOTE: Toggz control whether the "current user" can use the Admin Console by returning true|false from FeatureUser.isFeatureAdmin().
	 */
	@Override
	public FeatureUser getCurrentUser() {
		// if null is returned, is same as NoOpUserProvider.
		SimpleFeatureUser featureUser = null;

		// e.g. could be use.
		// User user = userService.getCurrentUser();
		//
		// if (user != null) {
		// featureUser = new SimpleFeatureUser(user.getUserId(),
		// userService.isUserAdmin());
		// featureUser.setAttribute("email", user.getEmail());
		// featureUser.setAttribute("nickname", user.getNickname());
		// }
		//

		// ___________________________________________
		// Code for testing gradual strategy.
		// Togglz is calculating a hash value from the username which is then
		// normalized to a value between 0 and 100
		// Considering we're starting from 0-100 value, this is an unnecessary
		// overhead. A price we need to pay for using Togglz
		// to do load balancing...
//		String username = Integer.toString(random.nextInt(1000000));
//		
//		featureUser = new SimpleFeatureUser(username, false);
		// -------------------------------------------
		
		// ________________________________________________________________________________________________________
		// NOTE: It is important to know, that togglz controls the access to the admin console by the current user.
		// To access the admin console, the SimpleFeatureUser must be set to true, as a Feature adminUser.
		// --------------------------------------------------------------------------------------------------------
		featureUser = new SimpleFeatureUser("bomc", true);
		this.logger.debug(LOG_PREFIX + "getCurrentUser [currentUser=" + featureUser.getName() + "]");

		return featureUser;
	}

}