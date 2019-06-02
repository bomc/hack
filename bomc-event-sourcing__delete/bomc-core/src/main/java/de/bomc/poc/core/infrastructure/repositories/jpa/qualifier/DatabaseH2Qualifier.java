package de.bomc.poc.core.infrastructure.repositories.jpa.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * A qualifier to select the H2 db.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 *
 */
@Documented
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE, ElementType.METHOD })
public @interface DatabaseH2Qualifier {
	//
}
