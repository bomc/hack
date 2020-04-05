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

/**
 * The implementation of {@link PingService}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 20.09.2019
 */
@Service // makes it eligible for Spring Component Scan
public class PingService {

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
		
		String pong = "not available";
		
		final Iterable<PingEntity> pingEntityIterable = this.pingRepository.findAll();
		
		if(pingEntityIterable.iterator().hasNext()) {
			pong = pingEntityIterable.iterator().next().getPong();
		}
		
		return pong;
	}
}
