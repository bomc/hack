/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.controller.interfaces.rest;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * A helper class for finding ports.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public final class PortFinder {

    private PortFinder() {
        // Prevents instantiation.
    }

    public static int findPort() throws IOException {
        final ServerSocket server = new ServerSocket(0);
        final int port = server.getLocalPort();
        server.close();

        return port;
    }
}
