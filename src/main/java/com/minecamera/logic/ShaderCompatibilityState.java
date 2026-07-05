package com.minecamera.logic;

public enum ShaderCompatibilityState {
	NONE(false, false),
	IRIS_INSTALLED_NO_SHADER(true, false),
	IRIS_SHADER_ACTIVE(true, true);

	private final boolean irisPresent;
	private final boolean shaderPackActive;

	ShaderCompatibilityState(boolean irisPresent, boolean shaderPackActive) {
		this.irisPresent = irisPresent;
		this.shaderPackActive = shaderPackActive;
	}

	public boolean irisPresent() {
		return irisPresent;
	}

	public boolean shaderPackActive() {
		return shaderPackActive;
	}
}
