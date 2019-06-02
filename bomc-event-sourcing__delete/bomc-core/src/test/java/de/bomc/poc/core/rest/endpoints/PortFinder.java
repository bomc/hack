package de.bomc.poc.core.rest.endpoints;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * A helper class for finding ports.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
public class PortFinder {

	public static int findPort() throws IOException {
		final ServerSocket server = new ServerSocket(0);
		final int port = server.getLocalPort();
		server.close();

		return port;
	}
}
