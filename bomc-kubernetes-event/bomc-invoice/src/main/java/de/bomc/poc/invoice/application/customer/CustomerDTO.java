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
package de.bomc.poc.invoice.application.customer;

import java.io.Serializable;

/**
 * A data transfer object for handling {@link CustomerEntity} transformmation to
 * a DTO.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class CustomerDTO implements Serializable{

    /**
     * The serial uid.
     */
    private static final long serialVersionUID = -9139941990105177567L;
    // The name of the customer.
    private String name;
    // The first name of the customer.
    private String firstname;
    // The username of the customer (email)
    private String username;

    /**
     * Creates a new instance of <code>CustomerDTO</code>.
     */
    public CustomerDTO() {
        // Indicates a pojo.
    }

    // _______________________________________________
    // Getter, setter for attributes, should only be used during mapping.
    // -----------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        
        result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        
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
        
        final CustomerDTO other = (CustomerDTO) obj;
        
        if (firstname == null) {
            if (other.firstname != null) {
                return false;
            }
        } else if (!firstname.equals(other.firstname)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return "CustomerDTO [name=" + name + ", firstname=" + firstname + ", username=" + username + "]";
    }

    // _______________________________________________
    // Implementation of the builder pattern as inner class.
    // -----------------------------------------------
    
    public static IName username(final String username) {
        return new CustomerDTO.Builder(username);
    }
    
    public interface IName {
        IFirstname name(String name);
    }
    
    public interface IFirstname {
        IBuild firstname(String firstname);
    }
    
    public interface IBuild {
        
        CustomerDTO build();
    }
    
    /**
     * The builder implementation for CustomerDTO.
     */
    private static final class Builder
            implements IBuild, IName, IFirstname {

        private final CustomerDTO instance = new CustomerDTO();

        public Builder(final String username) {
            this.instance.username = username;
        }

        @Override
        public IBuild firstname(String firstname) {
            this.instance.firstname = firstname;
            
            return this;
        }

        @Override
        public IFirstname name(String name) {
            this.instance.name = name;
            
            return this;
        }
        
        @Override
        public CustomerDTO build() {
            return this.instance;
        }
    }
}
