package de.bomc.poc.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.ActivationParameter;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.DefaultActivationStrategy;
import org.togglz.core.annotation.Label;
import org.togglz.core.annotation.Owner;
import org.togglz.core.context.FeatureContext;

import de.bomc.poc.togglz.qualifier.FeatureGroup_1;
import de.bomc.poc.togglz.qualifier.FeatureGroup_2;

/**
 * The feature enum that defines the toggle switches.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
public enum TogglzFeatures implements Feature {

	// 'EnabledByDefault', means is already activated during initialization.
	@FeatureGroup_1
	@EnabledByDefault
	@Label("switch feature 1")
	@Owner("bomc")
	FEATURE_1,

	@FeatureGroup_2
	@Label("When activated, delivers the snapshot version - switch feature 2")
	@Owner("bomc")
	FEATURE_2,

	@FeatureGroup_2
	@Label("switch feature 3, for unknown.")
	@Owner("unknown")
	@DefaultActivationStrategy(id = "gradual", parameters = { @ActivationParameter(name = "percentage", value = "5") })
	FEATURE_3,

	;

	/**
	 * In a normal case, a defined toggle switch is only inactive and thus
	 * automatically protects the code within the feature switch. The mechanism
	 * can avoid many undesirable actions.
	 * 
	 * @return the defined state.
	 */
	public boolean isActive() {
		return FeatureContext.getFeatureManager().isActive(this);
	}

}
