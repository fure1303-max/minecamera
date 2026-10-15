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
	private static final long COMMIT_TIMEOUT_MILLIS = 15_000L;
	private static final ClientCaptureService CAPTURE_SERVICE = new ClientCaptureService();
	private static final CaptureCommitSession COMMIT_SESSION = new CaptureCommitSession(COMMIT_TIMEOUT_MILLIS);
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
		COMMIT_SESSION.begin(capturedPhoto.mediaId(), System.currentTimeMillis());
		try {
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
		} catch (RuntimeException exception) {
			COMMIT_SESSION.reset();
			completeFailure("capture_commit_send_failed");
		}
	}

	public static void onCommitResult(CommitPhotoCaptureResultPayload result) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (pendingCommit == null || !COMMIT_SESSION.matches(result.mediaId())) {
			return;
		}

		CapturedPhoto completed = pendingCommit;
		pendingCommit = null;
		COMMIT_SESSION.reset();
		captureInProgress = false;
		if (result.success()) {
			MineCameraModeController.onCaptureSucceeded(completed.path().getFileName().toString());
		} else {
			ClientCaptureService.deleteCapture(client, completed);
			MineCameraModeController.onCaptureFailed(result.error());
		}
	}

	public static void tick(MinecraftClient client) {
		if (pendingCommit == null || !COMMIT_SESSION.expire(System.currentTimeMillis())) {
			return;
		}
		CapturedPhoto timedOutPhoto = pendingCommit;
		pendingCommit = null;
		captureInProgress = false;
		ClientCaptureService.deleteCapture(client, timedOutPhoto);
		MineCameraModeController.onCaptureFailed("capture_commit_timeout");
	}

	public static void onDisconnected() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (pendingCommit != null && client != null) {
			ClientCaptureService.deleteCapture(client, pendingCommit);
		}
		pendingRequest = null;
		pendingCommit = null;
		captureInProgress = false;
		COMMIT_SESSION.reset();
	}

	private static void completeFailure(String error) {
		pendingCommit = null;
		COMMIT_SESSION.reset();
		captureInProgress = false;
		MineCameraModeController.onCaptureFailed(error);
	}
}
