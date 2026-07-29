package com.minecamera.client.media;

import com.minecamera.media.DisplayThumbnailPolicy;
import com.minecamera.media.LocalMediaCatalog;
import com.minecamera.media.MineCameraPaths;
import com.minecamera.media.ResolvedMedia;
import com.minecamera.network.DisplayThumbnailPayload;
import com.minecamera.network.PublishDisplayThumbnailPayload;
import com.minecamera.network.RequestDisplayThumbnailPayload;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public final class ClientDisplayMediaSync {
	private static final long RETRY_DELAY_MILLIS = 5_000L;
	private static final Set<String> pendingRequests = ConcurrentHashMap.newKeySet();
	private static final Set<String> publishedMedia = ConcurrentHashMap.newKeySet();
	private static final Map<String, Long> retryAfter = new ConcurrentHashMap<>();

	private ClientDisplayMediaSync() {
	}

	public static Optional<Path> syncedThumbnail(MinecraftClient client, String mediaId) {
		if (!DisplayThumbnailPolicy.isValidMediaId(mediaId)) {
			return Optional.empty();
		}
		Path path = cachePath(client, mediaId);
		return Files.isRegularFile(path) ? Optional.of(path) : Optional.empty();
	}

	public static void request(MinecraftClient client, String mediaId) {
		if (!DisplayThumbnailPolicy.isValidMediaId(mediaId) || pendingRequests.contains(mediaId)) {
			return;
		}
		long now = System.currentTimeMillis();
		if (retryAfter.getOrDefault(mediaId, 0L) > now || !ClientPlayNetworking.canSend(RequestDisplayThumbnailPayload.ID)) {
			return;
		}
		pendingRequests.add(mediaId);
		ClientPlayNetworking.send(new RequestDisplayThumbnailPayload(mediaId));
	}

	public static void accept(MinecraftClient client, DisplayThumbnailPayload payload) {
		pendingRequests.remove(payload.mediaId());
		if (!DisplayThumbnailPolicy.isValidMediaId(payload.mediaId()) || DisplayThumbnailPolicy.inspectJpeg(payload.thumbnailBytes()).isEmpty()) {
			retryAfter.put(payload.mediaId(), System.currentTimeMillis() + RETRY_DELAY_MILLIS);
			return;
		}

		try {
			Path path = cachePath(client, payload.mediaId());
			Files.createDirectories(path.getParent());
			Files.write(path, payload.thumbnailBytes());
			retryAfter.remove(payload.mediaId());
			ClientPhotoTextureCache.getInstance().invalidate(payload.mediaId());
		} catch (IOException exception) {
			retryAfter.put(payload.mediaId(), System.currentTimeMillis() + RETRY_DELAY_MILLIS);
		}
	}

	public static void publishIfAvailable(MinecraftClient client, String mediaId) {
		if (!DisplayThumbnailPolicy.isValidMediaId(mediaId) || publishedMedia.contains(mediaId)
			|| !ClientPlayNetworking.canSend(PublishDisplayThumbnailPayload.ID)) {
			return;
		}
		publishedMedia.add(mediaId);
		CompletableFuture.runAsync(() -> publishLocalMedia(client, mediaId));
	}

	private static void publishLocalMedia(MinecraftClient client, String mediaId) {
		try {
			ResolvedMedia resolved = new LocalMediaCatalog(MineCameraPaths.fromRunDirectory(client.runDirectory.toPath()).root())
				.find(mediaId)
				.orElse(null);
			if (resolved == null || !resolved.available()) {
				publishedMedia.remove(mediaId);
				return;
			}
			byte[] bytes = DisplayThumbnailEncoder.encode(resolved.absolutePath());
			client.execute(() -> ClientPlayNetworking.send(new PublishDisplayThumbnailPayload(mediaId, bytes)));
		} catch (IOException exception) {
			publishedMedia.remove(mediaId);
		}
	}

	private static Path cachePath(MinecraftClient client, String mediaId) {
		return MineCameraPaths.fromRunDirectory(client.runDirectory.toPath()).displayCacheRoot().resolve(mediaId + ".jpg");
	}
}
