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
package de.bomc.poc.order.infrastructure.persistence.customer;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.order.domain.model.customer.CustomerEntity;
import de.bomc.poc.order.domain.model.customer.JpaCustomerDao;
import de.bomc.poc.order.infrastructure.persistence.basis.impl.AbstractJpaDao;
import de.bomc.poc.order.infrastructure.persistence.basis.qualifier.JpaDao;

/**
 * <pre>
 *  The <code>JpaOrderDaoImpl</code> class is a extension of the {@link AbstractJpaDao} about
 *  functionality regarding {@link CustomerEntity}s.
 *  All methods have to be invoked within an active transaction context.
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 03.02.2019
 */
@JpaDao
public class JpaCustomerDaoImpl extends AbstractJpaDao<CustomerEntity> implements JpaCustomerDao {

    private static final String LOG_PREFIX = "JpaOrderDaoImpl#";
    @Inject
    @LoggerQualifier
    private Logger logger;

    @Override
    protected Class<CustomerEntity> getPersistentClass() {
        return CustomerEntity.class;
    }

    // Define here methods for functionality from db for CustomerEntity.
    // ...

    @Override
    public LocalDateTime findLatestModifiedDateTime(final String userId) {
        this.logger.debug(LOG_PREFIX + "findLatestModifiedDate [userId=" + userId + "]");

        final TypedQuery<LocalDateTime> queryObject = this.getEntityManager()
                .createNamedQuery(CustomerEntity.NQ_FIND_BY_LATEST_MODIFIED_DATE_TIME_CUSTOMER, LocalDateTime.class);
        final List<LocalDateTime> latestModifiedDateTimeList = queryObject.getResultList();

        // Return the first element, there is only one element available.
        return latestModifiedDateTimeList.get(0);
    }

    @Override
    public CustomerEntity findByUsername(final String username, final String userId) {
        this.logger.debug(LOG_PREFIX + "findByUsername [username=" + username + ", userId=" + userId + "]");

        final TypedQuery<CustomerEntity> queryObject = this.getEntityManager()
                .createNamedQuery(CustomerEntity.NQ_FIND_BY_USERNAME, CustomerEntity.class);
        queryObject.setParameter("username", username);
        final List<CustomerEntity> customerEntityList = queryObject.getResultList();
        
        // Username is unique, so only one element is returned.
        return customerEntityList.get(0);
    }
}
