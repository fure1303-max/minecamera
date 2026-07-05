package com.minecamera.logic;

public final class CameraShaderCompatibilityPolicy {
	private CameraShaderCompatibilityPolicy() {
	}

	public static ShaderCompatibilityState resolve(boolean irisPresent, boolean shaderPackInUse) {
		if (!irisPresent) {
			return ShaderCompatibilityState.NONE;
		}
		return shaderPackInUse
			? ShaderCompatibilityState.IRIS_SHADER_ACTIVE
			: ShaderCompatibilityState.IRIS_INSTALLED_NO_SHADER;
	}

	public static boolean shouldShowShaderHudIndicator(ShaderCompatibilityState state) {
		return state == ShaderCompatibilityState.IRIS_SHADER_ACTIVE;
	}

	public static String previewFailureStatusKey(ShaderCompatibilityState state) {
		return state == ShaderCompatibilityState.IRIS_SHADER_ACTIVE
			? "status.shader_preview_incompatible"
			: "status.preview_failed_keep_film";
	}
}
