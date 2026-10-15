package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class IrisOffscreenBootstrapPolicyTest {
	@Test
	void doesNotRefreshWhenIrisShaderIsInactive() {
		IrisOffscreenBootstrapPolicy policy = new IrisOffscreenBootstrapPolicy();
		Object world = new Object();

		assertFalse(policy.shouldRefreshResolution(ShaderCompatibilityState.NONE, world));
		assertFalse(policy.shouldRefreshResolution(ShaderCompatibilityState.IRIS_INSTALLED_NO_SHADER, world));
	}

	@Test
	void refreshesOnceForTheFirstActiveWorld() {
		IrisOffscreenBootstrapPolicy policy = new IrisOffscreenBootstrapPolicy();
		Object world = new Object();

		assertTrue(policy.shouldRefreshResolution(ShaderCompatibilityState.IRIS_SHADER_ACTIVE, world));

		policy.markRefreshed(world);

		assertFalse(policy.shouldRefreshResolution(ShaderCompatibilityState.IRIS_SHADER_ACTIVE, world));
	}

	@Test
	void refreshesAgainWhenTheWorldInstanceChanges() {
		IrisOffscreenBootstrapPolicy policy = new IrisOffscreenBootstrapPolicy();
		Object firstWorld = new Object();
		Object secondWorld = new Object();

		assertTrue(policy.shouldRefreshResolution(ShaderCompatibilityState.IRIS_SHADER_ACTIVE, firstWorld));
		policy.markRefreshed(firstWorld);

		assertTrue(policy.shouldRefreshResolution(ShaderCompatibilityState.IRIS_SHADER_ACTIVE, secondWorld));
	}

	@Test
	void resetsAfterLeavingTheWorldOrDisablingShaderPack() {
		IrisOffscreenBootstrapPolicy policy = new IrisOffscreenBootstrapPolicy();
		Object world = new Object();

		assertTrue(policy.shouldRefreshResolution(ShaderCompatibilityState.IRIS_SHADER_ACTIVE, world));
		policy.markRefreshed(world);

		assertFalse(policy.shouldRefreshResolution(ShaderCompatibilityState.NONE, world));
		assertTrue(policy.shouldRefreshResolution(ShaderCompatibilityState.IRIS_SHADER_ACTIVE, world));

		policy.markRefreshed(world);
		assertFalse(policy.shouldRefreshResolution(ShaderCompatibilityState.IRIS_SHADER_ACTIVE, null));
		assertTrue(policy.shouldRefreshResolution(ShaderCompatibilityState.IRIS_SHADER_ACTIVE, world));
	}
}
