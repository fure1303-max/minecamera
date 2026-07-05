package com.minecamera.logic;

public final class CameraFocalLengthPolicy {
	public static final int MIN_MM = 10;
	public static final int MAX_MM = 600;
	private static final int DEFAULT_STEP_MM = 5;
	private static final int ACCELERATED_STEP_MM = 25;

	private CameraFocalLengthPolicy() {
	}

	public static int clamp(int focalLengthMm) {
		return Math.max(MIN_MM, Math.min(MAX_MM, focalLengthMm));
	}

	public static int step(int currentMm, int wheelNotches, boolean accelerated) {
		int stepSize = accelerated ? ACCELERATED_STEP_MM : DEFAULT_STEP_MM;
		return clamp(currentMm + (wheelNotches * stepSize));
	}
}
