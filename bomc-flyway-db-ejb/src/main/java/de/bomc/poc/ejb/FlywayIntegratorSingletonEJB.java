/**
 * Project: bomc-flyway-db-ejb
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
 * Copyright (c): BOMC, 2017
 */
package de.bomc.poc.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;

import de.bomc.poc.logging.qualifier.LoggerQualifier;

/**
 * A singleton that migrates the database schema at startup.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Singleton
@Startup
@TransactionManagement(value = TransactionManagementType.BEAN)
public class FlywayIntegratorSingletonEJB {

	private static final String LOG_PREFIX = "FlywayIntegratorSingletonEJB#";
	@Inject
	@LoggerQualifier
	private Logger logger;

	// Inject actual datasource.
	@Resource//(name = "java:jboss/datasources/${datasource.name}")
	private DataSource dataSource;

	@PostConstruct
	private void onStartup() {
		logger.debug(LOG_PREFIX + "onStartup");

		if (dataSource == null) {
			logger.error(LOG_PREFIX + "onStartup - no datasource found to execute the db migrations!");

			throw new EJBException(LOG_PREFIX + "onStartup - no datasource found to execute the db migrations!");
		}

		final Flyway flyway = new Flyway();
		flyway.setDataSource(dataSource);
		flyway.setBaselineOnMigrate(true);// .baseline();
		final MigrationInfo migrationInfo = flyway.info().current();

		if (migrationInfo == null) {
			logger.info(LOG_PREFIX + "onStartup - No existing database at the actual datasource!");
		} else {
			logger.info(LOG_PREFIX + "onStartup - Found a database [version=" + migrationInfo.getVersion()
					+ ", description=" + migrationInfo.getDescription() + "]");
		}

		flyway.migrate();
		logger.info(LOG_PREFIX + "onStartup - Successfully migrated database [to_version="
				+ flyway.info().current().getVersion() + ", baselineDescription=" + flyway.getBaselineDescription() + "]");
	}
}
