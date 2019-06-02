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
package de.bomc.poc.zookeeper.discovery.interceptor;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import de.bomc.poc.zookeeper.discovery.qualifier.LockQualifier;

/**
 * The interceptors are directly bound to the bean and its lifecycle, a lock can
 * be defined in the interceptor for the bean.
 * <p>
 * This lock allows both readers and writers to reacquire read or write locks in
 * the style of a {@link ReentrantLock}. Non-reentrant readers are not allowed
 * until all write locks held by the writing thread have been released.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
@LockQualifier
@Interceptor
public class LockInterceptor {

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

	@AroundInvoke
	public Object concurrencyControl(InvocationContext ctx) throws Exception {
		LockQualifier lockAnnotation = ctx.getMethod().getAnnotation(LockQualifier.class);

		if (lockAnnotation == null) {
			lockAnnotation = ctx.getTarget().getClass().getAnnotation(LockQualifier.class);
		}

		Object returnValue = null;
		switch (lockAnnotation.value()) {
		case WRITE:
			ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
			try {
				writeLock.lock();
				returnValue = ctx.proceed();
			} finally {
				//
				// Important to unlock.
				writeLock.unlock();
			}
			break;
		case READ:
			ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
			try {
				readLock.lock();
				returnValue = ctx.proceed();
			} finally {
				//
				// Important to unlock.
				readLock.unlock();
			}
			break;
		}
		return returnValue;
	}
}
