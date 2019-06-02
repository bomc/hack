package de.bomc.poc.togglz.qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.togglz.core.annotation.FeatureGroup;
import org.togglz.core.annotation.Label;

@FeatureGroup
@Label("FeatureForGroup_1")
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureGroup_1 {
	// no content
}
