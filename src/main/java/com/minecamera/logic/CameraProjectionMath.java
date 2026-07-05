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

	public record SensorWindow(double widthMm, double heightMm) {
	}
}
