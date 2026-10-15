package com.minecamera.client.camera;

record CameraInputLatches(
	boolean captureMouseDown,
	boolean presetKeyDown,
	boolean closeMouseDown,
	boolean reviewToggleKeyDown,
	boolean previousReviewKeyDown,
	boolean nextReviewKeyDown
) {
	static CameraInputLatches reset() {
		return new CameraInputLatches(false, false, false, false, false, false);
	}

	CameraInputLatches withCaptureMouseDown(boolean value) {
		return new CameraInputLatches(value, presetKeyDown, closeMouseDown, reviewToggleKeyDown, previousReviewKeyDown, nextReviewKeyDown);
	}

	CameraInputLatches withPresetKeyDown(boolean value) {
		return new CameraInputLatches(captureMouseDown, value, closeMouseDown, reviewToggleKeyDown, previousReviewKeyDown, nextReviewKeyDown);
	}

	CameraInputLatches withCloseMouseDown(boolean value) {
		return new CameraInputLatches(captureMouseDown, presetKeyDown, value, reviewToggleKeyDown, previousReviewKeyDown, nextReviewKeyDown);
	}

	CameraInputLatches withReviewToggleKeyDown(boolean value) {
		return new CameraInputLatches(captureMouseDown, presetKeyDown, closeMouseDown, value, previousReviewKeyDown, nextReviewKeyDown);
	}

	CameraInputLatches withPreviousReviewKeyDown(boolean value) {
		return new CameraInputLatches(captureMouseDown, presetKeyDown, closeMouseDown, reviewToggleKeyDown, value, nextReviewKeyDown);
	}

	CameraInputLatches withNextReviewKeyDown(boolean value) {
		return new CameraInputLatches(captureMouseDown, presetKeyDown, closeMouseDown, reviewToggleKeyDown, previousReviewKeyDown, value);
	}
}
