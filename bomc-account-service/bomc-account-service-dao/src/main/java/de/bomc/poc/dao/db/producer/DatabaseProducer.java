package de.bomc.poc.dao.db.producer;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.bomc.poc.dao.db.qualifier.ProdDatabase;
import de.bomc.poc.dao.db.qualifier.LocalDatabase;

/**
 * A producer for different <code>EntityManager</code> stages.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
public class DatabaseProducer {

	public static final String BOMC_LOCAL_UNIT_NAME = "bomc-local-PU";
	public static final String BOMC_PROD_UNIT_NAME = "bomc-prod-PU";
	
	@Produces
	@Alternative
	@LocalDatabase
	@PersistenceContext(unitName = DatabaseProducer.BOMC_LOCAL_UNIT_NAME)
	private EntityManager entityManagerLocal;

	@Produces
	@Alternative
	@ProdDatabase
	@PersistenceContext(unitName = DatabaseProducer.BOMC_PROD_UNIT_NAME)
	private EntityManager entityManagerProd;
}
