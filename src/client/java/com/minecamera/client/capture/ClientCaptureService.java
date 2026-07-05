package com.minecamera.client.capture;

import com.minecamera.logic.CaptureCompletionGate;
import com.minecamera.logic.CameraRigSnapshot;
import com.minecamera.media.MediaIndexStore;
import com.minecamera.media.MineCameraPaths;
import com.minecamera.media.StoredMediaEntry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;

public final class ClientCaptureService {
	private final MineCameraOffscreenRenderService renderService = new MineCameraOffscreenRenderService();

	public void captureCurrentView(
		MinecraftClient client,
		RenderTickCounter tickCounter,
		CameraRigSnapshot snapshot,
		String author,
		String captureSource,
		Consumer<Path> onSuccess,
		Consumer<String> onFailure
	) {
		try {
			CaptureCompletionGate completionGate = new CaptureCompletionGate();
			MineCameraPaths paths = MineCameraPaths.fromRunDirectory(client.runDirectory.toPath());
			Path root = paths.root();
			Path captureRoot = paths.captureRoot();
			Files.createDirectories(captureRoot);

			String mediaId = UUID.randomUUID().toString();
			Path finalPath = captureRoot.resolve(mediaId + ".png");
			SimpleFramebuffer captureFramebuffer = new SimpleFramebuffer(
				"MineCamera Capture",
				snapshot.captureWidth(),
				snapshot.captureHeight(),
				true
			);

			try {
				renderService.render(client, tickCounter, snapshot, captureFramebuffer);
			} catch (RuntimeException exception) {
				captureFramebuffer.delete();
				completionGate.run(() -> onFailure.accept(exception.getMessage()));
				return;
			}

			ScreenshotRecorder.takeScreenshot(captureFramebuffer, nativeImage -> {
				try {
					writePng(nativeImage, finalPath);
					writeIndex(root, mediaId, finalPath, snapshot.captureWidth(), snapshot.captureHeight(), author, captureSource);
					completionGate.run(() -> onSuccess.accept(finalPath));
				} catch (IOException exception) {
					completionGate.run(() -> onFailure.accept(exception.getMessage()));
				} finally {
					nativeImage.close();
					captureFramebuffer.delete();
				}
			});
		} catch (IOException exception) {
			onFailure.accept(exception.getMessage());
		}
	}

	private static void writeIndex(
		Path root,
		String mediaId,
		Path imagePath,
		int targetWidth,
		int targetHeight,
		String author,
		String captureSource
	) throws IOException {
		MediaIndexStore store = new MediaIndexStore(root);
		Map<String, StoredMediaEntry> entries = new LinkedHashMap<>(store.load());
		entries.put(
			mediaId,
			new StoredMediaEntry(
				mediaId,
				root.relativize(imagePath).toString().replace('\\', '/'),
				targetWidth,
				targetHeight,
				"photo",
				author,
				captureSource,
				System.currentTimeMillis() / 1000L
			)
		);
		store.save(entries);
	}

	private static void writePng(NativeImage image, Path finalPath) throws IOException {
		image.writeTo(finalPath);
	}
}
