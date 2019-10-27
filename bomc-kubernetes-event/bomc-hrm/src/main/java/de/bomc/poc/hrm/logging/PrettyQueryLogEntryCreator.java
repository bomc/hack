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
package de.bomc.poc.hrm.logging;

import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;

import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;

/**
 * Allows pretty logging for hibernate queries.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 25.10.2019
 */
public class PrettyQueryLogEntryCreator extends DefaultQueryLogEntryCreator {
    
    private final Formatter formatter = FormatStyle.BASIC.getFormatter();

    @Override
    protected String formatQuery(final String query) {
        return this.formatter.format(query);
    }

}
