package de.bomc.poc.togglz;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.togglz.core.Feature;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.metadata.enums.EnumFeatureMetaData;
import org.togglz.core.spi.FeatureProvider;

/**
 * A provider for multiple enum features.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
public class MultipleEnumFeaturesProvider implements FeatureProvider {

	private final Set<Feature> features = new LinkedHashSet<>();
	
	@SuppressWarnings("unchecked")
	public MultipleEnumFeaturesProvider(Class<? extends Feature>... enumTypes) {
		for (Class<? extends Feature> clazz : enumTypes) {
			for (Feature feature : clazz.getEnumConstants()) {
				features.add(feature);
			} // end for
		} // end for
	}

	@Override
	public Set<Feature> getFeatures() {
		return Collections.unmodifiableSet(features);
	}

	@Override
	public FeatureMetaData getMetaData(Feature feature) {
		return new EnumFeatureMetaData(getFeatureByName(feature.name()));
	}

	private Feature getFeatureByName(String name) {
		for (Feature feature : getFeatures()) {
			if (feature.name().equals(name)) {
				return feature;
			}
		}

		throw new IllegalArgumentException("Unknown feature: " + name);
	}
}
