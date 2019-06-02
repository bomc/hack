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
package de.bomc.poc.order.application.customer;

import java.time.LocalDateTime;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.bomc.poc.order.application.basis.log.qualifier.AuditLogQualifier;
import de.bomc.poc.order.application.customer.dto.CustomerDTO;
import de.bomc.poc.order.application.customer.mapping.DTOEntityCustomerMapper;
import de.bomc.poc.order.domain.model.customer.CustomerEntity;
import de.bomc.poc.order.domain.model.customer.JpaCustomerDao;
import de.bomc.poc.order.infrastructure.persistence.basis.qualifier.JpaDao;

/**
 * This EJB is used as controller for handling customers.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Stateless
@AuditLogQualifier
@Local(CustomerController.class)
public class CustomerControllerEJB implements CustomerController {

    @Inject
    @JpaDao
    private JpaCustomerDao jpaCustomerDao;

    @Override
    public LocalDateTime findLatestModifiedDateTime(final String userId) {

        return this.jpaCustomerDao.findLatestModifiedDateTime(userId);
    }

    @Override
    public Long createCustomer(final CustomerDTO customerDTO, final String userId) {

        final CustomerEntity customerEntity = DTOEntityCustomerMapper.INSTANCE.mapDTOToEntity(customerDTO);
        this.jpaCustomerDao.persist(customerEntity, userId);

        final Long id = customerEntity.getId();

        return id;
    }

    @Override
    public List<CustomerDTO> findAllCustomers(final String userId) {

        final List<CustomerEntity> customerEnityList = this.jpaCustomerDao.findAll();
        final List<CustomerDTO> customerDTOList = DTOEntityCustomerMapper.INSTANCE.mapEntityToDTO(customerEnityList);

        return customerDTOList;
    }

    @Override
    public CustomerDTO findCustomerByUsername(final String username, final String userId) {

        final CustomerEntity customerEntity = this.jpaCustomerDao.findByUsername(username, userId);
        final CustomerDTO customerDTO = DTOEntityCustomerMapper.INSTANCE.mapEntityToDTO(customerEntity);

        return customerDTO;
    }

} // end class
