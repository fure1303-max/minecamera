package com.minecamera.client.media;

import com.minecamera.media.DisplayThumbnailPolicy;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public final class DisplayThumbnailEncoder {
	private static final float JPEG_QUALITY = 0.82F;
	private static final int MIN_EDGE = 256;

	private DisplayThumbnailEncoder() {
	}

	public static byte[] encode(Path source) throws IOException {
		BufferedImage original = ImageIO.read(source.toFile());
		if (original == null) {
			throw new IOException("Unsupported capture image");
		}

		int targetWidth = original.getWidth();
		int targetHeight = original.getHeight();
		double scale = Math.min(1.0D, (double) DisplayThumbnailPolicy.MAX_EDGE / Math.max(targetWidth, targetHeight));
		targetWidth = Math.max(1, (int) Math.round(targetWidth * scale));
		targetHeight = Math.max(1, (int) Math.round(targetHeight * scale));

		while (true) {
			BufferedImage resized = resize(original, targetWidth, targetHeight);
			byte[] encoded = encodeJpeg(resized);
			if (encoded.length <= DisplayThumbnailPolicy.MAX_BYTES) {
				return encoded;
			}
			if (Math.max(targetWidth, targetHeight) <= MIN_EDGE) {
				throw new IOException("Display thumbnail exceeds network limit");
			}
			targetWidth = Math.max(1, (int) Math.round(targetWidth * 0.8D));
			targetHeight = Math.max(1, (int) Math.round(targetHeight * 0.8D));
		}
	}

	private static BufferedImage resize(BufferedImage source, int width, int height) {
		BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = target.createGraphics();
		try {
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, width, height);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			graphics.drawImage(source, 0, 0, width, height, null);
		} finally {
			graphics.dispose();
		}
		return target;
	}

	private static byte[] encodeJpeg(BufferedImage image) throws IOException {
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
		if (!writers.hasNext()) {
			throw new IOException("JPEG writer unavailable");
		}

		ImageWriter writer = writers.next();
		try (ByteArrayOutputStream bytes = new ByteArrayOutputStream(); ImageOutputStream output = ImageIO.createImageOutputStream(bytes)) {
			writer.setOutput(output);
			ImageWriteParam parameters = writer.getDefaultWriteParam();
			parameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			parameters.setCompressionQuality(JPEG_QUALITY);
			writer.write(null, new IIOImage(image, null, null), parameters);
			output.flush();
			return bytes.toByteArray();
		} finally {
			writer.dispose();
		}
	}
}
