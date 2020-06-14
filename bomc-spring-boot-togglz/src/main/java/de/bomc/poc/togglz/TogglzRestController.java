package de.bomc.poc.togglz;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.util.NamedFeature;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * gradle bootRun
 * http://localhost:8080/togglz-console/index
 */
@RestController
@RequestMapping("/togglz")
public class TogglzRestController {

	private static final String LOG_PREFIX = TogglzRestController.class.getName() + "#";
	
	private final FeatureManager featureManager;
	private final ObjectMapper objectMapper;

	public TogglzRestController(final FeatureManager featureManager, final ObjectMapper objectMapper) {
		this.featureManager = featureManager;
		this.objectMapper = objectMapper;
	}

	/**
	 * curl -v GET http://localhost:8080/togglz
	 */
	@GetMapping
	public ResponseEntity<JsonNode> getTogglzSetting() {

		final ObjectNode togglzSetting = objectMapper.createObjectNode();

		if (featureManager.isActive(ToggleFeaturesEnum.MY_TOGGLZ_FEATURE_1)) {
			System.out.println(LOG_PREFIX + "getTogglzSetting MY_TOGGLZ_FEATURE_1 is active");
			
			togglzSetting.put("My Togglz Feature 1", "is enabled");
		}

		if (featureManager.isActive(ToggleFeaturesEnum.MY_TOGGLZ_FEATURE_2)) {
			System.out.println(LOG_PREFIX + "getTogglzSetting MY_TOGGLZ_FEATURE_1 is active");
			
			togglzSetting.put("My Togglz Feature 2", "is enabled");
		}

		return ResponseEntity.ok(togglzSetting);
	}

	/**
	 * curl -v GET http://localhost:8080/togglz/named-feature
	 */
	@GetMapping
	@RequestMapping("/named-feature")
	public ResponseEntity<ArrayNode> getNamedFeature() {

		if (featureManager.isActive(new NamedFeature("MY_NAMED_FEATURE"))) {
			ArrayNode array = objectMapper.createArrayNode();
			array.add("My Named Feature is activated");

			return ResponseEntity.ok(array);
		}

		return ResponseEntity.noContent().build();
	}
}