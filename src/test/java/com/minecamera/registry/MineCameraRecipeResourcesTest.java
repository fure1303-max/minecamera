package com.minecamera.registry;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MineCameraRecipeResourcesTest {
	@Test
	void shipsCameraRecipe() throws IOException {
		String json = resourceText("data/minecamera/recipes/camera.json");
		assertAll(
			() -> assertTrue(json.contains("\"minecraft:diamond\"")),
			() -> assertTrue(json.contains("\"minecraft:iron_ingot\"")),
			() -> assertTrue(json.contains("\"minecraft:glass_pane\"")),
			() -> assertTrue(json.contains("\"id\": \"minecamera:camera\""))
		);
	}

	@Test
	void shipsTripodRecipe() throws IOException {
		String json = resourceText("data/minecamera/recipes/tripod.json");
		assertAll(
			() -> assertTrue(json.contains("\"minecraft:stick\"")),
			() -> assertTrue(json.contains("\"minecraft:iron_ingot\"")),
			() -> assertTrue(json.contains("\"id\": \"minecamera:tripod\""))
		);
	}

	@Test
	void shipsFilmRecipe() throws IOException {
		String json = resourceText("data/minecamera/recipes/film.json");
		assertAll(
			() -> assertTrue(json.contains("\"minecraft:paper\"")),
			() -> assertTrue(json.contains("\"minecraft:black_dye\"")),
			() -> assertTrue(json.contains("\"count\": 2")),
			() -> assertTrue(json.contains("\"id\": \"minecamera:film\""))
		);
	}

	@Test
	void shipsPhotoDisplayFrameRecipe() throws IOException {
		String json = resourceText("data/minecamera/recipes/photo_display_frame.json");
		assertAll(
			() -> assertTrue(json.contains("\"minecraft:stick\"")),
			() -> assertTrue(json.contains("\"minecraft:paper\"")),
			() -> assertTrue(json.contains("\"id\": \"minecamera:photo_display_frame\""))
		);
	}

	private static String resourceText(String path) throws IOException {
		try (InputStream stream = MineCameraRecipeResourcesTest.class.getClassLoader().getResourceAsStream(path)) {
			assertNotNull(stream, () -> "Missing resource: " + path);
			return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
