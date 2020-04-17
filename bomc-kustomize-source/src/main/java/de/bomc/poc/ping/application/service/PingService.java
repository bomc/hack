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
package de.bomc.poc.ping.application.service;

import org.springframework.stereotype.Service;

import de.bomc.poc.ping.domain.model.PingEntity;
import de.bomc.poc.ping.infrastructure.PingRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * The implementation of {@link PingService}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 20.09.2019
 */
@Slf4j
@Service // makes it eligible for Spring Component Scan
public class PingService {

	private static final String LOG_PREFIX = PingService.class.getName() + "#";
	
	/* --------------------- member variables ----------------------- */
	
	private final PingRepository pingRepository;
	
	/* --------------------- constructor ---------------------------- */

	/**
	 * Creates a new instance of <code>UserService</code>.
	 * 
	 * @param pingRepository     the given ping repository to inject.
	 */
	public PingService(final PingRepository pingRepository) {

		this.pingRepository = pingRepository;
	}

	/* --------------------- methods -------------------------------- */

	public String getPing() {
		log.debug(LOG_PREFIX + "getPing");
		
		String pong = "not available";
		
		final Iterable<PingEntity> pingEntityIterable = this.pingRepository.findAll();
		
		if(pingEntityIterable.iterator().hasNext()) {
			pong = pingEntityIterable.iterator().next().getPong();
		}
		
		return pong;
	}
}
