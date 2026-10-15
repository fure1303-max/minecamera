package com.minecamera.media;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public final class DisplayThumbnailPolicy {
	public static final int MAX_EDGE = 1_024;
	public static final int MAX_PIXELS = 1_048_576;
	public static final int MAX_BYTES = 1_048_576;

	private DisplayThumbnailPolicy() {
	}

	public static boolean isValidMediaId(String mediaId) {
		if (mediaId == null || mediaId.isBlank()) {
			return false;
		}
		try {
			return UUID.fromString(mediaId).toString().equals(mediaId);
		} catch (IllegalArgumentException exception) {
			return false;
		}
	}

	public static Optional<DisplayThumbnailInfo> inspectJpeg(byte[] bytes) {
		if (bytes == null || bytes.length == 0 || bytes.length > MAX_BYTES) {
			return Optional.empty();
		}

		try (ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
			if (input == null) {
				return Optional.empty();
			}
			Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
			if (!readers.hasNext()) {
				return Optional.empty();
			}

			ImageReader reader = readers.next();
			try {
				if (!"JPEG".equalsIgnoreCase(reader.getFormatName())) {
					return Optional.empty();
				}
				reader.setInput(input, true, true);
				int width = reader.getWidth(0);
				int height = reader.getHeight(0);
				if (width <= 0 || height <= 0 || width > MAX_EDGE || height > MAX_EDGE || (long) width * height > MAX_PIXELS) {
					return Optional.empty();
				}
				return Optional.of(new DisplayThumbnailInfo(width, height));
			} finally {
				reader.dispose();
			}
		} catch (IOException exception) {
			return Optional.empty();
		}
	}

	public record DisplayThumbnailInfo(int width, int height) {
	}
}
