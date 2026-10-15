package com.minecamera.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

final class DisplayThumbnailPolicyTest {
	@Test
	void acceptsBoundedJpegThumbnail() throws IOException {
		byte[] bytes = encode(320, 180, "jpeg");

		DisplayThumbnailPolicy.DisplayThumbnailInfo info = DisplayThumbnailPolicy.inspectJpeg(bytes).orElseThrow();

		assertEquals(320, info.width());
		assertEquals(180, info.height());
	}

	@Test
	void rejectsPngAndOversizedDimensions() throws IOException {
		assertTrue(DisplayThumbnailPolicy.inspectJpeg(encode(32, 32, "png")).isEmpty());
		assertTrue(DisplayThumbnailPolicy.inspectJpeg(encode(DisplayThumbnailPolicy.MAX_EDGE + 1, 1, "jpeg")).isEmpty());
	}

	@Test
	void onlyAcceptsCanonicalUuidMediaIds() {
		String mediaId = UUID.randomUUID().toString();

		assertTrue(DisplayThumbnailPolicy.isValidMediaId(mediaId));
		assertFalse(DisplayThumbnailPolicy.isValidMediaId("../capture"));
		assertFalse(DisplayThumbnailPolicy.isValidMediaId(mediaId.toUpperCase()));
	}

	private static byte[] encode(int width, int height, String format) throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			ImageIO.write(image, format, output);
			return output.toByteArray();
		}
	}
}
