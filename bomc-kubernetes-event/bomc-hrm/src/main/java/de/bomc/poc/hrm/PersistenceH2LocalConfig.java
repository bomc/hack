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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@Profile("local")
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "h2LocalEntityManagerFactory", transactionManagerRef = "h2LocalTransactionManager", basePackages = {
		"de.bomc.poc.hrm.infrastructure" })
@PropertySource("classpath:persistence-h2Local.properties")
public class PersistenceH2LocalConfig extends AbstractConfig {
	
	private static final String DATASOURCE_POOL_NAME_VALUE = "bomc-h2Local";
	
	@Bean(name = "h2LocalDataSource")
	public DataSource h2LocalDataSource() {

		// Set the driver properties.
		final Properties driverProperties = new Properties();
        driverProperties.setProperty(DRIVER_URL_PROPERTY_KEY, driverSourceUrl);
        driverProperties.setProperty(DRIVER_USER_PROPERTY_KEY, driverSourceUsername);
        driverProperties.setProperty(DRIVER_PASSWORD_PROPERTY_KEY, driverSourcePassword);

        // Set dataSource properties.
        final Properties properties = new Properties();
        properties.put(HIKARI_DATASOURCE_CLASSNAME_PROPERTY_KEY, dataSourceClassName);
        properties.put(HIKARI_DATASOURCE_PROPETIES_PROPERTY_KEY, driverProperties);
        //properties.setProperty(DATASOURCE_MIN_POOLSIZE_PROPERTY_KEY, String.valueOf(dataSourceMinimumPoolSize));
        properties.setProperty(HIKARI_DATASOURCE_MAX_POOLSIZE_PROPERTY_KEY, String.valueOf(dataSourceMaximumPoolSize));
        properties.setProperty(HIKARI_DATASOURCE_CONNECTION_TIMEOUT_PROPERTY_KEY, String.valueOf(dataSourceConnectionTimeout));
		properties.setProperty(HIKARI_DATASOURCE_CONNECTION_TEST_QUERY_KEY, dataSourceConnectionTestQuery);
		properties.setProperty(HIKARI_DATASOURCE_POOL_NAME_KEY, DATASOURCE_POOL_NAME_VALUE);
		
        // Create dataSource and set properties.
        final HikariDataSource datasource = new HikariDataSource(new HikariConfig(properties));
        
        return datasource;
	}

	@Bean(name = "h2LocalEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean h2LocalEntityManagerFactory(
			@Qualifier("h2LocalDataSource") final DataSource h2LocalDataSource) {
		final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

		em.setDataSource(h2LocalDataSource);
		em.setPackagesToScan(new String[] { PACKAGE_TO_SCAN });
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setJpaProperties(hibernateProperties());

		return em;
	}

	@Bean(name = "h2LocalTransactionManager")
	public JpaTransactionManager transactionManager(@Qualifier("h2LocalEntityManagerFactory") final EntityManagerFactory h2LocalEntityManagerFactory) {
		
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(h2LocalEntityManagerFactory);
        
        return transactionManager;
	}

    @Bean(name = "h2LocalTransactionTemplate")
    public TransactionTemplate transactionTemplate(@Qualifier("h2LocalEntityManagerFactory") final EntityManagerFactory h2LocalEntityManagerFactory) {
        
    	return new TransactionTemplate(transactionManager(h2LocalEntityManagerFactory));
    }
    
}
