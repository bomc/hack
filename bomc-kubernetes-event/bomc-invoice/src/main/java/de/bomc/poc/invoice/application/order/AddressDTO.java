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
package de.bomc.poc.invoice.application.order;

import java.io.Serializable;

/**
 * A data transfer object for adresses.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class AddressDTO implements Serializable {

    /**
     * The serial uid.
     */
    private static final long serialVersionUID = 4412767732328796909L;
    private String street;
    private String zip;
    private String city;
    
    /**
     * Creates a new instance <code>AddressDTO</code>.
     */
    public AddressDTO () {
        // Indicates a pojo.
    }

    // _______________________________________________
    // Getter, setter for attributes, should only be used during mapping.
    // -----------------------------------------------
    
    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(final String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((street == null) ? 0 : street.hashCode());
        result = prime * result + ((zip == null) ? 0 : zip.hashCode());
        
        return result;
    }

    /* (non-Javadoc)
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
        
        final AddressDTO other = (AddressDTO) obj;
        
        if (city == null) {
            if (other.city != null) {
                return false;
            }
        } else if (!city.equals(other.city)) {
            return false;
        }
        if (street == null) {
            if (other.street != null) {
                return false;
            }
        } else if (!street.equals(other.street)) {
            return false;
        }
        if (zip == null) {
            if (other.zip != null) {
                return false;
            }
        } else if (!zip.equals(other.zip)) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AddressDTO [street=" + street + ", zip=" + zip + ", city=" + city + "]";
    }

    // _______________________________________________
    // Implementation of the builder pattern as inner class.
    // -----------------------------------------------
    
    public static IStreet zip(final String zip) {
        return new AddressDTO.Builder(zip);
    }
    
    public interface IStreet {
        ICity street(String street);
    }
    
    public interface ICity {
        IBuild city(String city);
    }
    
    public interface IBuild {
        AddressDTO build();
    }   
    
    /**
     * The builder implementation for AddressDTO.
     */
    private static final class Builder implements IStreet, ICity, IBuild {
        
        private final AddressDTO instance = new AddressDTO();

        public Builder(final String zip) {
            this.instance.zip = zip;
        }
        
        @Override
        public IBuild city(final String city) {
            this.instance.city = city;
            
            return this;
        }

        @Override
        public ICity street(final String street) {
            this.instance.street = street;
            
            return this;
        }
        
        @Override
        public AddressDTO build() {
            
            return this.instance;
        }
    }
}
