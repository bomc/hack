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
package de.bomc.poc.order.infrastructure.persistence.order;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.order.domain.model.order.JpaOrderDao;
import de.bomc.poc.order.domain.model.order.OrderEntity;
import de.bomc.poc.order.infrastructure.persistence.basis.impl.AbstractJpaDao;
import de.bomc.poc.order.infrastructure.persistence.basis.qualifier.JpaDao;

/**
 * <pre>
 *  The <code>JpaOrderDaoImpl</code> class is a extension of the {@link AbstractJpaDao} about
 *  functionality regarding {@link OrderEntity}s.
 *  All methods have to be invoked within an active transaction context.
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 03.02.2019
 */
@JpaDao
public class JpaOrderDaoImpl extends AbstractJpaDao<OrderEntity> implements JpaOrderDao {

    private static final String LOG_PREFIX = "JpaOrderDaoImpl#";
    @Inject
    @LoggerQualifier
    private Logger logger;

    @Override
    protected Class<OrderEntity> getPersistentClass() {
        return OrderEntity.class;
    }

    // Define here methods for functionality from db for OrderEntity.
    // ...

    @Override
    public LocalDateTime findLatestModifiedDateTime(String userId) {
        this.logger.debug(LOG_PREFIX + "findLatestModifiedDateTime [userId=" + userId + "]");

        final TypedQuery<LocalDateTime> queryObject = this.getEntityManager()
                .createNamedQuery(OrderEntity.NQ_FIND_BY_LATEST_MODIFIED_DATE_TIME_ORDER, LocalDateTime.class);
        final List<LocalDateTime> latestModifiedDateTimeList = queryObject.getResultList();

        // Return the first element, there is only one element available.
        return latestModifiedDateTimeList.get(0);
    }

    @Override
    public List<OrderEntity> findByAllOlderThanGivenDate(final LocalDateTime modifyDateTime,
            final String userId) {
        this.logger.debug(LOG_PREFIX + "findByAllOlderThanGivenDate [modifyDateTime=" + modifyDateTime
                + ", userId=" + userId + "]");
         
        final Map<String, Object> params = new HashMap<>();
        params.put("createDateTime", modifyDateTime);
        params.put("modifyDateTime", modifyDateTime);
        
        final List<OrderEntity> orderEntityList = findByNamedParameters(OrderEntity.NQ_FIND_ALL_OLDER_THAN_GIVEN_DATE, params);
        
        return orderEntityList;
    }
}
