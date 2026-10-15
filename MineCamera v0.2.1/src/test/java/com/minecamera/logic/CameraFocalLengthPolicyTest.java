package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class CameraFocalLengthPolicyTest {
	@Test
	void clampsValuesToTenAndSixHundredMillimeters() {
		assertEquals(10, CameraFocalLengthPolicy.clamp(1));
		assertEquals(10, CameraFocalLengthPolicy.clamp(10));
		assertEquals(600, CameraFocalLengthPolicy.clamp(900));
		assertEquals(600, CameraFocalLengthPolicy.clamp(600));
	}

	@Test
	void stepsFiveMillimetersPerNotchByDefault() {
		assertEquals(55, CameraFocalLengthPolicy.step(50, 1, false));
		assertEquals(45, CameraFocalLengthPolicy.step(50, -1, false));
		assertEquals(65, CameraFocalLengthPolicy.step(50, 3, false));
	}

	@Test
	void stepsTwentyFiveMillimetersPerNotchWhenAccelerated() {
		assertEquals(75, CameraFocalLengthPolicy.step(50, 1, true));
		assertEquals(25, CameraFocalLengthPolicy.step(50, -1, true));
		assertEquals(125, CameraFocalLengthPolicy.step(50, 3, true));
	}

	@Test
	void clampsAfterStepAtBothEnds() {
		assertEquals(10, CameraFocalLengthPolicy.step(10, -1, false));
		assertEquals(10, CameraFocalLengthPolicy.step(15, -2, false));
		assertEquals(600, CameraFocalLengthPolicy.step(600, 1, false));
		assertEquals(600, CameraFocalLengthPolicy.step(590, 1, true));
	}
}
