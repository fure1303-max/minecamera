package com.minecamera.logic;

public final class CameraModeScrollEventPolicy {
	private CameraModeScrollEventPolicy() {
	}

	public static Action decide(boolean cameraModeActive, CameraPreviewMode previewMode) {
		if (!cameraModeActive) {
			return Action.PASS_THROUGH;
		}
		if (previewMode == CameraPreviewMode.LIVE) {
			return Action.ADJUST_FOCAL_LENGTH;
		}
		return Action.CONSUME_ONLY;
	}

	public enum Action {
		PASS_THROUGH,
		CONSUME_ONLY,
		ADJUST_FOCAL_LENGTH
	}
}
