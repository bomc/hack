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
package de.bomc.poc.invoice.infrastructure.persistence.basis.impl;

import de.bomc.poc.invoice.domain.model.basis.AbstractMetadataEntity;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An abstract class for the GenericDao implementation on Metadata Entity level.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
public abstract class AbstractMetadataEntityJpaRepository<T extends AbstractMetadataEntity> extends AbstractJpaRepository<T> {

    private static final String LOG_PREFIX = "AbstractMetadataEntityJpaDao#";
    private Logger LOGGER = Logger.getLogger(AbstractMetadataEntityJpaRepository.class.getName());

    protected AbstractMetadataEntityJpaRepository() {
        //
    }

    /**
     * This method is considered as a hook to do something before an update is
     * performed.
     * 
     * @param entity
     *            The Entity that is updated
     */
    protected void beforeUpdate(final T entity, final String executingUser) {
        super.beforeUpdate(entity, executingUser);

        LOGGER.log(Level.FINE, LOG_PREFIX + "beforeUpdate");

        // create or modify?
        if (entity.isNew()) {
            entity.setCreateDateTime(LocalDateTime.now());
            entity.setCreateUser(executingUser);
        } else {
            entity.setModifyDateTime(LocalDateTime.now());
            entity.setModifyUser(executingUser);
        }
    }
}
