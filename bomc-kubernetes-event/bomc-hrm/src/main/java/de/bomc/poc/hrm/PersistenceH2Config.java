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

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
@PropertySource("classpath:persistence-h2.properties")
public class PersistenceH2Config {

	// _______________________________________________
	// Constants
	// -----------------------------------------------
	private static final String HIBERNATE_DDL_AUTO = "hibernate.hbm2ddl.auto";
	private static final String HIBERNATE_DIALECT = "hibernate.dialect";
//	private static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
//	private static final String HIBERNATE_USE_SQL_COMMENTS = "hibernate.use_sql_comments";
//	private static final String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
	private static final String PACKAGE_TO_SCAN = "de.bomc.poc.hrm.domain";
	// _______________________________________________
	// Member variables
	// -----------------------------------------------
	@Value("${datasource.driver-class-name}")
	private String dataSourceDriverClassName;
	@Value("${datasource.url}")
	private String dataSourceUrl;
	@Value("${datasource.username}")
	private String dataSourceUsername;
	@Value("${datasource.password}")
	private String dataSourcePassword;
	@Value("${jpa.properties.hibernate.ddl-auto}")
	private String jpaHibernateDdlAuto;
//    @Value("${jpa.properties.hibernate.show_sql}")
//    private String jpaHibernateShowSql;
//    @Value("${jpa.properties.hibernate.use_sql_comments}")
//    private String jpaHibernateUseSqlComments;    
//    @Value("${jpa.properties.hibernate.format_sql}")
//    private String jpaHibernateFormatSql;
	@Value("${jpa.properties.hibernate.dialect}")
	private String jpaPropHibernateDialect;

	@Bean(name = "h2DataSource")
	public DataSource h2DataSource() {
		final DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();

		driverManagerDataSource.setDriverClassName(this.dataSourceDriverClassName);
		driverManagerDataSource.setUrl(this.dataSourceUrl);
		driverManagerDataSource.setUsername(this.dataSourceUsername);
		driverManagerDataSource.setPassword(this.dataSourcePassword);

		return driverManagerDataSource;
	}

	@Bean(name = "h2EntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean h2EntityManagerFactory(
			@Qualifier("h2DataSource") final DataSource h2DataSource) {
		final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

		em.setDataSource(h2DataSource);
		em.setPackagesToScan(new String[] { PACKAGE_TO_SCAN });
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setJpaProperties(additionalProperties());

		return em;
	}

	@Bean(name = "h2TransactionManager")
	public DataSourceTransactionManager transactionManager(@Qualifier("h2DataSource") final DataSource h2DataSource) {
		final DataSourceTransactionManager txManager = new DataSourceTransactionManager();
		txManager.setDataSource(h2DataSource);

		return txManager;
	}

	// _______________________________________________
	// Helper methods
	// -----------------------------------------------
	final Properties additionalProperties() {
		final Properties hibernateProperties = new Properties();

		hibernateProperties.setProperty(HIBERNATE_DDL_AUTO, this.jpaHibernateDdlAuto);
		hibernateProperties.setProperty(HIBERNATE_DIALECT, this.jpaPropHibernateDialect);
//		hibernateProperties.setProperty(HIBERNATE_SHOW_SQL, this.jpaHibernateShowSql);
//		hibernateProperties.setProperty(HIBERNATE_USE_SQL_COMMENTS, this.jpaHibernateUseSqlComments);
//		hibernateProperties.setProperty(HIBERNATE_FORMAT_SQL, this.jpaHibernateFormatSql);

		return hibernateProperties;
	}
}
