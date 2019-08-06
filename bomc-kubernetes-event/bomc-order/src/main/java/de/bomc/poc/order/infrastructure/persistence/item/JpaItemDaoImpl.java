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
package de.bomc.poc.order.infrastructure.persistence.item;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.order.domain.model.item.ItemEntity;
import de.bomc.poc.order.domain.model.item.JpaItemDao;
import de.bomc.poc.order.infrastructure.persistence.basis.impl.AbstractJpaDao;
import de.bomc.poc.order.infrastructure.persistence.basis.qualifier.JpaDao;

/**
 * <pre>
 *  The <code>JpaItemDaoImpl</code> class is a extension of the {@link AbstractJpaDao} about
 *  functionality regarding {@link ItemEntity}s.
 *  All methods have to be invoked within an active transaction context.
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 03.02.2019
 */
@JpaDao
public class JpaItemDaoImpl extends AbstractJpaDao<ItemEntity> implements JpaItemDao {

    private static final String LOG_PREFIX = "JpaItemDaoImpl#";
    @Inject
    @LoggerQualifier
    private Logger logger;

    @Override
    protected Class<ItemEntity> getPersistentClass() {
        return ItemEntity.class;
    }

    // Define here methods for functionality from db for ItemEntity.
    // ...

    @Override
    public ItemEntity findByName(final String name, final String userId) {
        this.logger.debug(LOG_PREFIX + "findByName [name=" + name + ", userId=" + userId + "]");

        final TypedQuery<ItemEntity> queryObject = getEntityManager().createNamedQuery(ItemEntity.NQ_FIND_BY_ITEM_NAME,
                ItemEntity.class);
        queryObject.setParameter("name", name);
        final List<ItemEntity> itemEntityList = queryObject.getResultList();

        // There is only one element, because the item name is unique. Or a
        // empty list if given name is not available in db.
        if(itemEntityList != null && !itemEntityList.isEmpty()) {    
            return itemEntityList.get(0);
        } else {
            return null;
        }
    }
}
