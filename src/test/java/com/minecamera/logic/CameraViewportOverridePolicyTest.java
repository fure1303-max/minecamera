package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

final class CameraViewportOverridePolicyTest {
	@Test
	void onlyTripodCameraModeOverridesWorldViewport() {
		assertTrue(CameraViewportOverridePolicy.shouldOverrideWorldViewport(true, "tripod", ShaderCompatibilityState.NONE, false));
		assertFalse(CameraViewportOverridePolicy.shouldOverrideWorldViewport(true, "handheld", ShaderCompatibilityState.NONE, false));
		assertFalse(CameraViewportOverridePolicy.shouldOverrideWorldViewport(false, "tripod", ShaderCompatibilityState.NONE, false));
	}

	@Test
	void blocksSessionViewportOverrideDuringIrisShadowPass() {
		assertFalse(
			CameraViewportOverridePolicy.shouldOverrideWorldViewport(
				true,
				"tripod",
				ShaderCompatibilityState.IRIS_SHADER_ACTIVE,
				true
			)
		);
		assertTrue(
			CameraViewportOverridePolicy.shouldOverrideWorldViewport(
				true,
				"tripod",
				ShaderCompatibilityState.IRIS_SHADER_ACTIVE,
				false
			)
		);
	}

	@Test
	void prefersOffscreenSnapshotWhenCaptureRenderIsActive() {
		CameraRigSnapshot offscreen = snapshot("tripod", new BlockPos(1, 2, 3));
		CameraRigSnapshot session = snapshot("tripod", new BlockPos(4, 5, 6));

		assertEquals(offscreen, CameraViewportOverridePolicy.selectSnapshot(offscreen, session));
	}

	@Test
	void fallsBackToSessionSnapshotWhenNoOffscreenRenderIsActive() {
		CameraRigSnapshot session = snapshot("tripod", new BlockPos(4, 5, 6));

		assertEquals(session, CameraViewportOverridePolicy.selectSnapshot(null, session));
		assertNull(CameraViewportOverridePolicy.selectSnapshot(null, null));
	}

	private static CameraRigSnapshot snapshot(String source, BlockPos tripodPos) {
		return new CameraRigSnapshot(
			new Vec3d(10.5D, 65.35D, -2.5D),
			35.0F,
			-12.0F,
			3840,
			2160,
			50,
			2.8D,
			FocusMode.AUTO,
			8.0D,
			source,
			tripodPos
		);
	}
}
