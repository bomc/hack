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
package ${package};

import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import javax.persistence.EntityManager;

/**
 * A base class for all unit test classes.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
public abstract class AbstractUnitTest {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(AbstractUnitTest.class);
    @SuppressWarnings("unused")
    private static final String LOG_PREFIX = "AbstractUnitTest${symbol_pound}";
    // Read this from 'test/resources/META-INF/persistence.xml',
    // 'persistence-unit name'.
    // NOTE@MVN: unit name.
    private static final String PERSISTENCE_UNIT_NAME = "bomc-h2-pu";
    @Rule
    public final EntityManagerProvider emProvider = EntityManagerProvider.persistenceUnit(PERSISTENCE_UNIT_NAME);
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    protected EntityManager entityManager;
    //
    // Constants

    // _______________________________________________
    // Helper methods.
    // -----------------------------------------------
}
