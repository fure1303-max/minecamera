package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

final class TripodCameraPoseTest {
	@Test
	void anchorsCameraAboveTripodCenter() {
		TripodCameraPose pose = TripodCameraPose.fromBlock(new BlockPos(10, 64, -3), 35.0F, -12.0F);

		assertEquals(new Vec3d(10.5D, 65.35D, -2.5D), pose.position());
		assertEquals(35.0F, pose.yaw());
		assertEquals(-12.0F, pose.pitch());
	}
}
