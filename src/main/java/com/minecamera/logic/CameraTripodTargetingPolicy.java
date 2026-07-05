package com.minecamera.logic;

public final class CameraTripodTargetingPolicy {
	private CameraTripodTargetingPolicy() {
	}

	public static CameraTripodTargetingAction decide(boolean targetingTripod, boolean tripodHasCamera, boolean sneaking) {
		if (!targetingTripod) {
			return CameraTripodTargetingAction.PASS;
		}
		if (sneaking && !tripodHasCamera) {
			return CameraTripodTargetingAction.MOUNT_CAMERA;
		}
		if (!sneaking && tripodHasCamera) {
			return CameraTripodTargetingAction.OPEN_TRIPOD_UI;
		}
		return CameraTripodTargetingAction.BLOCK_HANDHELD_UI;
	}
}
