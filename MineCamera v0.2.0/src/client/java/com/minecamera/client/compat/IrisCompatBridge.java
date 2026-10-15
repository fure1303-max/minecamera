package com.minecamera.client.compat;

import com.minecamera.logic.CameraShaderCompatibilityPolicy;
import com.minecamera.logic.ShaderCompatibilityState;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Method;

public final class IrisCompatBridge {
	private static final Accessor ACCESSOR = createAccessor();

	private IrisCompatBridge() {
	}

	public static ShaderCompatibilityState shaderState() {
		return ACCESSOR.shaderState();
	}

	public static boolean isRenderingShadowPass() {
		return ACCESSOR.isRenderingShadowPass();
	}

	private static Accessor createAccessor() {
		if (!FabricLoader.getInstance().isModLoaded("iris")) {
			return MissingAccessor.INSTANCE;
		}
		try {
			Class<?> irisApiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
			Method getInstance = irisApiClass.getMethod("getInstance");
			Method isShaderPackInUse = irisApiClass.getMethod("isShaderPackInUse");
			Method isRenderingShadowPass = irisApiClass.getMethod("isRenderingShadowPass");
			Object api = getInstance.invoke(null);
			if (api == null) {
				return PresentWithoutApiAccessor.INSTANCE;
			}
			return new ReflectionAccessor(api, isShaderPackInUse, isRenderingShadowPass);
		} catch (ReflectiveOperationException | LinkageError exception) {
			return PresentWithoutApiAccessor.INSTANCE;
		}
	}

	private interface Accessor {
		ShaderCompatibilityState shaderState();

		boolean isRenderingShadowPass();
	}

	private enum MissingAccessor implements Accessor {
		INSTANCE;

		@Override
		public ShaderCompatibilityState shaderState() {
			return ShaderCompatibilityState.NONE;
		}

		@Override
		public boolean isRenderingShadowPass() {
			return false;
		}
	}

	private enum PresentWithoutApiAccessor implements Accessor {
		INSTANCE;

		@Override
		public ShaderCompatibilityState shaderState() {
			return ShaderCompatibilityState.IRIS_INSTALLED_NO_SHADER;
		}

		@Override
		public boolean isRenderingShadowPass() {
			return false;
		}
	}

	private record ReflectionAccessor(Object api, Method shaderPackInUseMethod, Method shadowPassMethod) implements Accessor {
		@Override
		public ShaderCompatibilityState shaderState() {
			return CameraShaderCompatibilityPolicy.resolve(true, invokeBoolean(shaderPackInUseMethod));
		}

		@Override
		public boolean isRenderingShadowPass() {
			return invokeBoolean(shadowPassMethod);
		}

		private boolean invokeBoolean(Method method) {
			try {
				Object result = method.invoke(api);
				return result instanceof Boolean bool && bool;
			} catch (ReflectiveOperationException | RuntimeException exception) {
				return false;
			}
		}
	}
}
