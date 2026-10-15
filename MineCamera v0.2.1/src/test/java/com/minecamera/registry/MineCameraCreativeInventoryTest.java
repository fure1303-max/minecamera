package com.minecamera.registry;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MineCameraCreativeInventoryTest {
	@Test
	void exposesExpectedToolsTabOrder() {
		assertEquals(
			List.of("camera", "film", "tripod", "photo_display_frame"),
			MineCameraCreativeInventory.toolsTabItemIds()
		);
	}
}
