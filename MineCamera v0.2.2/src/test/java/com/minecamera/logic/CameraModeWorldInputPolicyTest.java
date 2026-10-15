package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class CameraModeWorldInputPolicyTest {
	@Test
	void inactiveModePreservesVanillaPressedStates() {
		CameraModeWorldInputPolicy.PressedStates states = CameraModeWorldInputPolicy.sanitizePressedStates(
			false,
			CameraPreviewMode.LIVE,
			true,
			false,
			true,
			false,
			true
		);

		assertEquals(new CameraModeWorldInputPolicy.PressedStates(true, false, true, false, true), states);
	}

	@Test
	void activeModeSuppressesAttackUseAndPickStates() {
		CameraModeWorldInputPolicy.PressedStates states = CameraModeWorldInputPolicy.sanitizePressedStates(
			true,
			CameraPreviewMode.LIVE,
			true,
			true,
			false,
			true,
			true
		);

		assertEquals(new CameraModeWorldInputPolicy.PressedStates(false, false, false, true, true), states);
	}

	@Test
	void reviewModeSuppressesLeftAndRightMovementKeys() {
		CameraModeWorldInputPolicy.PressedStates states = CameraModeWorldInputPolicy.sanitizePressedStates(
			true,
			CameraPreviewMode.REVIEW,
			false,
			false,
			false,
			true,
			true
		);

		assertEquals(new CameraModeWorldInputPolicy.PressedStates(false, false, false, false, false), states);
	}

	@Test
	void activeModeCancelsVanillaWorldActions() {
		assertTrue(CameraModeWorldInputPolicy.shouldCancelWorldAction(true));
		assertFalse(CameraModeWorldInputPolicy.shouldCancelWorldAction(false));
	}

	@Test
	void activeModeAlsoCancelsContinuousBlockBreaking() {
		assertTrue(CameraModeWorldInputPolicy.shouldCancelContinuousBlockBreaking(true));
		assertFalse(CameraModeWorldInputPolicy.shouldCancelContinuousBlockBreaking(false));
	}
}
