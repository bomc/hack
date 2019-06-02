#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.application.basis.jmx;

import java.time.format.DateTimeFormatter;
/**
 * The implementation of the root mbean.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class RootName extends AbstractMBean implements RootNameMBean {

    /**
     * Creates a instance of <code>AuthRootServer</code>.
     * NOTE@MVN: should be the projectname, will be set during mvn project creation (archetype).
     */
    public RootName() {
        super("Microservice");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object${symbol_pound}toString()
     */
    @Override
    public String toString() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(AbstractMBean.DATE_TIME_FORMATTER);
        final String strStartTime = startTime.format(formatter)
                                             .toString();

        return "AuthRootServer [startTime=" + strStartTime + ", name=" + name + "]";
    }

    @Override
    public void start() {
        // Nothing todo here.

    }

    @Override
    public void stop() {
        // Nothing todo here.

    }
}
