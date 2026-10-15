package com.minecamera.network;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

final class DisplayThumbnailRequestLimiterTest {
	@Test
	void limitsPlayerRequestRateAcrossDifferentMedia() {
		DisplayThumbnailRequestLimiter limiter = new DisplayThumbnailRequestLimiter(100L, 2_000L);
		UUID playerId = UUID.randomUUID();

		assertTrue(limiter.tryAcquire(playerId, "media-001", 1_000L));
		assertFalse(limiter.tryAcquire(playerId, "media-002", 1_050L));
		assertTrue(limiter.tryAcquire(playerId, "media-002", 1_100L));
	}

	@Test
	void limitsRepeatedMediaRequestsAfterPlayerCooldownExpires() {
		DisplayThumbnailRequestLimiter limiter = new DisplayThumbnailRequestLimiter(100L, 2_000L);
		UUID playerId = UUID.randomUUID();

		assertTrue(limiter.tryAcquire(playerId, "media-001", 1_000L));
		assertFalse(limiter.tryAcquire(playerId, "media-001", 1_100L));
		assertTrue(limiter.tryAcquire(playerId, "media-001", 3_000L));
	}

	@Test
	void keepsDifferentPlayersIndependent() {
		DisplayThumbnailRequestLimiter limiter = new DisplayThumbnailRequestLimiter(100L, 2_000L);

		assertTrue(limiter.tryAcquire(UUID.randomUUID(), "media-001", 1_000L));
		assertTrue(limiter.tryAcquire(UUID.randomUUID(), "media-001", 1_000L));
	}
}
