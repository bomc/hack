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
package de.bomc.poc.hrm.application.poller;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import de.bomc.poc.hrm.application.kafka.HrmKafkaProducer;
import de.bomc.poc.hrm.application.log.method.Loggable;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

/**
 * This components handles ... .
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 01.11.2019
 */
@Slf4j
//@Component
@PropertySource("classpath:scheduled.properties")
public class PollerJob {

	private static final String LOG_PREFIX = PollerJob.class.getName() + "#";

//	private Date lastModified = null;
	
//	@Value("${myrest.url}")
//	private String url = "http://www.google.de:80/";

//	@Autowired
//	private RestTemplate restTemplate;
//	@Autowired
//	private AsyncMessageProducer asyncMessageProducer;
	@Autowired
	private HrmKafkaProducer hrmKafkaProducer;

	@PostConstruct
	public void init() {
		log.info(LOG_PREFIX + "init - Order polling activated.");
	}

	/**
	 * NOTE: The 'fixedDelay' task is invoked for every specified interval but the
	 * time is measured from the completion time of each preceding invocation.
	 */
	@Timed(value = "bomc.hrm.poller.job.poll", longTask = true)
	@Loggable(result = false, params = false, value = LogLevel.DEBUG, time = true)
	@Scheduled(initialDelayString = "${bomc.hrm.schedule.poller.startDelay:PT1S}", fixedDelayString = "${bomc.hrm.schedule.poller.repeatInterval:PT10S}")
	public void poll() {

//		final HttpHeaders requestHeaders = new HttpHeaders();
//		
//		if (lastModified != null) {
//			requestHeaders.set(HttpHeaders.IF_MODIFIED_SINCE, DateUtils.formatDate(lastModified));
//		}
//
//		final HttpEntity<?> requestEntity = new HttpEntity(requestHeaders);
		/*final ResponseEntity<Feed> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class); */

//		if (response.getStatusCode() != HttpStatus.NOT_MODIFIED) {
//			log.trace("data has been modified");
//			Feed feed = response.getBody();
//			for (Entry entry : feed.getEntries()) {
//				if ((lastModified == null) || (entry.getUpdated().after(lastModified))) {
//					Invoice invoice = restTemplate.getForEntity(entry.getContents().get(0).getSrc(), Invoice.class)
//					        .getBody();
//					log.trace("saving invoice {}", invoice.getId());
//					invoiceService.generateInvoice(invoice);
//				}
//			}
//			if (response.getHeaders().getFirst(HttpHeaders.LAST_MODIFIED) != null) {
//				lastModified = DateUtils.parseDate(response.getHeaders().getFirst(HttpHeaders.LAST_MODIFIED));
//				log.trace("Last-Modified header {}", lastModified);
//			}
//		} else {
//			log.trace("no new data");
//		}
		
		final String messageToSend = "message on:" + Long.toString(System.currentTimeMillis());
		final String kafkaKey = "pollerKey";
		
////		this.hrmKafkaProducer.publishMessageToTopic(kafkaKey, messageToSend);
		
		this.hrmKafkaProducer.publishMessageToTopic(kafkaKey, messageToSend);
//		
//		final CompletableFuture<String> retMessage = this.asyncMessageProducer.sendMessage(kafkaKey, messageToSend);
//		
//		try {
//			log.debug(LOG_PREFIX + "poll [retMessage=" + retMessage.get() + "]");
//		} catch (InterruptedException | ExecutionException e) {
//			log.error(LOG_PREFIX + "poll failed! " + e);
//		}
	}

}
