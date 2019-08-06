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
package de.bomc.poc.invoice.domain.model.idempotent;

import de.bomc.poc.invoice.infrastructure.persistence.basis.JpaGenericRepository;

/**
 * The <code>JpaIdempotentRepository</code> class offers functionality
 * regarding {@link IdempotentMessage} entity classes.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 16.05.2018
 */
public interface JpaIdempotentRepository extends JpaGenericRepository<IdempotentMessage> {

	// Define here methods for functionality from db for IdempotentMessage entity.
	// ...
}
