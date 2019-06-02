/**
 * Project: MY_POC_MICROSERVICE
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
package de.bomc.poc.concurrency.producer;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import de.bomc.poc.concurrency.qualifier.ManagedScheduledExecutorServiceQualifier;
import de.bomc.poc.concurrency.qualifier.ManagedThreadFactoryQualifier;

/**
 * A producer for managed executer services.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Dependent
public class ManagedConcurrencyProducer {

	@Produces
	@ManagedScheduledExecutorServiceQualifier
	@Resource(lookup = "java:jboss/ee/concurrency/scheduler/default")
	private ManagedScheduledExecutorService scheduler;
	
	@Produces
	@ManagedThreadFactoryQualifier
	@Resource(lookup = "java:jboss/ee/concurrency/factory/default")
	private ManagedThreadFactory defaultManagedThreadFactory;
}
