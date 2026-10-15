package com.minecamera.client.camera;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class CameraInputRouterTest {
	@Test
	void pressEdgesTriggerActionsAndUpdateLatches() {
		CameraInputDecisions decisions = CameraInputRouter.evaluate(
			new CameraInputFrame(true, true, true, true, true, true),
			CameraInputLatches.reset()
		);

		assertTrue(decisions.captureTriggered());
		assertTrue(decisions.presetTriggered());
		assertTrue(decisions.reviewToggleTriggered());
		assertTrue(decisions.previousReviewTriggered());
		assertTrue(decisions.nextReviewTriggered());
		assertTrue(decisions.closeTriggered());
		assertTrue(decisions.nextLatches().captureMouseDown());
		assertTrue(decisions.nextLatches().closeMouseDown());
	}

	@Test
	void heldButtonsDoNotRetriggerOnSecondFrame() {
		CameraInputLatches previous = new CameraInputLatches(true, true, true, true, true, true);

		CameraInputDecisions decisions = CameraInputRouter.evaluate(
			new CameraInputFrame(true, true, true, true, true, true),
			previous
		);

		assertFalse(decisions.captureTriggered());
		assertFalse(decisions.presetTriggered());
		assertFalse(decisions.reviewToggleTriggered());
		assertFalse(decisions.previousReviewTriggered());
		assertFalse(decisions.nextReviewTriggered());
		assertFalse(decisions.closeTriggered());
	}
}
