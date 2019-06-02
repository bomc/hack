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

import de.bomc.poc.order.application.customer.dto.CustomerDTO;

/**
 * This controller handles the customer domain.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public interface CustomerController {

    /**
     * Find the latest modified date for customer entries in db.
     * 
     * @param userId
     *            the exceuting user (used for auditing).
     * @return the latest modified date/time.
     */
    LocalDateTime findLatestModifiedDateTime(String userId);

    /**
     * Create a new customer in db.
     * 
     * @param customerDTO
     *            the customer data.
     * @param userId
     *            the executed user.
     * @return the technical id.
     */
    Long createCustomer(CustomerDTO customerDTO, String userId);

    /**
     * Find all available customers in db.
     * 
     * @param userId
     *            the executed user.
     * @return all available customers.
     */
    List<CustomerDTO> findAllCustomers(String userId);

    /**
     * Find customer by given username.
     * 
     * @param userName
     *            the given username.
     * @param userId
     *            the executed user.
     * @return the customer by the given username.
     */
    CustomerDTO findCustomerByUsername(String userName, String userId);
}
