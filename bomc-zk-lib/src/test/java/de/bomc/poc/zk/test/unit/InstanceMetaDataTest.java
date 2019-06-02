package de.bomc.poc.zk.test.unit;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.bomc.poc.zk.services.InstanceMetaData;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Tests {@link InstanceMetaData}
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
public class InstanceMetaDataTest {

    private static final Logger LOGGER = Logger.getLogger(InstanceMetaDataTest.class);
    private static final String LOG_PREFIX = "InstanceMetaDataTest#";
    private static final String WEB_ARCHIVE_NAME = "service-registry-war";
    private static final String SERVICE_NAME = "my_service_name_in_registry";
    private static final String DESCRIPTION = "my_description";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8080;

    @Test
    public void test010_equals_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_equals_pass");

        final InstanceMetaData instanceMetaData1 = InstanceMetaData.hostAdress(HOST)
                                                                   .port(PORT)
                                                                   .serviceName(SERVICE_NAME)
                                                                   .contextRoot(WEB_ARCHIVE_NAME)
                                                                   .applicationPath(SERVICE_NAME)
                                                                   .description(DESCRIPTION)
                                                                   .build();

        final InstanceMetaData instanceMetaData2 = InstanceMetaData.hostAdress(HOST)
                                                                   .port(PORT)
                                                                   .serviceName(SERVICE_NAME)
                                                                   .contextRoot(WEB_ARCHIVE_NAME)
                                                                   .applicationPath(SERVICE_NAME)
                                                                   .description(DESCRIPTION)
                                                                   .build();

        final Set<InstanceMetaData> hashSet = new HashSet<>(2);
        hashSet.add(instanceMetaData1);
        hashSet.add(instanceMetaData2);

        assertThat(hashSet.size(), is(equalTo(1)));
        assertThat(instanceMetaData1, is(equalTo(hashSet.iterator()
                                                        .next())));
    }

    @Test
    public void test020_notEquals_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_notEquals_pass");

        final InstanceMetaData instanceMetaData1 = InstanceMetaData.hostAdress(HOST)
                                                                   .port(PORT)
                                                                   .serviceName(SERVICE_NAME)
                                                                   .contextRoot(WEB_ARCHIVE_NAME)
                                                                   .applicationPath(SERVICE_NAME)
                                                                   .description(DESCRIPTION)
                                                                   .build();

        final InstanceMetaData instanceMetaData2 = InstanceMetaData.hostAdress(HOST)
                                                                   .port(PORT)
                                                                   .serviceName(SERVICE_NAME)
                                                                   .contextRoot(WEB_ARCHIVE_NAME)
                                                                   .applicationPath(SERVICE_NAME + "/other")
                                                                   .description(DESCRIPTION)
                                                                   .build();

        final Set<InstanceMetaData> hashSet = new HashSet<>(2);
        hashSet.add(instanceMetaData1);
        hashSet.add(instanceMetaData2);

        assertThat(hashSet.size(), is(equalTo(2)));
        assertThat(instanceMetaData1, is(equalTo(hashSet.iterator()
                                                        .next())));
    }

    /**
     * @return a initialized {@link InstanceMetaData} instance.
     */
    private InstanceMetaData getInstanceMetaData(final String host, final int port, final String serviceName, final String webArchiveName, final String applicationPath, final String description) {
        // Metadata for service registration.
        final InstanceMetaData instanceMetaData = InstanceMetaData.hostAdress(host)
                                                                  .port(port)
                                                                  .serviceName(serviceName)
                                                                  .contextRoot(webArchiveName)
                                                                  .applicationPath(applicationPath)
                                                                  .description("description")
                                                                  .build();

        return instanceMetaData;
    }
}
