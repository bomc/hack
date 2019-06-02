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
package de.bomc.poc.config;

import de.bomc.poc.exception.app.AppInitializationException;
import de.bomc.poc.logger.qualifier.LoggerQualifier;

import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import java.lang.management.ManagementFactory;
import java.util.regex.Pattern;

/**
 * This is a singleton ejb, that reads at startup the environment parameter (ipaddress, port, node-name and secure parameter) from environment-system.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@Startup
@Singleton
public class EnvConfigSingletonEJB {

    /**
     * Logger.
     */
    @Inject
    @LoggerQualifier
    private Logger logger;
    // The environment configuration parameter.
    private String address;
    private int port;
    private int securePort;
    private String nodeName;
    private String webArchiveName;

    /**
     * Reads at startup the environment parameter from wildfly ecosystem.
     * @throws AppInitializationException
     */
    @PostConstruct
    public void init() throws AppInitializationException {
        this.logger.debug("EnvConfigSingletonEJB#init");

        try {
            final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

            final ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
            this.address = (String)mBeanServer.getAttribute(http, "boundAddress");
            this.port = (Integer)mBeanServer.getAttribute(http, "boundPort");

            final ObjectName ws = new ObjectName("jboss.ws", "service", "ServerConfig");
            // hostName = (String) mBeanServer.getAttribute(ws, "WebServiceHost");
            // hostPort = (int) mBeanServer.getAttribute(ws, "WebServicePort");
            this.securePort = (int)mBeanServer.getAttribute(ws, "WebServiceSecurePort");

            final ObjectName serverMBean = new ObjectName("jgroups:type=channel,cluster=\"ee\"");
            this.nodeName = (String)mBeanServer.getAttribute(serverMBean, "address");

            // Alternative for reading app name:
            // -at Resource(lookup="java:app/AppName")
            // private String appName;
            // 
            // Get archive name via jndi-context 'java:global/webArchiveName/...'.
            InitialContext ctx = new InitialContext();
            NamingEnumeration<Binding> namingEnumerationGlobal = ctx.listBindings("java:global");
            while ((namingEnumerationGlobal != null) && (namingEnumerationGlobal.hasMoreElements())) {
                final Binding binding = namingEnumerationGlobal.next();
                this.webArchiveName = binding.getName();
            }

            this.logger.info(
                "EnvConfigSingletonEJB#init [hostName=" + this.address + ", port=" + this.port + ", securePort=" + this.securePort + ", nodeName=" + this.nodeName + ", webArchiveName=" + this.webArchiveName + "]");
        } catch (Exception ex) {
            final String errorMsg = "Reading bind address and bind port from wildfly ecosystem failed!";

            this.logger.error("EnvConfigSingletonEJB#init - " + errorMsg);

            throw new AppInitializationException(ex);
        }
    }

    public String getBindAddress() {
        this.logger.debug("EnvConfigSingletonEJB#getBindAddress [address=" + this.address + "]");

        return this.address;
    }

    public int getBindPort() {
        this.logger.debug("EnvConfigSingletonEJB#getBindPort [port=" + this.port + "]");

        return this.port;
    }

    public int getBindSecurePort() {
        this.logger.debug("EnvConfigSingletonEJB#getBindSecurePort [securePort=" + this.securePort + "]");

        return this.securePort;
    }

    public String getNodeName() {
        this.logger.debug("EnvConfigSingletonEJB#getNodeName [nodeName=" + this.nodeName + "]");

        return this.nodeName;
    }

    public String getWebArchiveName() {
        this.logger.debug("EnvConfigSingletonEJB#getWebArchiveName [webArchiveName=" + this.webArchiveName + "]");

        return this.webArchiveName;
    }

    /**
     * @return the relative path in zookeeper to add the discovery uri.
     */
    public String getZnodeBasePath() {
        Object[] nodeParts = Pattern.compile("\\.")
                                    .splitAsStream(this.nodeName)
                                    .toArray();
        final StringBuilder baseRootZnode = new StringBuilder();
        baseRootZnode.append("/").append(this.webArchiveName).append("/").append(nodeParts[0]).append("/").append(nodeParts[1]);//.append("/");

        this.logger.debug("EnvConfigSingletonEJB#getZnodeBasePath [znodeBasePath=" + baseRootZnode.toString() + "]");

        return baseRootZnode.toString();
    }
}
