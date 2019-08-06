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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private List<OrderLineDTO> orderLineDTOList = new ArrayList<>();
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

    public List<OrderLineDTO> getOrderLineDTOList() {
        return Collections.unmodifiableList(orderLineDTOList);
    }

    public void setOrderLineDTOList(final List<OrderLineDTO> orderLineDTOList) {

        if (orderLineDTOList == null) {
            final String errMsg = LOG_PREFIX + "setOrderLineDTOList - orderLineDTOList is null.";
            final AppRuntimeException appRuntimeException = new AppRuntimeException(errMsg,
                    AppErrorCodeEnum.APP_ILLEGAL_ARGUMENT_10604);
            LOGGER.error(appRuntimeException.stackTraceToString());

            throw appRuntimeException;
        }

        this.orderLineDTOList.addAll(orderLineDTOList);
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

        result = prime * result + ((this.orderId == null) ? 0 : this.orderId.hashCode());
        result = prime * result + ((this.billingAddress == null) ? 0 : this.billingAddress.hashCode());
        result = prime * result + ((this.customer == null) ? 0 : this.customer.hashCode());
        result = prime * result + ((this.orderLineDTOList == null) ? 0 : this.orderLineDTOList.hashCode());
        result = prime * result + ((this.shippingAddress == null) ? 0 : this.shippingAddress.hashCode());

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
        if (orderLineDTOList == null) {
            if (other.orderLineDTOList != null) {
                return false;
            }
        } else if (!orderLineDTOList.equals(other.orderLineDTOList)) {
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
                + customer + ", orderLineDTOList.size=" + orderLineDTOList.size() + "]";
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
        IOrderLineList customer(CustomerDTO customer);
    }

    public interface IOrderLineList {
        IBuild orderLineList(List<OrderLineDTO> orderLineDTOList);
    }

    public interface IBuild {
        OrderDTO build();
    }

    /**
     * The builder implementation for OrderDTO.
     */
    private static final class Builder implements IBuild, IShippingAddress, IBillingAddress, ICustomer, IOrderLineList {

        private final OrderDTO instance = new OrderDTO();

        public Builder(final Long orderId) {
            this.instance.orderId = orderId;
        }

        @Override
        public IBuild orderLineList(final List<OrderLineDTO> orderLineDTOList) {
            this.instance.setOrderLineDTOList(orderLineDTOList);

            return this;
        }

        @Override
        public IOrderLineList customer(final CustomerDTO customer) {
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
