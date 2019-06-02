package de.bomc.poc.togglz;

import java.io.File;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.file.FileBasedStateRepository;
import org.togglz.core.repository.jdbc.JDBCStateRepository;
import org.togglz.core.repository.mem.InMemoryStateRepository;
import org.togglz.core.spi.FeatureProvider;
import org.togglz.core.user.UserProvider;

import de.bomc.poc.logging.qualifier.LoggerQualifier;

/**
 * A producer that exposes the feature enum to the TogglzConfiguration.
 * 
 * <pre>
 * ___________________________________________________ 
 * NOTE:
 * Activation Strategies
 *
 * The following dialog allows you to activate a feature and chose a specific activation strategy:
 *
 * none: The feature is simply activated
 * Client IP: Activated for clients with a specific IP
 * Gradual rollout: You may select the percentage of users e.g. selecting 25% activates the feature for every fourth user
 * Release date: You may specify the release date in the format yyyy-MM-dd
 * ScriptEngine: You may execute a script language e.g. ECMAScript – I must admit that I’ve never tried this one..
 * Server IP: Restrict the feature to a specific server IP .. e.g. activate for 2 of 6 servers by adding their IP address
 * Username: Restrict to specific users .. you could – for example activate a specific feature for the users test-user and test-admin or something similar
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@ApplicationScoped
public class TogglzFeatureProducer {

	public static final String TOGGLZ_FEATURES_PROPERTIES_FILE = "/togglz-features.properties";
	public static final String FEATURE_MANAGER_NAME = "versionTogglzFeatureManager";
	private static final String LOG_PREFIX = "TogglzFeatureProducer#";
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	private TogglzUserProvider togglzUserProvider;
	@Resource(mappedName = "jboss/datasources/TooglzDS")
	private DataSource dataSource;

	@SuppressWarnings("unchecked")
	final FeatureProvider featureProvider = new MultipleEnumFeaturesProvider(TogglzFeatures.class);

	@Produces
	public FeatureManager produce() {
		final FeatureManagerBuilder featureManagerBuilder = new FeatureManagerBuilder();
		return featureManagerBuilder.featureProvider(featureProvider).userProvider(this.getUserProvider())
				.stateRepository(this.getJDBCStateRepository()).name(FEATURE_MANAGER_NAME).build();
	}

	/**
	 * File based repository for writing switch state changes to file. This does
	 * not rocks. Here a file should be reading, this is not allowed in the
	 * context of the EE spec.
	 * 
	 * @return a file based state repository.
	 */
	@SuppressWarnings("unused")
	private StateRepository getFileStateRepository() {
		this.logger.debug(LOG_PREFIX + "getFileRepository");

		FileBasedStateRepository stateRepository = null;

		try {
			// Get the togglz features from file.
			final File togglzFeatureFile = new File(
					this.getClass().getClassLoader().getResource(TOGGLZ_FEATURES_PROPERTIES_FILE).getPath());
			stateRepository = new FileBasedStateRepository(togglzFeatureFile);

			this.logger.debug(LOG_PREFIX + "getFileStateRepository - [file.exists=" + togglzFeatureFile.exists() + "]");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return stateRepository;
	}

	/**
	 * In memory based repository for holding switch state changes in-memory
	 * during runtime. NOTE: At start all feature switches are deactivated,
	 * except the feature wich are annotated with 'EnabledByDefault'.
	 * 
	 * @return a file based state repository.
	 */
	@SuppressWarnings("unused")
	private StateRepository getInMemoryStateRepository() {

		return new InMemoryStateRepository();
	}

	private StateRepository getJDBCStateRepository() {

		return new JDBCStateRepository(dataSource);
	}

	private UserProvider getUserProvider() {

		return this.togglzUserProvider;
	}
}
