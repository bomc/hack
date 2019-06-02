/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.order.infrastructure.persistence.basis.impl;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;

import de.bomc.poc.order.domain.model.basis.AbstractMetadataEntity;

import java.time.LocalDateTime;

import javax.inject.Inject;

/**
 * An abstract class for the GenericDao implementation on Metadata Entity level.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
public abstract class AbstractMetadataEntityJpaDao<T extends AbstractMetadataEntity> extends AbstractJpaDao<T> {

    private static final String LOG_PREFIX = "AbstractMetadataEntityJpaDao#";
    @Inject
    @LoggerQualifier
    private Logger logger;

    protected AbstractMetadataEntityJpaDao() {
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

        this.logger.debug(LOG_PREFIX + "beforeUpdate");

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
