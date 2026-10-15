package com.minecamera.logic;

public record CameraOpticalState(
	int focalLengthMm,
	double apertureFStop,
	FocusMode focusMode,
	double focusDistance,
	float lastSentTripodYaw,
	float lastSentTripodPitch
) {
	public static CameraOpticalState defaults(double defaultFocusDistance) {
		CameraCaptureDefaults defaults = CameraCaptureDefaults.basicPhoto();
		return new CameraOpticalState(
			defaults.focalLengthMm(),
			defaults.apertureFStop(),
			defaults.focusMode(),
			defaultFocusDistance,
			Float.NaN,
			Float.NaN
		);
	}

	public CameraOpticalState withFocalLengthMm(int nextFocalLengthMm) {
		return new CameraOpticalState(
			nextFocalLengthMm,
			apertureFStop,
			focusMode,
			focusDistance,
			lastSentTripodYaw,
			lastSentTripodPitch
		);
	}

	public CameraOpticalState withLastSentTripodPose(float yaw, float pitch) {
		return new CameraOpticalState(
			focalLengthMm,
			apertureFStop,
			focusMode,
			focusDistance,
			yaw,
			pitch
		);
	}

	public CameraOpticalState clearTripodPoseCache() {
		return new CameraOpticalState(
			focalLengthMm,
			apertureFStop,
			focusMode,
			focusDistance,
			Float.NaN,
			Float.NaN
		);
	}

	public boolean matchesLastSentTripodPose(float yaw, float pitch) {
		return Float.compare(lastSentTripodYaw, yaw) == 0 && Float.compare(lastSentTripodPitch, pitch) == 0;
	}
}
