package com.minecamera.logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record TripodCameraPose(Vec3d position, float yaw, float pitch) {
	private static final double CAMERA_HEIGHT = 1.35D;

	public static TripodCameraPose fromBlock(BlockPos pos, float yaw, float pitch) {
		return new TripodCameraPose(
			new Vec3d(pos.getX() + 0.5D, pos.getY() + CAMERA_HEIGHT, pos.getZ() + 0.5D),
			yaw,
			pitch
		);
	}
}
