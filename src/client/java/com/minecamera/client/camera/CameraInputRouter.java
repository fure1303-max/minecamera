package com.minecamera.client.camera;

import com.minecamera.logic.CameraModeInputLatchPolicy;

final class CameraInputRouter {
	private CameraInputRouter() {
	}

	static CameraInputDecisions evaluate(CameraInputFrame frame, CameraInputLatches previousLatches) {
		boolean captureTriggered = pressedEdge(previousLatches.captureMouseDown(), frame.captureMouseDown());
		boolean presetTriggered = pressedEdge(previousLatches.presetKeyDown(), frame.presetKeyDown());
		boolean reviewToggleTriggered = pressedEdge(previousLatches.reviewToggleKeyDown(), frame.reviewToggleKeyDown());
		boolean previousReviewTriggered = pressedEdge(previousLatches.previousReviewKeyDown(), frame.previousReviewKeyDown());
		boolean nextReviewTriggered = pressedEdge(previousLatches.nextReviewKeyDown(), frame.nextReviewKeyDown());
		boolean closeTriggered = CameraModeInputLatchPolicy.shouldTriggerClose(previousLatches.closeMouseDown(), frame.closeMouseDown());

		CameraInputLatches nextLatches = previousLatches
			.withCaptureMouseDown(frame.captureMouseDown())
			.withPresetKeyDown(frame.presetKeyDown())
			.withReviewToggleKeyDown(frame.reviewToggleKeyDown())
			.withPreviousReviewKeyDown(frame.previousReviewKeyDown())
			.withNextReviewKeyDown(frame.nextReviewKeyDown())
			.withCloseMouseDown(frame.closeMouseDown());

		return new CameraInputDecisions(
			captureTriggered,
			presetTriggered,
			reviewToggleTriggered,
			previousReviewTriggered,
			nextReviewTriggered,
			closeTriggered,
			nextLatches
		);
	}

	private static boolean pressedEdge(boolean wasDown, boolean isDown) {
		return isDown && !wasDown;
	}
}
