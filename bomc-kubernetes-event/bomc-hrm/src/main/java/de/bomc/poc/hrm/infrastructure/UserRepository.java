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

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.bomc.poc.hrm.domain.model.UserEntity;

/**
 * The repository for {@link UserEntity} handling.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	@Query("FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.username = ?1")
	Stream<UserEntity> findByUsername(String username);

	@Query("FROM UserEntity u WHERE u.id = ?1")
	Optional<UserEntity> findById(Long id);
	
	@Query("FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.username = ?1 and u.persistedPassword = ?2")
	Stream<UserEntity> findByUsernameAndPassword(String username, String persistedPassword);

}
