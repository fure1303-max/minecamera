package com.minecamera.logic;

public final class CameraModeWorldInputPolicy {
	private CameraModeWorldInputPolicy() {
	}

	public static PressedStates sanitizePressedStates(
		boolean cameraModeActive,
		CameraPreviewMode previewMode,
		boolean attackPressed,
		boolean usePressed,
		boolean pickPressed,
		boolean leftPressed,
		boolean rightPressed
	) {
		if (!cameraModeActive) {
			return new PressedStates(attackPressed, usePressed, pickPressed, leftPressed, rightPressed);
		}
		if (previewMode == CameraPreviewMode.REVIEW) {
			return new PressedStates(false, false, false, false, false);
		}
		return new PressedStates(false, false, false, leftPressed, rightPressed);
	}

	public static boolean shouldCancelWorldAction(boolean cameraModeActive) {
		return cameraModeActive;
	}

	public static boolean shouldCancelContinuousBlockBreaking(boolean cameraModeActive) {
		return cameraModeActive;
	}

	public record PressedStates(boolean attackPressed, boolean usePressed, boolean pickPressed, boolean leftPressed, boolean rightPressed) {
	}
}
