package de.bomc.poc.zk.concurrent.producer;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import de.bomc.poc.zk.concurrent.qualifier.ManagedThreadFactoryQualifier;

/**
 * A producer for managed executer services.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 22.07.2016
 */
@Dependent
public class ManagedConcurrencyProducer {

    @Produces
    @ManagedThreadFactoryQualifier
    @Resource(lookup = "java:jboss/ee/concurrency/factory/default")
    private ManagedThreadFactory defaultManagedThreadFactory;
}
