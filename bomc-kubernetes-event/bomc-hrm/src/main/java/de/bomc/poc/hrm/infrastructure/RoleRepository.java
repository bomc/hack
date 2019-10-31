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
package de.bomc.poc.hrm.infrastructure;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.bomc.poc.hrm.domain.model.RoleEntity;

/**
 * The repository for {@link RoleEntity} handling.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 30.10.2019
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

	@Query("FROM RoleEntity r WHERE r.name = ?1")
	Stream<RoleEntity> findByName(String name);
	
	/**
	 * Checks if user has the permission.
	 * 
	 * @param username   the given username.
	 * @param permission the permission to check.
	 * @return a role instance or 'null' if the user has not the permission.
	 */
	@Query("FROM RoleEntity r JOIN r.users u JOIN r.permissions p WHERE u.username = ?1 AND p.name = ?2")
	RoleEntity findRoleByUsernameAndPermission(String username, String permission);
}
