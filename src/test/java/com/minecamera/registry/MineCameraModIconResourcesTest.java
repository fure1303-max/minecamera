package com.minecamera.registry;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MineCameraModIconResourcesTest {
	@Test
	void declaresModIconAndShipsImage() throws IOException {
		String modJson = resourceText("fabric.mod.json");
		assertAll(
			() -> assertTrue(modJson.contains("\"icon\": \"assets/minecamera/icon.png\"")),
			() -> assertNotNull(
				MineCameraModIconResourcesTest.class.getClassLoader().getResource("assets/minecamera/icon.png"),
				"Missing mod icon resource"
			)
		);
	}

	private static String resourceText(String path) throws IOException {
		try (InputStream stream = MineCameraModIconResourcesTest.class.getClassLoader().getResourceAsStream(path)) {
			assertNotNull(stream, () -> "Missing resource: " + path);
			return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
