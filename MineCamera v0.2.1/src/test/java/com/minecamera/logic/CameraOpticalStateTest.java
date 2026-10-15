package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class CameraOpticalStateTest {
	@Test
	void defaultsMirrorBasicPhotoCaptureDefaults() {
		CameraCaptureDefaults defaults = CameraCaptureDefaults.basicPhoto();

		CameraOpticalState state = CameraOpticalState.defaults(8.0D);

		assertEquals(defaults.focalLengthMm(), state.focalLengthMm());
		assertEquals(defaults.apertureFStop(), state.apertureFStop());
		assertEquals(defaults.focusMode(), state.focusMode());
		assertEquals(8.0D, state.focusDistance());
		assertTrue(Float.isNaN(state.lastSentTripodYaw()));
		assertTrue(Float.isNaN(state.lastSentTripodPitch()));
	}

	@Test
	void cachesAndClearsLastSentTripodPose() {
		CameraOpticalState state = CameraOpticalState.defaults(8.0D)
			.withLastSentTripodPose(32.0F, -12.0F);

		assertTrue(state.matchesLastSentTripodPose(32.0F, -12.0F));
		assertFalse(state.matchesLastSentTripodPose(31.0F, -12.0F));

		CameraOpticalState cleared = state.clearTripodPoseCache();
		assertFalse(cleared.matchesLastSentTripodPose(32.0F, -12.0F));
		assertTrue(Float.isNaN(cleared.lastSentTripodYaw()));
		assertTrue(Float.isNaN(cleared.lastSentTripodPitch()));
	}
}
