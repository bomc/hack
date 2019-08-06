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
package de.bomc.poc.invoice.domain.model.core;

import de.bomc.poc.invoice.infrastructure.persistence.basis.JpaGenericRepository;

/**
 * An JpaCustomerDao offers functionality regarding {@link InvoiceEntity}
 * entity classes.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 03.02.2019
 */
public interface JpaInvoiceAggregateRepository extends JpaGenericRepository<InvoiceAggregateEntity> {

    // Define here methods for functionality from db for Customer entity.
    // ...

}
