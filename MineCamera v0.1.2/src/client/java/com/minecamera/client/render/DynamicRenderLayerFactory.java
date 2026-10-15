package com.minecamera.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.util.Identifier;

public final class DynamicRenderLayerFactory {
	private static final int MAX_LAYERS = 65;
	private final Map<Identifier, RenderLayer> cache = new LinkedHashMap<>(16, 0.75F, true);

	public RenderLayer getOrCreate(Identifier textureId) {
		RenderLayer layer = cache.computeIfAbsent(textureId, DynamicRenderLayerFactory::createRenderLayer);
		evictIfNeeded();
		return layer;
	}

	public void clear() {
		cache.clear();
	}

	private void evictIfNeeded() {
		Iterator<Identifier> iterator = cache.keySet().iterator();
		while (cache.size() > MAX_LAYERS && iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
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
