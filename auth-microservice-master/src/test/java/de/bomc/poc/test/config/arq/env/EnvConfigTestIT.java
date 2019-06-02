/**
 * Project: MY_POC
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.config.arq.env;

import de.bomc.poc.config.EnvConfigKeys;
import de.bomc.poc.config.EnvConfigSingletonEJB;
import de.bomc.poc.config.producer.EnvConfigProducer;
import de.bomc.poc.config.qualifier.EnvConfigQualifier;
import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.exception.app.AppInitializationException;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.test.config.arq.ArquillianEnvBase;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test the reading and injecting of environment parameter at runtime.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@RunWith(Arquillian.class)
public class EnvConfigTestIT extends ArquillianEnvBase {
    private static final String WEB_ARCHIVE_NAME = "auth-env";
    @Inject
    @LoggerQualifier
    private Logger logger;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive
            webArchive =
            ArquillianEnvBase.createTestArchive(WEB_ARCHIVE_NAME)
                             .addClasses(EnvConfigTestIT.class)
                             .addClasses(EnvConfigKeys.class, EnvConfigQualifier.class, EnvConfigSingletonEJB.class, EnvConfigProducer.class)
                             .addClasses(AppAuthRuntimeException.class, AppInitializationException.class)
                             .addClasses(LoggerProducer.class, LoggerQualifier.class);

        System.out.println(webArchive.toString(true));

        return webArchive;
    }

    @Inject
    @EnvConfigQualifier(key = EnvConfigKeys.BIND_ADDRESS)
    private String bindAddress;
    @Inject
    @EnvConfigQualifier(key = EnvConfigKeys.BIND_PORT)
    private String port;
    @Inject
    @EnvConfigQualifier(key = EnvConfigKeys.BIND_SECURE_PORT)
    private String securePort;
    @Inject
    @EnvConfigQualifier(key = EnvConfigKeys.NODE_NAME)
    private String nodeName;
    @Inject
    @EnvConfigQualifier(key = EnvConfigKeys.ZNODE_BASE_PATH)
    private String zNodeBasePath;

    /**
     * Tests the injected instance environment variables.
     * <p/>
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=EnvConfigTestIT#test01_envInjection_Pass
     * </pre>
     */
    @Test
    public void test01_envInjection_Pass() {
        this.logger.debug("EnvironmentConfigurationTestIT#test01_envInjection_Pass");

        // assertThat(bindAddress,
        // org.hamcrest.CoreMatchers.is(org.hamcrest.CoreMatchers.equalTo("192.168.4.1")));
        assertThat(this.bindAddress, notNullValue());

        // assertThat(port,
        // org.hamcrest.CoreMatchers.is(org.hamcrest.CoreMatchers.equalTo("8180")));
        assertThat(this.port, notNullValue());

        // assertThat(securePort,
        // org.hamcrest.CoreMatchers.is(org.hamcrest.CoreMatchers.equalTo("8443")));
        assertThat(this.securePort, notNullValue());

        assertThat(this.zNodeBasePath, notNullValue());

        logger.info(
            "EnvironmentConfigurationTestIT#test01_envInjection_Pass [" + this.bindAddress + ":" + this.port + "/" + this.securePort + ", nodeName=" + this.nodeName + ", zNodeBasePath=" + this.zNodeBasePath + "]");
    }
}
