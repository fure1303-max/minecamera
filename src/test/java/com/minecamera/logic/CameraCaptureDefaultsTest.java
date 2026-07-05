package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class CameraCaptureDefaultsTest {
	@Test
	void basicPhotoDefaultsToTwentyMillimeterLens() {
		assertEquals(20, CameraCaptureDefaults.basicPhoto().focalLengthMm());
	}
}
