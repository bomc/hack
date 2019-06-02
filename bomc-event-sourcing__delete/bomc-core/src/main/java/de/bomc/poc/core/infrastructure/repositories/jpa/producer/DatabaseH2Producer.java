package de.bomc.poc.core.infrastructure.repositories.jpa.producer;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.bomc.poc.core.infrastructure.repositories.jpa.qualifier.DatabaseH2Qualifier;

/**
 * A producer for a H2-<code>EntityManager</code>.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börnerc</a>
 */
public class DatabaseH2Producer {

	public static final String UNIT_H2_NAME = "poc-core-pu";

	@Produces
	@DatabaseH2Qualifier
	@PersistenceContext(unitName = DatabaseH2Producer.UNIT_H2_NAME)
	private EntityManager entityManager;
}
