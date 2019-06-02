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
package de.bomc.poc.order.interfaces.rest.v1.basis.exception.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The dto shows the content of a exception.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class ExceptionLogDTO implements Serializable {

    /**
     * The serial UID
     */
    private static final long serialVersionUID = -8764942930628305868L;
    // Short error decription.
    private String shortErrorCodeDescription;
    // The error category, defined by ErrorCode.getCategory.
    private String category;
    // The reponse status.
    private String responseStatus;
    // The exceptionUuid created by the exception instance.
    private String exceptionUuid;
    // The create date/time.
    private LocalDateTime createDateTime;

    /**
     * Default co
     */
    public ExceptionLogDTO() {
        // Indicates a pojo.
    }

    // _______________________________________________
    // Getter, setter for attributes, should only be used during mapping.
    // -----------------------------------------------

    public String getShortErrorCodeDescription() {
        return this.shortErrorCodeDescription;
    }

    public void setShortErrorCodeDescription(final String shortErrorCodeDescription) {
        this.shortErrorCodeDescription = shortErrorCodeDescription;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getResponseStatus() {
        return this.responseStatus;
    }

    public void setResponseStatus(final String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getExceptionUuid() {
        return this.exceptionUuid;
    }

    public void setExceptionUuid(final String uuid) {
        this.exceptionUuid = uuid;
    }

    public LocalDateTime getCreateDateTime() {
        return this.createDateTime;
    }

    public void setCreateDateTime(final LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    @Override
    public String toString() {
        return "ExceptionLogDTO [shortErrorCodeDescription=" + this.shortErrorCodeDescription + ", category="
                + this.category + ", responseStatus=" + this.responseStatus + ", exceptionUuid=" + this.exceptionUuid
                + ", createDateTime=" + this.createDateTime + "]";
    }

    // _______________________________________________
    // Implementation of the builder pattern as inner class.
    // -----------------------------------------------

    public static IExceptionUuid category(final String category) {
        return new ExceptionLogDTO.Builder(category);
    }

    public interface IExceptionUuid {

        IResponseStatus exceptionUuid(String exceptionUuid);
    }

    public interface IResponseStatus {

        ICreateDateTime responseStatus(String responseStatus);
    }

    public interface ICreateDateTime {

        IShortErrorCodeDescription createDateTime(LocalDateTime createDateTime);
    }

    public interface IShortErrorCodeDescription {

        IBuild shortErrorCodeDescription(String shortErrorCodeDescription);
    }

    public interface IBuild {

        ExceptionLogDTO build();
    }

    /**
     * The builder implementation for ExceptionLogDTO.
     */
    private static final class Builder
            implements IBuild, IExceptionUuid, IResponseStatus, ICreateDateTime, IShortErrorCodeDescription {

        private final ExceptionLogDTO instance = new ExceptionLogDTO();

        public Builder(final String category) {
            this.instance.category = category;
        }

        @Override
        public IBuild shortErrorCodeDescription(final String shortErrorCodeDescription) {
            this.instance.shortErrorCodeDescription = shortErrorCodeDescription;

            return this;
        }

        @Override
        public IShortErrorCodeDescription createDateTime(final LocalDateTime createDateTime) {
            this.instance.createDateTime = createDateTime;

            return this;
        }

        @Override
        public ICreateDateTime responseStatus(final String responseStatus) {
            this.instance.responseStatus = responseStatus;

            return this;
        }

        @Override
        public IResponseStatus exceptionUuid(final String exceptionUuid) {
            this.instance.exceptionUuid = exceptionUuid;

            return this;
        }

        @Override
        public ExceptionLogDTO build() {
            return this.instance;
        }
    }
}
