package com.minecamera.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DisplayThumbnailRequestLimiter {
	private final long playerCooldownMillis;
	private final long mediaCooldownMillis;
	private final Map<UUID, Long> playerRetryAfter = new HashMap<>();
	private final Map<RequestKey, Long> mediaRetryAfter = new HashMap<>();

	public DisplayThumbnailRequestLimiter(long playerCooldownMillis, long mediaCooldownMillis) {
		if (playerCooldownMillis < 0L || mediaCooldownMillis < 0L) {
			throw new IllegalArgumentException("Thumbnail request cooldowns must be non-negative");
		}
		this.playerCooldownMillis = playerCooldownMillis;
		this.mediaCooldownMillis = mediaCooldownMillis;
	}

	public synchronized boolean tryAcquire(UUID playerId, String mediaId, long nowMillis) {
		pruneExpired(nowMillis);
		RequestKey key = new RequestKey(playerId, mediaId);
		if (playerRetryAfter.getOrDefault(playerId, 0L) > nowMillis
			|| mediaRetryAfter.getOrDefault(key, 0L) > nowMillis) {
			return false;
		}

		playerRetryAfter.put(playerId, nowMillis + playerCooldownMillis);
		mediaRetryAfter.put(key, nowMillis + mediaCooldownMillis);
		return true;
	}

	private void pruneExpired(long nowMillis) {
		playerRetryAfter.entrySet().removeIf(entry -> entry.getValue() <= nowMillis);
		mediaRetryAfter.entrySet().removeIf(entry -> entry.getValue() <= nowMillis);
	}

	private record RequestKey(UUID playerId, String mediaId) {
	}
}
