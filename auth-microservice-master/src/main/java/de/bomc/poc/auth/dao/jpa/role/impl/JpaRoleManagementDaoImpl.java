package de.bomc.poc.auth.dao.jpa.role.impl;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import de.bomc.poc.auth.model.usermanagement.Grant;
import org.apache.log4j.Logger;

import de.bomc.poc.auth.dao.jpa.generic.impl.AbstractJpaDao;
import de.bomc.poc.auth.dao.jpa.qualifier.JpaDao;
import de.bomc.poc.auth.dao.jpa.role.JpaRoleManagementDao;
import de.bomc.poc.auth.model.usermanagement.Role;
import de.bomc.poc.logger.qualifier.LoggerQualifier;

/**
 * A dao for {@link Role} management data access.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.03.2016
 */
@JpaDao
public class JpaRoleManagementDaoImpl extends AbstractJpaDao<Role> implements JpaRoleManagementDao {

    private static final String LOGGER_PREFIX = "roleManagementDao";
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
    protected Class<Role> getPersistentClass() {
        return Role.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Role> findAll() {
        List<Role> roleList = this.getEntityManager().createNamedQuery(Role.NQ_FIND_ALL).getResultList();

   		return roleList;
   	}

    /**
     * {@inheritDoc}
     */
    // TODO Write test to check functionality of transactional with integration test.
    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean removeAllGrantsFromRole(@NotNull final Long id, @NotNull final Set<? extends Grant> securityObjects) {
        logger.debug("JpaRoleManagementDaoImpl#removeAllGrantsFromRole [id=" + Long.toString(id) + ", securityObjects.size=" + securityObjects.size() + "]");

        final Role role = this.findById(id);

        return role.removeGrants(securityObjects);
    }
}
