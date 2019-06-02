package de.bomc.poc.zk.test.arq;

import de.bomc.poc.zk.concurrent.producer.ManagedConcurrencyProducer;
import de.bomc.poc.zk.concurrent.qualifier.ManagedThreadFactoryQualifier;
import de.bomc.poc.zk.curator.producer.CuratorFrameworkProducer;
import de.bomc.poc.zk.curator.producer.SystemDefaultDnsResolver;
import de.bomc.poc.zk.curator.qualifier.CuratorFrameworkQualifier;
import de.bomc.poc.zk.exception.AppZookeeperException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * This class tests connecting to zookeeper with the
 * {@link CuratorFrameworkProducer}.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
@RunWith(Arquillian.class)
public class CuratorFrameworkProducerTestIT extends ArquillianBase {

	private static final Logger LOGGER = Logger.getLogger(CuratorFrameworkProducerTestIT.class);
	private static final String LOG_PREFIX = "CuratorFrameworkProducerTestIT#";
	private static final String WEB_ARCHIVE_NAME = "curator-producer-war";

	@Deployment
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = ArquillianBase.createTestArchive(WEB_ARCHIVE_NAME + ".war");

		webArchive.addClasses(CuratorFrameworkProducerTestIT.class).addClass(AppZookeeperException.class)
				.addClasses(CuratorFrameworkProducer.class, CuratorFrameworkQualifier.class)
				.addClasses(ManagedConcurrencyProducer.class, ManagedThreadFactoryQualifier.class)
				.addClass(SystemDefaultDnsResolver.class)
				.addAsResource("zookeeper.properties");

		LOGGER.info(LOG_PREFIX + "archiveContent: " + webArchive.toString(true));

		return webArchive;
	}

	@Inject
	@CuratorFrameworkQualifier
	private CuratorFramework client;

	/**
	 * <pre>
	 * 	mvn clean install -Parq-wildfly-remote -Dtest=CuratorFrameworkProducerTestIT#test010_useCuratorProducer_Pass
	 * 
	 * <b><code>test010_useCuratorProducer_Pass</code>:</b><br>
	 * 	Tests producing a zookeeper client which is wrapped by the CuratorFrameWork using the {@link CuratorFrameworkProducer}.
	 *
	 * <b>Preconditions:</b><br>
	 *
	 * <b>Scenario:</b><br>
	 * 	The following steps are executed:
	 * 	- The artifact is successful deployed in wildfly and the server is running.
	 * 	- The CuratorFramework client is successful injected.</li>
	 *
	 * <b>Postconditions:</b><br>
	 *	The CuratorFramework client is successful injected.
	 * </pre>
	 */
	@Test
	@InSequence(10)
	public void test010_useCuratorProducer_Pass() {
		LOGGER.debug(LOG_PREFIX + "test010_useCuratorProducer_Pass");

		try {
			final CuratorFrameworkState state = this.client.getState();

			LOGGER.debug(LOG_PREFIX + "test010_useCuratorProducer_Pass - [state.name=" + state.name() + "]");

			assertThat(state.name(), is(equalTo(CuratorFrameworkState.STARTED.name())));
		} finally {
			// Client must not not be closed, is done by the Producer (Dispose).
		}
	}
}
