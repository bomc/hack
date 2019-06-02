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
package de.bomc.poc.test.config.unit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Properties;
import java.util.StringJoiner;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Simple Test for concatenating properties to a comma separated string with
 * java 8. Is used for creating the corum list for the curator client.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PropertiesConcatenateTest {

	@Before
	public void before() {
		//
	}

	@Test
	public void test01_concatenate_pass() {
		System.out.println("PropertiesConcatenateTest#test01_concatenate_pass");
		
		final Properties properties = new Properties();
		properties.setProperty("zookeeper.instance.1", "127.0.0.1:8180");
		properties.setProperty("zookeeper.instance.2", "127.0.0.2:8280");
		properties.setProperty("zookeeper.instance.3", "127.0.0.3:8380");
		
		StringJoiner stringJoiner = new StringJoiner(",");
		properties.forEach((host, port) -> stringJoiner.add(port.toString()));
		
		assertThat(stringJoiner.toString(), is(equalTo("127.0.0.1:8180,127.0.0.2:8280,127.0.0.3:8380")));
	}
	
	@Test
	public void test02_concatenateFileFromClasspath() throws Exception {
		System.out.println("PropertiesConcatenateTest#test02_concatenateFileFromClasspath");
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		properties.load(classLoader.getResourceAsStream("zookeeper.properties"));
		    
		StringJoiner stringJoiner = new StringJoiner(",");
		properties.forEach((instance, address) -> stringJoiner.add(address.toString()));

		System.out.println(stringJoiner.toString());
	}
}
