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
package de.bomc.poc.invoice.domain.model.idempotent;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import de.bomc.poc.invoice.domain.model.basis.AbstractEntity;

/**
 * A entity that holds the idempotent id.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Entity
@Table(name = "T_IDEMPOTENT_MESSAGE"/*,
    uniqueConstraints =
    @UniqueConstraint(columnNames = {"C_MESSAGE_ID"})*/)
@NamedQueries({
    @NamedQuery(name = IdempotentMessage.NQ_QUERY_STRING, query = "select im from IdempotentMessage im where im.messageId = ?1 and im.processorName = ?2"),
    @NamedQuery(name = IdempotentMessage.NQ_QUERY_CLEAR_STRING, query = "select im from IdempotentMessage im where im.processorName = ?1")
})
public class IdempotentMessage extends AbstractEntity<IdempotentMessage> implements Serializable {

    /**
	 * The serial UUID.
	 */
	private static final long serialVersionUID = -905791234918245768L;
	/**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(IdempotentMessage.class.getName());
    /**
     * A log prefix.
     */
    private static final String LOG_PREFIX = "IdempotentMessage#";

    /* --------------------- constants ------------------------------ */
    /**
     * The default prefix String for each created <code>IdempotentMessage</code>-NamedQuery.
     */
    protected static final String NQ_PREFIX = "IDEMPOTENT_MESSAGE.";
    /**
     * <pre>
     * Query to find <code>IdempotentMessage</code> by processorName and messageId.
     * Query parameter index <strong>1</strong> : The processorName to search for.
     * Query parameter index <strong>2</strong> : The messageId to search for.
     * </pre>
     */
    public static final String NQ_QUERY_STRING = NQ_PREFIX + "queryString";
    /**
     * <pre>
     * Query to find <code>IdempotentMessage</code> by processorName.
     * Query parameter index <strong>1</strong> : The processorName to search for.
     * </pre>
     */
    public static final String NQ_QUERY_CLEAR_STRING = NQ_PREFIX + "queryClearString";
    /* --------------------- columns -------------------------------- */
    @Column(name = "C_MESSAGE_ID", unique = true, nullable = false)
    private String messageId;
    @Column(name = "C_PROCESSOR_NAME", nullable = false)
    private String processorName;

    /* --------------------- constructors --------------------------- */
    public IdempotentMessage() {
        LOGGER.log(Level.FINE, LOG_PREFIX + "co");

        // Used by Jpa-Provider.
    }

    public IdempotentMessage(final String messageId, final String processorName) {
    	LOGGER.log(Level.FINE, LOG_PREFIX + "[messageId=" + messageId + ", processorName=" + processorName + "]");

        this.messageId = messageId;
        this.processorName = processorName;
    }

    /* ----------------------------- methods ------------------------- */

    /**
     * @return the type of this entity.
     */
    protected Class<IdempotentMessage> getEntityClass() {
        return IdempotentMessage.class;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getProcessorName() {
        return this.processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    @Override
    public String toString() {
        return "IdempotentMessage [processorName=" + getProcessorName() + ", messageId=" + getMessageId() + ", createdDate=" + super.getCreateDateTime() + "]";
    }
}
