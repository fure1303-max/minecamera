package com.minecamera.client.capture;

import com.minecamera.client.camera.MineCameraModeController;
import com.minecamera.logic.CaptureCompletionGate;
import com.minecamera.logic.CameraRigSnapshot;
import com.minecamera.network.CommitPhotoCapturePayload;
import com.minecamera.network.CommitPhotoCaptureResultPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;

public final class MineCameraCaptureCoordinator {
	private static final ClientCaptureService CAPTURE_SERVICE = new ClientCaptureService();
	private static MineCameraCaptureRequest pendingRequest;
	private static CapturedPhoto pendingCommit;
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
			photo -> gate.run(() -> commitCapture(client, request, author, photo)),
			error -> gate.run(() -> completeFailure(error))
		);
	}

	private static void commitCapture(
		MinecraftClient client,
		MineCameraCaptureRequest request,
		String author,
		CapturedPhoto capturedPhoto
	) {
		if (!ClientPlayNetworking.canSend(CommitPhotoCapturePayload.ID)) {
			ClientCaptureService.deleteCapture(client, capturedPhoto);
			completeFailure("server_missing_capture_protocol");
			return;
		}

		CameraRigSnapshot snapshot = request.snapshot();
		pendingCommit = capturedPhoto;
		ClientPlayNetworking.send(new CommitPhotoCapturePayload(
			request.filmSlot(),
			capturedPhoto.mediaId(),
			snapshot.captureWidth(),
			snapshot.captureHeight(),
			snapshot.focalLengthMm(),
			snapshot.apertureFStop(),
			snapshot.focusMode().name(),
			author,
			System.currentTimeMillis() / 1000L,
			snapshot.sourceType(),
			capturedPhoto.thumbnailBytes()
		));
	}

	public static void onCommitResult(CommitPhotoCaptureResultPayload result) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (pendingCommit == null || !pendingCommit.mediaId().equals(result.mediaId())) {
			return;
		}

		CapturedPhoto completed = pendingCommit;
		pendingCommit = null;
		captureInProgress = false;
		if (result.success()) {
			MineCameraModeController.onCaptureSucceeded(completed.path().getFileName().toString());
		} else {
			ClientCaptureService.deleteCapture(client, completed);
			MineCameraModeController.onCaptureFailed(result.error());
		}
	}

	private static void completeFailure(String error) {
		pendingCommit = null;
		captureInProgress = false;
		MineCameraModeController.onCaptureFailed(error);
	}
}
