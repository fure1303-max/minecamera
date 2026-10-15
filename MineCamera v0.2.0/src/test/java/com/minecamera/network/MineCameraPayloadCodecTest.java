package com.minecamera.network;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.netty.buffer.Unpooled;
import java.util.UUID;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import org.junit.jupiter.api.Test;

final class MineCameraPayloadCodecTest {
	@Test
	void roundTripsCaptureCommitPayload() {
		CommitPhotoCapturePayload payload = new CommitPhotoCapturePayload(
			4, UUID.randomUUID().toString(), 1920, 1080, 50, 2.8D, "AUTO", "Tester", 1234L, "handheld", new byte[] {1, 2, 3}
		);
		RegistryByteBuf buffer = buffer();

		CommitPhotoCapturePayload.CODEC.encode(buffer, payload);
		CommitPhotoCapturePayload decoded = CommitPhotoCapturePayload.CODEC.decode(buffer);

		assertEquals(payload.filmSlot(), decoded.filmSlot());
		assertEquals(payload.mediaId(), decoded.mediaId());
		assertEquals(payload.width(), decoded.width());
		assertEquals(payload.height(), decoded.height());
		assertEquals(payload.focalLength(), decoded.focalLength());
		assertEquals(payload.aperture(), decoded.aperture());
		assertEquals(payload.focusMode(), decoded.focusMode());
		assertEquals(payload.author(), decoded.author());
		assertEquals(payload.capturedAtEpochSeconds(), decoded.capturedAtEpochSeconds());
		assertEquals(payload.captureSource(), decoded.captureSource());
		assertArrayEquals(payload.thumbnailBytes(), decoded.thumbnailBytes());
	}

	@Test
	void roundTripsThumbnailResponsePayload() {
		DisplayThumbnailPayload payload = new DisplayThumbnailPayload(UUID.randomUUID().toString(), new byte[] {9, 8, 7});
		RegistryByteBuf buffer = buffer();

		DisplayThumbnailPayload.CODEC.encode(buffer, payload);
		DisplayThumbnailPayload decoded = DisplayThumbnailPayload.CODEC.decode(buffer);

		assertEquals(payload.mediaId(), decoded.mediaId());
		assertArrayEquals(payload.thumbnailBytes(), decoded.thumbnailBytes());
	}

	private static RegistryByteBuf buffer() {
		return new RegistryByteBuf(Unpooled.buffer(), DynamicRegistryManager.EMPTY);
	}
}
