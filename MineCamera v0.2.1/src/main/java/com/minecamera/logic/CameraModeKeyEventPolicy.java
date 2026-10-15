package com.minecamera.logic;

public final class CameraModeKeyEventPolicy {
	private CameraModeKeyEventPolicy() {
	}

	public static boolean shouldConsumeKeyEvent(
		boolean cameraModeActive,
		CameraPreviewMode previewMode,
		boolean pressOrRepeat,
		boolean matchesTogglePerspective,
		boolean matchesLeftMovement,
		boolean matchesRightMovement
	) {
		if (!cameraModeActive || !pressOrRepeat) {
			return false;
		}
		if (matchesTogglePerspective) {
			return true;
		}
		return previewMode == CameraPreviewMode.REVIEW && (matchesLeftMovement || matchesRightMovement);
	}
}
