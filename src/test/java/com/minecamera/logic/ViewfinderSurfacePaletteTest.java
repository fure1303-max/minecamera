package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class ViewfinderSurfacePaletteTest {
	@Test
	void usesOpaqueBackgroundForViewfinderSurface() {
		int color = ViewfinderSurfacePalette.panelBackgroundColor();

		assertEquals(0xFF, (color >>> 24) & 0xFF);
		assertEquals(0xB4D2FF, color & 0x00FFFFFF);
	}
}
