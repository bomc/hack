package de.bomc.poc.zk.services.lock.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import de.bomc.poc.zk.services.lock.qualifier.LockQualifier;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <pre>
 * The interceptors are directly bound to the bean and its lifecycle, a lock can be defined in the interceptor for the bean.
 * This lock allows both readers and writers to reacquire read or write locks in the style of a {@link ReentrantLock}.
 * Non-reentrant readers are not allowed until all write locks held by the writing thread have been released.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
@Interceptor
@LockQualifier
public class LockInterceptor {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    @AroundInvoke
    public Object concurrencyControl(final InvocationContext ctx) throws Exception {
        LockQualifier
            lockAnnotation =
            ctx.getMethod()
               .getAnnotation(LockQualifier.class);

        if (lockAnnotation == null) {
            lockAnnotation =
                ctx.getTarget()
                   .getClass()
                   .getAnnotation(LockQualifier.class);
        }

        Object returnValue = null;
        switch (lockAnnotation.value()) {
            case WRITE:
                final ReentrantReadWriteLock.WriteLock writeLock = this.lock.writeLock();
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
                final ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
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
