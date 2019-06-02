/**
 * <pre>
 *
 * Last change:
 *
 *  by: $Author$
 *
 *  date: $Date$
 *
 *  revision: $Revision$
 *
 *    © Bomc 2018
 *
 * </pre>
 */
package de.bomc.poc.hystrix.generic;

import com.netflix.hystrix.HystrixExecutable;
import com.netflix.hystrix.HystrixInvokable;
import com.netflix.hystrix.HystrixObservable;
import org.apache.log4j.Logger;

/**
 * <p>
 * Invokes necessary method of {@link HystrixExecutable} or
 * {@link HystrixObservable} for specified execution type:
 * </p>
 * {@link ExecutionTypeEnum#SYNCHRONOUS} to {@link HystrixExecutable#execute()}
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public class CommandExecutor {

    private static final String LOG_PREFIX = "CommandExecutor#";
    private static final Logger LOGGER = Logger.getLogger(CommandExecutor.class);

    /**
     * Calls a method of {@link HystrixExecutable} in accordance with specified
     * execution type.
     * @param invokable         {@link HystrixInvokable}
     * @param executionTypeEnum {@link ExecutionTypeEnum}
     * @param metaHolder        {@link MetaHolder}
     * @return the result of invocation of specific method.
     */
    public static Object execute(final HystrixInvokable<?> invokable, final ExecutionTypeEnum executionTypeEnum, final MetaHolder metaHolder) {
        LOGGER.debug(LOG_PREFIX + "execute [invokable, executionType.name=" + executionTypeEnum.name() + ", metaHolder.commandGroupKey=" + metaHolder.getCommandGroupKey() + "]");

        validate(invokable, LOG_PREFIX + "execute - the given instance 'invokable' is null");
        validate(metaHolder, LOG_PREFIX + "execute - the given instance 'metaHolder' is null");

        switch (executionTypeEnum) {
            case SYNCHRONOUS:
                return castToExecutable(invokable, executionTypeEnum).execute();
            default:
                throw new RuntimeException("unsupported execution type: " + executionTypeEnum);
        }
    }

    private static <T> void validate(final T object, final String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }

    private static HystrixExecutable<?> castToExecutable(final HystrixInvokable<?> invokable, final ExecutionTypeEnum executionTypeEnum) {
        LOGGER.debug(LOG_PREFIX + "castToExecutable [invokable, executionType=" + executionTypeEnum.getClass()
                                                                                                   .getName() + "]");

        if (invokable instanceof HystrixExecutable) {
            return (HystrixExecutable<?>)invokable;
        }

        throw new RuntimeException(LOG_PREFIX + "castToExecutable - Command should implement " + HystrixExecutable.class.getCanonicalName() + " interface to execute in: " + executionTypeEnum + " mode");
    }
}
