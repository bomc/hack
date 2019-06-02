/**
 * Project: bomc-exception-lib-ext
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
package de.bomc.poc.exception.core.event;

/**
 * An event that is fired if an exception is catched by the
 * {@link ExceptionHandlerInterceptor}
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
public class ExceptionLogEvent {

	// Short error decription.
	private String shortErrorCodeDescription;
	// The error category, defined by ErrorCode.getCategory.
	private String category;
	// The reponse status.
	private String responseStatus;
	// The uuid created by the exception instance.
	private String uuid;

	/**
	 * Default co
	 */
	public ExceptionLogEvent() {
		// Indicates a pojo.
	}

	// _______________________________________________
	// Getter, setter for attributes, should only be used during mapping.
	// -----------------------------------------------

	/**
	 * @return the shortErrorCodeDescription
	 */
	public final String getShortErrorCodeDescription() {
		return shortErrorCodeDescription;
	}

	/**
	 * @param shortErrorCodeDescription
	 *            the shortErrorCodeDescription to set
	 */
	public final void setShortErrorCodeDescription(final String shortErrorCodeDescription) {
		this.shortErrorCodeDescription = shortErrorCodeDescription;
	}

	/**
	 * @return the category
	 */
	public final String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public final void setCategory(final String category) {
		this.category = category;
	}

	/**
	 * @return the responseStatus
	 */
	public final String getResponseStatus() {
		return responseStatus;
	}

	/**
	 * @param responseStatus
	 *            the responseStatus to set
	 */
	public final void setResponseStatus(final String responseStatus) {
		this.responseStatus = responseStatus;
	}

	/**
	 * @return the uuid
	 */
	public final String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid
	 *            the uuid to set
	 */
	public final void setUuid(final String uuid) {
		this.uuid = uuid;
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

		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((responseStatus == null) ? 0 : responseStatus.hashCode());
		result = prime * result + ((shortErrorCodeDescription == null) ? 0 : shortErrorCodeDescription.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		ExceptionLogEvent other = (ExceptionLogEvent) obj;

		if (category == null) {
			if (other.category != null) {
				return false;
			}
		} else if (!category.equals(other.category)) {
			return false;
		}

		if (responseStatus == null) {
			if (other.responseStatus != null) {
				return false;
			}
		} else if (!responseStatus.equals(other.responseStatus)) {
			return false;
		}

		if (shortErrorCodeDescription == null) {
			if (other.shortErrorCodeDescription != null) {
				return false;
			}
		} else if (!shortErrorCodeDescription.equals(other.shortErrorCodeDescription)) {
			return false;
		}

		if (uuid == null) {
			if (other.uuid != null) {
				return false;
			}
		} else if (!uuid.equals(other.uuid)) {
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
		return "ExceptionLogEvent [shortErrorCodeDescription=" + shortErrorCodeDescription + ", category=" + category
				+ ", responseStatus=" + responseStatus + ", uuid=" + uuid + "]";
	}

	// _______________________________________________
	// Implementation of the builder pattern as inner class.
	// -----------------------------------------------

	public static IShortErrorCodeDescription category(final String category) {
		return new ExceptionLogEvent.Builder(category);
	}

	public interface IShortErrorCodeDescription {

		IResponseStatus shortErrorCodeDescription(String shortErrorCodeDescription);
	}

	public interface IResponseStatus {

		IUuid responseStatus(String responseStatus);
	}

	public interface IUuid {

		IBuild uuid(String uuid);
	}

	public interface IBuild {

		ExceptionLogEvent build();
	}

	/**
	 * The builder implementation for ExceptionLogEvent.
	 */
	private static final class Builder implements IBuild, IResponseStatus, IShortErrorCodeDescription, IUuid {

		private final ExceptionLogEvent instance = new ExceptionLogEvent();

		public Builder(final String category) {
			this.instance.category = category;
		}

		@Override
		public IResponseStatus shortErrorCodeDescription(final String shortErrorCodeDescription) {
			this.instance.shortErrorCodeDescription = shortErrorCodeDescription;

			return this;
		}

		@Override
		public IUuid responseStatus(final String responseStatus) {
			this.instance.responseStatus = responseStatus;

			return this;
		}

		@Override
		public IBuild uuid(final String uuid) {
			this.instance.uuid = uuid;

			return this;
		}

		@Override
		public ExceptionLogEvent build() {
			return this.instance;
		}
	}
}
