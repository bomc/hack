package de.bomc.poc.zk.curator.producer;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.conn.DnsResolver;

/**
 * Default dns resolver that uses
 * {@link java.net.InetAddress#getAllByName(String)} to resolve hosts to ip
 * addresses.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
public class SystemDefaultDnsResolver implements DnsResolver {
	public static final SystemDefaultDnsResolver INSTANCE = new SystemDefaultDnsResolver();

	public InetAddress[] resolve(String host) throws UnknownHostException {
		return InetAddress.getAllByName(host);
	}
}
