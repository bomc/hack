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
package de.bomc.poc.invoice.infrastructure.persistence.idempotent;

import java.util.logging.Logger;

import javax.inject.Inject;

import de.bomc.poc.invoice.application.log.LoggerQualifier;
import de.bomc.poc.invoice.domain.model.idempotent.IdempotentMessage;
import de.bomc.poc.invoice.domain.model.idempotent.JpaIdempotentRepository;
import de.bomc.poc.invoice.infrastructure.persistence.basis.impl.AbstractMetadataEntityJpaRepository;
import de.bomc.poc.invoice.infrastructure.persistence.basis.qualifier.JpaRepository;

/**
 * <pre>
 * The <code>JpaIdempotentRepositoryImpl</code> class is an extension of the {@link AbstractMetadataEntityJpaDao} about functionality regarding {@link IdempotentMessage}s.
 * All methods have to be invoked within an active transaction context.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 16.05.2019
 */
@JpaRepository
public class JpaIdempotentRepositoryImpl extends AbstractMetadataEntityJpaRepository<IdempotentMessage> implements JpaIdempotentRepository {

    private static final String LOGGER_PREFIX = "JpaIdempotentRepositoryImpl#";
    /**
     * Logger.
     */
    @Inject
    @LoggerQualifier(logPrefix = LOGGER_PREFIX)
    private Logger logger;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<IdempotentMessage> getPersistentClass() {
        return IdempotentMessage.class;
    }

    // Define here methods for functionality from db for IdempotentMessage entity.
    // ...
}