package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class CameraShaderCompatibilityPolicyTest {
	@Test
	void resolvesNoIrisWhenCompatibilityModIsMissing() {
		assertEquals(
			ShaderCompatibilityState.NONE,
			CameraShaderCompatibilityPolicy.resolve(false, false)
		);
		assertEquals(
			ShaderCompatibilityState.NONE,
			CameraShaderCompatibilityPolicy.resolve(false, true)
		);
	}

	@Test
	void resolvesInstalledNoShaderWhenIrisIsPresentWithoutActivePack() {
		assertEquals(
			ShaderCompatibilityState.IRIS_INSTALLED_NO_SHADER,
			CameraShaderCompatibilityPolicy.resolve(true, false)
		);
	}

	@Test
	void resolvesShaderActiveWhenIrisIsRenderingThroughShaderPack() {
		assertEquals(
			ShaderCompatibilityState.IRIS_SHADER_ACTIVE,
			CameraShaderCompatibilityPolicy.resolve(true, true)
		);
	}

	@Test
	void onlyShowsShaderHudIndicatorWhileShaderPackIsActive() {
		assertFalse(CameraShaderCompatibilityPolicy.shouldShowShaderHudIndicator(ShaderCompatibilityState.NONE));
		assertFalse(CameraShaderCompatibilityPolicy.shouldShowShaderHudIndicator(ShaderCompatibilityState.IRIS_INSTALLED_NO_SHADER));
		assertTrue(CameraShaderCompatibilityPolicy.shouldShowShaderHudIndicator(ShaderCompatibilityState.IRIS_SHADER_ACTIVE));
	}

	@Test
	void usesShaderSpecificPreviewFailureStatusOnlyWhenShaderPackIsActive() {
		assertEquals(
			"status.preview_failed_keep_film",
			CameraShaderCompatibilityPolicy.previewFailureStatusKey(ShaderCompatibilityState.NONE)
		);
		assertEquals(
			"status.preview_failed_keep_film",
			CameraShaderCompatibilityPolicy.previewFailureStatusKey(ShaderCompatibilityState.IRIS_INSTALLED_NO_SHADER)
		);
		assertEquals(
			"status.shader_preview_incompatible",
			CameraShaderCompatibilityPolicy.previewFailureStatusKey(ShaderCompatibilityState.IRIS_SHADER_ACTIVE)
		);
	}
}
