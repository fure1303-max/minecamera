package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class CameraModeBindingPolicyTest {
	@Test
	void handheldModeClosesWhenHeldItemStopsBeingCamera() {
		assertTrue(CameraModeBindingPolicy.shouldClose(false, false));
	}

	@Test
	void handheldModeStaysOpenWhileCameraIsStillHeld() {
		assertFalse(CameraModeBindingPolicy.shouldClose(false, true));
	}

	@Test
	void tripodModeDoesNotDependOnHeldCameraItem() {
		assertFalse(CameraModeBindingPolicy.shouldClose(true, false));
	}
}
