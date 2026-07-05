package com.minecamera.logic;

public final class FocusMath {
	private FocusMath() {
	}

	public static double computeBlurFactor(double sceneDistance, double focusDistance, double apertureFStop, double focalLengthMm) {
		double safeFocusDistance = Math.max(0.001D, focusDistance);
		double safeAperture = Math.max(0.1D, apertureFStop);
		double safeFocalLength = Math.max(1.0D, focalLengthMm);

		double distanceDelta = Math.abs(sceneDistance - safeFocusDistance);
		double apertureFactor = 8.0D / safeAperture;
		double focalFactor = safeFocalLength / 50.0D;
		double normalizedDelta = distanceDelta / safeFocusDistance;

		return Math.min(1.0D, normalizedDelta * apertureFactor * focalFactor * 0.2D);
	}
}
