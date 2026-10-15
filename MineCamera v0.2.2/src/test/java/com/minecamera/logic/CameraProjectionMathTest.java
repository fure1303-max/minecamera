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

	@Test
	void keepsCaptureFovWhenViewportIsAtLeastAsWideAsTarget() {
		float captureFov = CameraProjectionMath.verticalFovDegrees(50, 2880, 2880);
		float sixteenByNineFov = CameraProjectionMath.verticalFovDegrees(50, 3840, 2160);

		assertEquals(
			sixteenByNineFov,
			CameraProjectionMath.framedViewportVerticalFovDegrees(sixteenByNineFov, 1920, 1080, 3840, 2160),
			0.0001D
		);
		assertEquals(
			captureFov,
			CameraProjectionMath.framedViewportVerticalFovDegrees(captureFov, 1920, 1080, 2880, 2880),
			0.0001D
		);
		assertEquals(
			captureFov,
			CameraProjectionMath.framedViewportVerticalFovDegrees(captureFov, 1920, 1080, 1080, 1920),
			0.0001D
		);
	}

	@Test
	void expandsViewportFovForTargetsWiderThanTheScreen() {
		int captureWidth = 3840;
		int captureHeight = 1646;
		float captureFov = CameraProjectionMath.verticalFovDegrees(50, captureWidth, captureHeight);
		float viewportFov = CameraProjectionMath.framedViewportVerticalFovDegrees(
			captureFov,
			1920,
			1080,
			captureWidth,
			captureHeight
		);

		double captureHorizontalScale = Math.tan(Math.toRadians(captureFov) / 2.0D) * captureWidth / captureHeight;
		double viewportHorizontalScale = Math.tan(Math.toRadians(viewportFov) / 2.0D) * 1920.0D / 1080.0D;
		assertTrue(viewportFov > captureFov);
		assertEquals(captureHorizontalScale, viewportHorizontalScale, 0.0001D);
	}

	@Test
	void rejectsInvalidFramedViewportDimensions() {
		org.junit.jupiter.api.Assertions.assertThrows(
			IllegalArgumentException.class,
			() -> CameraProjectionMath.framedViewportVerticalFovDegrees(45.0F, 0, 1080, 1920, 1080)
		);
		org.junit.jupiter.api.Assertions.assertThrows(
			IllegalArgumentException.class,
			() -> CameraProjectionMath.framedViewportVerticalFovDegrees(45.0F, 1920, 1080, 1920, 0)
		);
	}
}
