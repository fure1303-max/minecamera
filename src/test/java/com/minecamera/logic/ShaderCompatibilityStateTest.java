package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class ShaderCompatibilityStateTest {
	@Test
	void exposesWhetherIrisIsPresent() {
		assertFalse(ShaderCompatibilityState.NONE.irisPresent());
		assertTrue(ShaderCompatibilityState.IRIS_INSTALLED_NO_SHADER.irisPresent());
		assertTrue(ShaderCompatibilityState.IRIS_SHADER_ACTIVE.irisPresent());
	}

	@Test
	void exposesWhetherShaderPackIsActive() {
		assertFalse(ShaderCompatibilityState.NONE.shaderPackActive());
		assertFalse(ShaderCompatibilityState.IRIS_INSTALLED_NO_SHADER.shaderPackActive());
		assertTrue(ShaderCompatibilityState.IRIS_SHADER_ACTIVE.shaderPackActive());
	}
}
