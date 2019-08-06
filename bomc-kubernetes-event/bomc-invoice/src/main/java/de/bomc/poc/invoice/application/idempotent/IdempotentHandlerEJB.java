/**
 * Project: bomc-invoice
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
package de.bomc.poc.invoice.application.idempotent;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.exception.ConstraintViolationException;

import de.bomc.poc.exception.core.ExceptionUtil;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.invoice.application.internal.AppErrorCodeEnum;
import de.bomc.poc.invoice.application.log.LoggerQualifier;
import de.bomc.poc.invoice.domain.model.idempotent.IdempotentMessage;
import de.bomc.poc.invoice.domain.model.idempotent.JpaIdempotentRepository;
import de.bomc.poc.invoice.infrastructure.persistence.basis.qualifier.JpaRepository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An ejb that handles the Idempotent functionality against the db.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Stateless
@Transactional(Transactional.TxType.REQUIRES_NEW)
public class IdempotentHandlerEJB {

	private static String LOG_PREFIX = "IdempotentHandlerEJB#";
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	@JpaRepository
	private JpaIdempotentRepository jpaIdempotentRepository;

	/**
	 * Query if a db entry is in db available by the given extractedMessageId and
	 * extractedProcessorName.
	 * 
	 * @param messageId     the given messageId
	 * @param processorName the given processorName.
	 * @return if a db entity is available, the entity is returned. Otherwise a
	 *         empty list is returned.
	 */
	public List<IdempotentMessage> queryString(final String messageId, final String processorName) {
		this.logger.log(Level.FINE,
				LOG_PREFIX + "queryString [messageId=" + messageId + ", processorName=" + processorName + "]");

		final List<IdempotentMessage> idempotentMessageList = this.jpaIdempotentRepository
				.findByPositionalParameters(IdempotentMessage.NQ_QUERY_STRING, messageId, processorName);

		return idempotentMessageList;
	}

	/**
	 * Persist the given IdempotentMessage object.
	 * 
	 * @param idempotentMessage the object to persist.
	 * @param userId            the given userId.
	 */
	public void persist(final IdempotentMessage idempotentMessage, final String userId) {
		this.logger.log(Level.FINE, LOG_PREFIX + "persist [" + idempotentMessage + ", userId=" + userId + "]");

		try {
			this.jpaIdempotentRepository.persist(idempotentMessage, userId);
			this.jpaIdempotentRepository.clearEntityManagerCache();
		} catch (final Exception ex) {
			if (ExceptionUtil.is(ex, ConstraintViolationException.class) == true) {
				this.logger.warning(LOG_PREFIX + "persist - a request is already running [idempotentMessage.messageId="
						+ idempotentMessage.getMessageId() + "]");
			} else {
				final AppRuntimeException appRuntimeException = new AppRuntimeException(ex, AppErrorCodeEnum.APP_COMMON_PERSISTENCE_FAILURE_10607);
				this.logger.log(Level.SEVERE, appRuntimeException.stackTraceToString());
				
				throw appRuntimeException;
			}
		}
	}

	/**
	 * Remove the entity from db. NOTE: the given idempotentMessage is detached,
	 * because it is loaded from another transaction. So it has to be attached by
	 * calling the method "merge" on the entityManager.
	 * 
	 * @param idempotentMessage the object to remove.
	 * @param userId            the given userId.
	 */
	public void remove(final IdempotentMessage idempotentMessage, final String userId) {
		this.logger.log(Level.FINE, LOG_PREFIX + "remove [" + idempotentMessage + ", userId=" + userId + "]");

		// NOTE: the entity is detached, here it has to be attached.
		final IdempotentMessage idempotentMessageMerged = this.jpaIdempotentRepository.merge(idempotentMessage, userId);

		this.jpaIdempotentRepository.remove(idempotentMessageMerged);
	}

}
