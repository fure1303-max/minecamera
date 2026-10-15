package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class TripodInteractionLogicTest {
	@Test
	void mountsCameraWhenSneakingWithCameraAndTripodIsEmpty() {
		assertEquals(
			TripodInteractionAction.MOUNT_CAMERA,
			TripodInteractionLogic.decide(false, true, true, false)
		);
	}

	@Test
	void dismountsCameraWhenSneakingWithEmptyHandAndTripodHasCamera() {
		assertEquals(
			TripodInteractionAction.DISMOUNT_CAMERA,
			TripodInteractionLogic.decide(true, true, false, true)
		);
	}

	@Test
	void opensCameraUiWhenTripodHasMountedCamera() {
		assertEquals(
			TripodInteractionAction.OPEN_CAMERA_UI,
			TripodInteractionLogic.decide(true, false, false, false)
		);
	}

	@Test
	void passesWhenInteractionDoesNotMatchAnyTripodAction() {
		assertEquals(
			TripodInteractionAction.PASS,
			TripodInteractionLogic.decide(false, false, false, true)
		);
	}
}
