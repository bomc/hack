package de.bomc.poc.zk.exception;

/**
 * A AppZookeeperException indicates failures in connection with zookeeper registrations and discovery.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
public class AppZookeeperException extends RuntimeException {

    /**
     * The serial UID.
     */
    private static final long serialVersionUID = -4671667940639753548L;

    /**
     * Create a new AppZookeeperException.
     */
    public AppZookeeperException() {
    }

    /**
     * Create a new AppZookeeperException.
     * @param message The message text
     */
    public AppZookeeperException(final String message) {
        super(message);
    }

    /**
     * Create a new AppZookeeperException.
     * @param cause The root cause of the exception
     */
    public AppZookeeperException(final Throwable cause) {
        super(cause);
    }

    /**
     * Create a new AppZookeeperException.
     * @param message The message text
     * @param cause   The root cause of the exception
     */
    public AppZookeeperException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
