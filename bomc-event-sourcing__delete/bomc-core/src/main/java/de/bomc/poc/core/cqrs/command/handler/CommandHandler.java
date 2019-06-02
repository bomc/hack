package de.bomc.poc.core.cqrs.command.handler;

/**
 * 
 *
 * @param <C>
 *            command
 * @param <R>
 *            result type - for asynchronous {@link Command}commands
 *            (asynchronous=true) should be {@link Void}
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 16.08.2018
 */
public interface CommandHandler<C, R> {

	public R handle(C command);
}
