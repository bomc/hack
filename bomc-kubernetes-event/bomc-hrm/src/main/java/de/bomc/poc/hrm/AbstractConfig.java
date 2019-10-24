package de.bomc.poc.hrm;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractConfig {
	// _______________________________________________
	// Constants
	// -----------------------------------------------
	protected static final String DRIVER_URL_PROPERTY_KEY = "url";
	protected static final String DRIVER_USER_PROPERTY_KEY = "user";
	protected static final String DRIVER_PASSWORD_PROPERTY_KEY = "password";
	protected static final String DATASOURCE_CLASSNAME_PROPERTY_KEY = "dataSourceClassName";
	protected static final String DATASOURCE_PROPETIES_PROPERTY_KEY = "dataSourceProperties";
	protected static final String DATASOURCE_MIN_POOLSIZE_PROPERTY_KEY = "minimumPoolSize";
	protected static final String DATASOURCE_MAX_POOLSIZE_PROPERTY_KEY = "maximumPoolSize";
	protected static final String DATASOURCE_CONNECTION_TIMEOUT_PROPERTY_KEY = "connectionTimeout";
	
	protected static final String HIBERNATE_DDL_AUTO = "hibernate.hbm2ddl.auto";
	protected static final String HIBERNATE_DIALECT = "hibernate.dialect";

	protected static final String PACKAGE_TO_SCAN = "de.bomc.poc.hrm.domain";
	
	// _______________________________________________
	// Member variables
	// -----------------------------------------------
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
	@Value("${jpa.properties.hibernate.ddl-auto}")
	protected String jpaHibernateDdlAuto;
	@Value("${jpa.properties.hibernate.dialect}")
	protected String jpaPropHibernateDialect;
	
	// _______________________________________________
	// Helper methods
	// -----------------------------------------------
	final Properties hibernateProperties() {
		final Properties hibernateProperties = new Properties();

		hibernateProperties.setProperty(HIBERNATE_DDL_AUTO, this.jpaHibernateDdlAuto);
		hibernateProperties.setProperty(HIBERNATE_DIALECT, this.jpaPropHibernateDialect);
                
		return hibernateProperties;
	}
}
