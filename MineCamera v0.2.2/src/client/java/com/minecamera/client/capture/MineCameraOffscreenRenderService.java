package com.minecamera.client.capture;

import com.minecamera.client.compat.IrisCompatBridge;
import com.minecamera.client.mixin.MinecraftClientFramebufferAccessor;
import com.minecamera.logic.CameraRigSnapshot;
import com.minecamera.logic.IrisOffscreenBootstrapPolicy;
import com.minecamera.logic.ViewfinderSurfacePalette;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.RenderTickCounter;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.system.MemoryStack;

public final class MineCameraOffscreenRenderService {
	private static final double CLEAR_DEPTH = 1.0D;
	private static final IrisOffscreenBootstrapPolicy IRIS_BOOTSTRAP_POLICY = new IrisOffscreenBootstrapPolicy();

	public void render(MinecraftClient client, RenderTickCounter tickCounter, CameraRigSnapshot snapshot, Framebuffer targetFramebuffer) {
		bootstrapIrisResolutionChainIfNeeded(client);
		Framebuffer originalFramebuffer = ((MinecraftClientFramebufferAccessor) client).minecamera$getFramebuffer();
		GpuTextureView originalColorOverride = RenderSystem.outputColorTextureOverride;
		GpuTextureView originalDepthOverride = RenderSystem.outputDepthTextureOverride;
		GpuTextureView targetColorOverride = targetFramebuffer.getColorAttachmentView();
		GpuTextureView targetDepthOverride = targetFramebuffer.useDepthAttachment ? targetFramebuffer.getDepthAttachmentView() : null;
		int[] originalViewport = captureViewport();
		try {
			MineCameraCaptureRenderContext.begin(
				snapshot,
				targetFramebuffer.textureWidth,
				targetFramebuffer.textureHeight,
				targetColorOverride,
				targetDepthOverride
			);
			clearTargetFramebuffer(targetFramebuffer);
			((MinecraftClientFramebufferAccessor) client).minecamera$setFramebuffer(targetFramebuffer);
			RenderSystem.outputColorTextureOverride = targetColorOverride;
			RenderSystem.outputDepthTextureOverride = targetDepthOverride;
			GlStateManager._viewport(0, 0, targetFramebuffer.textureWidth, targetFramebuffer.textureHeight);
			client.gameRenderer.renderWorld(tickCounter);
		} finally {
			((MinecraftClientFramebufferAccessor) client).minecamera$setFramebuffer(originalFramebuffer);
			RenderSystem.outputColorTextureOverride = originalColorOverride;
			RenderSystem.outputDepthTextureOverride = originalDepthOverride;
			restoreViewport(originalViewport);
			MineCameraCaptureRenderContext.end();
		}
	}

	private static void bootstrapIrisResolutionChainIfNeeded(MinecraftClient client) {
		Object worldToken = client.world;
		if (!IRIS_BOOTSTRAP_POLICY.shouldRefreshResolution(IrisCompatBridge.shaderState(), worldToken)) {
			return;
		}

		// Mirror the user-discovered workaround once per world so Iris rebuilds its world-size render targets.
		client.onResolutionChanged();
		IRIS_BOOTSTRAP_POLICY.markRefreshed(worldToken);
	}

	private static void clearTargetFramebuffer(Framebuffer targetFramebuffer) {
		if (targetFramebuffer.getColorAttachment() == null) {
			return;
		}

		CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
		if (targetFramebuffer.useDepthAttachment && targetFramebuffer.getDepthAttachment() != null) {
			encoder.clearColorAndDepthTextures(
				targetFramebuffer.getColorAttachment(),
				ViewfinderSurfacePalette.panelBackgroundColor(),
				targetFramebuffer.getDepthAttachment(),
				CLEAR_DEPTH
			);
			return;
		}

		encoder.clearColorTexture(
			targetFramebuffer.getColorAttachment(),
			ViewfinderSurfacePalette.panelBackgroundColor()
		);
	}

	private static int[] captureViewport() {
		RenderSystem.assertOnRenderThread();
		try (MemoryStack stack = MemoryStack.stackPush()) {
			var viewport = stack.mallocInt(4);
			GL11C.glGetIntegerv(GL11C.GL_VIEWPORT, viewport);
			return new int[] { viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3) };
		}
	}

	private static void restoreViewport(int[] viewport) {
		if (viewport == null || viewport.length != 4) {
			return;
		}
		GlStateManager._viewport(viewport[0], viewport[1], viewport[2], viewport[3]);
	}
}
