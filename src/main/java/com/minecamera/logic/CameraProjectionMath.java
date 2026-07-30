package com.minecamera.logic;

public final class CameraProjectionMath {
	private static final double FULL_FRAME_WIDTH_MM = 36.0D;
	private static final double FULL_FRAME_HEIGHT_MM = 24.0D;
	private static final double FULL_FRAME_ASPECT = FULL_FRAME_WIDTH_MM / FULL_FRAME_HEIGHT_MM;

	private CameraProjectionMath() {
	}

	public static SensorWindow sensorWindowFor(int captureWidth, int captureHeight) {
		if (captureWidth <= 0 || captureHeight <= 0) {
			throw new IllegalArgumentException("Capture dimensions must be positive");
		}

		double aspect = captureWidth / (double) captureHeight;
		if (aspect >= FULL_FRAME_ASPECT) {
			return new SensorWindow(FULL_FRAME_WIDTH_MM, FULL_FRAME_WIDTH_MM / aspect);
		}
		return new SensorWindow(FULL_FRAME_HEIGHT_MM * aspect, FULL_FRAME_HEIGHT_MM);
	}

	public static float verticalFovDegrees(int focalLengthMm, int captureWidth, int captureHeight) {
		double safeFocalLength = Math.max(1.0D, focalLengthMm);
		SensorWindow sensor = sensorWindowFor(captureWidth, captureHeight);
		double radians = 2.0D * Math.atan(sensor.heightMm() / (2.0D * safeFocalLength));
		return (float) Math.toDegrees(radians);
	}

	public static float framedViewportVerticalFovDegrees(
		float captureVerticalFovDegrees,
		int viewportWidth,
		int viewportHeight,
		int captureWidth,
		int captureHeight
	) {
		if (viewportWidth <= 0 || viewportHeight <= 0) {
			throw new IllegalArgumentException("Viewport dimensions must be positive");
		}
		if (captureWidth <= 0 || captureHeight <= 0) {
			throw new IllegalArgumentException("Capture dimensions must be positive");
		}
		if (captureVerticalFovDegrees <= 0.0F || captureVerticalFovDegrees >= 180.0F) {
			throw new IllegalArgumentException("Capture vertical FOV must be between 0 and 180 degrees");
		}

		double viewportAspect = viewportWidth / (double) viewportHeight;
		double captureAspect = captureWidth / (double) captureHeight;
		if (viewportAspect >= captureAspect) {
			return captureVerticalFovDegrees;
		}

		double captureHalfAngle = Math.toRadians(captureVerticalFovDegrees) / 2.0D;
		double viewportHalfAngle = Math.atan(Math.tan(captureHalfAngle) * captureAspect / viewportAspect);
		return (float) Math.toDegrees(viewportHalfAngle * 2.0D);
	}

	public record SensorWindow(double widthMm, double heightMm) {
	}
}
