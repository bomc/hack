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
package de.bomc.poc.hrm.config.datasource;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Configuration for persistence.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Profile("dev")
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "h2EntityManagerFactory", transactionManagerRef = "h2TransactionManager", basePackages = {
		"de.bomc.poc.hrm.infrastructure" })
@PropertySource("classpath:persistence-h2-dev.properties")
public class DataSourceDevH2Config extends AbstractDataSourceConfig {

	private static final String DATASOURCE_POOL_NAME_VALUE = "bomc-h2";

	@Bean(name = "h2DataSource")
	public DataSource h2DataSource() {

		// Set the driver properties.
		final Properties driverProperties = new Properties();
		driverProperties.setProperty(DRIVER_URL_PROPERTY_KEY, driverSourceUrl);
		driverProperties.setProperty(DRIVER_USER_PROPERTY_KEY, driverSourceUsername);
		driverProperties.setProperty(DRIVER_PASSWORD_PROPERTY_KEY, driverSourcePassword);

		// Set dataSource properties.
		final Properties properties = new Properties();
		properties.put(HIKARI_DATASOURCE_CLASSNAME_PROPERTY_KEY, dataSourceClassName);
		properties.put(HIKARI_DATASOURCE_PROPETIES_PROPERTY_KEY, driverProperties);
		// properties.setProperty(HIKARI_DATASOURCE_MIN_POOLSIZE_PROPERTY_KEY, String.valueOf(dataSourceMinimumPoolSize));
		properties.setProperty(HIKARI_DATASOURCE_MAX_POOLSIZE_PROPERTY_KEY, String.valueOf(dataSourceMaximumPoolSize));
		properties.setProperty(HIKARI_DATASOURCE_CONNECTION_TIMEOUT_PROPERTY_KEY,
				String.valueOf(dataSourceConnectionTimeout));
		properties.setProperty(HIKARI_DATASOURCE_CONNECTION_TEST_QUERY_KEY, dataSourceConnectionTestQuery);
		properties.setProperty(HIKARI_DATASOURCE_POOL_NAME_KEY, DATASOURCE_POOL_NAME_VALUE);

		// Create dataSource and set properties.
		final HikariDataSource datasource = new HikariDataSource(new HikariConfig(properties));

		return datasource;
	}

	@FlywayDataSource
	@Bean // (initMethod = "migrate") is called programmatically during bean initialization.
	public Flyway flyway(@Qualifier("h2DataSource") final HikariDataSource dataSource) {

		final Flyway flyway = Flyway.configure().dataSource(dataSource)
				.locations("classpath:db/migration", "classpath:dev/db/migration").baselineOnMigrate(true)
				.schemas(jpaPropHibernateDefaultSchema).load();
		
		if(isFlywayEnabled) {
			flyway.migrate();
		}
		
		return flyway;
	}

	@Bean(name = "h2EntityManagerFactory")
	// Depends on flyway bean, so that database initialization runs before Hibernate
	// gets bootstrapped.
	@DependsOn("flyway")
	public LocalContainerEntityManagerFactoryBean h2EntityManagerFactory(
			@Qualifier("h2DataSource") final DataSource h2DataSource) {
		final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

		em.setDataSource(h2DataSource);
		em.setPackagesToScan(new String[] { PACKAGE_TO_SCAN });
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setJpaProperties(hibernateProperties());

		return em;
	}

	@Bean(name = "h2TransactionManager")
	public JpaTransactionManager transactionManager(
			@Qualifier("h2EntityManagerFactory") final EntityManagerFactory h2EntityManagerFactory) {

		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(h2EntityManagerFactory);

		return transactionManager;
	}

	@Bean(name = "h2TransactionTemplate")
	public TransactionTemplate transactionTemplate(
			@Qualifier("h2EntityManagerFactory") final EntityManagerFactory h2EntityManagerFactory) {

		return new TransactionTemplate(transactionManager(h2EntityManagerFactory));
	}

}
