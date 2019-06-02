/**
 * Project: MY_POC
 * <p/>
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
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.zookeeper.unit;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.bomc.poc.zookeeper.InstanceMetaData;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests building of {@link InstanceMetaData}.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InstanceMetaDataTest {

	@Test
	public void test01_createInstanceMetaData_Pass() {
		System.out.println("InstanceMetaDataTest#test01_createInstanceMetaData");

		final String hostAddress = "192.168.4.1";
		final int port = 8180;
		final String serviceName = "test";
		final String contextRoot = "contextRoot";
		final String applicationPath = "applicationPath";
		final String description = "description";

		final InstanceMetaData instanceMetaData = InstanceMetaData.hostAdress(hostAddress).port(port)
				.serviceName(serviceName).contextRoot(contextRoot).applicationPath(applicationPath)
				.description(description).build();

		assertThat(instanceMetaData, notNullValue());
		assertThat(hostAddress, is(equalTo(instanceMetaData.getHostAdress())));
		assertThat(port, is(equalTo(instanceMetaData.getPort())));
		assertThat(serviceName, is(equalTo(instanceMetaData.getServiceName())));
		assertThat(contextRoot, is(equalTo(instanceMetaData.getContextRoot())));
		assertThat(applicationPath, is(equalTo(instanceMetaData.getApplicationPath())));
		assertThat(description, is(equalTo(instanceMetaData.getDescription())));

		InstanceMetaData instanceMetaData1 = InstanceMetaData.hostAdress(hostAddress).port(port)
				.serviceName(serviceName).contextRoot(contextRoot).applicationPath(applicationPath).description(null)
				.build();

		assertThat(instanceMetaData1, notNullValue());
		assertThat(hostAddress, is(equalTo(instanceMetaData1.getHostAdress())));
		assertThat(port, is(equalTo(instanceMetaData1.getPort())));
		assertThat(serviceName, is(equalTo(instanceMetaData1.getServiceName())));
		assertThat(contextRoot, is(equalTo(instanceMetaData1.getContextRoot())));
		assertThat(applicationPath, is(equalTo(instanceMetaData1.getApplicationPath())));
		assertThat(instanceMetaData1.getDescription(), nullValue());

		assertThat(instanceMetaData.equals(instanceMetaData1), equalTo(true));

		InstanceMetaData instanceMetaData2 = InstanceMetaData.hostAdress("hostAddress1").port(port)
				.serviceName(serviceName).contextRoot(contextRoot).applicationPath(applicationPath).description(null)
				.build();

		assertThat(instanceMetaData.equals(instanceMetaData2), equalTo(false));
	}
}
