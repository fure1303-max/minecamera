package com.minecamera.client.camera;

import com.minecamera.logic.CameraModeSession;
import com.minecamera.logic.CameraOpticalState;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

final class CameraModeLifecycle {
	private CameraModeLifecycle() {
	}

	static CameraModeRuntimeState openHandheld(Hand hand, Text idleStatus, CameraReviewState emptyReviewState, double defaultFocusDistance) {
		return new CameraModeRuntimeState(
			true,
			hand,
			CameraModeSession.handheld(),
			null,
			idleStatus,
			emptyReviewState,
			null,
			false,
			CameraOpticalState.defaults(defaultFocusDistance)
		);
	}

	static CameraModeRuntimeState openTripod(Hand hand, BlockPos tripodPos, Text idleStatus, CameraReviewState emptyReviewState, double defaultFocusDistance) {
		return new CameraModeRuntimeState(
			true,
			hand,
			CameraModeSession.tripod(),
			tripodPos.toImmutable(),
			idleStatus,
			emptyReviewState,
			null,
			false,
			CameraOpticalState.defaults(defaultFocusDistance)
		);
	}

	static CameraModeRuntimeState closed(Text idleStatus, CameraReviewState emptyReviewState, double defaultFocusDistance) {
		return new CameraModeRuntimeState(
			false,
			Hand.MAIN_HAND,
			CameraModeSession.handheld(),
			null,
			idleStatus,
			emptyReviewState,
			null,
			false,
			CameraOpticalState.defaults(defaultFocusDistance)
		);
	}
}
