package com.minecamera.client.camera;

import com.minecamera.logic.CameraModeSession;
import com.minecamera.logic.CameraOpticalState;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

record CameraModeRuntimeState(
	boolean active,
	Hand activeHand,
	CameraModeSession session,
	BlockPos tripodPos,
	Text status,
	CameraReviewState reviewState,
	String latestCapturedMediaId,
	CameraOpticalState opticalState
) {
	boolean isTripodActive() {
		return active && tripodPos != null;
	}

	CameraModeRuntimeState withSession(CameraModeSession nextSession) {
		return new CameraModeRuntimeState(
			active,
			activeHand,
			nextSession,
			tripodPos,
			status,
			reviewState,
			latestCapturedMediaId,
			opticalState
		);
	}

	CameraModeRuntimeState withStatus(Text nextStatus) {
		return new CameraModeRuntimeState(
			active,
			activeHand,
			session,
			tripodPos,
			nextStatus,
			reviewState,
			latestCapturedMediaId,
			opticalState
		);
	}

	CameraModeRuntimeState withReviewState(CameraReviewState nextReviewState) {
		return new CameraModeRuntimeState(
			active,
			activeHand,
			session,
			tripodPos,
			status,
			nextReviewState,
			latestCapturedMediaId,
			opticalState
		);
	}

	CameraModeRuntimeState withLatestCapturedMediaId(String mediaId) {
		return new CameraModeRuntimeState(
			active,
			activeHand,
			session,
			tripodPos,
			status,
			reviewState,
			mediaId,
			opticalState
		);
	}

	CameraModeRuntimeState withOpticalState(CameraOpticalState nextOpticalState) {
		return new CameraModeRuntimeState(
			active,
			activeHand,
			session,
			tripodPos,
			status,
			reviewState,
			latestCapturedMediaId,
			nextOpticalState
		);
	}
}
