package com.minecamera.client.media;

import com.minecamera.media.LocalMediaCatalog;
import com.minecamera.media.MineCameraPaths;
import com.minecamera.media.ResolvedMedia;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public final class ClientPhotoTextureCache {
	private static final ClientPhotoTextureCache INSTANCE = new ClientPhotoTextureCache();

	private final Map<String, LoadedPhotoTexture> cache = new HashMap<>();

	private ClientPhotoTextureCache() {
	}

	public static ClientPhotoTextureCache getInstance() {
		return INSTANCE;
	}

	public Optional<LoadedPhotoTexture> resolve(MinecraftClient client, String mediaId) {
		if (mediaId == null || mediaId.isBlank()) {
			return Optional.empty();
		}

		LoadedPhotoTexture cached = cache.get(mediaId);
		if (cached != null) {
			return Optional.of(cached);
		}

		try {
			ResolvedMedia resolved = new LocalMediaCatalog(MineCameraPaths.fromRunDirectory(client.runDirectory.toPath()).root())
				.find(mediaId)
				.orElse(null);
			if (resolved == null || !resolved.available()) {
				return Optional.empty();
			}

			try (InputStream inputStream = Files.newInputStream(resolved.absolutePath())) {
				NativeImage image = NativeImage.read(inputStream);
				Identifier textureId = Identifier.of("minecamera", "photo/" + mediaId);
				client.getTextureManager().registerTexture(
					textureId,
					new NativeImageBackedTexture(() -> "minecamera_photo_" + mediaId, image)
				);
				LoadedPhotoTexture loaded = new LoadedPhotoTexture(mediaId, textureId, image.getWidth(), image.getHeight());
				cache.put(mediaId, loaded);
				return Optional.of(loaded);
			}
		} catch (IOException exception) {
			return Optional.empty();
		}
	}

	public void invalidate(String mediaId) {
		LoadedPhotoTexture removed = cache.remove(mediaId);
		if (removed == null) {
			return;
		}

		MinecraftClient client = MinecraftClient.getInstance();
		if (client != null) {
			client.getTextureManager().destroyTexture(removed.textureId());
		}
	}

	public void clear() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client != null) {
			for (LoadedPhotoTexture texture : cache.values()) {
				client.getTextureManager().destroyTexture(texture.textureId());
			}
		}
		cache.clear();
	}
}
