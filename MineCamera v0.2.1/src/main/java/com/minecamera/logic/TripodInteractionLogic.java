package com.minecamera.logic;

public final class TripodInteractionLogic {
	private TripodInteractionLogic() {
	}

	public static TripodInteractionAction decide(
		boolean hasMountedCamera,
		boolean sneaking,
		boolean holdingCamera,
		boolean emptyHand
	) {
		if (sneaking && !hasMountedCamera && holdingCamera) {
			return TripodInteractionAction.MOUNT_CAMERA;
		}
		if (sneaking && hasMountedCamera && emptyHand) {
			return TripodInteractionAction.DISMOUNT_CAMERA;
		}
		if (!sneaking && hasMountedCamera) {
			return TripodInteractionAction.OPEN_CAMERA_UI;
		}
		return TripodInteractionAction.PASS;
	}
}
