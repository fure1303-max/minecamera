package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class CameraModeHudLayoutTest {
	@Test
	void centersSquareGuideAgainstWholeScreen() {
		CameraModeHudLayout layout = CameraModeHudLayout.create(1920, 1080, 2880, 2880);

		assertEquals(420, layout.frameX());
		assertEquals(0, layout.frameY());
		assertEquals(1080, layout.frameWidth());
		assertEquals(1080, layout.frameHeight());
	}

	@Test
	void centersUltraWideGuideVertically() {
		CameraModeHudLayout layout = CameraModeHudLayout.create(1920, 1080, 3840, 1646);

		assertEquals(0, layout.frameX());
		assertEquals(128, layout.frameY());
		assertEquals(1920, layout.frameWidth());
		assertEquals(823, layout.frameHeight());
	}
}
