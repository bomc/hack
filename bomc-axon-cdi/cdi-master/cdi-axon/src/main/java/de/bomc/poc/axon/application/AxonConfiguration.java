/**
 * Project: cdi-axon
 * <pre>
 *
 * Last change:
 *
 *  by:       $Author$
 *
 *  date:     $Date$
 *
 *  revision: $Revision$
 *
 *  © Bomc 2018
 *
 * </pre>
 */
package de.bomc.poc.axon.application;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.axonframework.cdi.transaction.JtaTransactionManager;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.jpa.SimpleEntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.jpa.JpaTokenStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;

/**
 * Initializing the axon configuration.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
@ApplicationScoped
public class AxonConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "AxonConfiguration#";

	/**
	 * Produces the entity manager.
	 * 
	 * @return entity manager.
	 */
	@Produces
	@ApplicationScoped
	public EntityManager entityManager() {
		try {
			LOGGER.debug(LOG_PREFIX + "entityManager - produces");

			return (EntityManager) new InitialContext().lookup("java:comp/env/bomcOrderPU/entitymanager");
		} catch (NamingException ex) {
			LOGGER.error(LOG_PREFIX + "entityManager - Failed to look up entity manager.", ex);
		}

		return null;
	}

	/**
	 * Produces the entity manager provider.
	 * 
	 * @return entity manager provider.
	 */
	@Produces
	@ApplicationScoped
	public EntityManagerProvider entityManagerProvider(EntityManager entityManager) {
		LOGGER.debug(LOG_PREFIX + "entityManagerProvider - produces");

		return new SimpleEntityManagerProvider(entityManager);
	}

	@Produces
	@ApplicationScoped
	public TransactionManager transactionManager() {
		LOGGER.debug(LOG_PREFIX + "transactionManager - produces");

		return new JtaTransactionManager();
		// return NoTransactionManager.INSTANCE;
	}

	/**
	 * Produces container transaction aware JPA storage engine.
	 * 
	 * @return Event storage engine.
	 */
	@Produces
	@ApplicationScoped
	public EventStorageEngine eventStorageEngine(EntityManagerProvider entityManagerProvider,
			TransactionManager transactionManager) {
		LOGGER.debug(LOG_PREFIX + "eventStorageEngine - produces");

		return new JpaEventStorageEngine(entityManagerProvider, transactionManager);
	}

	/**
	 * Produces JPA token store.
	 * 
	 * @return token store.
	 */
	@Produces
	@ApplicationScoped
	public TokenStore tokenStore(EntityManagerProvider entityManagerProvider, Serializer serializer) {
		LOGGER.debug(LOG_PREFIX + "tokenStore - produces");

		return new JpaTokenStore(entityManagerProvider, serializer);
	}

	/**
	 * Produces Jackson serializer.
	 * 
	 * @return serializer.
	 */
	@Produces
	@ApplicationScoped
	public Serializer serializer() {
		LOGGER.debug(LOG_PREFIX + "serializer - produces");

		return new JacksonSerializer();
	}
}
