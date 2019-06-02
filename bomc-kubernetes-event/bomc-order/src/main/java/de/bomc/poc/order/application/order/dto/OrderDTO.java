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
package de.bomc.poc.order.application.order.dto;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.order.application.customer.dto.CustomerDTO;
import de.bomc.poc.order.application.internal.AppErrorCodeEnum;

/**
 * A data transfer object for order.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class OrderDTO {

    private static final String LOG_PREFIX = OrderDTO.class.getSimpleName() + "#";
    private static final Logger LOGGER = Logger.getLogger(OrderDTO.class);
    // _______________________________________________
    // Member variables.
    // -----------------------------------------------
    private AddressDTO shippingAddress;
    private AddressDTO billingAddress;
    private CustomerDTO customer;
    private Set<OrderLineDTO> orderLineDTOSet = new HashSet<>();
    private Long orderId;

    /**
     * Creates a new instance <code>OrderDTO</code>.
     */
    public OrderDTO() {
        // Indicates a pojo.
    }

    // _______________________________________________
    // Getter, setter for attributes, should only be used during mapping.
    // -----------------------------------------------

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }
    
    public Long getOrderId() {
        return this.orderId;
    }
    
    public AddressDTO getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(final AddressDTO shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public AddressDTO getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(final AddressDTO billingAddress) {
        this.billingAddress = billingAddress;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(final CustomerDTO customer) {
        this.customer = customer;
    }

    public Set<OrderLineDTO> getOrderLineDTOSet() {
        return Collections.unmodifiableSet(orderLineDTOSet);
    }

    public void setOrderLineDTOSet(final Set<OrderLineDTO> orderLineDTOSet) {

        if (orderLineDTOSet == null) {
            final String errMsg = LOG_PREFIX + "setOrderLineDTOSet - orderLineDTOSet is null.";
            final AppRuntimeException appRuntimeException = new AppRuntimeException(errMsg,
                    AppErrorCodeEnum.APP_ILLEGAL_ARGUMENT_10604);
            LOGGER.error(appRuntimeException.stackTraceToString());

            throw appRuntimeException;
        }

        this.orderLineDTOSet.addAll(orderLineDTOSet);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
        result = prime * result + ((billingAddress == null) ? 0 : billingAddress.hashCode());
        result = prime * result + ((customer == null) ? 0 : customer.hashCode());
        result = prime * result + ((orderLineDTOSet == null) ? 0 : orderLineDTOSet.hashCode());
        result = prime * result + ((shippingAddress == null) ? 0 : shippingAddress.hashCode());

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final OrderDTO other = (OrderDTO) obj;

        if (orderId == null) {
            if (other.orderId != null) {
                return false;
            }
        } else if (!orderId.equals(other.orderId)) {
            return false;
        }
        if (billingAddress == null) {
            if (other.billingAddress != null) {
                return false;
            }
        } else if (!billingAddress.equals(other.billingAddress)) {
            return false;
        }
        if (customer == null) {
            if (other.customer != null) {
                return false;
            }
        } else if (!customer.equals(other.customer)) {
            return false;
        }
        if (orderLineDTOSet == null) {
            if (other.orderLineDTOSet != null) {
                return false;
            }
        } else if (!orderLineDTOSet.equals(other.orderLineDTOSet)) {
            return false;
        }
        if (shippingAddress == null) {
            if (other.shippingAddress != null) {
                return false;
            }
        } else if (!shippingAddress.equals(other.shippingAddress)) {
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OrderDTO [orderId=" + orderId + ", shippingAddress=" + shippingAddress + ", billingAddress=" + billingAddress + ", customer="
                + customer + ", orderLineDTOSet.size=" + orderLineDTOSet.size() + "]";
    }

    // _______________________________________________
    // Implementation of the builder pattern as inner class.
    // -----------------------------------------------

    public static IBillingAddress orderId(final Long orderId) {
        return new OrderDTO.Builder(orderId);
    }

    public interface IBillingAddress  {
        IShippingAddress billingAddress(AddressDTO billingAddress);
    }
    
    public interface IShippingAddress {
        ICustomer shippingAddress(AddressDTO shippingAddress);
    }

    public interface ICustomer {
        IOrderLineSet customer(CustomerDTO customer);
    }

    public interface IOrderLineSet {
        IBuild orderLineSet(Set<OrderLineDTO> orderLineDTOSet);
    }

    public interface IBuild {
        OrderDTO build();
    }

    /**
     * The builder implementation for OrderDTO.
     */
    private static final class Builder implements IBuild, IShippingAddress, IBillingAddress, ICustomer, IOrderLineSet {

        private final OrderDTO instance = new OrderDTO();

        public Builder(final Long orderId) {
            this.instance.orderId = orderId;
        }

        @Override
        public IBuild orderLineSet(final Set<OrderLineDTO> orderLineDTOSet) {
            this.instance.setOrderLineDTOSet(orderLineDTOSet);

            return this;
        }

        @Override
        public IOrderLineSet customer(final CustomerDTO customer) {
            this.instance.customer = customer;

            return this;
        }

        @Override
        public ICustomer shippingAddress(final AddressDTO shippingAddress) {
            this.instance.shippingAddress = shippingAddress;
            
            return this;
        }
        
        @Override
        public IShippingAddress billingAddress(AddressDTO billingAddress) {
            this.instance.billingAddress = billingAddress;
            
            return this;
        }
        
        @Override
        public OrderDTO build() {
            return this.instance;
        }
    }
}
