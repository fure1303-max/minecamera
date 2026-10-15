package com.minecamera.registry;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TripodMountedVisualResourcesTest {
	@Test
	void shipsDistinctMountedTripodBlockstateAndModel() throws IOException {
		String blockstate = resourceText("assets/minecamera/blockstates/tripod.json");
		String mountedModel = resourceText("assets/minecamera/models/block/tripod_loaded.json");

		assertAll(
			() -> assertTrue(blockstate.contains("\"enabled=false\"")),
			() -> assertTrue(blockstate.contains("\"model\": \"minecamera:block/tripod\"")),
			() -> assertTrue(blockstate.contains("\"enabled=true\"")),
			() -> assertTrue(blockstate.contains("\"model\": \"minecamera:block/tripod_loaded\"")),
			() -> assertTrue(mountedModel.contains("\"up\": \"minecamera:block/tripod_top_loaded\""))
		);
	}

	private static String resourceText(String path) throws IOException {
		try (InputStream stream = TripodMountedVisualResourcesTest.class.getClassLoader().getResourceAsStream(path)) {
			assertNotNull(stream, () -> "Missing resource: " + path);
			return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
