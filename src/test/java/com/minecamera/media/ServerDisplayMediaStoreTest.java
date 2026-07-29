package com.minecamera.media;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class ServerDisplayMediaStoreTest {
	@TempDir
	Path temporaryDirectory;

	@Test
	void storesAndReadsValidatedThumbnail() throws IOException {
		ServerDisplayMediaStore store = new ServerDisplayMediaStore(temporaryDirectory);
		String mediaId = UUID.randomUUID().toString();
		byte[] bytes = jpeg();

		assertTrue(store.write(mediaId, bytes));
		assertArrayEquals(bytes, store.read(mediaId).orElseThrow());
	}

	@Test
	void rejectsUnsafeMediaIdAndInvalidImage() throws IOException {
		ServerDisplayMediaStore store = new ServerDisplayMediaStore(temporaryDirectory);

		assertFalse(store.write("../outside", jpeg()));
		assertFalse(store.write(UUID.randomUUID().toString(), new byte[] {1, 2, 3}));
	}

	private static byte[] jpeg() throws IOException {
		BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			ImageIO.write(image, "jpeg", output);
			return output.toByteArray();
		}
	}
}
