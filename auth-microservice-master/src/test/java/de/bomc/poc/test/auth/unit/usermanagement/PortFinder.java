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
 * Copyright (c): BOMC, 2015
 */
package de.bomc.poc.test.auth.unit.usermanagement;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * A helper class for finding ports.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class PortFinder {

    public static int findPort() throws IOException {
        final ServerSocket server = new ServerSocket(0);
        final int port = server.getLocalPort();
        server.close();

        return port;
    }
}
	
