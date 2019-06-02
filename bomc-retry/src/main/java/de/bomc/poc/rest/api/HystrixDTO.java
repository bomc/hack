/**
 * <pre>
 *
 * Last change:
 *
 *  by: $Author$
 *
 *  date: $Date$
 *
 *  revision: $Revision$
 *
 *    © Bomc 2018
 *
 * </pre>
 */
package de.bomc.poc.rest.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response dto that returns the current version..
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 17.01.2018
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HystrixDTO extends FallbackDTO implements Serializable {

	private static final long serialVersionUID = 7587452260515869898L;

	private String version;
	
	public HystrixDTO() {
		super(false, null);
	}

	public HystrixDTO(String version) {
		super(false, null);

		this.version = version;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "HystrixDTO [version=" + version + ", errorMsg=" + getErrorMsg() + ", isFallback=" + isFallback() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = super.hashCode();
		result = prime * result + ((version == null) ? 0 : version.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof HystrixDTO))
			return false;

		HystrixDTO other = (HystrixDTO) obj;

		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;

		return true;
	}

}
