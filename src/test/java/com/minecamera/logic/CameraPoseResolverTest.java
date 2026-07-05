package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

final class CameraPoseResolverTest {
	@Test
	void resolvesHandheldSnapshotFromPlayerEyePose() {
		CameraRigState rigState = CameraRigState.handheld(2880, 2880, 50, 2.8D, FocusMode.AUTO, 8.0D);

		CameraRigSnapshot snapshot = CameraPoseResolver.resolveHandheld(rigState, new Vec3d(1.0D, 65.62D, -3.0D), 20.0F, -10.0F);

		assertEquals(new Vec3d(1.0D, 65.62D, -3.0D), snapshot.position());
		assertEquals(20.0F, snapshot.yaw());
		assertEquals(-10.0F, snapshot.pitch());
		assertEquals("handheld", snapshot.sourceType());
		assertNull(snapshot.tripodPos());
	}

	@Test
	void resolvesTripodSnapshotFromStoredTripodAngles() {
		BlockPos tripodPos = new BlockPos(10, 64, -3);
		CameraRigState rigState = CameraRigState.tripod(3840, 2160, 50, 2.8D, FocusMode.AUTO, 8.0D, tripodPos);

		CameraRigSnapshot snapshot = CameraPoseResolver.resolveTripod(rigState, 35.0F, -12.0F);

		assertEquals(new Vec3d(10.5D, 65.35D, -2.5D), snapshot.position());
		assertEquals(35.0F, snapshot.yaw());
		assertEquals(-12.0F, snapshot.pitch());
		assertEquals("tripod", snapshot.sourceType());
		assertEquals(tripodPos, snapshot.tripodPos());
	}

	@Test
	void clampsPitchIntoTripodSafeRange() {
		assertEquals(-85.0F, CameraPoseResolver.clampPitch(-200.0F));
		assertEquals(40.0F, CameraPoseResolver.clampPitch(40.0F));
		assertEquals(85.0F, CameraPoseResolver.clampPitch(200.0F));
	}
}
