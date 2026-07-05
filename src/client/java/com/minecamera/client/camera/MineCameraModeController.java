package com.minecamera.client.camera;

import com.minecamera.MineCameraMod;
import com.minecamera.client.compat.IrisCompatBridge;
import com.minecamera.client.capture.MineCameraCaptureCoordinator;
import com.minecamera.client.media.ClientPhotoTextureCache;
import com.minecamera.logic.CameraPoseResolver;
import com.minecamera.logic.CameraFocalLengthPolicy;
import com.minecamera.logic.CameraOpticalState;
import com.minecamera.logic.CameraShaderCompatibilityPolicy;
import com.minecamera.logic.CameraRigSnapshot;
import com.minecamera.logic.CameraRigState;
import com.minecamera.logic.CameraModeBindingPolicy;
import com.minecamera.logic.CameraModeInputLatchPolicy;
import com.minecamera.logic.CameraModeScrollEventPolicy;
import com.minecamera.logic.CameraModeSession;
import com.minecamera.logic.CameraModeTogglePolicy;
import com.minecamera.logic.CameraModeWorldInputPolicy;
import com.minecamera.logic.CameraPreviewMode;
import com.minecamera.logic.CameraReviewStrip;
import com.minecamera.logic.CameraViewportOverridePolicy;
import com.minecamera.logic.ResolutionPreset;
import com.minecamera.logic.ShaderCompatibilityState;
import com.minecamera.registry.MineCameraItems;
import com.minecamera.world.block.TripodBlock;
import com.minecamera.world.block.entity.TripodBlockEntity;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class MineCameraModeController {
	private static final Identifier HUD_ID = MineCameraMod.id("camera_hud");
	private static final ClientPhotoTextureCache PHOTO_TEXTURES = ClientPhotoTextureCache.getInstance();
	private static final MineCameraViewfinderService VIEWFINDER = new MineCameraViewfinderService();
	private static final CameraReviewService REVIEWS = new CameraReviewService(PHOTO_TEXTURES);
	private static final CameraHudRenderer HUD = new CameraHudRenderer();
	private static final TripodPoseSyncController TRIPOD_POSE_SYNC = new TripodPoseSyncController();
	private static final CameraCaptureWorkflow CAPTURE_WORKFLOW = new CameraCaptureWorkflow();
	private static final double DEFAULT_FOCUS_DISTANCE = 8.0D;

	private static CameraModeRuntimeState runtime = CameraModeLifecycle.closed(
		idleStatusText(),
		REVIEWS.emptyState(),
		DEFAULT_FOCUS_DISTANCE
	);
	private static CameraInputLatches inputLatches = CameraInputLatches.reset();
	private static int reopenGuardTicks;

	private MineCameraModeController() {
	}

	public static void initialize() {
		ClientTickEvents.END_CLIENT_TICK.register(MineCameraModeController::onEndTick);
		HudElementRegistry.addLast(HUD_ID, MineCameraModeController::renderHud);
	}

	public static void openHandheld(Hand hand) {
		if (reopenGuardTicks > 0) {
			return;
		}
		if (CameraModeTogglePolicy.decide(false, runtime.active(), isTripodActive()) == CameraModeTogglePolicy.Action.CLOSE) {
			close();
			return;
		}
		runtime = CameraModeLifecycle.openHandheld(hand, idleStatusText(), REVIEWS.emptyState(), DEFAULT_FOCUS_DISTANCE);
		VIEWFINDER.clear(MinecraftClient.getInstance());
		resetKeyLatches();
		inputLatches = inputLatches.withCloseMouseDown(CameraModeInputLatchPolicy.closeLatchOnOpen(true));
	}

	public static void openTripod(Hand hand, BlockPos pos) {
		if (reopenGuardTicks > 0) {
			return;
		}
		if (CameraModeTogglePolicy.decide(true, runtime.active(), isTripodActive()) == CameraModeTogglePolicy.Action.CLOSE) {
			close();
			return;
		}
		runtime = CameraModeLifecycle.openTripod(hand, pos, idleStatusText(), REVIEWS.emptyState(), DEFAULT_FOCUS_DISTANCE);
		VIEWFINDER.clear(MinecraftClient.getInstance());
		resetKeyLatches();
		inputLatches = inputLatches.withCloseMouseDown(CameraModeInputLatchPolicy.closeLatchOnOpen(true));
		runtime = runtime.withOpticalState(TRIPOD_POSE_SYNC.initializeFromWorld(
			MinecraftClient.getInstance(),
			runtime.tripodPos(),
			runtime.opticalState()
		));
	}

	public static boolean isActive() {
		return runtime.active();
	}

	public static CameraPreviewMode previewMode() {
		return runtime.session().previewMode();
	}

	public static boolean isTripodActive() {
		return runtime.isTripodActive();
	}

	public static void onCaptureSucceeded(String fileName) {
		runtime = runtime.withLatestCapturedMediaId(mediaIdFromFileName(fileName));
		MinecraftClient client = MinecraftClient.getInstance();
		CameraReviewState nextReviewState = REVIEWS.refresh(
			client,
			client.player,
			runtime.latestCapturedMediaId(),
			runtime.session().selectedReviewMediaId(),
			false
		);
		runtime = runtime.withReviewState(nextReviewState).withStatus(text("status.saved", fileName));
	}

	public static void onCaptureFailed(String error) {
		runtime = runtime
			.withPreviewFailureActive(false)
			.withStatus(text("status.capture_failed_keep_film", displayError(error)));
	}

	public static void refreshViewfinderPreview(MinecraftClient client, RenderTickCounter tickCounter) {
		if (!runtime.active() || runtime.session().previewMode() != CameraPreviewMode.LIVE) {
			return;
		}
		CameraRigSnapshot snapshot = buildCurrentSnapshot(client);
		if (snapshot == null) {
			return;
		}
		ViewfinderRefreshResult result = VIEWFINDER.requestRefresh(client, tickCounter, snapshot);
		if (result.failed()) {
			runtime = runtime
				.withPreviewFailureActive(true)
				.withStatus(text(
					CameraShaderCompatibilityPolicy.previewFailureStatusKey(currentShaderCompatibilityState()),
					displayError(result.error())
				));
			return;
		}
		if (result.succeeded() && runtime.previewFailureActive()) {
			runtime = runtime.withPreviewFailureActive(false).withStatus(idleStatusText());
		}
	}

	public static CameraRigSnapshot currentWorldViewportSnapshot(MinecraftClient client) {
		if (!CameraViewportOverridePolicy.shouldOverrideWorldViewport(
			runtime.active(),
			runtime.session().source(),
			currentShaderCompatibilityState(),
			IrisCompatBridge.isRenderingShadowPass()
		)) {
			return null;
		}
		return buildCurrentSnapshot(client);
	}

	public static boolean handleMouseScroll(MinecraftClient client, double verticalAmount) {
		CameraModeScrollEventPolicy.Action action = CameraModeScrollEventPolicy.decide(runtime.active(), runtime.session().previewMode());
		if (action == CameraModeScrollEventPolicy.Action.PASS_THROUGH) {
			return false;
		}
		if (action == CameraModeScrollEventPolicy.Action.CONSUME_ONLY) {
			return true;
		}

		int wheelNotches = normalizedWheelNotches(verticalAmount);
		if (wheelNotches == 0) {
			return true;
		}

		CameraOpticalState opticalState = runtime.opticalState();
		int nextFocalLength = CameraFocalLengthPolicy.step(opticalState.focalLengthMm(), wheelNotches, isShiftDown(client));
		if (nextFocalLength != opticalState.focalLengthMm()) {
			runtime = runtime
				.withOpticalState(opticalState.withFocalLengthMm(nextFocalLength))
				.withStatus(text("status.focal", nextFocalLength));
		}
		return true;
	}

	private static void onEndTick(MinecraftClient client) {
		if (reopenGuardTicks > 0) {
			reopenGuardTicks--;
		}
		if (!runtime.active()) {
			return;
		}
		if (client.player == null || client.world == null) {
			close();
			return;
		}
		if (client.currentScreen != null) {
			close();
			return;
		}
		if (shouldCloseForBinding(client.player)) {
			close();
			return;
		}
		captureLiveTripodPose(client);
		suppressVanillaWorldInput(client);
		CameraInputDecisions decisions = CameraInputRouter.evaluate(readInputFrame(client), inputLatches);
		inputLatches = decisions.nextLatches();
		if (decisions.captureTriggered()) {
			requestCaptureIfLive(client);
		}
		if (decisions.presetTriggered()) {
			cyclePresetIfLive(client);
		}
		if (decisions.reviewToggleTriggered()) {
			togglePreviewMode(client);
		}
		if (decisions.previousReviewTriggered()) {
			cycleReview(client, -1);
		}
		if (decisions.nextReviewTriggered()) {
			cycleReview(client, 1);
		}
		if (decisions.closeTriggered()) {
			close();
		}
	}

	private static void requestCaptureIfLive(MinecraftClient client) {
		if (runtime.session().previewMode() != CameraPreviewMode.LIVE) {
			return;
		}
		requestCapture(client);
	}

	private static void cyclePresetIfLive(MinecraftClient client) {
		if (runtime.session().previewMode() != CameraPreviewMode.LIVE) {
			return;
		}
		cyclePreset(client);
	}

	private static void requestCapture(MinecraftClient client) {
		CameraCaptureWorkflow.CaptureAttempt attempt = CAPTURE_WORKFLOW.prepareRequest(
			client,
			runtime,
			MineCameraModeController::buildCurrentSnapshot
		);
		if (!attempt.accepted()) {
			runtime = runtime.withStatus(attempt.status());
			return;
		}
		boolean accepted = MineCameraCaptureCoordinator.request(attempt.request());
		if (!accepted) {
			runtime = runtime.withStatus(text("status.capture_busy"));
			return;
		}

		runtime = runtime.withStatus(attempt.status());
	}

	private static void cyclePreset(MinecraftClient client) {
		CameraModeSession nextSession = runtime.session().nextPreset();
		ResolutionPreset preset = nextSession.currentPreset();
		runtime = runtime
			.withSession(nextSession)
			.withStatus(text("status.preset", preset.label(), preset.width(), preset.height()));
	}

	private static void togglePreviewMode(MinecraftClient client) {
		if (runtime.session().previewMode() == CameraPreviewMode.LIVE) {
			CameraReviewState reviewState = REVIEWS.refresh(client, client.player, runtime.latestCapturedMediaId(), null, true);
			CameraModeSession reviewSession = runtime.session()
				.withPreviewMode(CameraPreviewMode.REVIEW)
				.withSelectedReviewMediaId(reviewState.selectedMediaId());
			runtime = runtime
				.withReviewState(reviewState)
				.withSession(reviewSession)
				.withStatus(REVIEWS.statusText(reviewState));
			return;
		}

		runtime = runtime
			.withSession(runtime.session().withPreviewMode(CameraPreviewMode.LIVE).withSelectedReviewMediaId(null))
			.withReviewState(REVIEWS.emptyState())
			.withStatus(text("status.preview_live"));
	}

	private static void cycleReview(MinecraftClient client, int delta) {
		if (runtime.session().previewMode() != CameraPreviewMode.REVIEW) {
			return;
		}
		CameraReviewState reviewState = REVIEWS.refreshIfStale(
			client,
			client.player,
			runtime.reviewState(),
			runtime.latestCapturedMediaId(),
			false
		);
		if (reviewState.isEmpty()) {
			runtime = runtime.withReviewState(reviewState).withStatus(text("status.no_preview_film"));
			return;
		}

		String nextMediaId = CameraReviewStrip.stepSelectedMediaId(reviewState.films(), reviewState.selectedMediaId(), delta);
		CameraModeSession nextSession = runtime.session().withSelectedReviewMediaId(nextMediaId);
		CameraReviewState refreshedReviewState = REVIEWS.refresh(
			client,
			client.player,
			runtime.latestCapturedMediaId(),
			nextMediaId,
			false
		);
		runtime = runtime
			.withSession(nextSession.withSelectedReviewMediaId(refreshedReviewState.selectedMediaId()))
			.withReviewState(refreshedReviewState)
			.withStatus(REVIEWS.statusText(refreshedReviewState));
	}

	private static void renderHud(DrawContext context, RenderTickCounter tickCounter) {
		if (!runtime.active()) {
			return;
		}

		MinecraftClient client = MinecraftClient.getInstance();
		HUD.render(
			context,
			client,
			runtime.session(),
			runtime.opticalState(),
			runtime.reviewState(),
			VIEWFINDER,
			runtime.status(),
			currentShaderCompatibilityState(),
			CameraHudRenderer.countBlankFilms(client.player)
		);
	}

	private static void close() {
		VIEWFINDER.clear(MinecraftClient.getInstance());
		runtime = CameraModeLifecycle.closed(idleStatusText(), REVIEWS.emptyState(), DEFAULT_FOCUS_DISTANCE);
		reopenGuardTicks = 2;
		resetKeyLatches();
	}

	private static CameraInputFrame readInputFrame(MinecraftClient client) {
		long handle = client.getWindow().getHandle();
		return new CameraInputFrame(
			GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS,
			GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_TAB) == GLFW.GLFW_PRESS,
			GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS,
			GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS,
			GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS,
			GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS
		);
	}

	private static void resetKeyLatches() {
		inputLatches = CameraInputLatches.reset();
	}

	private static void suppressVanillaWorldInput(MinecraftClient client) {
		CameraModeWorldInputPolicy.PressedStates states = CameraModeWorldInputPolicy.sanitizePressedStates(
			runtime.active(),
			runtime.session().previewMode(),
			client.options.attackKey.isPressed(),
			client.options.useKey.isPressed(),
			client.options.pickItemKey.isPressed(),
			client.options.leftKey.isPressed(),
			client.options.rightKey.isPressed()
		);
		applyPressedState(client.options.attackKey, states.attackPressed());
		applyPressedState(client.options.useKey, states.usePressed());
		applyPressedState(client.options.pickItemKey, states.pickPressed());
		applyPressedState(client.options.leftKey, states.leftPressed());
		applyPressedState(client.options.rightKey, states.rightPressed());
	}

	private static void applyPressedState(KeyBinding keyBinding, boolean pressed) {
		keyBinding.setPressed(pressed);
	}

	private static int normalizedWheelNotches(double verticalAmount) {
		int rounded = (int) Math.round(verticalAmount);
		if (rounded != 0) {
			return rounded;
		}
		if (verticalAmount > 0.0D) {
			return 1;
		}
		if (verticalAmount < 0.0D) {
			return -1;
		}
		return 0;
	}

	private static boolean isShiftDown(MinecraftClient client) {
		long windowHandle = client.getWindow().getHandle();
		return GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
			|| GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
	}

	private static boolean shouldCloseForBinding(PlayerEntity player) {
		if (player == null) {
			return true;
		}
		if (isTripodActive()) {
			return !hasMountedTripod(MinecraftClient.getInstance());
		}
		boolean heldItemIsCamera = player.getStackInHand(runtime.activeHand()).isOf(MineCameraItems.CAMERA);
		return CameraModeBindingPolicy.shouldClose(isTripodActive(), heldItemIsCamera);
	}

	private static CameraRigState buildCurrentRigState() {
		ResolutionPreset preset = runtime.session().currentPreset();
		CameraOpticalState opticalState = runtime.opticalState();
		if (isTripodActive()) {
			return CameraRigState.tripod(
				preset.width(),
				preset.height(),
				opticalState.focalLengthMm(),
				opticalState.apertureFStop(),
				opticalState.focusMode(),
				opticalState.focusDistance(),
				runtime.tripodPos()
			);
		}
		return CameraRigState.handheld(
			preset.width(),
			preset.height(),
			opticalState.focalLengthMm(),
			opticalState.apertureFStop(),
			opticalState.focusMode(),
			opticalState.focusDistance()
		);
	}

	private static CameraRigSnapshot buildCurrentSnapshot(MinecraftClient client) {
		if (client.player == null) {
			return null;
		}
		CameraRigState rigState = buildCurrentRigState();
		if (isTripodActive()) {
			TripodBlockEntity tripod = getTripodBlockEntity(client);
			float yaw = tripod != null && tripod.hasCameraPose() ? tripod.cameraYaw() : client.player.getYaw();
			float pitch = tripod != null && tripod.hasCameraPose() ? tripod.cameraPitch() : client.player.getPitch();
			return CameraPoseResolver.resolveTripod(rigState, yaw, pitch);
		}
		return CameraPoseResolver.resolveHandheld(rigState, client.player.getEyePos(), client.player.getYaw(), client.player.getPitch());
	}

	private static void captureLiveTripodPose(MinecraftClient client) {
		if (!isTripodActive() || runtime.session().previewMode() != CameraPreviewMode.LIVE || client.player == null) {
			return;
		}
		runtime = runtime.withOpticalState(TRIPOD_POSE_SYNC.captureLivePose(client, runtime.tripodPos(), runtime.opticalState()));
	}

	private static boolean hasMountedTripod(MinecraftClient client) {
		if (runtime.tripodPos() == null || client.world == null) {
			return false;
		}
		return client.world.getBlockState(runtime.tripodPos()).getBlock() instanceof TripodBlock
			&& client.world.getBlockState(runtime.tripodPos()).get(TripodBlock.HAS_CAMERA);
	}

	private static TripodBlockEntity getTripodBlockEntity(MinecraftClient client) {
		if (runtime.tripodPos() == null || client.world == null) {
			return null;
		}
		return client.world.getBlockEntity(runtime.tripodPos()) instanceof TripodBlockEntity tripod ? tripod : null;
	}

	private static MutableText text(String key, Object... args) {
		return Text.translatable("screen.minecamera." + key, args);
	}

	private static Text idleStatusText() {
		return currentShaderCompatibilityState() == ShaderCompatibilityState.IRIS_SHADER_ACTIVE
			? text("status.shader_enabled")
			: text("status.ready");
	}

	private static ShaderCompatibilityState currentShaderCompatibilityState() {
		return IrisCompatBridge.shaderState();
	}

	private static String displayError(String error) {
		return error == null || error.isBlank() ? "unknown" : error;
	}

	private static String mediaIdFromFileName(String fileName) {
		return fileName.endsWith(".png") ? fileName.substring(0, fileName.length() - 4) : fileName;
	}
}
