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
	boolean previewFailureActive,
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
			previewFailureActive,
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
			previewFailureActive,
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
			previewFailureActive,
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
			previewFailureActive,
			opticalState
		);
	}

	CameraModeRuntimeState withPreviewFailureActive(boolean activePreviewFailure) {
		return new CameraModeRuntimeState(
			active,
			activeHand,
			session,
			tripodPos,
			status,
			reviewState,
			latestCapturedMediaId,
			activePreviewFailure,
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
			previewFailureActive,
			nextOpticalState
		);
	}
}
