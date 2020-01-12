/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: micha
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.application.poller;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import de.bomc.poc.hrm.application.kafka.HrmKafkaProducer;
import de.bomc.poc.hrm.application.log.method.Loggable;

/**
 * TODO ...
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.01.2020
 */
//@Component
public class AsyncMessageProducer {
	
//	private HrmKafkaProducer hrmKafkaProducer;
//	
//	public AsyncMessageProducer(final HrmKafkaProducer hrmKafkaProducer) {
//		this.hrmKafkaProducer = hrmKafkaProducer;
//	}
//	
//	@Async
//	@Loggable(time = true)
//	public CompletableFuture<String> sendMessage(final String key, final String value) {
//		
//		hrmKafkaProducer.publishMessageToTopic(key, value);
//		
//		return CompletableFuture.completedFuture(key + ", " + value);
//	}
	
}
