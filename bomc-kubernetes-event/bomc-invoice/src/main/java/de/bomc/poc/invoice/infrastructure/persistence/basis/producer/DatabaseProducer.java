/**
 * Project: bomc-invoice
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
package de.bomc.poc.invoice.infrastructure.persistence.basis.producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * A producer for different <code>EntityManager</code> stages.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@ApplicationScoped
public class DatabaseProducer {

    // Persistence unit names.
    // NOTE@MVN: will be changed during maven project creation.
    public static final String PU_NAME = "bomc-pu";
    @Produces
    @PersistenceContext(unitName = DatabaseProducer.PU_NAME)
    private EntityManager entityManager;
}
