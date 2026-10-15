package com.minecamera.logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record CameraRigSnapshot(
	Vec3d position,
	float yaw,
	float pitch,
	int captureWidth,
	int captureHeight,
	int focalLengthMm,
	double apertureFStop,
	FocusMode focusMode,
	double focusDistance,
	String sourceType,
	BlockPos tripodPos
) {
	public float verticalFovDegrees() {
		return CameraProjectionMath.verticalFovDegrees(focalLengthMm, captureWidth, captureHeight);
	}
}
