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
package de.bomc.poc.hrm;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
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
@Profile("prod")
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "postgresqlEntityManagerFactory", transactionManagerRef = "postgresqlTransactionManager", basePackages = {
		"de.bomc.poc.hrm.infrastructure" })
@PropertySource("classpath:persistence-postgresql.properties")
public class PersistencePostgreSqlConfig extends AbstractConfig {

	@Bean(name = "postgresqlDataSource")
	public DataSource postgresqlDataSource() {

		// Set the driver properties.
		final Properties driverProperties = new Properties();
		driverProperties.setProperty(DRIVER_URL_PROPERTY_KEY, driverSourceUrl);
		driverProperties.setProperty(DRIVER_USER_PROPERTY_KEY, driverSourceUsername);
		driverProperties.setProperty(DRIVER_PASSWORD_PROPERTY_KEY, driverSourcePassword);

		// Set dataSource properties.
		final Properties properties = new Properties();
		properties.put(DATASOURCE_CLASSNAME_PROPERTY_KEY, dataSourceClassName);
		properties.put(DATASOURCE_PROPETIES_PROPERTY_KEY, driverProperties);
		// properties.setProperty(DATASOURCE_MIN_POOLSIZE_PROPERTY_KEY, String.valueOf(dataSourceMinimumPoolSize));
		properties.setProperty(DATASOURCE_MAX_POOLSIZE_PROPERTY_KEY, String.valueOf(dataSourceMaximumPoolSize));
		properties.setProperty(DATASOURCE_CONNECTION_TIMEOUT_PROPERTY_KEY, String.valueOf(dataSourceConnectionTimeout));

		// Create dataSource and set properties.
		final DataSource datasource = new HikariDataSource(new HikariConfig(properties));
		
		return datasource;
	}

	@Bean(name = "postgresqlEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean h2EntityManagerFactory(
			@Qualifier("postgresqlDataSource") final DataSource postgresqlDataSource) {

		final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEntityManagerFactoryBean.setPersistenceUnitName(getClass().getSimpleName());
		localContainerEntityManagerFactoryBean.setPersistenceProvider(new HibernatePersistenceProvider());
		localContainerEntityManagerFactoryBean.setDataSource(postgresqlDataSource);
		localContainerEntityManagerFactoryBean.setPackagesToScan(new String[] { PACKAGE_TO_SCAN });

		final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		localContainerEntityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
		localContainerEntityManagerFactoryBean.setJpaProperties(hibernateProperties());

		return localContainerEntityManagerFactoryBean;
	}

	@Bean(name = "postgresqlTransactionManager")
	public JpaTransactionManager transactionManager(
			@Qualifier("postgresqlEntityManagerFactory") final EntityManagerFactory postgresqlEntityManagerFactory) {

		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(postgresqlEntityManagerFactory);

		return transactionManager;
	}

	@Bean(name = "postgresqlTransactionTemplate")
	public TransactionTemplate transactionTemplate(
			@Qualifier("postgresqlEntityManagerFactory") final EntityManagerFactory postgresqlEntityManagerFactory) {

		return new TransactionTemplate(transactionManager(postgresqlEntityManagerFactory));
	}

}