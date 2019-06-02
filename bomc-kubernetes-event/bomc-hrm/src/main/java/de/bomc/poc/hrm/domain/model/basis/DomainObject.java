/**
 * Project: POC PaaS
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
package de.bomc.poc.hrm.domain.model.basis;

import java.time.LocalDateTime;

/**
 * <pre>
 * A DomainObject, implementation classes offer basic functionality
 * characteristic to all persisted domain objects.
 *
 * Each domain object:
 *
 * must have a field for optimistic locking purpose.
 * must return whether it is a transient or persisted instance.
 * must return the technical key value to the caller.
 * must return the creation user of this instance.
 * must return the creation date of this instance.
 * must return the modified user of this instance.
 * must return the modified date of this instance.
 *
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
public interface DomainObject {

    /**
     * Return the technical key value.
     * 
     * @return The technical, unique key
     */
    Long getId();

    /**
     * Set the technical key value. Is necessary for Mapstruct.
     * 
     * @param id
     *            The technical, unique key.
     */
    void setId(Long id);

    /**
     * Return the value of the optimistic locking field.
     * 
     * @return the version number
     */
    Long getVersion();

    /**
     * Return the value of the optimistic locking field.
     * 
     * @return the version number
     */
    void setVersion(Long version);

    /**
     * Check whether the instance is a transient or persisted one.
     * 
     * @return <code>true</code> if transient (not persisted before), otherwise
     *         <code>false</code>
     */
    boolean isNew();

    /**
     * Returns the user who created this instance. This means the technical user
     * as system and not a individual person.
     * 
     * @return the user.
     */
    String getCreateUser();

    /**
     * Set the user who created this instance. This means the technical user as
     * system and not a individual person.
     * 
     * @param createUser
     *            the given user to set.
     */
    void setCreateUser(String createUser);

    /**
     * Returns the creation date/time of this instance.
     * 
     * @return the creation date/time.
     */
    LocalDateTime getCreateDateTime();

    /**
     * Set the creation date/time of this instance.
     * 
     * @param createDateTime
     *            the given date/time to set.
     */
    void setCreateDateTime(LocalDateTime createDateTime);

    /**
     * Returns the user who modified this instance. This means the technical
     * user as system and not a individual person.
     * 
     * @return the user.
     */
    String getModifyUser();

    /**
     * Set the user who modified this instance. This means the technical user as
     * system and not a individual person.
     * 
     * @param modifyUser
     *            the given user to set.
     */
    void setModifyUser(String modifyUser);

    /**
     * Returns the modified date of this instance.
     * 
     * @return the modified date.
     */
    LocalDateTime getModifyDateTime();

    /**
     * Set the modified date of this instance.
     * 
     * @param modifyDate
     *            the given date to set.
     */
    void setModifyDateTime(LocalDateTime modifyDateTime);
}
