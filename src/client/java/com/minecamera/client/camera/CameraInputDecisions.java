package com.minecamera.client.camera;

record CameraInputDecisions(
	boolean captureTriggered,
	boolean presetTriggered,
	boolean reviewToggleTriggered,
	boolean previousReviewTriggered,
	boolean nextReviewTriggered,
	boolean closeTriggered,
	CameraInputLatches nextLatches
) {
}
