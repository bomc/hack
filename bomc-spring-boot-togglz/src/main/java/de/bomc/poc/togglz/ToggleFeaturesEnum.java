package de.bomc.poc.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;

public enum ToggleFeaturesEnum implements Feature {

	  @EnabledByDefault
	  @Label("My togglz feature 1, enabled by default.")
	  MY_TOGGLZ_FEATURE_1,

	  @ToggleFeatureGroup
	  @Label("My togglz feature 2, not enabled by default.")
	  MY_TOGGLZ_FEATURE_2,
	  
	  @ToggleFeatureGroup
	  @Label("My togglz feature 3, not enabled by default.")
	  MY_TOGGLZ_FEATURE_3
	}
