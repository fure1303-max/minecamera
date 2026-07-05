package com.minecamera.client.capture;

import com.minecamera.client.camera.MineCameraModeController;
import com.minecamera.film.CaptureSettings;
import com.minecamera.film.FilmContentType;
import com.minecamera.film.FilmRecord;
import com.minecamera.film.FilmService;
import com.minecamera.logic.CaptureCompletionGate;
import com.minecamera.logic.CameraRigSnapshot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public final class MineCameraCaptureCoordinator {
	private static final ClientCaptureService CAPTURE_SERVICE = new ClientCaptureService();
	private static MineCameraCaptureRequest pendingRequest;
	private static boolean captureInProgress;

	private MineCameraCaptureCoordinator() {
	}

	public static boolean request(MineCameraCaptureRequest request) {
		if (pendingRequest != null || captureInProgress) {
			return false;
		}
		pendingRequest = request;
		return true;
	}

	public static void capturePendingFrame(MinecraftClient client, RenderTickCounter tickCounter) {
		if (pendingRequest == null || captureInProgress) {
			return;
		}
		if (client.player == null || client.world == null) {
			pendingRequest = null;
			return;
		}

		MineCameraCaptureRequest request = pendingRequest;
		pendingRequest = null;
		captureInProgress = true;
		CaptureCompletionGate gate = new CaptureCompletionGate();
		String author = client.player.getName().getString();
		CAPTURE_SERVICE.captureCurrentView(
			client,
			tickCounter,
			request.snapshot(),
			author,
			request.snapshot().sourceType(),
			path -> gate.run(() -> completeSuccess(client, request, author, path.getFileName().toString())),
			error -> gate.run(() -> completeFailure(error))
		);
	}

	private static void completeSuccess(MinecraftClient client, MineCameraCaptureRequest request, String author, String fileName) {
		captureInProgress = false;
		PlayerEntity player = client.player;
		if (player != null) {
			CameraRigSnapshot snapshot = request.snapshot();
			String mediaId = fileName.endsWith(".png") ? fileName.substring(0, fileName.length() - 4) : fileName;
			ItemStack blankFilm = player.getInventory().getStack(request.filmSlot());
			if (FilmService.isFilm(blankFilm) && FilmService.isBlank(blankFilm)) {
				FilmRecord filmRecord = FilmRecord.exposed(
					FilmContentType.PHOTO,
					mediaId,
					new CaptureSettings(
						snapshot.captureWidth(),
						snapshot.captureHeight(),
						snapshot.focalLengthMm(),
						snapshot.apertureFStop(),
						snapshot.focusMode()
					),
					author,
					System.currentTimeMillis() / 1000L,
					snapshot.sourceType()
				);
				ItemStack exposedFilm = FilmService.createExposedFilm(blankFilm, filmRecord);
				if (exposedFilm != blankFilm) {
					player.getInventory().insertStack(exposedFilm);
				}
			}
		}
		MineCameraModeController.onCaptureSucceeded(fileName);
	}

	private static void completeFailure(String error) {
		captureInProgress = false;
		MineCameraModeController.onCaptureFailed(error);
	}
}
