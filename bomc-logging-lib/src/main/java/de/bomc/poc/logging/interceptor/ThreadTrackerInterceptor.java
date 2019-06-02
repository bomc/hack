package de.bomc.poc.logging.interceptor;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import de.bomc.poc.logging.qualifier.ThreadTrackerQualifier;

/**
 * A generic interceptor saves the origin Thread-Name first, then changes it
 * before the actual method is invoked. Finally, the thread will be renamed to
 * his origin name again. With this simple solution, you are possible to take a
 * "snapshot" of the server's current load and see the methods currently
 * executed. It is much easier to find a deadlock... The interesting beans (or
 * e.g. all facades), can be easily "instrumented" using the &#064;Interceptors
 * annotation.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @since 19.07.2016
 */
@Interceptor
@ThreadTrackerQualifier
@Priority(Interceptor.Priority.APPLICATION + 900)
public class ThreadTrackerInterceptor {

	@AroundInvoke
	public Object annotateThread(final InvocationContext invCtx) throws Exception {
		
		final String originName = Thread.currentThread().getName();
		final String beanName = invCtx.getTarget().getClass().getSimpleName();// .getName();
		final String tracingName = beanName + "#" + invCtx.getMethod().getName() + " " + originName;

		try {
			Thread.currentThread().setName(tracingName);
			return invCtx.proceed();
		} finally {
			Thread.currentThread().setName(originName);
		}
	}
}
