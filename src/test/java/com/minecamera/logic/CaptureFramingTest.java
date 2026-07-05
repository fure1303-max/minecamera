package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class CaptureFramingTest {
	@Test
	void cropsCenterSquareFromWideSource() {
		CaptureFraming.CropRect crop = CaptureFraming.cropFor(1920, 1080, 2880, 2880);

		assertEquals(420, crop.x());
		assertEquals(0, crop.y());
		assertEquals(1080, crop.width());
		assertEquals(1080, crop.height());
	}

	@Test
	void cropsCenterPortraitFromWideSource() {
		CaptureFraming.CropRect crop = CaptureFraming.cropFor(1920, 1080, 1080, 1920);

		assertEquals(656, crop.x());
		assertEquals(0, crop.y());
		assertEquals(608, crop.width());
		assertEquals(1080, crop.height());
	}

	@Test
	void fitsViewfinderInsidePreviewBox() {
		CaptureFraming.FrameRect rect = CaptureFraming.viewfinderFor(220, 120, 1080, 1920);

		assertEquals(76, rect.x());
		assertEquals(0, rect.y());
		assertEquals(68, rect.width());
		assertEquals(120, rect.height());
	}
}
