package de.bomc.poc.zk.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;

/**
 * A class that contains additional information for the registered <code>ServiceInstance</code>.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
@JsonRootName("instanceMetaData")
public class InstanceMetaData implements Serializable {

    /**
     * The serial UID.
     */
    private static final long serialVersionUID = -6752407026845515294L;
    @JsonProperty("hostAddress")
    private String hostAdress;
    @JsonProperty("port")
    private int port;
    @JsonProperty("serviceName")
    private String serviceName;
    @JsonProperty("contextRoot")
    private String contextRoot;
    @JsonProperty("applicationPath")
    private String applicationPath;
    @JsonProperty("description")
    private String description;

    /**
     * Creates a new instance of <code>InstanceMetaDataTest</code>.
     * 
     */
    private InstanceMetaData() {
    	//
    	// Is private, is used only from intern.
    }
    
    public static IPort hostAdress(final String hostAddress) {
    	return new InstanceMetaData.Builder(hostAddress);
    }
    
    public interface IPort {
    	IServiceName port(int port);
    }
    
    public interface IServiceName {
    	IContextRoot serviceName(String serviceName);
    }
    
    public interface IContextRoot {
    	IApplicationPath contextRoot(String contextRoot);
    }
    
    public interface IApplicationPath {
    	IBuild applicationPath(String applicationPath);
    }
    
    public interface IBuild {
    	IBuild description(String description);
    	
    	InstanceMetaData build();
    }
    
    private static class Builder implements IPort, IServiceName, IContextRoot, IApplicationPath, IBuild {
    	private final InstanceMetaData instance = new InstanceMetaData();
    	
    	public Builder(final String hostAdress) {
            this.instance.hostAdress = hostAdress;
    	}
    	
    	@Override
    	public IServiceName port(final int port) {
            this.instance.port = port;
    		
    		return this;
    	}
    	
    	@Override
    	public IContextRoot serviceName(final String serviceName) {
            this.instance.serviceName = serviceName;
    		
    		return this;
    	}
    	
    	@Override
    	public IApplicationPath contextRoot(final String contextRoot) {
            this.instance.contextRoot = contextRoot;
    		
    		return this;
    	}
    	
    	@Override
    	public IBuild applicationPath(final String applicationPath) {
            this.instance.applicationPath = applicationPath;
    		
    		return this;
    	}
    	
    	@Override
    	public IBuild description(final String description) {
            this.instance.description = description;
    		
    		return this;
    	}
    	
    	@Override
    	public InstanceMetaData build() {
    		return this.instance;
    	}
    }
    
    
    public String getDescription() {
        return this.description;
    }

    public String getHostAdress() {
        return this.hostAdress;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public int getPort() {
        return this.port;
    }

    public String getContextRoot() {
        return this.contextRoot;
    }

    public String getApplicationPath() {
        return this.applicationPath;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final InstanceMetaData that = (InstanceMetaData)o;

        if (this.port != that.port) {
            return false;
        }
        if (this.hostAdress != null ? !this.hostAdress.equals(that.hostAdress) : that.hostAdress != null) {
            return false;
        }
        if (this.serviceName != null ? !this.serviceName.equals(that.serviceName) : that.serviceName != null) {
            return false;
        }
        if (this.contextRoot != null ? !this.contextRoot.equals(that.contextRoot) : that.contextRoot != null) {
            return false;
        }
        return !(this.applicationPath != null ? !this.applicationPath.equals(that.applicationPath) : that.applicationPath != null);
    }

    @Override
    public int hashCode() {
        int result = this.hostAdress != null ? this.hostAdress.hashCode() : 0;
        result = 31 * result + this.port;
        result = 31 * result + (this.serviceName != null ? this.serviceName.hashCode() : 0);
        result = 31 * result + (this.contextRoot != null ? this.contextRoot.hashCode() : 0);
        result = 31 * result + (this.applicationPath != null ? this.applicationPath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InstanceMetaData [description="
               + this.description
               + ", hostAdress="
               + this.hostAdress
               + ", port="
               + this.port
               + ", serviceName="
               + this.serviceName
               + ", contextRoot="
               + this.contextRoot
               + ", applicationPath="
               + this.applicationPath
               + "]";
    }
}
