package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class TripodPoseUpdatePolicyTest {
	@Test
	void acceptsMountedTripodWithinRange() {
		assertTrue(TripodPoseUpdatePolicy.canApply(true, true, 36.0D));
	}

	@Test
	void rejectsNonTripodTargets() {
		assertFalse(TripodPoseUpdatePolicy.canApply(false, true, 4.0D));
	}

	@Test
	void rejectsTripodsWithoutMountedCamera() {
		assertFalse(TripodPoseUpdatePolicy.canApply(true, false, 4.0D));
	}

	@Test
	void rejectsPlayersTooFarAway() {
		assertFalse(TripodPoseUpdatePolicy.canApply(true, true, 65.0D));
	}
}
