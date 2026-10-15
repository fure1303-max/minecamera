package com.minecamera.client.capture;

import com.minecamera.logic.CameraRigSnapshot;
import com.minecamera.logic.OffscreenCameraRenderState;

public final class MineCameraCaptureRenderContext {
	private static final OffscreenCameraRenderState STATE = new OffscreenCameraRenderState();

	private MineCameraCaptureRenderContext() {
	}

	public static void begin(CameraRigSnapshot snapshot, int framebufferWidth, int framebufferHeight) {
		STATE.begin(snapshot, framebufferWidth, framebufferHeight);
	}

	public static void begin(
		CameraRigSnapshot snapshot,
		int framebufferWidth,
		int framebufferHeight,
		Object colorTextureOverride,
		Object depthTextureOverride
	) {
		STATE.begin(snapshot, framebufferWidth, framebufferHeight, colorTextureOverride, depthTextureOverride);
	}

	public static void end() {
		STATE.end();
	}

	public static boolean isActive() {
		return STATE.isActive();
	}

	public static int framebufferWidthOr(int fallbackWidth) {
		return STATE.framebufferWidthOr(fallbackWidth);
	}

	public static int framebufferHeightOr(int fallbackHeight) {
		return STATE.framebufferHeightOr(fallbackHeight);
	}

	public static int windowWidthOr(int fallbackWidth) {
		return STATE.windowWidthOr(fallbackWidth);
	}

	public static int windowHeightOr(int fallbackHeight) {
		return STATE.windowHeightOr(fallbackHeight);
	}

	public static int viewportWidthOr(int fallbackWidth) {
		return STATE.viewportWidthOr(fallbackWidth);
	}

	public static int viewportHeightOr(int fallbackHeight) {
		return STATE.viewportHeightOr(fallbackHeight);
	}

	public static float verticalFovOr(float fallbackFov) {
		return STATE.verticalFovOr(fallbackFov);
	}

	public static CameraRigSnapshot activeSnapshot() {
		return STATE.activeSnapshot();
	}

	public static boolean shouldSuppressHandRendering() {
		return STATE.shouldSuppressHandRendering();
	}
}
