package com.minecamera.client.camera;

import com.minecamera.client.capture.MineCameraOffscreenRenderService;
import com.minecamera.client.capture.MineCameraCaptureRenderContext;
import com.minecamera.client.mixin.DrawContextGpuTextureQuadInvoker;
import com.minecamera.logic.CameraRigSnapshot;
import com.minecamera.logic.ViewfinderFramebufferQuadPlan;
import com.minecamera.logic.ViewfinderPreviewPlan;
import com.minecamera.logic.ViewfinderSurfacePalette;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.Objects;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Util;

final class MineCameraViewfinderService {
	private static final RenderPipeline PREVIEW_PIPELINE = RenderPipelines.GUI_TEXTURED;
	private static final long REFRESH_INTERVAL_MS = 50L;

	private final MineCameraOffscreenRenderService renderService = new MineCameraOffscreenRenderService();
	private long nextRefreshAtMs;
	private SimpleFramebuffer previewFramebuffer;
	private boolean previewReady;

	ViewfinderRefreshResult requestRefresh(MinecraftClient client, RenderTickCounter tickCounter, CameraRigSnapshot snapshot) {
		Objects.requireNonNull(client, "client");
		if (client.player == null || client.world == null) {
			return ViewfinderRefreshResult.skipped();
		}
		long now = Util.getMeasuringTimeMs();
		if (now < nextRefreshAtMs) {
			return ViewfinderRefreshResult.skipped();
		}

		ViewfinderPreviewPlan plan = ViewfinderPreviewPlan.fromTarget(snapshot.captureWidth(), snapshot.captureHeight());
		ensureFramebuffer(plan.width(), plan.height());
		try {
			renderService.render(client, tickCounter, snapshot, previewFramebuffer);
			previewReady = true;
		} catch (RuntimeException exception) {
			previewReady = false;
			nextRefreshAtMs = now + REFRESH_INTERVAL_MS;
			return ViewfinderRefreshResult.failed(exception.getMessage());
		}
		nextRefreshAtMs = Util.getMeasuringTimeMs() + REFRESH_INTERVAL_MS;
		return ViewfinderRefreshResult.success();
	}

	void drawPreview(DrawContext context, int x, int y, int width, int height) {
		context.fill(x, y, x + width, y + height, ViewfinderSurfacePalette.panelBackgroundColor());
		if (!previewReady || previewFramebuffer == null || previewFramebuffer.getColorAttachmentView() == null) {
			return;
		}
		ViewfinderFramebufferQuadPlan plan = ViewfinderFramebufferQuadPlan.forHudRect(x, y, width, height);
		GpuSampler sampler = RenderSystem.getSamplerCache().get(FilterMode.NEAREST);
		((DrawContextGpuTextureQuadInvoker) (Object) context).minecamera$drawTexturedQuad(
			PREVIEW_PIPELINE,
			previewFramebuffer.getColorAttachmentView(),
			sampler,
			plan.x1(),
			plan.y1(),
			plan.x2(),
			plan.y2(),
			plan.minU(),
			plan.maxU(),
			plan.minV(),
			plan.maxV(),
			-1
		);
	}

	boolean hasPreview() {
		return previewReady && previewFramebuffer != null;
	}

	void clear(MinecraftClient client) {
		nextRefreshAtMs = 0L;
		previewReady = false;
		if (previewFramebuffer != null) {
			previewFramebuffer.delete();
			previewFramebuffer = null;
		}
	}

	private void ensureFramebuffer(int width, int height) {
		if (previewFramebuffer != null && previewFramebuffer.textureWidth == width && previewFramebuffer.textureHeight == height) {
			return;
		}
		if (previewFramebuffer != null) {
			previewFramebuffer.delete();
		}
		previewFramebuffer = new SimpleFramebuffer("MineCamera Viewfinder", width, height, true);
	}
}
