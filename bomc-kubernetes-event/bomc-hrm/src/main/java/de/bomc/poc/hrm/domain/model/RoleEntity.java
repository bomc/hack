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
 * A RoleEntity is a group of <code>UserEntity</code>s. Basically more than one
 * <code>UserEntity</code> belong to a RoleEntity. Security access policies are
 * assigned to <code>RoleEntity</code>s instead of <code>UserEntity</code>s.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
// LOMBOK
@ToString(callSuper = true, includeFieldNames = true, exclude = {"users", "permissions"})
// JPA
@Entity
@DiscriminatorValue("role")
@NamedQueries({
		@NamedQuery(name = RoleEntity.NQ_FIND_ALL, query = "select distinct(r) from RoleEntity r left join fetch r.users left join fetch r.permissions order by r.name"),
		@NamedQuery(name = RoleEntity.NQ_FIND_BY_ROLE_NAME, query = "select r from RoleEntity r where r.name = ?1"),
		@NamedQuery(name = RoleEntity.NQ_FIND_ALL_BY_USERNAME, query = "select r from RoleEntity r inner join r.users u where u.username = ?1 order by r.name") })
public class RoleEntity extends SecurityObjectEntity implements Serializable {

	private static final String LOG_PREFIX = "RoleEntity#";
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityObjectEntity.class.getName());
	private static final long serialVersionUID = 3582297273912759733L;

	/* ------------------- columns ------------------------------ */
	/**
	 * Whether or not this <code>RoleEntity</code> is immutable. Immutable
	 * <code>RoleEntity</code>s can't be modified.
	 */
	@Column(name = "c_immutable")
	private Boolean immutable = false;

	/* ------------------- collection mapping ------------------- */
	/**
	 * All {@link UserEntity}s assigned to this <code>RoleEntity</code>, this
	 * relationship is bidirectional.
	 */
	@OrderBy("username ASC")
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "t_role_user_join", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "user_id"), schema = "bomcdb")
	private Set<UserEntity> users = new HashSet<UserEntity>();
	/**
	 * All {@link PermissionEntity}s assigned to the <code>RoleEntity</code>, this
	 * relationship is unidirectional. RoleEntity is the owner of this relationship.
	 * Permissions will be loaded lazy, so permissions must be loaded in a
	 * transaction.
	 */
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "t_role_permission_join", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"), schema = "bomcdb")
	private Set<PermissionEntity> permissions = new HashSet<PermissionEntity>();

	/* ------------------- constants ---------------------------- */
	/**
	 * The default prefix String for each created <code>RoleEntity</code>. Name is *
	 * * * {@value} .
	 */
	public static final String ROLE_PREFIX = "ROLE_";
	/**
	 * Query to find all <code>RoleEntity</code>s. Name is {@value} .
	 */
	public static final String NQ_FIND_ALL = "RoleEntity.findAll";
	/**
	 * Query to find <strong>one</strong> <code>RoleEntity</code> by its natural
	 * key.
	 * <p/>
	 * <li>Query parameter index <strong>1</strong> : The name of the
	 * <code>RoleEntity</code> to search for.</li>
	 * <p>
	 * Name is {@value} .
	 * </p>
	 */
	public static final String NQ_FIND_BY_ROLE_NAME = "RoleEntity.findByRolename";
	/**
	 * Query to find <strong>all</strong> <code>RoleEntity</code>s by the given
	 * username.
	 * <p/>
	 * <li>Query parameter index <strong>1</strong> : The username of the
	 * <code>UserEntity</code> to search for.</li>
	 * <p>
	 * Name is {@value} .
	 * </p>
	 */
	public static final String NQ_FIND_ALL_BY_USERNAME = "RoleEntity.findAllRolesByUsername";

	/* ------------------- constructor ------------------------- */

	/**
	 * Accessed by the persistence provider.
	 */
	protected RoleEntity() {
		super();

		LOGGER.debug(LOG_PREFIX + "co");
	}

	/**
	 * Create a new <code>RoleEntity</code> with a name.
	 * 
	 * @param name The name of the <code>RoleEntity</code>
	 * @throws IllegalArgumentException when name is <code>null</code> or empty. Not
	 *                                  allowed to create a RoleEntity with an empty
	 *                                  name
	 */
	public RoleEntity(@NotEmpty final String name) {
		super(name);

		LOGGER.debug(LOG_PREFIX + "co [name=" + name + "]");
	}

	/**
	 * Create a new <code>RoleEntity</code> with a name and a description.
	 * 
	 * @param name        The name of the <code>RoleEntity</code>
	 * @param description The description text of the <code>RoleEntity</code>
	 * @throws IllegalArgumentException when name is <code>null</code> or empty. Not
	 *                                  allowed to create a RoleEntity with an empty
	 *                                  name.
	 */
	public RoleEntity(@NotEmpty final String name, final String description) {
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
	 * 
	 * @return the immutable.
	 */
	public Boolean isImmutable() {
		LOGGER.debug(LOG_PREFIX + "isImmutable [immutable=" + this.immutable + "]");

		return this.immutable;
	}

	/**
	 * Return an unmodifiable Set of all {@link UserEntity}s assigned to the
	 * <code>RoleEntity</code>.
	 * 
	 * @return A Set of all {@link UserEntity}s assigned to the
	 *         <code>RoleEntity</code>
	 */
	public Set<UserEntity> getUsers() {
		LOGGER.debug(LOG_PREFIX + "getUsers [users.size=" + this.users.size() + "]");

		return Collections.unmodifiableSet(this.users);
	}

	/**
	 * Add a set of {@link UserEntity}s to this roleEntity.
	 * 
	 * @param users the given roleEntity Set<RoleEntity>.
	 */
	public void setUsers(@NotNull final Set<UserEntity> users) {
		LOGGER.debug(LOG_PREFIX + "setUsers [users.size=" + users.size() + "]");

		users.stream().forEach(user -> {
			// Add user to roles.
			this.users.add(user);
			// Add on other side of relationship.
			user.internalAddRole(this);
		});
	}

	/**
	 * Add an existing {@link UserEntity} to the <code>RoleEntity</code>.
	 * 
	 * @param userEntity The {@link UserEntity} to add.
	 * @return <code>true</code> if the {@link UserEntity} was new in the collection
	 *         of {@link UserEntity}s, otherwise <code>false</code>
	 */
	public boolean addUser(@NotNull final UserEntity userEntity) {
		LOGGER.debug(LOG_PREFIX + "getUsers [user.username=" + userEntity.getUsername() + "]");

		// Add user to users.
		final boolean isAdded = this.users.add(userEntity);
		// Add on other side of relationship.
		final boolean isInternalAdded = userEntity.internalAddRole(this);

		return isInternalAdded && isAdded;
	}

	/**
	 * Remove a {@link UserEntity} from the <code>RoleEntity</code>.
	 * 
	 * @param userEntity The {@link UserEntity} to be removed
	 */
	public boolean removeUser(@NotNull final UserEntity userEntity) {
		LOGGER.debug(LOG_PREFIX + "removeUser [user.username=" + userEntity.getUsername() + "]");

		// This is a bidirectional relationship, so the relationship has to be
		// removed also on the other side.
		final boolean isInternalRemove = userEntity.internalRemoveRole(this);
		// Remove roleEntity from users.
		final boolean isRemove = this.users.remove(userEntity);

		return isInternalRemove && isRemove;
	}

	/**
	 * Add a {@link UserEntity} to the <code>RoleEntity</code>, for internal use
	 * package scope.
	 * 
	 * @param userEntity The {@link UserEntity} to add.
	 */
	protected boolean internalAddUser(@NotNull final UserEntity userEntity) {
		LOGGER.debug("UserEntity#internalAddUser [" + userEntity.toString() + "]");

		return this.users.add(userEntity);
	}

	/**
	 * Remove a {@link UserEntity} from the <code>RoleEntity</code> for internal use
	 * package scope.
	 * 
	 * @param userEntity The {@link UserEntity} to remove.
	 */
	protected boolean internalRemoveUser(@NotNull final UserEntity userEntity) {
		LOGGER.debug(LOG_PREFIX + "internalRemoveUser [" + userEntity.toString() + "]");

		final boolean isRemoved = this.users.remove(userEntity);

		return isRemoved;
	}

	/**
	 * Remove all {@link RoleEntity}s from the list.
	 */
	public void removeUsers() {
		LOGGER.debug(LOG_PREFIX + "removeUsers");

		final Iterator<UserEntity> itr = this.users.iterator();

		while (itr.hasNext()) {
			final UserEntity user = itr.next();

			final boolean isInternalRemove = user.internalRemoveRole(this);
			LOGGER.debug(LOG_PREFIX + "removeUsers [user.id=" + user.getId() + ", isInternalRemove=" + isInternalRemove
					+ "]");

			itr.remove();
		}
	}

	/**
	 * Return an unmodifiable Set of all {@link PermissionEntity}s belonging to the
	 * <code>RoleEntity</code>.
	 * 
	 * @return A Set of all {@link PermissionEntity}s belonging to this RoleEntity
	 */
	public Set<PermissionEntity> getPermissions() {
		return Collections.unmodifiableSet(this.permissions);
	}

	/**
	 * Add an existing {@link PermissionEntity} to the <code>RoleEntity</code>.
	 * 
	 * @param permissionEntity The {@link PermissionEntity} to be added to the
	 *                         <code>RoleEntity</code>.
	 * @return <code>true</code> if the {@link PermissionEntity} was new to the
	 *         collection of {@link PermissionEntity}s, otherwise <code>false</code>
	 */
	public boolean addPermission(@NotNull final PermissionEntity permissionEntity) {
		LOGGER.debug(LOG_PREFIX + "addPermission [permission=" + permissionEntity.toString() + "]");

		return this.permissions.add(permissionEntity);
	}

	/**
	 * Add an existing {@link PermissionEntity} to the <code>RoleEntity</code>.
	 * 
	 * @param permissionEntity The {@link PermissionEntity} to be added to the
	 *                         <code>RoleEntity</code>
	 * @return <code>true</code> if the {@link PermissionEntity} was successfully
	 *         removed from the Set of {@link PermissionEntity}s, otherwise
	 *         <code>false</code>
	 */
	public boolean removePermission(final PermissionEntity permissionEntity) {
		LOGGER.debug(LOG_PREFIX + "removePermission [permission=" + permissionEntity.toString() + "]");

		return this.permissions.remove(permissionEntity);
	}

	/**
	 * Add an existing {@link PermissionEntity} to the <code>RoleEntity</code>.
	 * 
	 * @param permissions A list of {@link PermissionEntity}s to be removed from the
	 *                    <code>RoleEntity</code>
	 * @return <code>true</code> if the {@link PermissionEntity} was successfully
	 *         removed from the Set of {@link PermissionEntity}s, otherwise
	 *         <code>false</code>
	 */
	public boolean removePermissions(@NotNull final Set<? extends PermissionEntity> permissions) {
		LOGGER.debug(LOG_PREFIX + "removePermissions [permissions.size=" + this.permissions.size() + "]");

		return this.permissions.removeAll(permissions);
	}

	/**
	 * Set all {@link PermissionEntity}s assigned to the <code>RoleEntity</code>.
	 * Already existing {@link PermissionEntity}s will be removed.
	 * 
	 * @param permissions A Set of {@link PermissionEntity}s to be assigned to the
	 *                    <code>RoleEntity</code>
	 * @throws IllegalArgumentException if permissions is <code>null</code>
	 */
	public void setPermissions(final Set<PermissionEntity> permissions) {
		LOGGER.debug(LOG_PREFIX + "setPermissions [permissions.size=" + permissions.size() + "]");

		this.permissions = permissions;
	}

	// ______________________________________________________________
	// Inner class
	/**
	 * A builder class to construct <code>RoleEntity</code> instances.
	 * 
	 * @author <a href="mailto:bomc@bomc.org">bomc</a>
	 */
	public static class Builder {

		private RoleEntity roleEntity;

		/**
		 * Create a new Builder.
		 * 
		 * @param name The name of the <code>RoleEntity</code>
		 */
		public Builder(@NotEmpty final String name) {
			LOGGER.debug("RoleEntity.Builder#co [name=" + name + "]");

			this.roleEntity = new RoleEntity(name);
			this.roleEntity.immutable = false;
		}

		/**
		 * Add a description text to the <code>RoleEntity</code>.
		 * 
		 * @param description as String
		 * @return the builder instance
		 */
		public Builder withDescription(final String description) {
			LOGGER.debug("RoleEntity.Builder#withDescription [description=" + description + "]");

			this.roleEntity.setDescription(description);
			return this;
		}

		/**
		 * Set the <code>RoleEntity</code> to be immutable.
		 * 
		 * @return the builder instance
		 */
		public Builder asImmutable() {
			LOGGER.debug("RoleEntity.Builder#asImmutable");

			this.roleEntity.immutable = true;
			return this;
		}

		/**
		 * Finally build and return the <code>RoleEntity</code> instance.
		 * 
		 * @return the constructed <code>RoleEntity</code>
		 */
		public RoleEntity build() {
			LOGGER.debug("RoleEntity.Builder#build");

			return this.roleEntity;
		}
	} // end inner class
}
