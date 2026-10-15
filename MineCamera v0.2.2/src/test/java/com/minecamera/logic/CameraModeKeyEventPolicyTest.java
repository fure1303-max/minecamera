package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class CameraModeKeyEventPolicyTest {
	@Test
	void inactiveCameraModeNeverConsumesKeys() {
		assertFalse(CameraModeKeyEventPolicy.shouldConsumeKeyEvent(
			false,
			CameraPreviewMode.LIVE,
			true,
			true,
			true,
			true
		));
	}

	@Test
	void liveModeConsumesPerspectiveToggleOnly() {
		assertTrue(CameraModeKeyEventPolicy.shouldConsumeKeyEvent(
			true,
			CameraPreviewMode.LIVE,
			true,
			true,
			false,
			false
		));
		assertFalse(CameraModeKeyEventPolicy.shouldConsumeKeyEvent(
			true,
			CameraPreviewMode.LIVE,
			true,
			false,
			true,
			false
		));
	}

	@Test
	void reviewModeConsumesLeftAndRightMovementKeys() {
		assertTrue(CameraModeKeyEventPolicy.shouldConsumeKeyEvent(
			true,
			CameraPreviewMode.REVIEW,
			true,
			false,
			true,
			false
		));
		assertTrue(CameraModeKeyEventPolicy.shouldConsumeKeyEvent(
			true,
			CameraPreviewMode.REVIEW,
			true,
			false,
			false,
			true
		));
	}

	@Test
	void releaseEventsAreNotConsumed() {
		assertFalse(CameraModeKeyEventPolicy.shouldConsumeKeyEvent(
			true,
			CameraPreviewMode.REVIEW,
			false,
			true,
			true,
			true
		));
	}
}
