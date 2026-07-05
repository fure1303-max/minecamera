package com.minecamera.logic;

public final class CameraViewportOverridePolicy {
	private CameraViewportOverridePolicy() {
	}

	public static boolean shouldOverrideWorldViewport(
		boolean cameraModeActive,
		String sourceType,
		ShaderCompatibilityState shaderState,
		boolean renderingShadowPass
	) {
		if (!cameraModeActive || !"tripod".equals(sourceType)) {
			return false;
		}
		return shaderState != ShaderCompatibilityState.IRIS_SHADER_ACTIVE || !renderingShadowPass;
	}

	public static CameraRigSnapshot selectSnapshot(CameraRigSnapshot offscreenSnapshot, CameraRigSnapshot sessionSnapshot) {
		return offscreenSnapshot != null ? offscreenSnapshot : sessionSnapshot;
	}
}
