package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class CameraModeScrollEventPolicyTest {
	@Test
	void inactiveModePassesScrollThrough() {
		assertEquals(
			CameraModeScrollEventPolicy.Action.PASS_THROUGH,
			CameraModeScrollEventPolicy.decide(false, CameraPreviewMode.LIVE)
		);
	}

	@Test
	void liveModeConsumesScrollAndAdjustsFocalLength() {
		assertEquals(
			CameraModeScrollEventPolicy.Action.ADJUST_FOCAL_LENGTH,
			CameraModeScrollEventPolicy.decide(true, CameraPreviewMode.LIVE)
		);
	}

	@Test
	void reviewModeConsumesScrollWithoutAdjustingFocalLength() {
		assertEquals(
			CameraModeScrollEventPolicy.Action.CONSUME_ONLY,
			CameraModeScrollEventPolicy.decide(true, CameraPreviewMode.REVIEW)
		);
	}
}
