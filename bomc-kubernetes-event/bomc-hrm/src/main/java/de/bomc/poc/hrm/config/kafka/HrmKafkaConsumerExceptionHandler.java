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
package de.bomc.poc.hrm.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.logging.LogLevel;
import org.springframework.kafka.listener.ErrorHandler;

import de.bomc.poc.hrm.application.log.method.Loggable;
import lombok.extern.slf4j.Slf4j;

/**
 * A exception hanlder for kafka error handling.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 14.01.2020
 */
@Slf4j
public class HrmKafkaConsumerExceptionHandler implements ErrorHandler {

	private static final String LOG_PREFIX = HrmKafkaConsumerExceptionHandler.class + "#";

	/**
	 * Handle the exception.
	 * 
	 * @param thrownException the exception.
	 * @param consumerRecord  the remaining records including the one that failed.
	 */
	@Override
	@Loggable(result = false, params = true, value = LogLevel.DEBUG, time = true)
	public void handle(final Exception exception, final ConsumerRecord<?, ?> consumerRecord) {

		if (exception == null) {
			return;
		}

		// ______________________________________
		// DO SOMETHING USEFULL WITH THE ERRORMESSAGE (e.g. notifying)
		// --------------------------------------
		log.error(LOG_PREFIX + "handleError - [partition=" + consumerRecord.partition() //
		        + ", offset=" + consumerRecord.offset() //
		        + ", topic=" + consumerRecord.topic() //
		        + ", exception.message=" + exception.getMessage() //
		        + "\n", exception);

	}

}
