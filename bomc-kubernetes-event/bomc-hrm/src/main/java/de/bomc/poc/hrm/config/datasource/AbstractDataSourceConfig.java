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

import org.springframework.beans.factory.annotation.Value;

/**
 * A abstract class for datasource configuration.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
public abstract class AbstractDataSourceConfig {

	// _______________________________________________
	// Constants
	// -----------------------------------------------
	protected static final String DRIVER_URL_PROPERTY_KEY = "url";
	protected static final String DRIVER_USER_PROPERTY_KEY = "user";
	protected static final String DRIVER_PASSWORD_PROPERTY_KEY = "password";
	protected static final String HIKARI_DATASOURCE_CLASSNAME_PROPERTY_KEY = "dataSourceClassName";
	protected static final String HIKARI_DATASOURCE_PROPETIES_PROPERTY_KEY = "dataSourceProperties";
	protected static final String HIKARI_DATASOURCE_MIN_POOLSIZE_PROPERTY_KEY = "minimumPoolSize";
	protected static final String HIKARI_DATASOURCE_MAX_POOLSIZE_PROPERTY_KEY = "maximumPoolSize";
	protected static final String HIKARI_DATASOURCE_CONNECTION_TIMEOUT_PROPERTY_KEY = "connectionTimeout";
	protected static final String HIKARI_DATASOURCE_CONNECTION_TEST_QUERY_KEY = "connectionTestQuery";
	protected static final String HIKARI_DATASOURCE_POOL_NAME_KEY = "poolName";

	protected static final String HIBERNATE_DDL_AUTO = "hibernate.hbm2ddl.auto";
	protected static final String HIBERNATE_DIALECT = "hibernate.dialect";
	protected static final String HIBERNATE_DEFAULT_SCHEMA = "hibernate.default_schema";

	protected static final String PACKAGE_TO_SCAN = "de.bomc.poc.hrm.domain";
	
	// _______________________________________________
	// Member variables
	// -----------------------------------------------
    @Value("${spring.flyway.enabled:true}")
    protected boolean isFlywayEnabled;
	@Value("${driver.url}")
	protected String driverSourceUrl;
	@Value("${driver.username}")
	protected String driverSourceUsername;
	@Value("${driver.password}")
	protected String driverSourcePassword;
	@Value("${dataSource.className}")
	protected String dataSourceClassName;
	@Value("${dataSource.minimumPoolSize}")
	protected String dataSourceMinimumPoolSize;
	@Value("${dataSource.maximumPoolSize}")
	protected String dataSourceMaximumPoolSize;
	@Value("${dataSource.connectionTimeout}")
	protected String dataSourceConnectionTimeout;
	@Value("${dataSource.connectionTestQuery}")
	protected String dataSourceConnectionTestQuery;
	@Value("${jpa.properties.hibernate.ddl-auto}")
	protected String jpaHibernateDdlAuto;
	@Value("${jpa.properties.hibernate.dialect}")
	protected String jpaPropHibernateDialect;
	@Value("${jpa.properties.hibernate.default_schema}")
	protected String jpaPropHibernateDefaultSchema;
	
	// _______________________________________________
	// Helper methods
	// -----------------------------------------------
	final Properties hibernateProperties() {
		final Properties hibernateProperties = new Properties();

		hibernateProperties.setProperty(HIBERNATE_DDL_AUTO, this.jpaHibernateDdlAuto);
		hibernateProperties.setProperty(HIBERNATE_DIALECT, this.jpaPropHibernateDialect);
		hibernateProperties.setProperty(HIBERNATE_DEFAULT_SCHEMA, jpaPropHibernateDefaultSchema);
		
		return hibernateProperties;
	}
}
