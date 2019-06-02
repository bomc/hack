package de.bomc.poc.integrator;

import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * <pre>
 * Das Konzept der Integrators. Diese sind eine neue Möglichkeit um das
 * Hochfahren der SessionFactory zu beeinflussen. Ein eigener Integrator muss
 * das Interface org.hibernate.integrator.spi.Integrator implementieren. Über
 * die Methode integrate()kann man sich in den Prozess des Hochfahrens
 * einklinken, wohingegen disintegrate() eine Beeinflussung des Herunterfahrens
 * der SessionFactory ermöglicht. Wichtig: Es muss unter /webapp/META-INF ein
 * Verzeichnis mit der Datei /services/org.hibernate.integrator.spi.Integrator
 * angelegt werden. In der Datei muss die Klasse angegeben werden, die das
 * Integrator Interface implementiert.
 * Damit Flyway die Dateien .sql findet, muss sie zwingend im Projekt unter 
 * /resources im Verzeichnis /db/migration/ angelegt werden.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
public class FlywayIntegrator implements Integrator {

	private static final String LOG_PREFIX = "FlywayIntegrator#";
	private static final Logger LOGGER = Logger.getLogger(FlywayIntegrator.class);

	@Override
	public void integrate(final Metadata metadata, final SessionFactoryImplementor sessionFactory,
			final SessionFactoryServiceRegistry serviceRegistry) {
		LOGGER.debug(LOG_PREFIX + "integrate - migrating database to the latest version.");

		final JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
		Connection connection;
		DataSource dataSource = null;

		try {
			connection = jdbcServices.getBootstrapJdbcConnectionAccess().obtainConnection();
			final Method method;
			if (connection != null) {
				method = connection.getClass().getMethod("getDataSource", (Class[]) null);
			} else {
				method = null;
			}

			if (method != null) {
				dataSource = (DataSource) method.invoke(connection);
			} else {
				dataSource = null;
			}
		} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | SQLException e) {
			LOGGER.debug(LOG_PREFIX + "integrate - connecting to db failed!" + e.toString());
		}

		final Flyway flyway = new Flyway();
		flyway.setDataSource(dataSource);

		final MigrationInfo migrationInfo = flyway.info().current();

		if (migrationInfo == null) {
			LOGGER.info(LOG_PREFIX + "integrate - no existing database at the actual datasource");
		} else {
			LOGGER.info(LOG_PREFIX + "integrate - found a database [version=" + migrationInfo.getVersion() + "]");
		}

		flyway.migrate();
		LOGGER.info(LOG_PREFIX + "integrate - successfully database migrated [version="
				+ flyway.info().current().getVersion() + "]");
	}

	@Override
	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		// Not needed here
	}
}
