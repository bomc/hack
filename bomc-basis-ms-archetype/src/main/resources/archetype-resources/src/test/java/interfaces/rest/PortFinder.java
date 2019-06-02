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
package ${package}.interfaces.rest;

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
