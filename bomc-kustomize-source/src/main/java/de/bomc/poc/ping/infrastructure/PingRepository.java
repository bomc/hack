/**
 * Project: ping
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
package de.bomc.poc.ping.infrastructure;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.bomc.poc.ping.domain.model.PingEntity;

/**
 * The repository for ping handling.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Repository
public interface PingRepository extends CrudRepository<PingEntity, Long> {
	//
}
