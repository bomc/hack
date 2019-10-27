/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.logging;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

/**
 * Either the JDBC Driver or the DataSource must be proxied to intercept
 * statement executions and log them along with the actual parameter values.
 * Besides statement logging, a JDBC proxy can provide other cross-cutting
 * features like long-running query detection or custom statement execution
 * listeners.
 * 
 * <pre>
 * https://blog.arnoldgalovics.com/configuring-a-datasource-proxy-in-spring-boot/
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Component
public class DatasourceProxyBeanPostProcessor implements BeanPostProcessor {

	// Read from 'application-dev' and 'application-prod' properties file.
	@Value("${datasource.name}")
	private String datasourceName;

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {

		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {

		if (bean instanceof DataSource) {
			final ProxyFactory factory = new ProxyFactory(bean);
			factory.setProxyTargetClass(true);
			factory.addAdvice(new ProxyDataSourceInterceptor((DataSource) bean, datasourceName));

			return factory.getProxy();
		}

		return bean;
	}

	private static class ProxyDataSourceInterceptor implements MethodInterceptor {
		private final DataSource dataSource;

		public ProxyDataSourceInterceptor(final DataSource dataSource, final String datasourceName) {
			super();

			// Use pretty formatted query with multiline enabled.
			final PrettyQueryLogEntryCreator queryLogEntryCreator = new PrettyQueryLogEntryCreator();
			queryLogEntryCreator.setMultiline(true);
			
			// Create a listener for logging out with slf4j.
			final SLF4JQueryLoggingListener slf4JQueryLoggingListener = new SLF4JQueryLoggingListener();
			slf4JQueryLoggingListener.setQueryLogEntryCreator(queryLogEntryCreator);
			slf4JQueryLoggingListener.setLogLevel(SLF4JLogLevel.INFO);

			this.dataSource = ProxyDataSourceBuilder
					.create(dataSource) //
					.name(datasourceName) //
					.countQuery() //
					.multiline() //
					.listener(slf4JQueryLoggingListener) //
					.logSlowQueryToSysOut(500, TimeUnit.MILLISECONDS) //
					.asJson() //
					.build();
		}

		@Override
		public Object invoke(final MethodInvocation invocation) throws Throwable {
			final Method proxyMethod = ReflectionUtils.findMethod(dataSource.getClass(),
					invocation.getMethod().getName());

			if (proxyMethod != null) {
				return proxyMethod.invoke(dataSource, invocation.getArguments());
			}

			return invocation.proceed();
		}
	}
	
}