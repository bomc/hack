/**
 * Project: hrm
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
package de.bomc.poc.hrm.domain.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.PreRemove;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A Role is a group of <code>User</code>s. Basically more than one <code>User</code> belong to a Role. Security access policies are assigned to <code>Role</code>s instead of <code>User</code>s.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
// LOMBOK
@ToString
// JPA
@Entity
@DiscriminatorValue("ROLE")
@NamedQueries({@NamedQuery(name = Role.NQ_FIND_ALL, query = "select distinct(r) from Role r left join fetch r.users left join fetch r.grants order by r.name"),
               @NamedQuery(name = Role.NQ_FIND_BY_UNIQUE_QUERY, query = "select r from Role r where r.name = ?1"),
               @NamedQuery(name = Role.NQ_FIND_ALL_BY_USERNAME, query = "select r from Role r inner join r.users u where u.username = ?1 order by r.name")})
public class Role extends SecurityObject implements Serializable {

	private static final String LOG_PREFIX = "Role#"; 
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityObject.class.getName());
    private static final long serialVersionUID = 3582297273912759733L;

	/* ------------------- columns ------------------------------ */
    /**
     * Whether or not this <code>Role</code> is immutable. Immutable <code>Role</code>s can't be modified.
     */
    @Column(name = "C_IMMUTABLE")
    private Boolean immutable = false;

	/* ------------------- collection mapping ------------------- */
    /**
     * All {@link User}s assigned to this <code>Role</code>, this relationship is bidirectional.
     */
    @OrderBy("username ASC")
    @ManyToMany(cascade = {CascadeType.REFRESH})
    @JoinTable(name = "COR_ROLE_USER_JOIN", joinColumns = @JoinColumn(name = "ROLE_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
    private Set<User> users = new HashSet<User>();
    /**
     * All {@link Grant}s assigned to the <code>Role</code>, this relationship is unidirectional. Role is the owner of this relationship.
     * Grants will be loaded lazy, so grants must be loaded in a transaction.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinTable(name = "COR_ROLE_ROLE_JOIN", joinColumns = @JoinColumn(name = "ROLE_ID"), inverseJoinColumns = @JoinColumn(name = "GRANT_ID"))
    private Set<Grant> grants = new HashSet<Grant>();

	/* ------------------- constants ---------------------------- */
    /**
     * The default prefix String for each created <code>Role</code>. Name is * * * {@value} .
     */
    public static final String ROLE_PREFIX = "ROLE_";
    /**
     * Query to find all <code>Role</code>s. Name is {@value} .
     */
    public static final String NQ_FIND_ALL = "Role.findAll";
    /**
     * Query to find <strong>one</strong> <code>Role</code> by its natural key.
     * <p/>
     * <li>Query parameter index <strong>1</strong> : The name of the <code>Role</code> to search for.</li> <p> Name is {@value} . </p>
     */
    public static final String NQ_FIND_BY_UNIQUE_QUERY = "Role.findByRolename";
    /**
     * Query to find <strong>all</strong> <code>Role</code>s by the given username.
     * <p/>
     * <li>Query parameter index <strong>1</strong> : The username of the <code>User</code> to search for.</li> <p> Name is {@value} . </p>
     */
    public static final String NQ_FIND_ALL_BY_USERNAME = "Role.findAllRolesByUsername";

	/* ------------------- constructor ------------------------- */

    /**
     * Accessed by the persistence provider.
     */
    protected Role() {
        super();

        LOGGER.debug(LOG_PREFIX + "co");
    }

    /**
     * Create a new <code>Role</code> with a name.
     * @param name The name of the <code>Role</code>
     * @throws IllegalArgumentException when name is <code>null</code> or empty. Not allowed to create a Role with an empty name
     */
    public Role(@NotEmpty final String name) {
        super(name);

        LOGGER.debug(LOG_PREFIX + "co [name=" + name + "]");
    }

    /**
     * Create a new <code>Role</code> with a name and a description.
     * @param name        The name of the <code>Role</code>
     * @param description The description text of the <code>Role</code>
     * @throws IllegalArgumentException when name is <code>null</code> or empty. Not allowed to create a Role with an empty name.
     */
    public Role(@NotEmpty final String name, String description) {
        super(name, description);

        LOGGER.debug(LOG_PREFIX + "co [name=" + name + ", description=" + description + "]");
    }

	/* ------------------- methods -------------------------- */

    @PreRemove
    public void preRemove() {
        LOGGER.debug(LOG_PREFIX + "preRemove");

        // Remove all referenced users.
        this.removeUsers();
    }

    /**
     * Get the immutable.
     * @return the immutable.
     */
    public Boolean isImmutable() {
        LOGGER.debug(LOG_PREFIX + "isImmutable [immutable=" + this.immutable + "]");

        return this.immutable;
    }

    /**
     * Return an unmodifiable Set of all {@link User}s assigned to the <code>Role</code>.
     * @return A Set of all {@link User}s assigned to the <code>Role</code>
     */
    public Set<User> getUsers() {
        LOGGER.debug(LOG_PREFIX + "getUsers [users.size=" + this.users.size() + "]");

        return Collections.unmodifiableSet(this.users);
    }

    /**
     * Add a set of {@link User}s to this role.
     * @param users the given role Set<Role>.
     */
    public void setUsers(@NotNull final Set<User> users) {
        LOGGER.debug(LOG_PREFIX + "setUsers [users.size=" + users.size() + "]");

        users.stream()
             .forEach(user -> {
                 // Add user to roles.
                 this.users.add(user);
                 // Add on other side of relationship.
                 user.internalAddRole(this);
             });
    }

    /**
     * Add an existing {@link User} to the <code>Role</code>.
     * @param user The {@link User} to add.
     * @return <code>true</code> if the {@link User} was new in the collection of {@link User}s, otherwise <code>false</code>
     */
    public boolean addUser(@NotNull final User user) {
        LOGGER.debug(LOG_PREFIX + "getUsers [user.username=" + user.getUsername() + "]");

        // Add user to users.
        final boolean isAdded = this.users.add(user);
        // Add on other side of relationship.
        final boolean isInternalAdded = user.internalAddRole(this);

        return isInternalAdded && isAdded;
    }

    /**
     * Remove a {@link User} from the <code>Role</code>.
     * @param user The {@link User} to be removed
     */
    public boolean removeUser(@NotNull final User user) {
        LOGGER.debug(LOG_PREFIX + "removeUser [user.username=" + user.getUsername() + "]");

        // This is a bidirectional relationship, so the relationship has to be
        // removed also on the other side.
        final boolean isInternalRemove = user.internalRemoveRole(this);
        // Remove role from users.
        final boolean isRemove = this.users.remove(user);

        return isInternalRemove && isRemove;
    }

    /**
     * Add a {@link User} to the <code>Role</code>, for internal use package scope.
     * @param user The {@link User} to add.
     */
    protected boolean internalAddUser(@NotNull final User user) {
        LOGGER.debug("User#internalAddUser [" + user.toString() + "]");

        return this.users.add(user);
    }

    /**
     * Remove a {@link User} from the <code>Role</code> for internal use package scope.
     * @param user The {@link User} to remove.
     */
    protected boolean internalRemoveUser(@NotNull final User user) {
        LOGGER.debug(LOG_PREFIX + "internalRemoveUser [" + user.toString() + "]");

        final boolean isRemoved = this.users.remove(user);

        return isRemoved;
    }

    /**
     * Remove all {@link Role}s from the list.
     */
    public void removeUsers() {
        LOGGER.debug(LOG_PREFIX + "removeUsers");

        Iterator<User> itr = this.users.iterator();

        while (itr.hasNext()) {
            final User user = itr.next();

            final boolean isInternalRemove = user.internalRemoveRole(this);
            LOGGER.debug(LOG_PREFIX + "removeUsers [user.id=" + user.getId() + ", isInternalRemove=" + isInternalRemove + "]");

            itr.remove();
        }
    }

    /**
     * Return an unmodifiable Set of all {@link Grant}s belonging to the <code>Role</code>.
     * @return A Set of all {@link Grant}s belonging to this Role
     */
    public Set<Grant> getGrants() {
        return Collections.unmodifiableSet(grants);
    }

    /**
     * Add an existing {@link Grant} to the <code>Role</code>.
     * @param grant The {@link Grant} to be added to the <code>Role</code>.
     * @return <code>true</code> if the {@link Grant} was new to the collection of {@link Grant}s, otherwise <code>false</code>
     */
    public boolean addGrant(@NotNull Grant grant) {
        LOGGER.debug(LOG_PREFIX + "addGrant [grant=" + grant.toString() + "]");

        return grants.add(grant);
    }

    /**
     * Add an existing {@link Grant} to the <code>Role</code>.
     * @param grant The {@link Grant} to be added to the <code>Role</code>
     * @return <code>true</code> if the {@link Grant} was successfully removed from the Set of {@link Grant}s, otherwise <code>false</code>
     */
    public boolean removeGrant(Grant grant) {
        LOGGER.debug(LOG_PREFIX + "removeGrant [grant=" + grant.toString() + "]");

        return grants.remove(grant);
    }

    /**
     * Add an existing {@link Grant} to the <code>Role</code>.
     * @param grants A list of {@link Grant}s to be removed from the <code>Role</code>
     * @return <code>true</code> if the {@link Grant} was successfully removed from the Set of {@link Grant}s, otherwise <code>false</code>
     */
    public boolean removeGrants(@NotNull Set<? extends Grant> grants) {
        LOGGER.debug(LOG_PREFIX + "removeGrants [grants.size=" + grants.size() + "]");

        return this.grants.removeAll(grants);
    }

    /**
     * Set all {@link Grant}s assigned to the <code>Role</code>. Already existing {@link Grant}s will be removed.
     * @param grants A Set of {@link Grant}s to be assigned to the <code>Role</code>
     * @throws IllegalArgumentException if grants is <code>null</code>
     */
    public void setGrants(Set<Grant> grants) {
        LOGGER.debug(LOG_PREFIX + "setGrants [grants.size=" + grants.size() + "]");

        this.grants = grants;
    }

    // ______________________________________________________________
    // Inner class
    /**
     * A builder class to construct <code>Role</code> instances.
     * @author <a href="mailto:bomc@bomc.org">bomc</a>
     */
    public static class Builder {

        private Role role;

        /**
         * Create a new Builder.
         * @param name The name of the <code>Role</code>
         */
        public Builder(@NotEmpty final String name) {
            LOGGER.debug("Role.Builder#co [name=" + name + "]");

            this.role = new Role(name);
            this.role.immutable = false;
        }

        /**
         * Add a description text to the <code>Role</code>.
         * @param description as String
         * @return the builder instance
         */
        public Builder withDescription(final String description) {
            LOGGER.debug("Role.Builder#withDescription [description=" + description + "]");

            this.role.setDescription(description);
            return this;
        }

        /**
         * Set the <code>Role</code> to be immutable.
         * @return the builder instance
         */
        public Builder asImmutable() {
            LOGGER.debug("Role.Builder#asImmutable");

            this.role.immutable = true;
            return this;
        }

        /**
         * Finally build and return the <code>Role</code> instance.
         * @return the constructed <code>Role</code>
         */
        public Role build() {
            LOGGER.debug("Role.Builder#build");

            return this.role;
        }
    } // end inner class
}
