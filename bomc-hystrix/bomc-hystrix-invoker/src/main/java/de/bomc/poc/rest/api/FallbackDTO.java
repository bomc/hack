package de.bomc.poc.rest.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response dto that describes the fallback case.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FallbackDTO implements Serializable {

	private static final long serialVersionUID = -759436163124698882L;

	private boolean fallback;
	private String errorMsg;

	public FallbackDTO() {
		super();
		// Used by json provider
	}
	
	public FallbackDTO(final boolean fallback, final String errorMsg) {
		this.fallback = fallback;
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return this.errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean isFallback() {
		return this.fallback;
	}

	public void setFallback(boolean fallback) {
		this.fallback = fallback;
	}

	
	@Override
	public String toString() {
		return "FallbackDTO [fallback=" + fallback + ", errorMsg=" + errorMsg + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((errorMsg == null) ? 0 : errorMsg.hashCode());
		result = prime * result + (fallback ? 1231 : 1237);
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FallbackDTO))
			return false;
		
		FallbackDTO other = (FallbackDTO) obj;
		
		if (errorMsg == null) {
			if (other.errorMsg != null)
				return false;
		} else if (!errorMsg.equals(other.errorMsg))
			return false;
		if (fallback != other.fallback)
			return false;
		
		return true;
	}

}
