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
 * Response dto that describes the fallback case.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 17.01.2018
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FallbackDTO implements Serializable {

	private static final long serialVersionUID = -759436163124698882L;

	private boolean fallback;
	private String errorMsg;
	private boolean isRetry = false;

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

	public void setErrorMsg(final String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean isFallback() {
		return this.fallback;
	}

	public void setFallback(final boolean fallback) {
		this.fallback = fallback;
	}

	/**
	 * @return true to indicate a retry otherwise false.
	 */
	public boolean isRetry() {
		return this.isRetry;
	}

	/**
	 * @param isRetry
	 *            the isRetry to set.
	 */
	public void setRetry(final boolean isRetry) {
		this.isRetry = isRetry;
	}

	@Override
	public String toString() {
		return "FallbackDTO [fallback=" + this.fallback + ", errorMsg=" + this.errorMsg + ", isRetry=" + this.isRetry
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((this.errorMsg == null) ? 0 : this.errorMsg.hashCode());
		result = prime * result + (this.fallback ? 1231 : 1237);
		result = prime * result + (this.isRetry ? 1231 : 1237);
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FallbackDTO other = (FallbackDTO) obj;
		if (this.errorMsg == null) {
			if (other.errorMsg != null)
				return false;
		} else if (!this.errorMsg.equals(other.errorMsg))
			return false;
		if (this.fallback != other.fallback)
			return false;
		if (this.isRetry != other.isRetry)
			return false;
		
		return true;
	}

}
