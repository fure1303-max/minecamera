package com.minecamera.logic;

public final class IrisOffscreenBootstrapPolicy {
	private Object bootstrappedWorldToken;

	public boolean shouldRefreshResolution(ShaderCompatibilityState shaderState, Object worldToken) {
		if (shaderState != ShaderCompatibilityState.IRIS_SHADER_ACTIVE || worldToken == null) {
			bootstrappedWorldToken = null;
			return false;
		}
		return bootstrappedWorldToken != worldToken;
	}

	public void markRefreshed(Object worldToken) {
		if (worldToken != null) {
			bootstrappedWorldToken = worldToken;
		}
	}
}
