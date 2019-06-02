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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.model.Product;

/**
 * A ejb that used the entityManager to invoke a query to get a product from db.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Stateless
public class DelegateDbAccessEJB {

	private static final String LOG_PREFIX = "DelegateDbAccessEJB#";
	@Inject
	@LoggerQualifier
	private Logger logger;
	@PersistenceContext(unitName = "poc-flyway-h2-pu")
	private EntityManager em;

	public void readAllProductsFromDb() {
		this.logger.debug(LOG_PREFIX + "readAllProductsFromDb");

		final TypedQuery<Product> typedQuery = em.createQuery("select p from Product p", Product.class);
		final List<Product> productList = typedQuery.getResultList();
		
		for (Iterator<Product> iterator = productList.iterator(); iterator.hasNext();) {
			Product product = (Product) iterator.next();
			
			this.logger.debug(LOG_PREFIX + "readAllProductsFromDb [" + product + "]");
		}

		// Print out all available tables.
		for (Iterator<String> iterator = this.getAllTables().iterator(); iterator.hasNext();) {
			final String table = (String) iterator.next();
			
			this.logger.debug(LOG_PREFIX + "readAllProductsFromDb - [table=" + table + "]");
		}
	}

	private List<String> getAllTables() {
		final List<String> tableNames = new ArrayList<>();
		final Session session = em.unwrap(Session.class);
		final SessionFactory sessionFactory = session.getSessionFactory();
		final Map<String, ClassMetadata> map = (Map<String, ClassMetadata>) sessionFactory.getAllClassMetadata();

		for (String entityName : map.keySet()) {
			final SessionFactoryImpl sfImpl = (SessionFactoryImpl) sessionFactory;
			final String tableName = ((AbstractEntityPersister) sfImpl.getEntityPersister(entityName)).getTableName();
			tableNames.add(tableName);
		}

		return tableNames;
	}
}
