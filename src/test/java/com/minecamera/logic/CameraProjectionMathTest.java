package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class CameraProjectionMathTest {
	@Test
	void usesFullFrameWindowForThreeToTwoAspect() {
		CameraProjectionMath.SensorWindow sensor = CameraProjectionMath.sensorWindowFor(3000, 2000);

		assertEquals(36.0D, sensor.widthMm(), 0.0001D);
		assertEquals(24.0D, sensor.heightMm(), 0.0001D);
	}

	@Test
	void cropsWidthForSquareAspect() {
		CameraProjectionMath.SensorWindow sensor = CameraProjectionMath.sensorWindowFor(2880, 2880);

		assertEquals(24.0D, sensor.widthMm(), 0.0001D);
		assertEquals(24.0D, sensor.heightMm(), 0.0001D);
	}

	@Test
	void cropsHeightForWideAspect() {
		CameraProjectionMath.SensorWindow sensor = CameraProjectionMath.sensorWindowFor(3840, 2160);

		assertEquals(36.0D, sensor.widthMm(), 0.0001D);
		assertEquals(20.25D, sensor.heightMm(), 0.0001D);
	}

	@Test
	void verticalFovNarrowsAsFocalLengthGetsLonger() {
		float wide = CameraProjectionMath.verticalFovDegrees(15, 3000, 2000);
		float normal = CameraProjectionMath.verticalFovDegrees(50, 3000, 2000);
		float tele = CameraProjectionMath.verticalFovDegrees(200, 3000, 2000);

		assertTrue(wide > normal);
		assertTrue(normal > tele);
	}

	@Test
	void keepsSquareAndThreeToTwoVerticalFovAlignedAtSameSensorHeight() {
		float threeToTwo = CameraProjectionMath.verticalFovDegrees(50, 3000, 2000);
		float square = CameraProjectionMath.verticalFovDegrees(50, 2880, 2880);
		float wide = CameraProjectionMath.verticalFovDegrees(50, 3840, 2160);

		assertEquals(26.99D, threeToTwo, 0.05D);
		assertEquals(26.99D, square, 0.05D);
		assertEquals(22.90D, wide, 0.05D);
	}
}
