package com.minecamera.logic;

import net.minecraft.util.math.BlockPos;

public record CameraRigState(
	String sourceType,
	int captureWidth,
	int captureHeight,
	int focalLengthMm,
	double apertureFStop,
	FocusMode focusMode,
	double focusDistance,
	BlockPos tripodPos
) {
	public static CameraRigState handheld(
		int captureWidth,
		int captureHeight,
		int focalLengthMm,
		double apertureFStop,
		FocusMode focusMode,
		double focusDistance
	) {
		return new CameraRigState("handheld", captureWidth, captureHeight, focalLengthMm, apertureFStop, focusMode, focusDistance, null);
	}

	public static CameraRigState tripod(
		int captureWidth,
		int captureHeight,
		int focalLengthMm,
		double apertureFStop,
		FocusMode focusMode,
		double focusDistance,
		BlockPos tripodPos
	) {
		return new CameraRigState("tripod", captureWidth, captureHeight, focalLengthMm, apertureFStop, focusMode, focusDistance, tripodPos);
	}
}
