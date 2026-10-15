package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

final class OffscreenCameraRenderStateTest {
	@Test
	void usesFallbacksWhileInactive() {
		OffscreenCameraRenderState state = new OffscreenCameraRenderState();

		assertFalse(state.isActive());
		assertEquals(1920, state.framebufferWidthOr(1920));
		assertEquals(1080, state.framebufferHeightOr(1080));
		assertEquals(70.0F, state.verticalFovOr(70.0F));
		assertNull(state.activeSnapshot());
		assertFalse(state.shouldSuppressHandRendering());
	}

	@Test
	void exposesActiveSnapshotFramebufferAndFov() {
		OffscreenCameraRenderState state = new OffscreenCameraRenderState();
		Object colorOverride = new Object();
		Object depthOverride = new Object();
		CameraRigSnapshot snapshot = new CameraRigSnapshot(
			new Vec3d(10.5D, 65.35D, -2.5D),
			35.0F,
			-12.0F,
			3840,
			2160,
			50,
			2.8D,
			FocusMode.AUTO,
			8.0D,
			"tripod",
			new BlockPos(10, 64, -3)
		);

		state.begin(snapshot, 512, 288, colorOverride, depthOverride);

		assertTrue(state.isActive());
		assertEquals(512, state.framebufferWidthOr(1920));
		assertEquals(288, state.framebufferHeightOr(1080));
		assertEquals(512, state.windowWidthOr(1920));
		assertEquals(288, state.windowHeightOr(1080));
		assertEquals(512, state.viewportWidthOr(1920));
		assertEquals(288, state.viewportHeightOr(1080));
		assertEquals(colorOverride, state.colorTextureOverrideOr(null));
		assertEquals(depthOverride, state.depthTextureOverrideOr(null));
		assertEquals(
			CameraProjectionMath.verticalFovDegrees(50, 3840, 2160),
			state.verticalFovOr(70.0F),
			0.0001F
		);
		assertNotNull(state.activeSnapshot());
		assertEquals(snapshot, state.activeSnapshot());
		assertTrue(state.shouldSuppressHandRendering());
	}

	@Test
	void clearsAllOverridesWhenRenderEnds() {
		OffscreenCameraRenderState state = new OffscreenCameraRenderState();
		Object colorOverride = new Object();
		Object depthOverride = new Object();
		CameraRigSnapshot snapshot = new CameraRigSnapshot(
			new Vec3d(0.0D, 64.0D, 0.0D),
			0.0F,
			0.0F,
			2880,
			2880,
			50,
			2.8D,
			FocusMode.AUTO,
			8.0D,
			"handheld",
			null
		);
		state.begin(snapshot, 512, 512, colorOverride, depthOverride);

		state.end();

		assertFalse(state.isActive());
		assertEquals(1600, state.framebufferWidthOr(1600));
		assertEquals(900, state.framebufferHeightOr(900));
		assertEquals(1600, state.windowWidthOr(1600));
		assertEquals(900, state.windowHeightOr(900));
		assertEquals(1600, state.viewportWidthOr(1600));
		assertEquals(900, state.viewportHeightOr(900));
		assertEquals("fallback-color", state.colorTextureOverrideOr("fallback-color"));
		assertEquals("fallback-depth", state.depthTextureOverrideOr("fallback-depth"));
		assertEquals(70.0F, state.verticalFovOr(70.0F));
		assertNull(state.activeSnapshot());
		assertFalse(state.shouldSuppressHandRendering());
	}

	@Test
	void rejectsNestedOffscreenRenderScopes() {
		OffscreenCameraRenderState state = new OffscreenCameraRenderState();
		Object colorOverride = new Object();
		Object depthOverride = new Object();
		CameraRigSnapshot snapshot = new CameraRigSnapshot(
			new Vec3d(0.0D, 64.0D, 0.0D),
			0.0F,
			0.0F,
			1920,
			1080,
			50,
			2.8D,
			FocusMode.AUTO,
			8.0D,
			"handheld",
			null
		);
		state.begin(snapshot, 512, 288, colorOverride, depthOverride);

		assertThrows(IllegalStateException.class, () -> state.begin(snapshot, 256, 144, colorOverride, depthOverride));
	}
}
