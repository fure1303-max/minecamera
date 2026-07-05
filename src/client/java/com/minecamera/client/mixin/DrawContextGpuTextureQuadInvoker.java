package com.minecamera.client.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrawContext.class)
public interface DrawContextGpuTextureQuadInvoker {
	@Invoker("drawTexturedQuad")
	void minecamera$drawTexturedQuad(
		RenderPipeline pipeline,
		GpuTextureView textureView,
		GpuSampler sampler,
		int x1,
		int y1,
		int x2,
		int y2,
		float minU,
		float maxU,
		float minV,
		float maxV,
		int color
	);
}
