#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.interfaces.rest.v1.basis.exception.dto;

import java.io.Serializable;
import java.time.LocalDate;

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
    // The create date.
    private LocalDate createDate;

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

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(final LocalDate createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "ExceptionLogDTO [shortErrorCodeDescription=" + this.shortErrorCodeDescription + ", category="
                + this.category + ", responseStatus=" + this.responseStatus + ", exceptionUuid=" + this.exceptionUuid
                + ", createDate=" + this.createDate + "]";
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

        ICreateDate responseStatus(String responseStatus);
    }

    public interface ICreateDate {

        IShortErrorCodeDescription createDate(LocalDate createDate);
    }

    public interface IShortErrorCodeDescription {

        IBuild shortErrorCodeDescription(String shortErrorCodeDescription);
    }

    public interface IBuild {

        ExceptionLogDTO build();
    }

    /**
     * The builder implementation for ExceptionLogEvent.
     */
    private static final class Builder
            implements IBuild, IExceptionUuid, IResponseStatus, ICreateDate, IShortErrorCodeDescription {

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
        public IShortErrorCodeDescription createDate(final LocalDate createDate) {
            this.instance.createDate = createDate;

            return this;
        }

        @Override
        public ICreateDate responseStatus(final String responseStatus) {
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
