/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: micha
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.config.swagger;

//import java.util.Arrays;
//import java.util.Collections;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.cors.CorsConfiguration;
//
//import springfox.documentation.spring.web.PropertySourcedRequestMappingHandlerMapping;

/**
 * TODO ...
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 28.11.2019
 */
//@Component
public class SwaggerPostProcessor /*implements BeanPostProcessor*/ {

//	private static final String SWAGGER_BEAN_NAME = "swagger2ContollerMapping";

//	/**
//	 * Apply this {@code BeanPostProcessor} to the given new bean instance
//	 * <i>before</i> any bean initialization callbacks (like InitializingBean's
//	 * {@code afterPropertiesSet} or a custom init-method). The bean will already be
//	 * populated with property values. The returned bean instance may be a wrapper
//	 * around the original.
//	 * <p>
//	 * The default implementation returns the given {@code bean} as-is.
//	 * 
//	 * @param bean     the new bean instance
//	 * @param beanName the name of the bean
//	 * @return the bean instance to use, either the original or a wrapped one; if
//	 *         {@code null}, no subsequent BeanPostProcessors will be invoked
//	 * @throws org.springframework.beans.BeansException in case of errors
//	 */
//	@Override
//	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
//		return bean;
//	}
//
//	/**
//	 * Apply this {@code BeanPostProcessor} to the given new bean instance
//	 * <i>after</i> any bean initialization callbacks (like InitializingBean's
//	 * {@code afterPropertiesSet} or a custom init-method). The bean will already be
//	 * populated with property values. The returned bean instance may be a wrapper
//	 * around the original.
//	 * <p>
//	 * In case of a FactoryBean, this callback will be invoked for both the
//	 * FactoryBean instance and the objects created by the FactoryBean (as of Spring
//	 * 2.0). The post-processor can decide whether to apply to either the
//	 * FactoryBean or created objects or both through corresponding
//	 * {@code bean instanceof FactoryBean} checks.
//	 * <p>
//	 * This callback will also be invoked after a short-circuiting triggered by a
//	 * {@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation}
//	 * method, in contrast to all other {@code BeanPostProcessor} callbacks.
//	 * <p>
//	 * The default implementation returns the given {@code bean} as-is.
//	 * 
//	 * @param bean     the new bean instance
//	 * @param beanName the name of the bean
//	 * @return the bean instance to use, either the original or a wrapped one; if
//	 *         {@code null}, no subsequent BeanPostProcessors will be invoked
//	 * @throws org.springframework.beans.BeansException in case of errors
//	 */
//	@Override
//	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
//
//		if (SWAGGER_BEAN_NAME.equals(beanName)) {
//			final Map<String, CorsConfiguration> corsConfigurations = new LinkedHashMap<>();
//			
//			final CorsConfiguration corsConfiguration = (new CorsConfiguration()).applyPermitDefaultValues();
//			corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
//			corsConfiguration.setAllowedMethods(
//			        Collections.unmodifiableList(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")));
//			
//			corsConfigurations.put("/**", corsConfiguration);
//			
//			PropertySourcedRequestMappingHandlerMapping.class.cast(bean).setCorsConfigurations(corsConfigurations);
//		}
//
//		return bean;
//	}
}
