/**
 * Project: MY_POC_MICROSERVICE
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.auth.model;

/**
 * <pre>
 * A DomainObject, implementation classes offer basic functionality
 * characteristic to all persisted domain objects.
 * <p>
 * Each domain object:
 * <ul>
 * <li>must have a field for optimistic locking purpose</li>
 * <li>must return whether it is a transient or persisted instance</li>
 * <li>must return the technical key value to the caller</li>
 * </ul>
 * </p>
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public interface DomainObject {

    /**
     * Return the technical key value.
     * @return The technical, unique key
     */
    Long getId();

    /**
     * Set the technical key value. Is necessary for Mapstruct.
     * @param id
     *          The technical, unique key.
     */
    void setId(Long id);

    /**
     * Return the value of the optimistic locking field.
     * @return the version number
     */
    Long getVersion();

    /**
     * Check whether the instance is a transient or persisted one.
     * @return <code>true</code> if transient (not persisted before), otherwise <code>false</code>
     */
    boolean isNew();
}
