package com.minecamera.client.camera;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class CameraInputLatchesTest {
	@Test
	void resetStartsWithAllLatchesReleased() {
		CameraInputLatches latches = CameraInputLatches.reset();

		assertFalse(latches.captureMouseDown());
		assertFalse(latches.presetKeyDown());
		assertFalse(latches.closeMouseDown());
		assertFalse(latches.reviewToggleKeyDown());
		assertFalse(latches.previousReviewKeyDown());
		assertFalse(latches.nextReviewKeyDown());
	}

	@Test
	void withersOnlyChangeRequestedLatch() {
		CameraInputLatches latches = CameraInputLatches.reset()
			.withCaptureMouseDown(true)
			.withCloseMouseDown(true);

		assertTrue(latches.captureMouseDown());
		assertFalse(latches.presetKeyDown());
		assertTrue(latches.closeMouseDown());
		assertFalse(latches.reviewToggleKeyDown());
	}
}
