package com.minecamera.client.camera;

record CameraInputFrame(
	boolean captureMouseDown,
	boolean presetKeyDown,
	boolean reviewToggleKeyDown,
	boolean previousReviewKeyDown,
	boolean nextReviewKeyDown,
	boolean closeMouseDown
) {
}
