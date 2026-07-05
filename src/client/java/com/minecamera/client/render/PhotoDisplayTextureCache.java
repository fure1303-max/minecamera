package com.minecamera.client.render;

import com.minecamera.client.media.ClientPhotoTextureCache;
import com.minecamera.client.media.LoadedPhotoTexture;
import com.minecamera.display.DisplayFrameState;
import com.minecamera.display.DisplayTextureSliceResolver;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;

public final class PhotoDisplayTextureCache {
	private final ClientPhotoTextureCache photoTextureCache = ClientPhotoTextureCache.getInstance();
	private final DynamicRenderLayerFactory renderLayerFactory = new DynamicRenderLayerFactory();
	private final Map<String, ResolvedDisplayTexture> cache = new HashMap<>();

	public Optional<ResolvedDisplayTexture> resolve(MinecraftClient client, DisplayFrameState state) {
		if (state == null || state.isEmpty()) {
			return Optional.empty();
		}

		ResolvedDisplayTexture cached = cache.get(state.mediaId());
		if (cached != null) {
			return Optional.of(cached);
		}

		Optional<ResolvedDisplayTexture> resolved = photoTextureCache.resolve(client, state.mediaId()).map(texture -> toResolvedTexture(state, texture));
		resolved.ifPresent(texture -> cache.put(state.mediaId(), texture));
		return resolved;
	}

	private ResolvedDisplayTexture toResolvedTexture(DisplayFrameState state, LoadedPhotoTexture texture) {
		return new ResolvedDisplayTexture(
			texture.textureId(),
			renderLayerFactory.getOrCreate(texture.textureId()),
			DisplayTextureSliceResolver.resolve(state)
		);
	}
}
