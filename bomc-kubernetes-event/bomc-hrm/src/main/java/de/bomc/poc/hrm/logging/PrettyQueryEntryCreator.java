package de.bomc.poc.hrm.logging;

import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;

import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;

/**
 *
 * use hibernate to format queries
 *
 * @author rajakolli
 * @version $Id: $Id
 */
public class PrettyQueryEntryCreator extends DefaultQueryLogEntryCreator {
    
    private Formatter formatter = FormatStyle.BASIC.getFormatter();

    /** {@inheritDoc} */
    @Override
    protected String formatQuery(String query) {
        return this.formatter.format(query);
    }

}
