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
package de.bomc.poc.invoice.infrastructure.persistence.invoice;

import de.bomc.poc.invoice.domain.model.core.InvoiceAggregateEntity;
import de.bomc.poc.invoice.domain.model.core.JpaInvoiceAggregateRepository;
import de.bomc.poc.invoice.infrastructure.persistence.basis.impl.AbstractJpaRepository;
import de.bomc.poc.invoice.infrastructure.persistence.basis.qualifier.JpaRepository;

/**
 * <pre>
 *  The <code>JpaInvoiceRepositoryImpl</code> class is a extension of the {@link AbstractRepositoryDao} about
 *  functionality regarding {@link InvoiceEntity}s.
 *  All methods have to be invoked within an active transaction context.
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 03.02.2019
 */
@JpaRepository
public class JpaInvoiceRepositoryImpl extends AbstractJpaRepository<InvoiceAggregateEntity> implements JpaInvoiceAggregateRepository {

//    private static final String LOG_PREFIX = "JpaInvoiceRepositoryImpl#";
//    @Inject
//	@LoggerQualifier
//	private Logger logger;

    @Override
    protected Class<InvoiceAggregateEntity> getPersistentClass() {
        return InvoiceAggregateEntity.class;
    }

    // Define here methods for functionality from db for CustomerEntity.
    // ...

}
