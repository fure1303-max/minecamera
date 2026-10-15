package com.minecamera.registry;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MineCameraItemDefinitionResourcesTest {
	@Test
	void shipsItemDefinitionForEachCoreItem() {
		assertAll(itemIds().stream().map(itemId -> () -> {
			String json = resourceText("assets/minecamera/items/" + itemId + ".json");
			assertTrue(json.contains("\"type\": \"minecraft:model\""));
			assertTrue(json.contains("\"model\": \"minecamera:item/" + itemId + "\""));
		}));
	}

	private static List<String> itemIds() {
		return List.of("camera", "film", "tripod", "photo_display_frame");
	}

	private static String resourceText(String path) throws IOException {
		try (InputStream stream = MineCameraItemDefinitionResourcesTest.class.getClassLoader().getResourceAsStream(path)) {
			assertNotNull(stream, () -> "Missing resource: " + path);
			return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
