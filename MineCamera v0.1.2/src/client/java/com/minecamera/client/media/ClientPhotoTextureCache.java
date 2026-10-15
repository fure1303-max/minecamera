package com.minecamera.client.media;

import com.minecamera.media.DisplayThumbnailPolicy;
import com.minecamera.media.LocalMediaCatalog;
import com.minecamera.media.MineCameraPaths;
import com.minecamera.media.ResolvedMedia;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public final class ClientPhotoTextureCache {
	private static final ClientPhotoTextureCache INSTANCE = new ClientPhotoTextureCache();
	private static final int MAX_TEXTURES = 64;
	private static final long MAX_ESTIMATED_BYTES = 256L * 1024L * 1024L;
	private static final Identifier MISSING_TEXTURE_ID = Identifier.of("minecamera", "textures/misc/missing_photo.png");

	private final Map<String, LoadedPhotoTexture> cache = new LinkedHashMap<>(16, 0.75F, true);
	private long estimatedBytes;

	private ClientPhotoTextureCache() {
	}

	public static ClientPhotoTextureCache getInstance() {
		return INSTANCE;
	}

	public Optional<LoadedPhotoTexture> resolve(MinecraftClient client, String mediaId) {
		if (!DisplayThumbnailPolicy.isValidMediaId(mediaId)) {
			return Optional.empty();
		}

		LoadedPhotoTexture cached = cache.get(mediaId);
		if (cached != null) {
			return Optional.of(cached);
		}

		Path source = resolveSource(client, mediaId).orElse(null);
		if (source == null) {
			ClientDisplayMediaSync.request(client, mediaId);
			return Optional.of(missing(mediaId));
		}

		try (InputStream inputStream = Files.newInputStream(source)) {
			NativeImage image = NativeImage.read(inputStream);
			Identifier textureId = Identifier.of("minecamera", "photo/" + mediaId);
			client.getTextureManager().registerTexture(
				textureId,
				new NativeImageBackedTexture(() -> "minecamera_photo_" + mediaId, image)
			);
			LoadedPhotoTexture loaded = new LoadedPhotoTexture(mediaId, textureId, image.getWidth(), image.getHeight());
			cache.put(mediaId, loaded);
			estimatedBytes += estimatedBytes(loaded);
			evictIfNeeded(client);
			return Optional.of(loaded);
		} catch (IOException exception) {
			ClientDisplayMediaSync.request(client, mediaId);
			return Optional.of(missing(mediaId));
		}
	}

	public void invalidate(String mediaId) {
		LoadedPhotoTexture removed = cache.remove(mediaId);
		if (removed == null) {
			return;
		}
		estimatedBytes -= estimatedBytes(removed);
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
		estimatedBytes = 0L;
	}

	private static Optional<Path> resolveSource(MinecraftClient client, String mediaId) {
		try {
			ResolvedMedia resolved = new LocalMediaCatalog(MineCameraPaths.fromRunDirectory(client.runDirectory.toPath()).root())
				.find(mediaId)
				.orElse(null);
			if (resolved != null && resolved.available()) {
				return Optional.of(resolved.absolutePath());
			}
		} catch (IOException ignored) {
		}
		return ClientDisplayMediaSync.syncedThumbnail(client, mediaId);
	}

	private void evictIfNeeded(MinecraftClient client) {
		Iterator<Map.Entry<String, LoadedPhotoTexture>> iterator = cache.entrySet().iterator();
		while ((cache.size() > MAX_TEXTURES || estimatedBytes > MAX_ESTIMATED_BYTES) && cache.size() > 1 && iterator.hasNext()) {
			LoadedPhotoTexture removed = iterator.next().getValue();
			iterator.remove();
			estimatedBytes -= estimatedBytes(removed);
			client.getTextureManager().destroyTexture(removed.textureId());
		}
	}

	private static long estimatedBytes(LoadedPhotoTexture texture) {
		return (long) texture.width() * texture.height() * 4L;
	}

	private static LoadedPhotoTexture missing(String mediaId) {
		return new LoadedPhotoTexture(mediaId, MISSING_TEXTURE_ID, 32, 32);
	}
}
