package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class CameraTripodTargetingPolicyTest {
	@Test
	void sneakingOnEmptyTripodMountsTheCamera() {
		assertEquals(
			CameraTripodTargetingAction.MOUNT_CAMERA,
			CameraTripodTargetingPolicy.decide(true, false, true)
		);
	}

	@Test
	void nonSneakingOnEmptyTripodConsumesTheClickWithoutOpeningHandheldUi() {
		assertEquals(
			CameraTripodTargetingAction.BLOCK_HANDHELD_UI,
			CameraTripodTargetingPolicy.decide(true, false, false)
		);
	}

	@Test
	void nonSneakingOnLoadedTripodOpensTripodCameraUi() {
		assertEquals(
			CameraTripodTargetingAction.OPEN_TRIPOD_UI,
			CameraTripodTargetingPolicy.decide(true, true, false)
		);
	}

	@Test
	void nonTripodTargetsFallBackToNormalCameraUse() {
		assertEquals(
			CameraTripodTargetingAction.PASS,
			CameraTripodTargetingPolicy.decide(false, false, false)
		);
	}
}
