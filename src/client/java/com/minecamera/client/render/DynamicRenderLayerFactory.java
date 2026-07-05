package com.minecamera.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.util.Identifier;

public final class DynamicRenderLayerFactory {
	private final Map<Identifier, RenderLayer> cache = new HashMap<>();

	public RenderLayer getOrCreate(Identifier textureId) {
		return cache.computeIfAbsent(textureId, DynamicRenderLayerFactory::createRenderLayer);
	}

	private static RenderLayer createRenderLayer(Identifier textureId) {
		RenderSetup setup = RenderSetup.builder(worldPhotoPipeline())
			.texture("Sampler0", textureId)
			.useLightmap()
			.translucent()
			.expectedBufferSize(256)
			.build();
		return RenderLayer.of("minecamera_photo_" + textureId.toString().replace(':', '_'), setup);
	}

	private static RenderPipeline worldPhotoPipeline() {
		return RenderPipelines.ENTITY_TRANSLUCENT;
	}
}
